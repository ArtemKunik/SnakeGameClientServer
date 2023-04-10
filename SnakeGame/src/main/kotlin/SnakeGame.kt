package org.example
import io.javalin.Javalin
import org.eclipse.jetty.websocket.api.Session
import java.util.*
import org.slf4j.LoggerFactory

data class Position(val x: Int, val y: Int)

enum class Direction { UP, DOWN, LEFT, RIGHT }

class SnakeGame(private val size: Int, private val startPos: Position) {
    var body: LinkedList<Position> = LinkedList()
    val logger = LoggerFactory.getLogger(SnakeGame::class.java)
    companion object {
        private val logger = LoggerFactory.getLogger(SnakeGame::class.java)

        @JvmStatic fun main(args: Array<String>) {
            val app = Javalin.create().start(40080)
            val game = Game(20, 10)

            app.ws("/game") { ws ->
                ws.onConnect { ctx ->
                    logger.info("Client connected")
                }
                ws.onClose { ctx ->
                    logger.info("Client disconnected")
                }
                ws.onMessage { ctx ->
                    try {
                        val message = ctx.message()
                        when (message) {
                            "UP" -> game.move(Direction.UP)
                            "DOWN" -> game.move(Direction.DOWN)
                            "LEFT" -> game.move(Direction.LEFT)
                            "RIGHT" -> game.move(Direction.RIGHT)
                        }
                        if (game.isGameOver()) {
                            ctx.send("GAME OVER")
                            ctx.session.close()
                        } else {
                            val state = game.getState()
                            ctx.send(state)
                        }
                    } catch (e: Exception) {
                        logger.error("Error occurred while handling message: {}", e.message, e)
                    }
                }
            }
        }
    }

    init {
        for (i in 0 until size) {
            body.add(Position(startPos.x - i, startPos.y))
        }
    }

    fun move(direction: Direction) {
        val head = body.first
        val newHead = when (direction) {
            Direction.UP -> Position(head.x, head.y - 1)
            Direction.DOWN -> Position(head.x, head.y + 1)
            Direction.LEFT -> Position(head.x - 1, head.y)
            Direction.RIGHT -> Position(head.x + 1, head.y)
        }
        body.addFirst(newHead)
        body.removeLast()
    }
    fun send(session: Session, message: String) {
        if (session.isOpen) {
            session.remote.sendString(message)
        }
    }
    fun grow() {
        val tail = body.last
        val newTail = Position(tail.x, tail.y)
        body.addLast(newTail)
    }

    fun contains(position: Position): Boolean {
        return body.contains(position)
    }

}


