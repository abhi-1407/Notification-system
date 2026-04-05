package com.abhilash.notifications.api.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic notifications() {
        return TopicBuilder.name("notifications").partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic retry1() {
        return TopicBuilder.name("notifications-retry-1").partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic retry2() {
        return TopicBuilder.name("notifications-retry-2").partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic retry3() {
        return TopicBuilder.name("notifications-retry-3").partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic dlq() {
        return TopicBuilder.name("notifications-dlq").partitions(1).replicas(1).build();
    }
}