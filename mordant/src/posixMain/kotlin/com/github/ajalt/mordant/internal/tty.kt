package com.github.ajalt.mordant.internal

import kotlinx.cinterop.*
import platform.posix.*

// https://www.gnu.org/software/libc/manual/html_node/getpass.html
internal actual fun ttySetEcho(echo: Boolean) = memScoped {
    val termios = alloc<termios>()
    if (tcgetattr(STDOUT_FILENO, termios.ptr) != 0) {
        return@memScoped
    }

    termios.c_lflag = if (echo) {
        termios.c_lflag or ECHO.convert()
    } else {
        termios.c_lflag and ECHO.inv().convert()
    }

    tcsetattr(0, TCSAFLUSH, termios.ptr)
}

internal actual fun ttySetCanonical(canonical: Boolean) = memScoped {
    val termios = alloc<termios>()
    if (tcgetattr(STDOUT_FILENO, termios.ptr) != 0) {
        return@memScoped
    }

    termios.c_lflag = if (canonical) {
        termios.c_lflag or ICANON.convert()
        termios.c_oflag or OPOST.convert()
    } else {
        termios.c_lflag and ICANON.inv().convert()
        termios.c_oflag and OPOST.inv().convert()
    }

    tcsetattr(0, TCSAFLUSH, termios.ptr)
}
