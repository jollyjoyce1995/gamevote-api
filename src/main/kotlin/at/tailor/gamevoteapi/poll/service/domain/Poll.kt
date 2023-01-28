package at.tailor.gamevoteapi.poll.service.domain

data class Poll (
    val id: Long? = null,
    val options: List<String>,
    val attendees: List<String>,
    val status: Status,
) {
    val votes: Map<String, List<String>?> = attendees.associateWith { null }

    companion object {
        public enum class Status {
            IN_PROGRESS, COMPLETED
        }
    }
}