package org.example
import java.util.*


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
         return snake.contains(head) && tail.contains(head)
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