package at.tailor.gamevoteapi.poll.service.persistence

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id

@Entity
data class Person(
    @Id @GeneratedValue var id: Long = 0,
    var name: String = "",
)
