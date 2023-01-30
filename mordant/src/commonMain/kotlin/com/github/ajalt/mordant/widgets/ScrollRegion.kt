package com.github.ajalt.mordant.widgets

import com.github.ajalt.mordant.rendering.*
import com.github.ajalt.mordant.terminal.Terminal

class ScrollRegion(
    val content: Widget,
    private val height: Int,
    private val contentAlign: VerticalAlign
) : Widget {
//    constructor(
//        content: Widget,
//        height: Int,
//        contentAlign: VerticalAlign
//    ) : this(
//        content,
//        height,
//        contentAlign
//    )

    override fun measure(t: Terminal, width: Int): WidthRange {
        return content.measure(t, width)
    }

    override fun render(t: Terminal, width: Int): Lines {
        return content.render(t, width).setSize(width, height, contentAlign)
    }
}