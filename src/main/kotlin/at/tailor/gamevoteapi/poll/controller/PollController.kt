package at.tailor.gamevoteapi.poll.controller

import at.tailor.gamevoteapi.poll.controller.dto.PollDTO
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
        return Poll(
            options = pollDTO.options,
            attendees = pollDTO.attendees,
            status = Poll.Companion.Status.IN_PROGRESS
        ).let { pollService.create(it) }
            .let { PollDTO(
                id = it.id,
                attendees = it.attendees,
                options = it.options,
                status = it.status.toString(),
            ) }
    }

    // todo: create listing

    // todo: votes

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

    // todo: edit
    // todo: get votes
    // todo: add vote
}