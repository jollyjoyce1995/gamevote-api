package at.tailor.gamevoteapi.poll.controller.data

import at.tailor.gamevoteapi.common.dto.ContextLink
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotEmpty

data class PollDTO (
    val id: Long? = null,
    @field:NotEmpty val attendees: List<String> = listOf(),
    @field:NotEmpty val options: List<String> = listOf(),
    val status: String? = null,
    @JsonProperty("_links")
    val links: Map<String, ContextLink>? = null
)