package at.tailor.gamevoteapi.party.service.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PartyRepository: JpaRepository<PartyEntity, Long> {
    fun existsByCode(code: String): Boolean
}