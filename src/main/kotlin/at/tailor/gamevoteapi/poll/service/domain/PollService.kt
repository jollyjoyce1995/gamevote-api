package at.tailor.gamevoteapi.poll.service.domain

import at.tailor.gamevoteapi.poll.service.persistence.PollEntity
import at.tailor.gamevoteapi.poll.service.persistence.PollRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PollService(
    val pollRepository: PollRepository
) {

    @Transactional
    fun create(poll: Poll): Poll {
        // map poll to pollEntity
        return PollEntity(
            options = poll.options,
            status = Poll.Companion.Status.IN_PROGRESS.toString(),
            attendees = poll.attendees
        ).let { pollRepository.save(it) }
            .let { toDomain(it) }
    }

    private fun toDomain(pollEntity: PollEntity): Poll {
        return Poll(
            id = pollEntity.id,
            options = pollEntity.options,
            attendees = pollEntity.attendees,
            status = Poll.Companion.Status.valueOf(pollEntity.status),
        )
    }

    /*
    @Transactional
    fun edit(poll: Poll): Poll {
        // get poll for database
        val pollEntity = pollRepository.findById(poll.id).orElseThrow()
        if (pollEntity.status == Poll.Companion.Status.IN_PROGRESS.toString() && poll.status == Poll.Companion.Status.COMPLETED) {
            pollEntity.status = Poll.Companion.Status.COMPLETED.toString()
        }
        return toDomain(pollRepository.save(pollEntity))
    }

    fun addVote(id: Long, attendee: String, options: List<String>) {
        // todo: add vote
    }
    */

}