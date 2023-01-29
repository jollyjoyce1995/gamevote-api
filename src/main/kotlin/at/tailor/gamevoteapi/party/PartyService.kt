package at.tailor.gamevoteapi.party

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
class PartyService(
    val partyRepository: PartyRepository
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
        status = PartyStatus.valueOf(it.status)
    )
}