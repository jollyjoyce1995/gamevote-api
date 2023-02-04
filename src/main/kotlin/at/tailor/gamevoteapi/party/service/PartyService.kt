package at.tailor.gamevoteapi.party.service

import at.tailor.gamevoteapi.party.persistence.PartyEntity
import at.tailor.gamevoteapi.party.persistence.PartyRepository
import at.tailor.gamevoteapi.party.service.data.Party
import at.tailor.gamevoteapi.party.service.data.PartyStatus
import at.tailor.gamevoteapi.party.service.data.PatchPartyRequest
import at.tailor.gamevoteapi.poll.service.domain.Poll
import at.tailor.gamevoteapi.poll.service.domain.PollService
import at.tailor.gamevoteapi.poll.service.persistence.PollRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
class PartyService(
    val partyRepository: PartyRepository,
    val pollService: PollService,
    val pollRepository: PollRepository,
) {

    @Transactional
    fun createParty(party: Party): Party {
        val partyEntity = toEntity(party)
        partyRepository.save(partyEntity)
        return toDomain(partyEntity)
    }

    @Transactional
    fun getParty(id: Long): Party {
        val partyEntity = partyRepository.findById(id).orElseThrow{ ResponseStatusException(HttpStatus.NOT_FOUND) }
        return toDomain(partyEntity)
    }

    private fun toEntity(
        it: Party
    ) = PartyEntity(
        id = it.id ?: 0,
        options = it.options.toList(),
        attendees = it.attendees.toList(),
        status = it.status.toString(),
    )

    private fun toDomain(it: PartyEntity) = Party(
        id = it.id,
        attendees = it.attendees.toSet(),
        options = it.options.toSet(),
        status = PartyStatus.valueOf(it.status),
        results = it.results.toMap()
    )

    @Transactional
    fun patchParty(id: Long, patchPartyRequest: PatchPartyRequest): Party {
        val partyEntity = partyRepository.findById(id).orElseThrow{ ResponseStatusException(HttpStatus.NOT_FOUND) }
        val party = toDomain(partyEntity)
        val fromStatus = party.status
        val toStatus = patchPartyRequest.status
        // no transition
        if (fromStatus == toStatus) {
            return party
        }

        val transition = Pair(fromStatus, toStatus)
        // illegal transitions
        if (transition ==  Pair(PartyStatus.NOMINATION, PartyStatus.RESULTS)) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        }

        if (setOf(PartyStatus.VOTING, PartyStatus.NOMINATION).contains(toStatus)) {
            if (partyEntity.poll != null) {
                val poll = pollService.toDomain(partyEntity.poll!!)
                poll.status = Poll.Companion.Status.COMPLETED
                pollService.updatePoll(poll)
                partyEntity.poll = null
                partyEntity.results = mapOf()
            }
        }
        if (PartyStatus.VOTING == toStatus) {
            val pollEntity = pollService.create(
                Poll(
                    options = party.options,
                    attendees = party.attendees,
                )
            ).let { pollRepository.findById(it.id!!).orElseThrow() }
            partyEntity.poll = pollEntity
        } else if (PartyStatus.RESULTS == toStatus) {
            val poll = pollService.toDomain(partyEntity.poll!!)
            val results = pollService.getResults(poll.id!!)
            partyEntity.results = results

            poll.status = Poll.Companion.Status.COMPLETED
            pollService.updatePoll(poll)
            partyEntity.poll = null
        }
        partyEntity.status = patchPartyRequest.status.toString()
        partyRepository.save(partyEntity)

        return toDomain(partyEntity)
    }
}