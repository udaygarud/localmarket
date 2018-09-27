package com.lmp.app.mq;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

  static final String topicExchangeName = "topicExchangeName";

  static final String queueName = "checkout-queue";

  //@Bean
  Queue queue() {
    return new Queue(queueName, false);
  }

  //@Bean
  CustomExchange delayExchange() {
      Map<String, Object> args = new HashMap<String, Object>();
      args.put("x-delayed-type", "direct");
      return new CustomExchange("topicExchangeName", "x-delayed-message", true, false, args);
  }

  //@Bean
  Binding binding(Queue queue, CustomExchange exchange) {
    return BindingBuilder.bind(queue).to(exchange).with("cart-checkout").noargs();
  }

  //@Bean
  SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
      MessageListenerAdapter listenerAdapter) {
    SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
    container.setConnectionFactory(connectionFactory);
    container.setQueueNames(queueName);
    container.setMessageListener(listenerAdapter);
    return container;
  }

  //@Bean
  MessageListenerAdapter listenerAdapter(CartCheckoutReceiver receiver) {
    return new MessageListenerAdapter(receiver, "receiveMessage");
  }
}
