package at.tailor.gamevoteapi.party

data class Party (
    val id: Long? = null,
    val attendees: Set<String> = setOf(),
    val options: Set<String> = setOf(),
    val status: PartyStatus = PartyStatus.NOMINATION,
)