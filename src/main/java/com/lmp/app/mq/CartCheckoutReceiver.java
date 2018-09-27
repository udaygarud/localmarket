package com.lmp.app.mq;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lmp.app.entity.CustomerOrder;
import com.lmp.app.entity.OrderStatus;
import com.lmp.app.model.CheckoutRequest;
import com.lmp.app.service.CustomerOrderService;

@Component
public class CartCheckoutReceiver {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private CustomerOrderService service;
  
  public void receiveMessage(String message) throws JsonParseException, JsonMappingException, IOException {
    logger.info("Received <" + message + ">");
    ObjectMapper objectMapper = new ObjectMapper();
    CheckoutRequest cRequest = objectMapper.readValue(message,
        new TypeReference<CheckoutRequest>(){});

    CustomerOrder order = service.getOrderByOrderId(cRequest.getOrderId());
    if(order == null || order.getStatus() != OrderStatus.REVIEW) {
      return ;
    }
    logger.info("canceling order {}", message);
    service.cancelCheckout(cRequest);
    logger.info("order cleaned up for orderId {}", cRequest.getOrderId());
  }

  public void receiveMessage(byte[] msg) throws JsonParseException, JsonMappingException, IOException {
    String message = new String(msg);
    logger.info("Received <" + message + ">");
    ObjectMapper objectMapper = new ObjectMapper();
    CheckoutRequest cRequest = objectMapper.readValue(message,
        new TypeReference<CheckoutRequest>(){});

    CustomerOrder order = service.getOrderByOrderId(cRequest.getOrderId());
    if(order == null || order.getStatus() != OrderStatus.REVIEW) {
      return ;
    }
    logger.info("canceling order {}", message);
    service.cancelCheckout(cRequest);
    logger.info("order cleaned up for orderId {}", cRequest.getOrderId());
  }
}