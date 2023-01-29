package at.tailor.gamevoteapi.party

import at.tailor.gamevoteapi.poll.service.persistence.Vote
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PartyRepository: JpaRepository<PartyEntity, Long>