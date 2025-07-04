package at.tailor.gamevoteapi.party.service.persistence

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import java.time.LocalDateTime

@Entity
data class BeerEntity(
    @Id @GeneratedValue var id: Long = 0,
    @ManyToOne var party: PartyEntity? = null,
    @Column var attendee: String = "",
    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL") var createdAt: LocalDateTime = LocalDateTime.now(),
) {
    companion object {
        const val TABLE_NAME = "beer_entity"
    }
}
