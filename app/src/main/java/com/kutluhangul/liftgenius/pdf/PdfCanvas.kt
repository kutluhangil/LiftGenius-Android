package com.kutluhangul.liftgenius.pdf

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument

/**
 * Thin wrapper over [PdfDocument] that tracks the vertical cursor and starts a new page
 * automatically when content overflows. All coordinates are PDF points (A4 = 595 × 842).
 */
class PdfCanvas(private val document: PdfDocument) {

    private var pageNumber = 0
    private var page: PdfDocument.Page? = null
    private var canvas: Canvas = startNewPage()
    private var cursorY: Float = MARGIN

    private val accent = Paint().apply { color = ACCENT }
    private val hairline = Paint().apply { color = HAIRLINE; strokeWidth = 0.7f }

    val brandPaint = textPaint(14f, bold = true, color = MUTED)
    val titlePaint = textPaint(22f, bold = true, color = ACCENT)
    val subtitlePaint = textPaint(11f, color = MUTED)
    val headingPaint = textPaint(14f, bold = true, color = INK)
    val bodyPaint = textPaint(11f, color = INK)
    val bodyBoldPaint = textPaint(11f, bold = true, color = INK)
    val mutedPaint = textPaint(10f, color = MUTED)

    private fun startNewPage(): Canvas {
        page?.let { document.finishPage(it) }
        pageNumber += 1
        val info = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
        val newPage = document.startPage(info)
        page = newPage
        canvas = newPage.canvas
        cursorY = MARGIN
        return canvas
    }

    /** Ensures [needed] points fit before the bottom margin, else breaks to a new page. */
    private fun ensureSpace(needed: Float) {
        if (cursorY + needed > PAGE_HEIGHT - MARGIN) {
            startNewPage()
        }
    }

    /** Brand header block — drawn once at the top of the document. */
    fun header(documentTitle: String, generatedOn: String) {
        canvas.drawRect(0f, 0f, PAGE_WIDTH.toFloat(), 6f, accent)
        canvas.drawText("LiftGenius", MARGIN, cursorY + 14f, brandPaint)
        cursorY += 34f
        canvas.drawText(documentTitle, MARGIN, cursorY, titlePaint)
        cursorY += 18f
        canvas.drawText(generatedOn, MARGIN, cursorY, subtitlePaint)
        cursorY += 16f
        divider()
        cursorY += 8f
    }

    fun heading(text: String) {
        ensureSpace(30f)
        cursorY += 6f
        canvas.drawText(text, MARGIN, cursorY, headingPaint)
        cursorY += 18f
    }

    fun body(text: String, indent: Float = 0f) {
        ensureSpace(16f)
        canvas.drawText(text, MARGIN + indent, cursorY, bodyPaint)
        cursorY += 16f
    }

    /** Label on the left, value right-aligned — for key/value and ledger rows. */
    fun row(label: String, value: String, valuePaint: Paint = bodyBoldPaint) {
        ensureSpace(18f)
        canvas.drawText(label, MARGIN, cursorY, bodyPaint)
        val valueWidth = valuePaint.measureText(value)
        canvas.drawText(value, PAGE_WIDTH - MARGIN - valueWidth, cursorY, valuePaint)
        cursorY += 18f
    }

    /** Wraps [text] to the content width across multiple lines. */
    fun paragraph(text: String, indent: Float = 0f) {
        val maxWidth = PAGE_WIDTH - 2 * MARGIN - indent
        text.split("\n").forEach { rawLine ->
            if (rawLine.isBlank()) {
                cursorY += 10f
                return@forEach
            }
            var line = ""
            rawLine.split(" ").forEach { word ->
                val candidate = if (line.isEmpty()) word else "$line $word"
                if (bodyPaint.measureText(candidate) > maxWidth) {
                    body(line, indent)
                    line = word
                } else {
                    line = candidate
                }
            }
            if (line.isNotEmpty()) body(line, indent)
        }
    }

    fun divider() {
        ensureSpace(8f)
        canvas.drawLine(MARGIN, cursorY, PAGE_WIDTH - MARGIN, cursorY, hairline)
        cursorY += 4f
    }

    fun spacer(points: Float = 8f) {
        cursorY += points
    }

    fun finish() {
        page?.let { document.finishPage(it) }
        page = null
    }

    private fun textPaint(size: Float, bold: Boolean = false, color: Int): Paint = Paint().apply {
        isAntiAlias = true
        textSize = size
        this.color = color
        typeface = if (bold) Typeface.create(Typeface.DEFAULT, Typeface.BOLD) else Typeface.DEFAULT
    }

    companion object {
        const val PAGE_WIDTH = 595
        const val PAGE_HEIGHT = 842
        const val MARGIN = 40f

        private const val ACCENT = 0xFFFF6B1A.toInt()
        private const val INK = 0xFF100C18.toInt()
        private const val MUTED = 0xFF6B6675.toInt()
        private const val HAIRLINE = 0xFFCEC9D6.toInt()
    }
}
