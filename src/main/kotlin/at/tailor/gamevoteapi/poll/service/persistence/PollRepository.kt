package at.tailor.gamevoteapi.poll.service.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PollRepository: JpaRepository<PollEntity, Long>