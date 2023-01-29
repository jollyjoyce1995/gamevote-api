package at.tailor.gamevoteapi.party

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
        return party.let {
            PartyDTO(
                id = it.id,
                attendees = it.attendees,
                options = it.options,
                status = it.status.toString()
            )
        }
    }
}