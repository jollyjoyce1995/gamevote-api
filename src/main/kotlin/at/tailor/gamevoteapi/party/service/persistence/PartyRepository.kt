package at.tailor.gamevoteapi.party.service.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface PartyRepository: JpaRepository<PartyEntity, Long> {
    fun existsByCode(code: String): Boolean
    fun findByCode(code: String): Optional<PartyEntity>
}