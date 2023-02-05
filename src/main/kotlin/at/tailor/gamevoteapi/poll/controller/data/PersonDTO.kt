package at.tailor.gamevoteapi.poll.controller.data

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull


data class PersonDTO (
    @NotNull var id: Long,
    @NotEmpty @NotNull var name: String
)
