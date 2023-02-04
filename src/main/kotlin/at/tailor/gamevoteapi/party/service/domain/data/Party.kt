package at.tailor.gamevoteapi.party.service.domain.data

data class Party (
    val id: Long? = null,
    val attendees: Set<String> = setOf(),
    val options: Set<String> = setOf(),
    val status: PartyStatus = PartyStatus.NOMINATION,
    val results: Map<String, Int>? = null
)