package at.tailor.gamevoteapi.poll.service.domain

import at.tailor.gamevoteapi.poll.service.persistence.PollEntity
import org.springframework.stereotype.Service

@Service
class PollConverter {

    fun toDomain(pollEntity: PollEntity): Poll {
        return Poll(
            id = pollEntity.id,
            options = pollEntity.options.map { it }.toSet(),
            attendees = pollEntity.attendees.map { it }.toSet(),
            status = Poll.Companion.Status.valueOf(pollEntity.status)
        )
    }
}