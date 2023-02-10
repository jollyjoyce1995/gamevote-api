package at.tailor.gamevoteapi.party.controller

import at.tailor.gamevoteapi.common.dto.ContextLink
import at.tailor.gamevoteapi.party.*
import at.tailor.gamevoteapi.party.controller.data.PartyDTO
import at.tailor.gamevoteapi.party.controller.data.PatchPartyDTO
import at.tailor.gamevoteapi.party.controller.data.StringValue
import at.tailor.gamevoteapi.party.service.domain.data.Party
import at.tailor.gamevoteapi.party.service.domain.PartyService
import at.tailor.gamevoteapi.party.service.domain.data.PartyStatus
import at.tailor.gamevoteapi.party.service.domain.data.PatchPartyRequest
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/parties")
class PartyController(
    val partyService: PartyService
) {

    @PostMapping
    fun createParty(@RequestBody partyDTO: PartyDTO): PartyDTO {
        val party = partyService.createParty(partyDTO.let {
            Party(
                attendees = it.attendees,
                options = it.options
            )
        })
        return toDTO(party)
    }

    private fun toDTO(party: Party): PartyDTO {
        val links = mutableMapOf(
            Pair("self", ContextLink("/parties/${party.id}"))
        )
        party.poll?.let { links.put("poll", ContextLink("/polls/${it.id}")) }

        return PartyDTO(
            id = party.id,
            attendees = party.attendees,
            options = party.options,
            status = party.status.toString(),
            results = party.results,
            links = links.toMap()
        )
    }

    // todo: also include a model for party that shows who has voted and who has not
    @GetMapping("/{id}")
    fun getParty(@PathVariable("id") id: Long): PartyDTO {
        val party = partyService.getParty(id)
        return toDTO(party)
    }

    @GetMapping("/{id}/options")
    fun getOptions(@PathVariable("id") id: Long): Set<String> {
        val party = partyService.getParty(id)
        return toDTO(party).options
    }

    @PostMapping("/{id}/options")
    fun postOption(@PathVariable("id") id: Long, @RequestBody value: StringValue): StringValue {
        partyService.addOption(id, value.value)
        return value
    }

    @DeleteMapping("/{id}/options/{optionId}")
    fun deleteOption(@PathVariable("id") id: Long, @PathVariable("optionId") optionId: Int) {
        partyService.deleteOption(id, optionId)
    }

    @GetMapping("/{id}/attendees")
    fun getAttendees(@PathVariable("id") id: Long): Set<String> {
        val party = partyService.getParty(id)
        return toDTO(party).attendees
    }

    @PostMapping("/{id}/attendees")
    fun postAttendee(@PathVariable("id") id: Long, @RequestBody value: StringValue): StringValue {
        partyService.addAttendee(id, value.value)
        // todo: throw exception if attendee is already added
        return value
    }

    @DeleteMapping("/{id}/attendees/{attendeeId}")
    fun deleteAttendee(@PathVariable("id") id: Long, @PathVariable("attendeeId") attendeeId: Int) {
        partyService.deleteAttendee(id, attendeeId)
    }

    @PatchMapping("/{id}")
    fun patchParty(@PathVariable("id") id: Long, @RequestBody patchPartyDTO: PatchPartyDTO): PartyDTO {
        return partyService.patchParty(id, PatchPartyRequest(
            status = PartyStatus.valueOf(patchPartyDTO.status)
        )
        ).let { toDTO(it) }
    }

}



