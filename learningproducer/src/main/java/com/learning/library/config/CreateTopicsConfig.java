package com.learning.library.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@Profile("local")
public class CreateTopicsConfig {

    @Bean
    NewTopic createBookTopic() {
        return TopicBuilder.name("create-books")
                .partitions(3)
                .replicas(1)
                .build();
    }
	
}
