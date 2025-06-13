package at.tailor.gamevoteapi.party.service.persistence

import org.springframework.data.jpa.repository.JpaRepository

interface BeerRepository: JpaRepository<BeerEntity, Long> {
}