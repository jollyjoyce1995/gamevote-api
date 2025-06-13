package at.tailor.gamevoteapi.party.controller

import at.tailor.gamevoteapi.common.dto.ContextLink
import at.tailor.gamevoteapi.party.*
import at.tailor.gamevoteapi.party.controller.data.BeerDTO
import at.tailor.gamevoteapi.party.controller.data.PartyDTO
import at.tailor.gamevoteapi.party.controller.data.PatchPartyDTO
import at.tailor.gamevoteapi.party.controller.data.StringValue
import at.tailor.gamevoteapi.party.service.domain.data.Party
import at.tailor.gamevoteapi.party.service.domain.PartyService
import at.tailor.gamevoteapi.party.service.domain.data.Beer
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
                options = it.options,
                beerCount = 0,
                beerPerAttendee = mapOf()
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
            code = party.code,
            links = links.toMap(),
            beerCount = party.beerCount,
            beerPerAttendee = party.beerPerAttendee.toMutableMap()
        )
    }
    
    @GetMapping("/{code}")
    fun getParty(@PathVariable("code") code: String): PartyDTO {
        val party = partyService.getParty(partyService.getIdForCode(code))
        return toDTO(party)
    }

    @GetMapping("/{code}/options")
    fun getOptions(@PathVariable("code") code: String): Set<String> {
        val party = partyService.getParty(partyService.getIdForCode(code))
        return toDTO(party).options
    }

    @PostMapping("/{code}/options")
    fun postOption(@PathVariable("code") code: String, @RequestBody value: StringValue): StringValue {
        partyService.addOption(partyService.getIdForCode(code), value.value)
        return value
    }

    @DeleteMapping("/{code}/options/{optionId}")
    fun deleteOption(@PathVariable("code") code: String, @PathVariable("optionId") optionId: Int) {
        partyService.deleteOption(partyService.getIdForCode(code), optionId)
    }

    @GetMapping("/{code}/attendees")
    fun getAttendees(@PathVariable("code") code: String): Set<String> {
        val party = partyService.getParty(partyService.getIdForCode(code))
        return toDTO(party).attendees
    }

    @PostMapping("/{code}/attendees")
    fun postAttendee(@PathVariable("code") code: String, @RequestBody value: StringValue): StringValue {
        partyService.addAttendee(partyService.getIdForCode(code), value.value)
        // todo: throw exception if attendee is already added bug: security risk, can take over somebody else
        return value
    }

    @DeleteMapping("/{code}/attendees/{attendeeId}")
    fun deleteAttendee(@PathVariable("code") code: String, @PathVariable("attendeeId") attendeeId: Int) {
        partyService.deleteAttendee(partyService.getIdForCode(code), attendeeId)
    }

    @PatchMapping("/{code}")
    fun patchParty(@PathVariable("code") code: String, @RequestBody patchPartyDTO: PatchPartyDTO): PartyDTO {
        return partyService.patchParty(partyService.getIdForCode(code), PatchPartyRequest(
            status = PartyStatus.valueOf(patchPartyDTO.status)
        )
        ).let { toDTO(it) }
    }

    @PostMapping("/{code}/beers")
    fun postBeer(@PathVariable("code") code: String, @RequestBody beer: BeerDTO) {
        partyService.postBeer(partyService.getIdForCode(code), Beer(attendee = beer.attendee))
    }

}



