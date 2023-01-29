package at.tailor.gamevoteapi.party

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PartyService(
    val partyRepository: PartyRepository
) {

    @Transactional
    fun createParty(party: Party): Party {
        val partyEntity = party.let {
            // map domain to entity
            PartyEntity(
                id = it.id ?: 0,
                options = it.options.toList(),
                attendees = it.attendees.toList(),
                status = party.status.toString(),
            )
        }
        partyRepository.save(partyEntity)

        return partyEntity.let {
            // map entity to domain
            Party(
                id = it.id,
                attendees = it.attendees.toSet(),
                options = it.options.toSet(),
                status = PartyStatus.valueOf(it.status)
            )
        }
    }
}