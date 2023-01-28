package at.tailor.gamevoteapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GamevoteApiApplication

fun main(args: Array<String>) {
    runApplication<GamevoteApiApplication>(*args)
}
