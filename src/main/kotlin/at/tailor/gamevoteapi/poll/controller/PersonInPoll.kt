package at.tailor.gamevoteapi.poll.controller

import com.fasterxml.jackson.annotation.JsonUnwrapped

data class PersonInPollDTO (
    @JsonUnwrapped val person: PersonDTO,
    val isAdmin: Boolean = false
)
