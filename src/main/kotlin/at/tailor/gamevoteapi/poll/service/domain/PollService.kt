package at.tailor.gamevoteapi.poll.service.domain

import at.tailor.gamevoteapi.poll.service.persistence.PollEntity
import at.tailor.gamevoteapi.poll.service.persistence.PollRepository
import at.tailor.gamevoteapi.poll.service.persistence.Vote
import at.tailor.gamevoteapi.poll.service.persistence.VoteRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
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
        if (
            currentPoll.status == Poll.Companion.Status.IN_PROGRESS &&
            poll.status == Poll.Companion.Status.COMPLETED
        ) {
            pollEntity.status = poll.status.toString()
            pollEntity = pollRepository.save(pollEntity)
        }



        return toDomain(pollEntity)
    }

    @Transactional
    fun getVotes(id: Long): Map<String, Map<String, Boolean>> {
        var pollEntity = pollRepository.findById(id).orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND) }
        return pollEntity.votes.associate { Pair(it.attendee, it.choices.toMap()) }
    }

    @Transactional
    fun addVote(id: Long, attendee: String, choices: Map<String, Boolean>): Map<String, Boolean> {
        var pollEntity = pollRepository.findById(id).orElseThrow{ ResponseStatusException(HttpStatus.NOT_FOUND) }
        val currentPoll = pollEntity.let { toDomain(pollEntity) }

        if (!currentPoll.attendees.contains(attendee)) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Not an attendee")
        }
        if (currentPoll.status != Poll.Companion.Status.IN_PROGRESS) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Poll is completed.")
        }

        if (choices.keys.any { !currentPoll.options.contains(it) }) throw throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Some options are not valid.")

        val attendees = pollEntity.votes.map { it.attendee }.toSet()
        if (attendees.contains(attendee)) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot override vote.")
        }

        val newVotes = pollEntity.votes.toMutableList()

        val normalizedChoices = pollEntity.options.associate {
            val choice = choices[it] ?: false
            Pair(it, choice)
        }

        newVotes.add(Vote(attendee = attendee, choices = normalizedChoices).let { voteRepository.save(it) })
        pollEntity.votes = newVotes

        val allAttendeesHaveAVote = currentPoll.attendees.all { possibleAttendee ->
            newVotes.map { it.attendee }.contains(possibleAttendee)
        }
        if (allAttendeesHaveAVote) {
            pollEntity.status = Poll.Companion.Status.COMPLETED.toString()
        }

        pollRepository.save(pollEntity)
        return normalizedChoices
    }

    @Transactional
    fun getResults(id: Long): Map<String, Int> {
        var pollEntity = pollRepository.findById(id).orElseThrow{ ResponseStatusException(HttpStatus.NOT_FOUND) }
        val votes = getVotes(id)
        return pollEntity.options.associate { option -> Pair(
            option,
            votes.filter { vote ->
                val choices = vote.value
                choices[option]!!
            }.size
        ) }.map { it }.sortedByDescending { it.value }.associate { it.toPair() }
    }
}