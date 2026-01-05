package com.utility.consumer.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

	public static final String EXCHANGE = "notification.exchange";
	public static final String QUEUE = "consumer.approved.queue";
	public static final String ROUTING_KEY = "consumer.approved";

	@Bean
	public DirectExchange consumerExchange() {
		return new DirectExchange(EXCHANGE);
	}

	@Bean
	public Queue consumerApprovedQueue() {
		return QueueBuilder.durable(QUEUE).build();
	}

	@Bean
	public Binding consumerApprovedBinding() {
		return BindingBuilder.bind(consumerApprovedQueue()).to(consumerExchange()).with(ROUTING_KEY);
	}

	@Bean
	public MessageConverter jsonMessageConverter() {
		return new Jackson2JsonMessageConverter();
	}

	@Bean
	public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory,
			MessageConverter jsonMessageConverter) {
		SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();

		factory.setConnectionFactory(connectionFactory);
		factory.setMessageConverter(jsonMessageConverter);
		return factory;
	}
}