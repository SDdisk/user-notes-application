package com.github.sddisk.usernotes.store.entity.user

import com.github.sddisk.usernotes.store.entity.Auditable
import com.github.sddisk.usernotes.store.entity.note.Note
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.hibernate.annotations.NaturalId
import java.util.UUID

@Entity
@Table(name = "user_table")
class User(
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id")
    var id: UUID? = null,
    var username: String = "",
    @NaturalId
    var email: String = "",
    var password: String = "",
    @Enumerated(EnumType.STRING)
    var role: Role = Role.USER,

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    var notes: MutableSet<Note> = mutableSetOf()
): Auditable() {

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