package com.github.sddisk.usernotesmailsender.service

import com.github.sddisk.usernotesmailsender.dto.EmailDto
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.MailSender
import org.springframework.mail.SimpleMailMessage
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class EmailService(
    private val mailSender: MailSender,
    @Value("\${spring.mail.sender.name}") private val senderName: String,
) {

    @Async
    fun sendEmail(emailDto: EmailDto) {
        val simpleMessage = SimpleMailMessage().apply {
            from = senderName
            setTo(emailDto.to)
            subject = emailDto.subject
            text = emailDto.text
        }
        log.info("Created simple message: $simpleMessage")

        mailSender.send(simpleMessage)
        log.info("Mail send to ${emailDto.to}")
    }

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }
}