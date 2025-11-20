package com.github.sddisk.usernotesmailsender.config

import org.apache.kafka.clients.admin.NewTopic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.TopicBuilder

@Configuration
class TopicConfiguration {

    @Bean
    fun createTopic(): NewTopic = TopicBuilder
        .name("user-registered")
        .partitions(3)
        //.replicas(2)
        .build()
}