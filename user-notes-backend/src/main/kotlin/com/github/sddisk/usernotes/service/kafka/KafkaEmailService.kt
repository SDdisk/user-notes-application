package com.github.sddisk.usernotes.service.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.sddisk.usernotes.api.dto.email.EmailDto
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class KafkaEmailService(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val objectMapper: ObjectMapper
) {
    private val topicName = "user-registered"

    fun sendWelcomeEmail(email: String, username: String) {
        val emailDto = EmailDto(
            to = email,
            subject = "Welcome to user notes!",
            text = "Thank you $username for creating account!"
        )

        val message = objectMapper.writeValueAsString(emailDto)

        log.info("Sending message to kafka: $message")
        kafkaTemplate.send(topicName, message)
    }

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }
}