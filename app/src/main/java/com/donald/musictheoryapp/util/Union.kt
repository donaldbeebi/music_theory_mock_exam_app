package com.donald.musictheoryapp.util

abstract class Either<out L, out R> {

    abstract val left: L
    abstract val right: R
    abstract val hasLeft: Boolean
    abstract val hasRight: Boolean

    fun getValue(whenLeft: (L) -> Unit, whenRight: (R) -> Unit) {
        when (hasLeft) {
            true -> whenLeft(left)
            false -> whenRight(right)
        }
    }

    class Left<L>(override val left: L) : Either<L, Nothing>() {

        override val right: Nothing
            get() = throw IllegalStateException("Right value does not exist")
        override val hasLeft = true
        override val hasRight = false

    }

    class Right<R>(override val right: R) : Either<Nothing, R>() {

        override val left: Nothing
            get() = throw IllegalStateException("Left value does not exist")
        override val hasLeft = false
        override val hasRight = true

    }

}