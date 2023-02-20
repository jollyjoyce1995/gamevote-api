package at.tailor.gamevoteapi.party.service.domain.data

import at.tailor.gamevoteapi.poll.service.domain.Poll

data class Party (
    val id: Long? = null,
    val attendees: Set<String> = setOf(),
    val options: Set<String> = setOf(),
    val status: PartyStatus = PartyStatus.NOMINATION,
    val results: Map<String, Int>? = null,
    val poll: Poll? = null,
    val code: String? = null,
    val beerCount: Int
)