package org.example
import io.javalin.Javalin
import org.eclipse.jetty.websocket.api.Session
import java.util.*

data class Position(val x: Int, val y: Int)

enum class Direction { UP, DOWN, LEFT, RIGHT }

class SnakeGame(private val size: Int, private val startPos: Position) {
    var body: LinkedList<Position> = LinkedList()
    companion object {
        @JvmStatic fun main(args: Array<String>) {
            val app = Javalin.create().start(7000)
            val game = Game(20, 10)

            app.ws("/game") { ws ->
                ws.onConnect { ctx ->
                    println("Client connected")
                }
                ws.onClose { ctx ->
                    println("Client disconnected")
                }
                ws.onMessage { ctx ->
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

class Game(private val width: Int, private val height: Int) {
    private var snake: SnakeGame = SnakeGame(3, Position(width / 2, height / 2))
    private var food: Position = randomPosition()

    fun move(direction: Direction) {
        snake.move(direction)
        if (snake.contains(food)) {
            snake.grow()
            food = randomPosition()
        }
    }

    fun getState(): String {
        val sb = StringBuilder()
        for (y in 0 until height) {
            for (x in 0 until width) {
                val position = Position(x, y)
                if (snake.contains(position)) {
                    sb.append("O")
                } else if (position == food) {
                    sb.append("X")
                } else {
                    sb.append(".")
                }
            }
            sb.append("\n")
        }
        return sb.toString()
    }

     fun isGameOver(): Boolean {
        val head = snake.body.first
        if (head.x < 0 || head.x >= width || head.y < 0 || head.y >= height) {
            return true
        }
        val tail = snake.body.subList(1, snake.body.size)
        if (snake.contains(head) && tail.contains(head)) {
            return true
        }
        return false
    }

    private fun randomPosition(): Position {
        val random = Random()
        var position: Position
        do {
            position = Position(random.nextInt(width), random.nextInt(height))
        } while (snake.contains(position))
        return position
    }
}


