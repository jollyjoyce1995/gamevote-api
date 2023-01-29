package at.tailor.gamevoteapi.party

data class PartyDTO (
    val id: Long? = null,
    val attendees: Set<String> = setOf(),
    val options: Set<String> = setOf(),
    val status: String? = null,
    val results: Map<String, Int>? = null
)
