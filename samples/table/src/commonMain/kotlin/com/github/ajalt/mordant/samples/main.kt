package com.github.ajalt.mordant.samples

import com.github.ajalt.mordant.animation.animation
import com.github.ajalt.mordant.rendering.*
import com.github.ajalt.mordant.rendering.BorderType.Companion.SQUARE_DOUBLE_SECTION_SEPARATOR
import com.github.ajalt.mordant.rendering.TextAlign.*
import com.github.ajalt.mordant.rendering.TextAlign.LEFT
import com.github.ajalt.mordant.rendering.TextAlign.RIGHT
import com.github.ajalt.mordant.rendering.TextColors.*
import com.github.ajalt.mordant.rendering.TextStyles.dim
import com.github.ajalt.mordant.table.*
import com.github.ajalt.mordant.table.Borders.*
import com.github.ajalt.mordant.terminal.Terminal
import com.github.ajalt.mordant.widgets.HorizontalRule
import com.github.ajalt.mordant.widgets.ScrollRegion
import com.github.ajalt.mordant.widgets.Text
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext


fun main() {
    val messages = (1..100).map {
        "Line number $it"
    }

    val terminal = Terminal()
    terminal.setRawMode(enabled = false)
    terminal.print(conversation("Take it ot the street paopsdkpoaskdo aksdpoak sdopkapsodkpoaksdokasodpkaopskdopaksopdkap", messages, terminal.info.height))
//    terminal.cursor.hide()

    while (true) {}


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
