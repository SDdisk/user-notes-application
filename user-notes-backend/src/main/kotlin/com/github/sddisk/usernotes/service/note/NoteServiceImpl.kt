package com.github.sddisk.usernotes.service.note

import com.github.sddisk.usernotes.api.dto.note.NoteDto
import com.github.sddisk.usernotes.exception.NoteNotFoundException
import com.github.sddisk.usernotes.exception.UserNotFoundException
import com.github.sddisk.usernotes.service.user.UserService
import com.github.sddisk.usernotes.store.entity.note.Note
import com.github.sddisk.usernotes.store.repository.note.NoteRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class NoteServiceImpl(
    private val noteRepository: NoteRepository,
    private val userService: UserService
) : NoteService {
    override fun getUserNotes(userDetails: UserDetails): List<NoteDto> {
        val user = currentUser(userDetails.username)
        return user.notes
            .map { it.toDto() }
    }

    override fun getUserNoteById(
        userDetails: UserDetails,
        noteId: UUID
    ): NoteDto {
        val user = currentUser(userDetails.username)
        return user.notes
            .find { note -> note.id == noteId }
            ?.toDto()
            ?: throw NoteNotFoundException("Note with id=${noteId} not found")
    }

    override fun createUserNote(
        userDetails: UserDetails,
        noteDto: NoteDto
    ): NoteDto {
        val user = currentUser(userDetails.username)
        val note = noteDto.toEntity()
        note.user = user

        val saved = noteRepository.save(note)
        return saved.toDto()
    }

    override fun updateUserNote(
        userDetails: UserDetails,
        updateNoteId: UUID,
        newNoteDto: NoteDto
    ): NoteDto {
        val user = currentUser(userDetails.username)
        val note = user.notes
            .find { note -> note.id == updateNoteId }
            ?: throw NoteNotFoundException("Note with id=${updateNoteId} not found")

        note.apply {
            title = newNoteDto.title
            description = newNoteDto.description
            isPinned = newNoteDto.isPinned
            isImportant = newNoteDto.isImportant
        }

        val saved = noteRepository.save(note)

        return saved.toDto()
    }

    override fun deleteUserNoteById(
        userDetails: UserDetails,
        noteId: UUID
    ) {
        val user = currentUser(userDetails.username)
        val existingNoteId = user.notes
            .find { note -> note.id == noteId }
            ?.id
            ?: throw NoteNotFoundException("Note with id=${noteId} not found")

        noteRepository.deleteById(existingNoteId)
    }

//    override fun userHasNote(
//        userDetails: UserDetails,
//        noteId: UUID
//    ) {
//        TODO("Not yet implemented")
//    }

    private fun currentUser(email: String) = userService.findByEmail(email)

    // entity -> dto
    private fun Note.toDto(): NoteDto = NoteDto(
        id = id,
        title = title,
        description = description,
        isPinned = isPinned,
        isImportant = isImportant
    )

    // dto -> entity
    private fun NoteDto.toEntity(): Note = Note(
        title = title,
        description = description,
        isPinned = isPinned,
        isImportant = isImportant
    )
}