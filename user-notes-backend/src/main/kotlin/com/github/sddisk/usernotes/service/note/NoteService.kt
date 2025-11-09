package com.github.sddisk.usernotes.service.note

import com.github.sddisk.usernotes.api.dto.note.NoteDto
import org.springframework.security.core.userdetails.UserDetails
import java.util.UUID

interface NoteService {
    // get all
    // get one
    // create
    // update
    // delete

    fun getUserNotes(userDetails: UserDetails): List<NoteDto>
    fun getUserNoteById(userDetails: UserDetails, noteId: UUID): NoteDto
    fun createUserNote(userDetails: UserDetails, noteDto: NoteDto): NoteDto
    fun updateUserNote(userDetails: UserDetails, updateNoteId: UUID, newNoteDto: NoteDto): NoteDto
    fun deleteUserNoteById(userDetails: UserDetails, noteId: UUID)

    //fun userHasNote(userDetails: UserDetails, noteId: UUID)
}