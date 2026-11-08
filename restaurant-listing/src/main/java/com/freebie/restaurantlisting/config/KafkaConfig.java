package com.freebie.restaurantlisting.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.freebie.restaurantlisting.event.Topics;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConfig {

    private static final int PARTITIONS = 3;

    @Bean
    NewTopic orderCreatedTopic() {
        return TopicBuilder.name(Topics.ORDER_CREATED).partitions(PARTITIONS).replicas(1).build();
    }

    @Bean
    NewTopic orderCreatedDeadLetterTopic() {
        // must have at least as many partitions as the original topic
        return TopicBuilder.name(Topics.ORDER_CREATED + ".DLT").partitions(PARTITIONS).replicas(1).build();
    }

    @Bean
    NewTopic restaurantDecisionTopic() {
        return TopicBuilder.name(Topics.RESTAURANT_DECISION).partitions(PARTITIONS).replicas(1).build();
    }

    @Bean
    NewTopic orderReadyTopic() {
        return TopicBuilder.name(Topics.ORDER_READY).partitions(PARTITIONS).replicas(1).build();
    }

    @Bean
    DefaultErrorHandler kafkaErrorHandler(KafkaTemplate<String, String> template) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(template);
        DefaultErrorHandler handler = new DefaultErrorHandler(recoverer, new FixedBackOff(1000L, 3L));
        // bad data will never succeed on retry, so send it to the dead-letter topic immediately
        handler.addNotRetryableExceptions(JsonProcessingException.class, IllegalArgumentException.class);
        return handler;
    }
}
