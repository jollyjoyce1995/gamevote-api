package at.tailor.gamevoteapi.poll.controller

import jakarta.validation.constraints.NotEmpty

data class PollDTO (
    val id: Long? = null,
    @field:NotEmpty val attendees: List<String> = listOf(),
    @field:NotEmpty val options: List<String> = listOf(),
    val status: String? = null
)