package at.tailor.gamevoteapi.poll.service.persistence

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id

@Entity
data class Option(
    @Id @GeneratedValue val id: Int = 0,
    val value: String = "",
)
