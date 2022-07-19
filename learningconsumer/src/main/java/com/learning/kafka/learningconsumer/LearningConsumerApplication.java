package com.learning.kafka.learningconsumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.ExponentialBackOff;
import org.springframework.util.backoff.FixedBackOff;

import java.util.List;

@SpringBootApplication
@EnableKafka
@Slf4j
public class LearningConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(LearningConsumerApplication.class, args);
	}

	@Bean
	ConcurrentKafkaListenerContainerFactory<?,?> kafkaListenerContainerFactory(ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
																			   ConsumerFactory<Object, Object> kafkaConsumerFactory){

		ConcurrentKafkaListenerContainerFactory<Object, Object> concurrentKafkaListenerContainerFactory = new ConcurrentKafkaListenerContainerFactory<>();
		configurer.configure(concurrentKafkaListenerContainerFactory, kafkaConsumerFactory);
		concurrentKafkaListenerContainerFactory.setConcurrency(3);
		concurrentKafkaListenerContainerFactory.setCommonErrorHandler(getDefaultErrorHandler());

		return concurrentKafkaListenerContainerFactory;
	}

	private DefaultErrorHandler getDefaultErrorHandler() {

		// FixedBackOff will retry after the exact interval provided.
		var fixedBackOff = new FixedBackOff(1000L, 3);
		var defaultErrorHandler = new DefaultErrorHandler(fixedBackOff);

		// Exponential BackOff comes in Handy, If we want to retry but with different intervals. (The interval will be exponential)
		// E.g. If we give starting interval as 1 second, and we want other retries after 5, 10, 20 e.t.c seconds. For that we will set multiplier.
		// So if First value is 1 seconds and multiple is 2. Then second attempt will be after 2 seconds.
		// Third will be after 2*2= 4 sec, 4th will be after 2*4 = 8, and it will go on.
		var exponentialBackOff = new ExponentialBackOff(1_000L,2);
		exponentialBackOff.setMultiplier(2.0);

		// If we want to skip some exception for which we don't want a retry.

		var exceptionList = List.of(IllegalStateException.class);
		exceptionList.forEach(defaultErrorHandler::addNotRetryableExceptions);

		defaultErrorHandler.setRetryListeners(((record, ex, deliveryAttempt) -> {
			log.info("Retry Failed For Records: {}. Delivery Attempt: {}, with Exception: {}", record.offset(), deliveryAttempt,ex.getMessage());
		}));

		return defaultErrorHandler;
	}
}
