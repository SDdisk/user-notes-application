package com.github.sddisk.usernotes.service.note

import com.github.sddisk.usernotes.api.dto.note.NoteDto
import com.github.sddisk.usernotes.exception.NoteNotFoundException
import com.github.sddisk.usernotes.exception.UserNotFoundException
import com.github.sddisk.usernotes.service.user.UserService
import com.github.sddisk.usernotes.store.entity.note.Note
import com.github.sddisk.usernotes.store.repository.note.NoteRepository
import org.slf4j.LoggerFactory
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class NoteServiceImpl(
    private val noteRepository: NoteRepository,
    private val userService: UserService
) : NoteService {
    override fun getUserNotes(userDetails: UserDetails): List<NoteDto> {
        log.info("Getting user notes")

        val user = currentUser(userDetails.username)

        log.info("Found notes")
        return user.notes
            .map { it.toDto() }
    }

    override fun getUserNoteById(
        userDetails: UserDetails,
        noteId: UUID
    ): NoteDto {
        log.info("Getting user note by id $noteId")

        val user = currentUser(userDetails.username)

        return user.notes
            .find { note -> note.id == noteId }
            ?.also { log.info("Found note $it") }
            ?.toDto()
            ?: throw NoteNotFoundException("Note with id=${noteId} not found")
    }

    override fun createUserNote(
        userDetails: UserDetails,
        noteDto: NoteDto
    ): NoteDto {
        log.info("Creating user note with dto $noteDto")

        val user = currentUser(userDetails.username)
        val note = noteDto.toEntity()
        note.user = user

        val saved = noteRepository.save(note)

        log.info("Note successfully created")
        return saved.toDto()
    }

    override fun updateUserNote(
        userDetails: UserDetails,
        updateNoteId: UUID,
        newNoteDto: NoteDto
    ): NoteDto {
        log.info("Updating user note with id $updateNoteId | with new data $newNoteDto")

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

        log.info("Note successfully updated")
        return saved.toDto()
    }

    override fun deleteUserNoteById(
        userDetails: UserDetails,
        noteId: UUID
    ) {
        log.info("Deleting user note with id $noteId")

        val user = currentUser(userDetails.username)
        val existingNoteId = user.notes
            .find { note -> note.id == noteId }
            ?.id
            ?: throw NoteNotFoundException("Note with id=${noteId} not found")

        noteRepository.deleteById(existingNoteId)
        log.info("Note successfully delete")
    }

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

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }
}