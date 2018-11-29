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

import com.lmp.app.entity.CustomerOrder;
import com.lmp.app.exceptions.UnauthorizedException;
import com.lmp.app.model.BaseResponse;
import com.lmp.app.model.CustomerOrderReq;
import com.lmp.app.model.CustomerOrderRequest;
import com.lmp.app.model.StoreRequest;
import com.lmp.app.model.validator.CustomerOrderRequestValidator;
import com.lmp.app.service.CustomerOrderService;
import com.lmp.app.service.StoreService;
import com.lmp.app.utils.ValidationErrorBuilder;
import com.lmp.db.pojo.StoreEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
@RestController
public class CustomerOrderController extends BaseController {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private CustomerOrderService service;
  @Autowired
  private StoreService storeService;

  private CustomerOrderRequestValidator customerOrderRequestValidator;

  @Autowired
  public CustomerOrderController(CustomerOrderRequestValidator customerOrderRequestValidator) {
      this.customerOrderRequestValidator = customerOrderRequestValidator;
  }

  @InitBinder("customerOrderRequest")
  public void setupBinder(WebDataBinder binder) {
      binder.addValidators(customerOrderRequestValidator);
  }

  @RequestMapping(value = "/customer-order", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<?> findByUserId(@Valid @RequestBody CustomerOrderRequest cRequest, Errors errors) {
    if (errors.hasErrors()) {
      return ResponseEntity.badRequest().body(ValidationErrorBuilder.fromBindingErrors(errors));
    }
    logger.info("customer order for the request " + cRequest.toString());
    return new ResponseEntity<BaseResponse>(service.getOrdersByUserId(cRequest), HttpStatus.OK);
  }

  @RequestMapping(value = "/order", method = RequestMethod.GET)
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<?> findById(@Valid @RequestParam("orderId") String id) {
   
    logger.info("customer order for order id {} ", id);
    return new ResponseEntity<CustomerOrder>(service.getOrdersById(id), HttpStatus.OK);
  }
  
  @RequestMapping(value = "/order/getOrderDetails", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<?> getOrder(@Valid @RequestBody CustomerOrderReq cRequest, Errors errors) {
	    if (errors.hasErrors()) {
	      return ResponseEntity.badRequest().body(ValidationErrorBuilder.fromBindingErrors(errors));
	    }
	    if(cRequest.getOrderId()!="" && cRequest.getStoreId()!=""){
	    CustomerOrder custom = service.getOrdersByIdAndStoreId(cRequest.getOrderId(),cRequest.getStoreId());
	    if(custom!=null && !custom.equals(null)){
	    	return new ResponseEntity<CustomerOrder>(custom, HttpStatus.OK);
	    }else{
	    BaseResponse response =new BaseResponse();
	    response.setErrorMessage("No data found ");
	    return new ResponseEntity<BaseResponse>(response, HttpStatus.OK);
	    }
	    }
	    
	    return ResponseEntity.badRequest().body(ValidationErrorBuilder.fromBindingErrors(errors));
	  }

  @RequestMapping(value = "/order/update", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<?> updateOrder(@Valid @RequestBody CustomerOrderRequest cRequest, Errors errors) {
    if (errors.hasErrors()) {
      return ResponseEntity.badRequest().body(ValidationErrorBuilder.fromBindingErrors(errors));
    }
    logger.info("customer order for the request " + cRequest.toString());
    service.updateOrder(cRequest);
   return new ResponseEntity<BaseResponse>(BaseResponse.responseStatus(com.lmp.app.entity.ResponseStatus.ORDER_UPDATED), HttpStatus.OK);
  }
  
  
  
  @RequestMapping(value = "/store-order", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<?> findByStoreOwner(@Valid @RequestBody CustomerOrderRequest cRequest, Errors errors) {

    ObjectMapper mapper = new ObjectMapper();
    StoreRequest obj = new StoreRequest();
    try {
      String jsonInString = mapper.writeValueAsString(obj);
      System.out.println(jsonInString);
    } catch (JsonProcessingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
  }
  
    if (errors.hasErrors()) {
      return ResponseEntity.badRequest().body(ValidationErrorBuilder.fromBindingErrors(errors));
    }
    logger.info("get store id of the owner {}", cRequest.getStoreId());
    //StoreEntity store = storeService.getStoreByOwnerEmail(cRequest.getUserId());
    StoreEntity store = storeService.getStoreById(cRequest.getStoreId());
    if(store == null) {
      logger.info("user {} does not have any stores" , cRequest.getStoreId());
      throw new UnauthorizedException();
    }
    cRequest.setStoreId(store.getId());
    logger.info("customer order for the request " + cRequest.toString());
    return new ResponseEntity<BaseResponse>(service.getOrdersByStoreId(cRequest), HttpStatus.OK);
  }
}
