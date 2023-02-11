package at.tailor.gamevoteapi.poll.controller

import at.tailor.gamevoteapi.common.dto.ContextLink
import at.tailor.gamevoteapi.poll.controller.data.PollDTO
import at.tailor.gamevoteapi.poll.service.domain.Poll
import at.tailor.gamevoteapi.poll.service.domain.PollService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.support.WebExchangeBindException
import java.util.function.Consumer


@RestController
@RequestMapping("/polls")
class PollController(val pollService: PollService) {

    @PostMapping
    fun createPoll(@Valid @RequestBody pollDTO: PollDTO): PollDTO {
        return mapDTOToDomain(pollDTO)
            .let { pollService.create(it) }
            .let { mapDomainToDTO(it) }
    }

    @GetMapping
    fun getPolls(): List<PollDTO> {
        val polls = pollService.getPolls()
        return polls.map { mapDomainToDTO(it) }
    }

    @GetMapping("/{id}")
    fun getPoll(@PathVariable("id") id: Long): PollDTO {
        return pollService.getPoll(id).let { mapDomainToDTO(it) }
    }

    @PutMapping("/{id}")
    fun putMapping(@PathVariable("id") id: Long, @Valid @RequestBody pollDTO: PollDTO): PollDTO {
        var poll = mapDTOToDomain(pollDTO)
        poll = pollService.updatePoll(poll)
        return poll.let { mapDomainToDTO(it) }
    }

    @GetMapping("/{id}/votes")
    fun getVotes(@PathVariable("id") id: Long): Map<String, Map<String, Int>> {
        return pollService.getVotes(id)
    }

    @GetMapping("/{id}/outstanding")
    fun getOutstanding(@PathVariable("id") id: Long): List<String> {
        return pollService.getOutstanding(id)
    }

    @PutMapping("/{id}/votes/{attendee}")
    fun putVote(
        @PathVariable("id") id: Long,
        @PathVariable("attendee") attendee: String,
        @RequestBody choices: Map<String, Int>
    ): Map<String, Int> {
        return pollService.addVote(id, attendee, choices)
    }

    @GetMapping("/{id}/results")
    fun getResults(
        @PathVariable("id") id: Long
    ): Map<String, Int> {
        return pollService.getResults(id)
    }

    private fun mapDTOToDomain(pollDTO: PollDTO) = Poll(
        id = pollDTO.id,
        options = pollDTO.options.toSet(),
        attendees = pollDTO.attendees.toSet(),
        status = pollDTO.status?.let { Poll.Companion.Status.valueOf(it) } ?: Poll.Companion.Status.IN_PROGRESS
    )

    private fun mapDomainToDTO(it: Poll): PollDTO {
        return PollDTO(
            id = it.id,
            attendees = it.attendees.map { it.toString() },
            options = it.options.map { it.toString() },
            status = it.status.toString(),
            links = mutableMapOf(
                Pair("outstanding", ContextLink("/polls/${it.id}/outstanding")),
                Pair("results", ContextLink("/polls/${it.id}/results"))
            )
        )
    }

    // todo: move this to a generic place
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(WebExchangeBindException::class)
    fun handleValidationExceptions(
        ex: WebExchangeBindException
    ): Map<String, String?>? {
        val errors: MutableMap<String, String?> = HashMap()
        ex.bindingResult.allErrors.forEach(Consumer { error: ObjectError ->
            val fieldName = (error as FieldError).field
            val errorMessage = error.getDefaultMessage()
            errors[fieldName] = errorMessage
        })
        return errors
    }
}