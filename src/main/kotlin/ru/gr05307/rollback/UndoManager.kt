package ru.gr05307.rollback

import ru.gr05307.painting.convertation.Plain
import java.util.ArrayDeque

class UndoManager(private val maxSize: Int = 100) {
    private val history = ArrayDeque<Plain>()

    fun save(plain: Plain) {
        history.addLast(plain.copy())
        if (history.size > maxSize) {
            history.removeFirst()
        }
    }

    fun undo(): Plain? {
        return if (history.isNotEmpty()) {
            history.removeLast()
        } else null
    }

    fun canUndo(): Boolean = history.isNotEmpty()
}