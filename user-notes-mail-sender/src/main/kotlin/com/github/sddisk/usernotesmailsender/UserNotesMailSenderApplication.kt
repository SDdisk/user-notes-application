package com.github.sddisk.usernotesmailsender

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class UserNotesMailSenderApplication

fun main(args: Array<String>) {
    runApplication<UserNotesMailSenderApplication>(*args)
}
