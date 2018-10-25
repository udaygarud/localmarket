package com.lmp.app.controller;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Strings;
import com.lmp.app.entity.CustomerOrder;
import com.lmp.app.entity.ShoppingCart;
import com.lmp.app.model.BaseResponse;
import com.lmp.app.model.CartResponse;
import com.lmp.app.model.CheckoutRequest;
import com.lmp.app.model.ShoppingCartRequest;
import com.lmp.app.model.validator.CartRequestValidator;
import com.lmp.app.service.CustomerOrderService;
import com.lmp.app.service.ShoppingCartService;
import com.lmp.app.utils.ValidationErrorBuilder;

@RestController
@RequestMapping("/cart")
public class ShoppingCartController extends BaseController {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private CartRequestValidator cartRequestValidator;

  @Autowired
  private ShoppingCartService service;
  @Autowired
  private CustomerOrderService orderService;

  @Autowired
  public ShoppingCartController(CartRequestValidator cartRequestValidator) {
    this.cartRequestValidator = cartRequestValidator;
  }

  @InitBinder("shoppingCartRequest")
  public void setupBinder(WebDataBinder binder) {
    binder.addValidators(cartRequestValidator);
  }

  @RequestMapping( method = RequestMethod.GET)
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<?> getCart(@Valid @RequestParam("id") String id) {
    logger.info("getting cart with id {} " + id);
    return new ResponseEntity<ShoppingCart>(service.getCart(id), HttpStatus.OK);
  }

  @RequestMapping(value = "/add", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<?> addToCart(@Valid @RequestBody ShoppingCartRequest shoppingCartRequest, Errors errors) {
    CartRequestValidator.validateQuantity(shoppingCartRequest, errors);
    if (errors.hasErrors()) {
      return ResponseEntity.badRequest().body(ValidationErrorBuilder.fromBindingErrors(errors));
    }
    logger.info("shopping cart request " + shoppingCartRequest.toString());
    ShoppingCart cart = service.add(shoppingCartRequest);
    if (cart == null) {
      logger.info("no cart found or add failed {}", shoppingCartRequest.toString());
      return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<ShoppingCart>(cart, HttpStatus.OK);
  }

  @RequestMapping(value = "/remove", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<?> removeFromCart(@Valid @RequestBody ShoppingCartRequest shoppingCartRequest, Errors errors) {
    if (errors.hasErrors()) {
      return ResponseEntity.badRequest().body(ValidationErrorBuilder.fromBindingErrors(errors));
    }
    logger.info("shopping cart request " + shoppingCartRequest.toString());
    ShoppingCart cart = service.remove(shoppingCartRequest);
    if (cart == null) {
      logger.info("no cart found or add failed {}", shoppingCartRequest.toString());
      return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<ShoppingCart>(cart, HttpStatus.OK);
  }

  @RequestMapping(value = "/update", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<?> updateCart(@Valid @RequestBody ShoppingCartRequest shoppingCartRequest, Errors errors) {
    CartRequestValidator.validateQuantity(shoppingCartRequest, errors);
    if (errors.hasErrors()) {
      return ResponseEntity.badRequest().body(ValidationErrorBuilder.fromBindingErrors(errors));
    }
    logger.info("shopping cart request " + shoppingCartRequest.toString());
    ShoppingCart cart = service.update(shoppingCartRequest);

    if (cart == null) {
      logger.info("no cart found or add failed {}", shoppingCartRequest.toString());
      return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<ShoppingCart>(cart, HttpStatus.OK);
  }

  @RequestMapping(value = "/moveToList", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<?> moveToList(@Valid @RequestBody ShoppingCartRequest shoppingCartRequest, Errors errors) {
    if (errors.hasErrors()) {
      return ResponseEntity.badRequest().body(ValidationErrorBuilder.fromBindingErrors(errors));
    }
    logger.info("move to list request" + shoppingCartRequest.toString());
    service.moveToList(shoppingCartRequest);
    logger.info("item moved to list {} ", shoppingCartRequest.getItemId());
    return new ResponseEntity<CartResponse>(
        BaseResponse.responseStatus(com.lmp.app.entity.ResponseStatus.MOVED_TO_LIST), HttpStatus.OK);
  }
  @RequestMapping(value = "/checkout", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<?> checkout(@Valid @RequestBody CheckoutRequest checkoutRequest, Errors errors) {
    if(Strings.isNullOrEmpty(checkoutRequest.getUserId())) {
      errors.reject("userId.required", "userId is required");
    }
    if (errors.hasErrors()) {
      return ResponseEntity.badRequest().body(ValidationErrorBuilder.fromBindingErrors(errors));
    }
    logger.info("Checkout request" + checkoutRequest.toString());
    CustomerOrder order=  orderService.placeOrder(checkoutRequest);
    logger.info("order placed. order number {} ", order.getId());
    return new ResponseEntity<CartResponse>(CartResponse.orderReceived(order), HttpStatus.OK);
  }

  @RequestMapping(value = "/confirmCheckout", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<?> confirmCheckout(@Valid @RequestBody CheckoutRequest checkoutRequest, Errors errors) {
    if(Strings.isNullOrEmpty(checkoutRequest.getUserId()) || Strings.isNullOrEmpty(checkoutRequest.getOrderId())) {
      errors.reject("userId.required", "userId and orderId are required");
    }
    if (errors.hasErrors()) {
      return ResponseEntity.badRequest().body(ValidationErrorBuilder.fromBindingErrors(errors));
    }
    logger.info("confirm checkout request" + checkoutRequest.toString());
    CustomerOrder order=  orderService.confirmOrder(checkoutRequest);
    logger.info("order placed. order number {} ", order.getId());
    return new ResponseEntity<CartResponse>(CartResponse.orderPlaced(order), HttpStatus.OK);
  }

  @RequestMapping(value = "/cancelCheckout", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<?> cancelCheckout(@Valid @RequestBody CheckoutRequest checkoutRequest, Errors errors) {
    if(Strings.isNullOrEmpty(checkoutRequest.getUserId()) || Strings.isNullOrEmpty(checkoutRequest.getOrderId())) {
      errors.reject("userId.required", "userId and orderId are required");
    }
    if (errors.hasErrors()) {
      return ResponseEntity.badRequest().body(ValidationErrorBuilder.fromBindingErrors(errors));
    }
    logger.info("confirm checkout request" + checkoutRequest.toString());
    CustomerOrder order=  orderService.cancelCheckout(checkoutRequest);
    logger.info("order placed. order number {} ", order.getId());
    return new ResponseEntity<CartResponse>(CartResponse.orderCancelled(order), HttpStatus.OK);
  }
}
