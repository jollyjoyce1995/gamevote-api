package at.tailor.gamevoteapi.party.service.domain

import at.tailor.gamevoteapi.party.service.domain.data.Party
import at.tailor.gamevoteapi.party.service.domain.data.PartyStatus
import at.tailor.gamevoteapi.party.service.persistence.PartyEntity
import at.tailor.gamevoteapi.poll.service.domain.Poll
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
        code = it.code ?: "",
    )

    fun toDomain(it: PartyEntity): Party {
        val poll = it.poll?.let{pollConverter.toDomain(it)}
        val status = if (poll == null) {
            PartyStatus.NOMINATION
        } else if (poll.status == Poll.Companion.Status.IN_PROGRESS) {
            PartyStatus.VOTING
        } else {
            PartyStatus.RESULTS
        }
        return Party(
            id = it.id,
            attendees = it.attendees.toSet(),
            options = it.options.toSet(),
            status = status,
            results = it.results.toMap(),
            poll = poll,
            code = it.code
        )
    }
}