package com.github.sddisk.usernotes.api.dto.note

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class NoteDto(
    val id: UUID?,
    val title: String,
    val description: String,
    @field:JsonProperty("pinned")
    val isPinned: Boolean,
    @field:JsonProperty("important")
    val isImportant: Boolean,
)
