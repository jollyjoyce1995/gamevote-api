package at.tailor.gamevoteapi.party

import at.tailor.gamevoteapi.common.dto.ContextLink
import at.tailor.gamevoteapi.poll.service.domain.PollService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/party")
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

    private fun toDTO(it: Party) = PartyDTO(
        id = it.id,
        attendees = it.attendees,
        options = it.options,
        status = it.status.toString(),
        results = it.results,
        links = mapOf(
            Pair("self", ContextLink("/party/${it.id}"))
        )
    )

    @GetMapping("/{id}")
    fun getParty(@PathVariable("id") id: Long): PartyDTO {
        val party = partyService.getParty(id)
        return toDTO(party)
    }

    // todo: add option
    // todo: add attendee
    // todo: remove option
    // todo: remove attendee



    @PatchMapping("/{id}")
    fun patchParty(@PathVariable("id") id: Long, @RequestBody patchPartyDTO: PatchPartyDTO): PartyDTO {
        return partyService.patchParty(id, PatchPartyRequest(
            status = PartyStatus.valueOf(patchPartyDTO.status)
        )).let { toDTO(it) }
    }

}



