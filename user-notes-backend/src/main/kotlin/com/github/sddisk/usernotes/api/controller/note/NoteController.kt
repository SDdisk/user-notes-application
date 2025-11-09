package com.github.sddisk.usernotes.api.controller.note

import com.github.sddisk.usernotes.api.dto.note.NoteDto
import com.github.sddisk.usernotes.service.note.NoteService
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/notes")
class NoteController(
    private val noteService: NoteService,
) {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun getUserNotes(@AuthenticationPrincipal userDetails: UserDetails) =
        noteService.getUserNotes(userDetails)

    @GetMapping("/{noteId}")
    @ResponseStatus(HttpStatus.OK)
    fun getUserNoteById(@AuthenticationPrincipal userDetails: UserDetails, @PathVariable noteId: UUID) =
        noteService.getUserNoteById(userDetails, noteId)

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    fun createUserNote(@AuthenticationPrincipal userDetails: UserDetails, @RequestBody noteDto: NoteDto) =
        noteService.createUserNote(userDetails, noteDto)

}