package com.github.sddisk.usernotes.store.entity.note

import com.github.sddisk.usernotes.store.entity.Auditable
import com.github.sddisk.usernotes.store.entity.user.User
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "note_table")
class Note(
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "note_id")
    var id: UUID? = null,
    var title: String = "",
    var description: String = "",
    var isPinned: Boolean = false,
    var isImportant: Boolean = false,

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    var user: User? = null,
): Auditable() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Note) return false
        return this.id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "Note(id=$id, title=$title, description=$description, isPinned=$isPinned, isImportant=$isImportant)"
}