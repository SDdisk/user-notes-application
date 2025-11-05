package com.github.sddisk.usernotes

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class UserNotesBackendApplication

fun main(args: Array<String>) {
    runApplication<UserNotesBackendApplication>(*args)
}
