package com.utility.notification.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;

@Configuration
public class RabbitMQConfig {

	public static final String EXCHANGE = "notification.exchange";

	public static final String ACCOUNT_QUEUE = "account.notification.queue";
	public static final String REJECT_QUEUE = "account.reject.queue";

	public static final String ACCOUNT_ROUTING_KEY = "account.approved";
	public static final String ACCOUNT_REJECTED_KEY = "account.rejected";
	public static final String BILL_QUEUE = "bill.notification.queue";
	public static final String BILL_ROUTING_KEY = "bill.generated";
	public static final String BILL_REMINDER_QUEUE = "bill.reminder.queue";
	public static final String BILL_REMINDER_KEY = "bill.due.reminder";
	public static final String PASSWORD_RESET_QUEUE = "password.reset.queue";
	public static final String PASSWORD_RESET_KEY   = "password.reset";
	public static final String PAYMENT_OTP_QUEUE = "payment.otp.queue";
	public static final String PAYMENT_OTP_KEY   = "payment.otp.generated";
	
	 @Bean
	    public Queue paymentOtpQueue() {
	        return QueueBuilder.durable(PAYMENT_OTP_QUEUE).build();
	    }

	    @Bean
	    public Binding paymentOtpBinding() {
	        return BindingBuilder
	                .bind(paymentOtpQueue())
	                .to(exchange())
	                .with(PAYMENT_OTP_KEY);
	    }
	    
	@Bean
	public Queue passwordResetQueue() {
	    return QueueBuilder.durable(PASSWORD_RESET_QUEUE).build();
	}

	@Bean
	public Binding passwordResetBinding() {
	    return BindingBuilder
	            .bind(passwordResetQueue())
	            .to(exchange())
	            .with(PASSWORD_RESET_KEY);
	}

	@Bean
	public Queue billReminderQueue() {
		return QueueBuilder.durable(BILL_REMINDER_QUEUE).build();
	}

	@Bean
	public Binding billReminderBinding() {
		return BindingBuilder.bind(billReminderQueue()).to(exchange()).with(BILL_REMINDER_KEY);
	}

	@Bean
	public Queue billQueue() {
		return QueueBuilder.durable(BILL_QUEUE).build();
	}

	@Bean
	public Binding billBinding() {
		return BindingBuilder.bind(billQueue()).to(exchange()).with(BILL_ROUTING_KEY);
	}

	@Bean
	public MessageConverter jsonMessageConverter() {
		return new Jackson2JsonMessageConverter();
	}

	@Bean
	public DirectExchange exchange() {
		return new DirectExchange(EXCHANGE);
	}

	@Bean
	public Queue accountQueue() {
		return QueueBuilder.durable(ACCOUNT_QUEUE).build();
	}

	@Bean
	public Queue rejectQueue() {
		return QueueBuilder.durable(REJECT_QUEUE).build();
	}

	@Bean
	public Binding accountBinding() {
		return BindingBuilder.bind(accountQueue()).to(exchange()).with(ACCOUNT_ROUTING_KEY);
	}

	@Bean
	public Binding rejectBinding() {
		return BindingBuilder.bind(rejectQueue()).to(exchange()).with(ACCOUNT_REJECTED_KEY);
	}
}