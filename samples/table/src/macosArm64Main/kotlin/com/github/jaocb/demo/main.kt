package com.github.jaocb.demo

import com.github.ajalt.mordant.animation.animation
import com.github.ajalt.mordant.rendering.*
import com.github.ajalt.mordant.rendering.TextAlign.LEFT
import com.github.ajalt.mordant.rendering.TextAlign.RIGHT
import com.github.ajalt.mordant.rendering.TextColors.*
import com.github.ajalt.mordant.table.*
import com.github.ajalt.mordant.terminal.Terminal
import com.github.ajalt.mordant.widgets.HorizontalRule
import com.github.ajalt.mordant.widgets.ScrollRegion
import com.github.ajalt.mordant.widgets.Text
import kotlinx.cinterop.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import platform.osx.curscr
import platform.posix.*
//import platform.posix.*


fun main() {
    val messages = (1..100).map {
        "Line number $it"
    }

    signal(SIGINT, staticCFunction<Int, Unit> {
        println("Interrupt: $it")
        Terminal().setRawMode(enabled = false)
        exit(0)
    })



    val terminal = Terminal()
    terminal.setRawMode(enabled = true)

    terminal.cursor.move {
        setPosition(0, 0)
        clearScreen()
    }
    val animation = terminal.animation<Pair<String, Input?>> {
        Text("${it.first} (${it.second})")
    }

//    terminal.print(Text("Hello World"))
//    animation.update("Hello World")
//
//    animation.update("Hello")
//
//    animation.update("Hello Wor")

//    terminal.cursor.move {
//        this.left(3)
//    }




//    terminal.print(conversation("Take it ot the street paopsdkpoaskdo aksdpoak sdopkapsodkpoaksdokasodpkaopskdopaksopdkap", messages, terminal.info.height))
//    terminal.cursor.hide()
//    println("native arm64")

//    while (true) {}

//    char c;
//    while (read(STDIN_FILENO, &c, 1) == 1 && c != 'q') {
//        if (iscntrl(c)) {
//            printf("%d\n", c);
//        } else {
//            printf("%d ('%c')\n", c, c);
//        }
//    }

//    NSEvent.addGlobalMonitorForEventsMatchingMask(NSEventMaskAny) {
//        println("event: $it")
//    }
//    NSApplication.sharedApplication.run()


//    memScoped {
//        val char = alloc<ByteVar>()
////    val char = alloc(ByteVar)
//        while (read(STDIN_FILENO, char.ptr, 1).toInt() == 1 && char.value.toInt().toChar() != "q".toCharArray().first()) {
//
//            println("byte: ${char.value}")
//        }
//    }

    runBlocking {
        var lineBuffer = StringBuilder()

        input().collect {
            if (it == Input.Character('q')) {
                exit(0)
            } else {
                when (it) {
                    is Input.Character -> {
                        lineBuffer.append(it.char)
                        animation.update(lineBuffer.toString() to null)
                    }
                    is Input.Backspace, is Input.DeleteKey -> {
                        if (!lineBuffer.isEmpty()) {
                            lineBuffer.deleteAt(lineBuffer.lastIndex)
                            animation.update(lineBuffer.toString() to null)
                        }
                    }
                    else -> {
                        animation.update(lineBuffer.toString() to it)
                    }
                }

            }
        }
    }



//    while (true) {
//        val c = readChar()
//        if (c == Input.Character('q')) {
//            exit(0)
//        } else {
//            println(c)
//        }
//    }

}


sealed class Input {
    data class Character(val char: Char): Input()
    class ArrowUp: Input()
    class ArrowDown: Input()
    class ArrowLeft: Input()
    class ArrowRight: Input()
    class HomeKey: Input()
    class EndKey: Input()
    class DeleteKey: Input()
    class PageUp: Input()
    class PageDown: Input()
    class Backspace: Input()
    class ReturnKey: Input()
}


fun input(): Flow<Input> = flow {
    while(true) {
        emit(readChar())
    }
}

fun readChar(): Input =
    memScoped {
        val byte = alloc<ByteVar>()
        var numBytesRead: ssize_t
        while (read(STDIN_FILENO, byte.ptr, 1).also { numBytesRead = it  } != 1L) {
            if (numBytesRead == -1L) { throw RuntimeException() }
        }

        val char  = byte.value.toInt().toChar()
        if (char == '\u001b') {
            var sequence = ByteArray(3)
            sequence.usePinned {
                if (read(STDIN_FILENO, it.addressOf(0), 1) != 1L) { return Input.Character('\u001b') }
                if (read(STDIN_FILENO, it.addressOf(1), 1) != 1L) { return Input.Character('\u001b') }
            }

            if (sequence[0].toInt().toChar() == '[') {
                when (sequence[1].toInt().toChar()) {
                    in '0'..'9' -> {
                        sequence.usePinned {
                            if (read(STDIN_FILENO, it.addressOf(2), 1) != 1L) { return Input.Character('\u001b') }
                        }
                        if (sequence[2].toInt().toChar() == '~') {
                            when (sequence[1].toInt().toChar()) {
                                '1', '7' -> return Input.HomeKey()
                                '3', '8' -> return Input.DeleteKey()
                                '4' -> return Input.EndKey()
                                '5' -> return Input.PageUp()
                                '6' -> return Input.PageDown()
                            }
                        }
                    }
                    'A' -> return Input.ArrowUp()
                    'B' -> return Input.ArrowDown()
                    'C' -> return Input.ArrowRight()
                    'D' -> return Input.ArrowLeft()
                }
            }
        }

        when (char.code) {
            127 -> return Input.Backspace()
            10 -> return Input.ReturnKey()
            else -> return Input.Character(char)
        }
    }

fun conversation(name: String, messages: List<String>, height: Int): Widget =
    verticalLayout {
        cell(Text(name, overflowWrap = OverflowWrap.ELLIPSES, whitespace = Whitespace.NOWRAP)) {
            style = white on gray
        }
        cell(ScrollRegion(
            messageContent(messages),
            height = height - 3,
            contentAlign = VerticalAlign.BOTTOM
        ))
        cell(HorizontalRule())
        cell("> ") {

        }

//        cellsFrom(messages.slice(0 until height - 1)) {
//            style = green
//            overflowWrap = OverflowWrap.BREAK_WORD
//        }
        align = LEFT
}

fun messageContent(messages: List<String>): Widget =
    verticalLayout {
        cellsFrom(messages.map(::message))
    }

fun message(message: String): Widget =
    horizontalLayout {
        column(0) {
            width = ColumnWidth.Auto
            align = RIGHT
            whitespace = Whitespace.NOWRAP
        }
        column(1) {
            width = ColumnWidth.Expand()
        }
        cell("<Mad Mike>") {
            style = blue
        }
        cell(Text(message, whitespace = Whitespace.NORMAL)) {
        }
    }
