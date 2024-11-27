package com.grupp.assessment.productexplorer

import android.util.Log
import timber.log.Timber

class TestTree: Timber.Tree() {
    val ANSI_RESET: String = "\u001B[0m"

    val ANSI_BLACK: String = "\u001B[30m"

    val ANSI_RED: String = "\u001B[31m"

    val ANSI_GREEN: String = "\u001B[32m"

    val ANSI_YELLOW: String = "\u001B[33m"

    val ANSI_BLUE: String = "\u001B[34m"

    val ANSI_PURPLE: String = "\u001B[35m"

    val ANSI_CYAN: String = "\u001B[36m"

    val ANSI_WHITE: String = "\u001B[37m"

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        val color = when(priority) {
            1 -> ANSI_YELLOW

            2 -> ANSI_RED

            3 -> ANSI_GREEN

            4 -> ANSI_PURPLE

            else -> ANSI_RESET
        }

        if(message.isNotEmpty()) println("$color$message${ANSI_RESET}")
        t?.also { print(it) }
    }
}