package at.tailor.gamevoteapi.poll.service.domain

import at.tailor.gamevoteapi.poll.service.persistence.PollEntity
import at.tailor.gamevoteapi.poll.service.persistence.PollRepository
import at.tailor.gamevoteapi.poll.service.persistence.Vote
import at.tailor.gamevoteapi.poll.service.persistence.VoteRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.HttpStatusCodeException
import org.springframework.web.server.ResponseStatusException


@Service
class PollService(
    val pollRepository: PollRepository,
    val voteRepository: VoteRepository,
) {

    @Transactional
    fun create(poll: Poll): Poll {
        // map poll to pollEntity
        return PollEntity(
            options = poll.options.toList(),
            status = Poll.Companion.Status.IN_PROGRESS.toString(),
            attendees = poll.attendees.toList()
        ).let { pollRepository.save(it) }
            .let { toDomain(it) }
    }

    @Transactional
    fun getPolls(): List<Poll> {
        val polls = pollRepository.findAll()
        return polls.map { toDomain(it) }
    }

    private fun toDomain(pollEntity: PollEntity): Poll {
        return Poll(
            id = pollEntity.id,
            options = pollEntity.options.map { it }.toSet(),
            attendees = pollEntity.attendees.map { it }.toSet(),
            status = Poll.Companion.Status.valueOf(pollEntity.status)
        )
    }

    @Transactional
    fun getPoll(id: Long): Poll {
        return pollRepository.findById(id).orElseThrow().let { toDomain(it) }
    }

    @Transactional
    fun updatePoll(poll: Poll): Poll {
        var pollEntity = pollRepository.findById(poll.id!!).orElseThrow()
        val currentPoll = pollEntity.let { toDomain(pollEntity) }

        if (
            currentPoll.status == Poll.Companion.Status.COMPLETED &&
            poll.status == Poll.Companion.Status.IN_PROGRESS
        ) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Can't reactivate completed polls."
            )
        }

        currentPoll.status = poll.status

        pollEntity.status = poll.status.toString()
        pollEntity = pollRepository.save(pollEntity)
        return toDomain(pollEntity)
    }

    @Transactional
    fun getVotes(id: Long): Map<String, Map<String, Boolean>> {
        var pollEntity = pollRepository.findById(id).orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND) }
        return pollEntity.votes.associate { Pair(it.attendee, it.choices.toMap()) }
    }

    @Transactional
    fun addVote(id: Long, attendee: String, choices: Map<String, Boolean>): Map<String, Boolean> {
        var pollEntity = pollRepository.findById(id).orElseThrow()
        val currentPoll = pollEntity.let { toDomain(pollEntity) }

        if (!currentPoll.attendees.contains(attendee)) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Not an attendee")
        }
        if (currentPoll.status != Poll.Companion.Status.IN_PROGRESS) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Poll is completed.")
        }

        if (choices.keys.any { !currentPoll.options.contains(it) }) throw throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Some options are not valid.")

        val votes = pollEntity.votes.associate { Pair(it.attendee, it.choices) }
        if (votes.keys.contains(attendee)) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot override vote.")
        }


        return votes.toMutableMap().let {
            it.put(attendee, choices)
            pollEntity.votes = it.toList().map {
                // todo: this is still a problem
                voteRepository.save(Vote(attendee = it.first, choices = it.second))
            }
            pollEntity = pollRepository.save(pollEntity)
            choices
        }
    }
}