package at.tailor.gamevoteapi.poll.controller.data

import com.fasterxml.jackson.annotation.JsonUnwrapped

data class PersonInPollDTO (
    @JsonUnwrapped val person: PersonDTO,
    val isAdmin: Boolean = false
)
