package at.tailor.gamevoteapi.poll.service.persistence

import at.tailor.gamevoteapi.party.service.persistence.PartyEntity
import jakarta.persistence.*

@Entity
data class PollEntity (
    @Id @GeneratedValue val id: Long = 0,
    @ElementCollection
    @CollectionTable(name = "poll_options", joinColumns = [JoinColumn(name = "entity_id")])
    @Column(name = "string_value")
    var options: List<String> = mutableListOf(),
    @ElementCollection
    @CollectionTable(name = "string_list", joinColumns = [JoinColumn(name = "entity_id")])
    @Column(name = "string_value")
    var attendees: List<String> = mutableListOf(),

    var status: String = "",
    @OneToMany
    var votes: List<Vote> = listOf(),
    @OneToOne(mappedBy = "poll")
    var party: PartyEntity? = null
) {

}