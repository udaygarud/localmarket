package com.lmp.app.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Strings;
import com.lmp.app.entity.CustomerOrder;
import com.lmp.app.entity.OrderStatus;
import com.lmp.app.entity.ShoppingCart;
import com.lmp.app.exceptions.EmptyCartException;
import com.lmp.app.exceptions.InvalidOrderStatusException;
import com.lmp.app.exceptions.OrderNotFoundException;
import com.lmp.app.model.CheckoutRequest;
import com.lmp.app.model.CustomerOrderRequest;
import com.lmp.app.model.SearchResponse;
import com.lmp.app.model.ShoppingCartRequest;
import com.lmp.db.pojo.CustomerOrderEntity;
import com.lmp.db.repository.CustomerOrderRepository;

@Service
public class CustomerOrderService {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private ShoppingCartService cartService;
  @Autowired
  private StoreInventoryService sItemService;
  @Autowired
  private CustomerOrderRepository orderRepo;

  public CustomerOrder getOrderByOrderId(String id) {
    if(Strings.isNullOrEmpty(id)) {
      return null;
    }
    Optional<CustomerOrderEntity> entity = orderRepo.findById(id);
    if(!entity.isPresent()) {
      return null;
    }
    return entity.get().toCustomerOrder();
  }

  public CustomerOrder getOrdersById(String orderId) {    
    Optional<CustomerOrderEntity> entity = orderRepo.findById(orderId);
    
    return entity.isPresent() ? entity.get().toCustomerOrder() : null;
  }
  
  public boolean updateOrder(CustomerOrderRequest cRequest) {
    Optional<CustomerOrderEntity> optional = orderRepo.findById(cRequest.getOrderId());
    if(!optional.isPresent()) {
      throw new OrderNotFoundException();
    }
    CustomerOrderEntity entity = optional.get();
    entity.setStatus(OrderStatus.valueOf(cRequest.getOrderStatus()));
    entity.setOrderedOn(System.currentTimeMillis());
    entity = orderRepo.save(entity);
    return true;
  }
  public SearchResponse<CustomerOrder> getOrdersByUserId(CustomerOrderRequest request) {
    Page<CustomerOrderEntity> orders = null;
    if(request.isGetAllStatusRequest()) {
      orders = orderRepo.findAllByCustomerId(request.getUserId(), request.pageRequesst());
    } else {
      orders = orderRepo.findAllByCustomerIdAndStatus(request.getUserId(), request.getOrderStatus(), request.pageRequesst());
    }
    return SearchResponse.buildOrderResponse(orders);
  }

  public SearchResponse<CustomerOrder> getOrdersByStoreId(CustomerOrderRequest request) {
    Page<CustomerOrderEntity> orders = null;
    if(request.isGetAllStatusRequest()) {
      orders = orderRepo.findAllByStoreId(request.getStoreId(), request.pageRequesst());
    } else {
      orders = orderRepo.findAllByStoreIdAndStatus(request.getStoreId(), request.getOrderStatus(), request.pageRequesst());
    }
    return SearchResponse.buildOrderResponse(orders);
  }

  @Transactional
  public CustomerOrder placeOrder(CheckoutRequest cRequest) {
    if (cRequest == null || Strings.isNullOrEmpty(cRequest.getUserId())) {
      return null;
    }
    // Get the cart for this order
    ShoppingCart cart = cartService.getCart(new ShoppingCartRequest(cRequest.getUserId()));
    if (cart == null || cart.getItems().isEmpty()) {
      throw new EmptyCartException();
    }
    // check the if cart items are in stock
    sItemService.verifyItemStock(cart.getItems());

    // if all good place the order
    CustomerOrderEntity saved = orderRepo.save(CustomerOrderEntity.fromCart(cart));
    // update stock for all items in cart
    sItemService.updateStockCount(cart.getItems(), false);
    cRequest.setOrderId(saved.getId());
    //queue the order
    /*try {
      publish.publish(cRequest);
    } catch (Exception e){
      logger.error("error queuing the checkout order in queue", e);
    }*/
    return saved.toCustomerOrder();
  }

  @Transactional
  public CustomerOrder confirmOrder(CheckoutRequest cRequest) {
    if (cRequest == null || Strings.isNullOrEmpty(cRequest.getUserId()) 
        || Strings.isNullOrEmpty(cRequest.getOrderId())) {
      return null;
    }
    Optional<CustomerOrderEntity> optional = orderRepo.findById(cRequest.getOrderId());
    if(!optional.isPresent()) {
      throw new OrderNotFoundException();
    }
    // check user and order id validation
    CustomerOrderEntity customerOrderEntity = optional.get();
    if(!customerOrderEntity.getCustomer().getId().equals(cRequest.getUserId())) {
      throw new InvalidOrderStatusException();
    }
    // invalid order
    if(!OrderStatus.REVIEW.equals(customerOrderEntity.getStatus())) {
      throw new InvalidOrderStatusException();
    }
    customerOrderEntity.setStatus(OrderStatus.NEW);
    customerOrderEntity.setOrderedOn(System.currentTimeMillis());
    customerOrderEntity = orderRepo.save(customerOrderEntity);

    //finally empty the cart
    cartService.clear(cRequest.getUserId());

    return customerOrderEntity.toCustomerOrder();
  }

  @Transactional
  public CustomerOrder cancelCheckout(CheckoutRequest cRequest) {
    if (cRequest == null || Strings.isNullOrEmpty(cRequest.getUserId()) 
        || Strings.isNullOrEmpty(cRequest.getOrderId())) {
      return null;
    }
    Optional<CustomerOrderEntity> optional = orderRepo.findById(cRequest.getOrderId());
    if(!optional.isPresent()) {
      throw new OrderNotFoundException();
    }
    // check user and order id validation
    CustomerOrderEntity customerOrderEntity = optional.get();
    if(!customerOrderEntity.getCustomer().getId().equals(cRequest.getUserId())) {
      throw new InvalidOrderStatusException();
    }
    // invalid order
    if(OrderStatus.REVIEW != customerOrderEntity.getStatus()) {
      throw new InvalidOrderStatusException();
    }
    // update the order status
    customerOrderEntity.setStatus(OrderStatus.CANCELLED);
    customerOrderEntity.setLastUpdatedOn(System.currentTimeMillis());
    customerOrderEntity = orderRepo.save(customerOrderEntity);
    // update the product stocks 
    // update stock for all items in cart
    sItemService.updateStockCount(customerOrderEntity.getItems(), true);

    return customerOrderEntity.toCustomerOrder();
  }
}
