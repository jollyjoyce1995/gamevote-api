package at.tailor.gamevoteapi.party.service.domain

import at.tailor.gamevoteapi.party.service.domain.data.Beer
import at.tailor.gamevoteapi.party.service.persistence.PartyRepository
import at.tailor.gamevoteapi.party.service.domain.data.Party
import at.tailor.gamevoteapi.party.service.domain.data.PartyStatus
import at.tailor.gamevoteapi.party.service.domain.data.PatchPartyRequest
import at.tailor.gamevoteapi.party.service.persistence.BeerEntity
import at.tailor.gamevoteapi.party.service.persistence.BeerRepository
import at.tailor.gamevoteapi.poll.service.domain.Poll
import at.tailor.gamevoteapi.poll.service.domain.PollService
import at.tailor.gamevoteapi.poll.service.persistence.PollRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.lang.IndexOutOfBoundsException
import java.util.*

@Service
class PartyService(
    val partyRepository: PartyRepository,
    val pollService: PollService,
    val pollRepository: PollRepository,
    val partyConverter: PartyConverter,
    val beerRepository: BeerRepository
) {

    private fun createRandomCode(): String {
        val possibleCharacters = "ABCDEFGHJKLMNPQRSTUVQXYZ23456789"
        return (0 until 6).map { possibleCharacters[Random().nextInt(possibleCharacters.length)] }.joinToString("")
    }

    private fun createCodeForParty(): String {
     lateinit var randomCode: String
        do {
            randomCode = createRandomCode()
        } while (partyRepository.existsByCode(randomCode))
        return randomCode
    }

    @Transactional
    fun createParty(party: Party): Party {
        val partyEntity = partyConverter.toEntity(party)
        partyEntity.code = createCodeForParty()
        partyRepository.save(partyEntity)
        return partyConverter.toDomain(partyEntity)
    }

    @Transactional
    fun getParty(id: Long): Party {
        val partyEntity = partyRepository.findById(id).orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND) }
        return partyConverter.toDomain(partyEntity)
    }

    @Transactional
    fun allowedTransitions(id: Long): Set<PartyStatus> {
        val partyEntity = partyRepository.findById(id).orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND) }
        val party = partyConverter.toDomain(partyEntity)
        val fromStatus = party.status
        return when(fromStatus) {
            PartyStatus.NOMINATION -> if (party.options.isNotEmpty()) setOf(PartyStatus.VOTING) else setOf()
            PartyStatus.VOTING -> setOf(PartyStatus.NOMINATION, PartyStatus.RESULTS)
            PartyStatus.RESULTS -> setOf(PartyStatus.NOMINATION, PartyStatus.VOTING)
        }
    }

    @Transactional
    fun patchParty(id: Long, patchPartyRequest: PatchPartyRequest): Party {
        val partyEntity = partyRepository.findById(id).orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND) }
        val party = partyConverter.toDomain(partyEntity)
        val fromStatus = party.status
        val toStatus = patchPartyRequest.status
        // no transition
        if (fromStatus == toStatus) {
            return party
        }

        // illegal transitions
        if (!allowedTransitions(id).contains(toStatus)) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        }

        if (setOf(PartyStatus.VOTING, PartyStatus.NOMINATION).contains(toStatus)) {
            if (partyEntity.poll != null) {
                val poll = pollService.pollConverter.toDomain(partyEntity.poll!!)
                poll.status = Poll.Companion.Status.COMPLETED
                pollService.updatePoll(poll)
                partyEntity.poll = null
                partyEntity.results = mutableMapOf()
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
            val poll = pollService.pollConverter.toDomain(partyEntity.poll!!)
            val results = pollService.getResults(poll.id!!)
            partyEntity.results = results

            poll.status = Poll.Companion.Status.COMPLETED
            pollService.updatePoll(poll)
        }
        partyEntity.status = patchPartyRequest.status.toString()
        partyRepository.save(partyEntity)

        return partyConverter.toDomain(partyEntity)
    }

    @Transactional
    fun addOption(id: Long, value: String) {
        val partyEntity = partyRepository.findById(id).orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND) }
        val newOptions = partyEntity.options.toMutableSet()
        newOptions += value
        partyEntity.options = newOptions.toMutableList()
        partyRepository.save(partyEntity)
    }

    @Transactional
    fun addAttendee(id: Long, value: String) {
        val partyEntity = partyRepository.findById(id).orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND) }
        if (partyEntity.attendees.contains(value)) throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        val newAttendees = partyEntity.attendees.toMutableSet()
        newAttendees += value
        partyEntity.attendees = newAttendees.toMutableList()

        partyEntity.poll?.let { pollService.addAttendee(it.id, value) }
        partyRepository.save(partyEntity)
    }

    @Transactional
    fun deleteAttendee(id: Long, attendeeId: Int) {
        val partyEntity = partyRepository.findById(id).orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND) }
        val newAttendees = partyEntity.attendees.toMutableList()
        try {
            newAttendees.removeAt(attendeeId)
        } catch (e: IndexOutOfBoundsException) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND)
        }
        partyEntity.attendees = newAttendees
        partyRepository.save(partyEntity)
    }

    @Transactional
    fun deleteOption(id: Long, optionId: Int) {
        val partyEntity = partyRepository.findById(id).orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND) }
        val newOptions = partyEntity.options.toMutableList()
        try {
            newOptions.removeAt(optionId)
        } catch (e: IndexOutOfBoundsException) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND)
        }
        partyEntity.options = newOptions
        partyRepository.save(partyEntity)
    }

    fun getIdForCode(code: String): Long {
        return partyRepository.findByCode(code).orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND) }.id
    }

    @Transactional
    fun postBeer(id: Long, beer: Beer) {
        val partyEntity = partyRepository.findById(id).orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND) }
        val beerEntity = BeerEntity(party = partyEntity, attendee = beer.attendee)
        beerRepository.save(beerEntity)
    }
}