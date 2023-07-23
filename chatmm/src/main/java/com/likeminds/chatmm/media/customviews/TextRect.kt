package com.likeminds.chatmm.media.customviews

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.FontMetricsInt
import android.graphics.Rect
import android.util.Log
import kotlin.math.min


/**
 * Draw text in a given rectangle and automatically wrap lines.
 */
internal class TextRect(private val paint: Paint) {

    private val metrics: FontMetricsInt = paint.fontMetricsInt
    private val starts = IntArray(MAX_LINES)
    private val stops = IntArray(MAX_LINES)
    private var lines = 0
    private var textHeight = 0
    private val bounds = Rect()
    private var text: String? = null
    private var wasCut = false

    /**
     * Calculate height of text block and prepare to draw it.
     *
     * @param text text to draw
     * @param maxWidth maximum width in pixels
     * @param maxHeight maximum height in pixels
     * @returns height of text in pixels
     */
    fun prepare(text: String, maxWidth: Int, maxHeight: Int): Int {
        lines = 0
        textHeight = 0
        this.text = text
        wasCut = false

        // get maximum number of characters in one line
        paint.getTextBounds("i", 0, 1, bounds)
        val maximumInLine = maxWidth / bounds.width()
        val length = text.length
        if (length < 1) {
            return 0
        }
        val lineHeight = -metrics.ascent + metrics.descent
        var start = 0
        var stop = min(maximumInLine, length)
        while (true) {

            // skip LF and spaces
            while (start < length) {
                val ch = text[start]
                if (ch != '\n' && ch != '\r' && ch != '\t' && ch != ' ') {
                    break
                }
                ++start
            }
            var o = stop + 1
            while (stop in (start + 1) until o) {
                o = stop
                var lowest = text.indexOf("\n", start)
                paint.getTextBounds(
                    text,
                    start,
                    stop,
                    bounds
                )
                if (lowest in start until stop ||
                    bounds.width() > maxWidth
                ) {
                    --stop
                    if (lowest < start || lowest > stop) {
                        val blank = text.lastIndexOf(" ", stop)
                        val hyphen = text.lastIndexOf("-", stop)
                        if (blank > start &&
                            (hyphen < start || blank > hyphen)
                        ) {
                            lowest = blank
                        } else if (hyphen > start) {
                            lowest = hyphen
                        }
                    }
                    if (lowest in start..stop) {
                        val ch = text[stop]
                        if (ch != '\n' && ch != ' ') {
                            ++lowest
                        }
                        stop = lowest
                    }
                    continue
                }
                break
            }
            if (start >= stop) {
                break
            }
            var minus = 0

            // cut off lf or space
            if (stop < length) {
                val ch = text[stop - 1]
                if (ch == '\n' || ch == ' ') {
                    minus = 1
                }
            }
            if (textHeight + lineHeight > maxHeight) {
                wasCut = true
                break
            }
            starts[lines] = start
            stops[lines] = stop - minus
            if (++lines > MAX_LINES) {
                wasCut = true
                break
            }
            if (textHeight > 0) {
                textHeight += metrics.leading
            }
            textHeight += lineHeight
            if (stop >= length) {
                break
            }
            start = stop
            stop = length
        }
        return textHeight
    }

    /**
     * Draw prepared text at given position.
     *
     * @param canvas canvas to draw text into
     * @param left left corner
     * @param top top corner
     */
    fun draw(canvas: Canvas, left: Int, top: Int) {
        if (textHeight == 0) {
            return
        }
        val before = -metrics.ascent
        val after = metrics.descent + metrics.leading
        var y = top
        val lastLine = lines - 1
        for (i in 0 until lines) {
            val line: String = if (wasCut && i == lastLine && stops[i] - starts[i] > 3) {
                text!!.substring(starts[i], stops[i] - 3) + "..."
            } else {
                text!!.substring(starts[i], stops[i])
            }
            y += before
            canvas.drawText(line, left.toFloat(), y.toFloat(), paint)
            y += after
        }
    }

    /**
     * Returns true if text was cut to fit into the maximum height
     */
    fun wasCut(): Boolean {
        return wasCut
    }

    fun containsXY(x: Int, y: Int): Boolean {
        Log.e("Bounds", "${bounds.centerX()}, ${bounds.centerY()}")
        return bounds.contains(x, y)
    }

    fun setBounds(x: Float, y: Float) {
        bounds.set(x.toInt(), y.toInt(), bounds.width(), bounds.height())
    }

    companion object {
        // maximum number of lines; this is a fixed number in order
        // to use a predefined array to avoid ArrayList (or something
        // similar) because filling it does involve allocating memory
        private const val MAX_LINES = 10
    }

}