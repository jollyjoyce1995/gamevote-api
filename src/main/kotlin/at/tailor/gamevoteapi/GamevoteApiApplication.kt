package at.tailor.gamevoteapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GamevoteApiApplication

fun main(args: Array<String>) {

    // todo: allow adding attendees after starting vote (doesn't work maybe because poll is already done when all other attendees have voted)
    runApplication<GamevoteApiApplication>(*args)
}
