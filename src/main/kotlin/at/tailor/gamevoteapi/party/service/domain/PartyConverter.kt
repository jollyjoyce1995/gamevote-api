package at.tailor.gamevoteapi.party.service.domain

import at.tailor.gamevoteapi.party.service.domain.data.Party
import at.tailor.gamevoteapi.party.service.domain.data.PartyStatus
import at.tailor.gamevoteapi.party.service.persistence.PartyEntity
import at.tailor.gamevoteapi.poll.service.domain.PollConverter
import org.springframework.stereotype.Service

@Service
class PartyConverter(
    val pollConverter: PollConverter
) {
    fun toEntity(
        it: Party
    ) = PartyEntity(
        id = it.id ?: 0,
        options = it.options.toList(),
        attendees = it.attendees.toList(),
        status = it.status.toString(),
    )

    fun toDomain(it: PartyEntity) = Party(
        id = it.id,
        attendees = it.attendees.toSet(),
        options = it.options.toSet(),
        status = PartyStatus.valueOf(it.status),
        results = it.results.toMap(),
        poll = it.poll?.let { pollConverter.toDomain(it) }
    )
}