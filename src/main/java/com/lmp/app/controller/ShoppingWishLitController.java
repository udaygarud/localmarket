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
import com.lmp.app.entity.ShoppingWishList;
import com.lmp.app.model.BaseResponse;
import com.lmp.app.model.CartResponse;
import com.lmp.app.model.CheckoutRequest;
import com.lmp.app.model.ShoppingWishListRequest;
import com.lmp.app.model.validator.WishListRequestValidator;
import com.lmp.app.service.CustomerOrderService;
import com.lmp.app.service.ShoppingWishService;
import com.lmp.app.utils.ValidationErrorBuilder;

@RestController
@RequestMapping("/wishList")
public class ShoppingWishLitController extends BaseController{
	 private final Logger logger = LoggerFactory.getLogger(this.getClass());
	 private WishListRequestValidator wishRequestValidator;

	 @Autowired
	  private ShoppingWishService service;
	  @Autowired
	  private CustomerOrderService orderService;

	  @Autowired
	  public ShoppingWishLitController(WishListRequestValidator wishRequestValidator) {
	    this.wishRequestValidator = wishRequestValidator;
	  }

	  @InitBinder("shoppingWishListRequest")
	  public void setupBinder(WebDataBinder binder) {
	    binder.addValidators(wishRequestValidator);
	  }

	  @RequestMapping(value="/getWish", method = RequestMethod.GET)
	  @ResponseStatus(HttpStatus.OK)
	  public ResponseEntity<?> getCart(@Valid @RequestParam("id") String id) {
	    logger.info("getting cart with id {} " + id);
	    return new ResponseEntity<ShoppingWishList>(service.getCart(id), HttpStatus.OK);
	  }

	  @RequestMapping(value = "/add", method = RequestMethod.POST)
	  @ResponseStatus(HttpStatus.OK)
	  public ResponseEntity<?> addToCart(@Valid @RequestBody ShoppingWishListRequest shoppingWishListRequest, Errors errors) {
		  WishListRequestValidator.validateQuantity(shoppingWishListRequest, errors);
	    if (errors.hasErrors()) {
	      return ResponseEntity.badRequest().body(ValidationErrorBuilder.fromBindingErrors(errors));
	    }
	    System.out.println("add in wish list");
	    logger.info("shopping wish list request " + shoppingWishListRequest.toString());
	    System.out.println(" in wish ");
	    ShoppingWishList cart = service.add(shoppingWishListRequest);
	    if (cart == null) {
	      logger.info("no cart found or add failed {}", shoppingWishListRequest.toString());
	      return new ResponseEntity(HttpStatus.NOT_FOUND);
	    }
	    return new ResponseEntity<ShoppingWishList>(cart, HttpStatus.OK);
	  }

	  @RequestMapping(value = "/remove", method = RequestMethod.POST)
	  @ResponseStatus(HttpStatus.OK)
	  public ResponseEntity<?> removeFromCart(@Valid @RequestBody ShoppingWishListRequest shoppingWishListRequest, Errors errors) {
	    if (errors.hasErrors()) {
	      return ResponseEntity.badRequest().body(ValidationErrorBuilder.fromBindingErrors(errors));
	    }
	    System.out.println("remove from wish list");
	    logger.info("shopping cart request " + shoppingWishListRequest.toString());
	    ShoppingWishList cart = service.remove(shoppingWishListRequest);
	    if (cart == null) {
	      logger.info("no cart found or add failed {}", shoppingWishListRequest.toString());
	      return new ResponseEntity(HttpStatus.NOT_FOUND);
	    }
	    return new ResponseEntity<ShoppingWishList>(cart, HttpStatus.OK);
	  }

	  
}
