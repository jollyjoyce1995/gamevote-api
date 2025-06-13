package at.tailor.gamevoteapi.party.controller.data

import at.tailor.gamevoteapi.common.dto.ContextLink
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class PartyDTO (
    val id: Long? = null,
    val attendees: Set<String> = setOf(),
    val options: Set<String> = setOf(),
    val status: String? = null,
    val results: Map<String, Int>? = null,
    val code: String? = null,
    @JsonProperty("_links")
    val links: Map<String, ContextLink>? = null,
    val beerCount: Int,
    val beerPerAttendee: MutableMap<String, Int> = mutableMapOf(),
    val beerChartData: Map<String, List<LocalDateTime>>?
)
