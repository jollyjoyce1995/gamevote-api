package at.tailor.gamevoteapi.poll.service.domain

data class Poll (
    val id: Long? = null,
    val options: Set<String>,
    val attendees: Set<String>,
    var status: Status = Status.IN_PROGRESS
) {

    companion object {
        public enum class Status {
            IN_PROGRESS, COMPLETED
        }
    }
}