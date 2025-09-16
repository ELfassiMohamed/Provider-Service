package com.provider_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;


@Configuration
public class RabbitMQConfig {
	 @Value("${rabbitmq.queue.patient-activation:patient-activation-queue}")
	    private String patientActivationQueue;
	    
	    @Value("${rabbitmq.queue.medical-updates:medical-updates-queue}")
	    private String medicalUpdatesQueue;
	    
	    @Value("${rabbitmq.queue.patient-registration:patient-registration-queue}")
	    private String patientRegistrationQueue;
	    
	    // Queue declarations
	    @Bean
	    public Queue patientActivationQueue() {
	        return new Queue(patientActivationQueue, true); // durable = true
	    }
	    
	    @Bean
	    public Queue medicalUpdatesQueue() {
	        return new Queue(medicalUpdatesQueue, true);
	    }
	    
	    @Bean
	    public Queue patientRegistrationQueue() {
	        return new Queue(patientRegistrationQueue, true);
	    }
	    
	    // Exchange declaration
	    @Bean
	    public TopicExchange exchange() {
	        return new TopicExchange("patient-care-exchange", true, false);
	    }
	    
	    // Bindings - Provider Service bindings
	    @Bean
	    public Binding patientActivationBinding() {
	        return BindingBuilder
	                .bind(patientActivationQueue())
	                .to(exchange())
	                .with("patient.activation");
	    }
	    
	    @Bean
	    public Binding medicalUpdatesBinding() {
	        return BindingBuilder
	                .bind(medicalUpdatesQueue())
	                .to(exchange())
	                .with("medical.updates");
	    }
	    
	    @Bean
	    public Binding patientRegistrationBinding() {
	        return BindingBuilder
	                .bind(patientRegistrationQueue())
	                .to(exchange())
	                .with("patient.registration");
	    }
	    
	    // Message converter for JSON
	    @Bean
	    public Jackson2JsonMessageConverter messageConverter() {
	        return new Jackson2JsonMessageConverter();
	    }
	    
	    // RabbitTemplate configuration
	    @Bean
	    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
	        RabbitTemplate template = new RabbitTemplate(connectionFactory);
	        template.setMessageConverter(messageConverter());
	        return template;
	    }
}
