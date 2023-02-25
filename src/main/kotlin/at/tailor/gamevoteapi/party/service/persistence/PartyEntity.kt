package at.tailor.gamevoteapi.party.service.persistence

import at.tailor.gamevoteapi.poll.service.persistence.PollEntity
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
    @OneToOne
    var poll: PollEntity? = null,
    @ElementCollection
    var results: Map<String, Int> = mapOf(),
    @Column(unique = true)
    var code: String? = null,
    // todo: remove
    @Column(name = "beer_count")
    var beerCount: Int = 0,
    @OneToMany(mappedBy = "party")
    val beers: MutableList<BeerEntity> = mutableListOf()
) {

}
