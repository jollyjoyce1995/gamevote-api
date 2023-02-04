package at.tailor.gamevoteapi.party.persistence

import at.tailor.gamevoteapi.poll.service.persistence.PollEntity
import at.tailor.gamevoteapi.poll.service.persistence.Vote
import jakarta.persistence.*

@Entity
data class PartyEntity(
    @Id @GeneratedValue val id: Long = 0,
    @ElementCollection
    @CollectionTable(name = "party_options", joinColumns = [JoinColumn(name = "entity_id")])
    @Column(name = "string_value")
    var options: List<String> = mutableListOf(),
    @ElementCollection
    @CollectionTable(name = "party_attendees", joinColumns = [JoinColumn(name = "entity_id")])
    @Column(name = "string_value")
    var attendees: List<String> = mutableListOf(),

    var status: String = "",
    @ManyToOne
    var poll: PollEntity? = null,
    @ElementCollection
    var results: Map<String, Int> = mapOf()
)
