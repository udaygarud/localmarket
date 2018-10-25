package com.lmp.app.mq;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lmp.app.model.CheckoutRequest;

@Component
public class Publisher {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private final RabbitTemplate rabbitTemplate;
  private final int DELAY = 5*60*1000; // 5 mins

  public Publisher(RabbitTemplate rabbitTemplate) {
    this.rabbitTemplate = rabbitTemplate;
  }

  public void publish(CheckoutRequest request) throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();
    String json = objectMapper.writeValueAsString(request);
    // we schedule delayed processing for checkout orders
    schedule("cart-checkout", json);
  }

  public void publish(String routingKey, String message) throws Exception {
    logger.info("Sending message...{}", message);
    rabbitTemplate.convertAndSend(RabbitMQConfig.topicExchangeName, routingKey, message);
  }

  public void schedule(String routingKey, String message) throws Exception {
    logger.info("Sending message...{}", message);
    MessageProperties props = new MessageProperties();
    props.setHeader(MessageProperties.X_DELAY, DELAY);
    rabbitTemplate.send(RabbitMQConfig.topicExchangeName, routingKey, new Message(message.getBytes(), props));
  }
}