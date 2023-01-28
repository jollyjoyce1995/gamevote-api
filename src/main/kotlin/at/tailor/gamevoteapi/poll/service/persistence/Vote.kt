package at.tailor.gamevoteapi.poll.service.persistence

import jakarta.persistence.*

@Entity
data class Vote(
    @Id @GeneratedValue var id: Long = 0,
    var attendee: String = "",

    @ElementCollection
    var choices: Map<String, Boolean> = emptyMap(),
)
