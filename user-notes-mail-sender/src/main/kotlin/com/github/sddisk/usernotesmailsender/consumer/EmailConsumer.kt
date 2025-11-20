package com.github.sddisk.usernotesmailsender.consumer

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.sddisk.usernotesmailsender.dto.EmailDto
import com.github.sddisk.usernotesmailsender.service.EmailService
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class EmailConsumer(
    private val emailService: EmailService,
    private val objectMapper: ObjectMapper,
) {

    @KafkaListener(topics = ["user-registered"], concurrency = "3")
    fun sendEmail(message: String) {
        log.info("Read message from topic user-registered: $message")
        val emailDto: EmailDto = objectMapper.readValue(message, EmailDto::class.java)
        log.info("Message converted to emailDto $emailDto")
        emailService.sendEmail(emailDto)
    }

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }
}