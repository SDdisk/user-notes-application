package com.github.sddisk.usernotes.store.repository.note

import com.github.sddisk.usernotes.store.entity.note.Note
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface NoteRepository : JpaRepository<Note, UUID>