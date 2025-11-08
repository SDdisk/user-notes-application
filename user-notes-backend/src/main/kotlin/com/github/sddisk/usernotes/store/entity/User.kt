package com.github.sddisk.usernotes.store.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.NaturalId
import java.util.UUID

@Entity
@Table(name = "user_table")
class User(
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,
    var username: String = "",
    @NaturalId
    var email: String = "",
    var password: String = "",
    var role: Role = Role.USER,
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is User) return false
        return this.email == other.email
    }
    override fun hashCode(): Int = email.hashCode()
    override fun toString(): String = "User(id=$id, username=$username, email=$email, password=[HIDDEN], role=$role)"
}

enum class Role {
    USER, ADMIN
}