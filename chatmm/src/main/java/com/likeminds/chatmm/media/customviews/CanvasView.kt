package com.likeminds.chatmm.media.customviews

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.Paint.Align
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.likeminds.chatmm.media.customviews.interfaces.CanvasListener
import com.likeminds.chatmm.media.customviews.interfaces.OnRotationGestureListener
import com.likeminds.chatmm.utils.ViewUtils
import java.util.*

internal class CanvasView : View, OnRotationGestureListener {

    private var mContext: Context? = null
    private var canvas: Canvas? = null
    private var bitmap: Bitmap? = null
    private val pathLists: MutableList<Path> = ArrayList()
    private val paintLists: MutableList<Paint> = ArrayList()

    private lateinit var rotationGestureDetector: RotationGestureDetector
    private var canvasListener: CanvasListener? = null

    // for Undo, Redo
    private var historyPointer = 0
    /**
     * This method is getter for mode.
     *
     * @return
     */
    /**
     * This method is setter for mode.
     *
     * @param mode
     */
    // Flags
    var mode = MediaEditMode.DRAW
    /**
     * This method is getter for drawer.
     *
     * @return
     */
    /**
     * This method is setter for drawer.
     *
     * @param drawer
     */
    private var isDown = false
    /**
     * This method is getter for stroke or fill.
     *
     * @return
     */
    /**
     * This method is setter for stroke or fill.
     *
     * @param style
     */
    // for Paint
    var paintStyle = Paint.Style.STROKE
    /**
     * This method is getter for stroke color.
     *
     * @return
     */
    /**
     * This method is setter for stroke color.
     *
     * @param color
     */
    var paintStrokeColor = Color.BLACK
    /**
     * This method is getter for fill color.
     * But, current Android API cannot set fill color (?).
     *
     * @return
     */
    /**
     * This method is setter for fill color.
     * But, current Android API cannot set fill color (?).
     *
     * @param color
     */
    private var paintStrokeWidth = 10f
    private var opacity = 255
    private var blur = 0f
    /**
     * This method is getter for line cap.
     *
     * @return
     */
    /**
     * This method is setter for line cap.
     *
     * @param cap
     */
    var lineCap = Paint.Cap.ROUND
    /**
     * This method is getter for drawn text.
     *
     * @return
     */
    /**
     * This method is setter for drawn text.
     *
     * @param text
     */
    // for Text
    var text = ""
    var textToCenter = false
    /**
     * This method is getter for font-family.
     *
     * @return
     */
    /**
     * This method is setter for font-family.
     *
     * @param face
     */
    var fontFamily: Typeface = Typeface.DEFAULT
    private var fontSize = ViewUtils.spToPx(20)
    private val textAlign = Align.RIGHT // fixed
    private var textPaint = Paint()
    private var textX = 0f
    private var textY = 0f
    private var textRect: TextRect? = null

    // for Drawer
    private var startX = 0f
    private var startY = 0f

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        setup(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setup(context)
    }

    constructor(context: Context) : super(context) {
        setup(context)
    }

    private fun setup(context: Context) {
        this.mContext = context
        pathLists.add(Path())
        paintLists.add(createPaint())
        historyPointer++
        textPaint.color = paintStrokeColor
        rotationGestureDetector = RotationGestureDetector(this)
    }

    fun setListener(canvasListener: CanvasListener) {
        this.canvasListener = canvasListener
    }

    /**
     * This method creates the instance of Paint.
     * In addition, this method sets styles for Paint.
     *
     * @return paint This is returned as the instance of Paint
     */
    private fun createPaint(): Paint {
        val paint = Paint()
        paint.isAntiAlias = true
        paint.style = paintStyle
        paint.strokeWidth = paintStrokeWidth
        paint.strokeCap = lineCap
        paint.strokeJoin = Paint.Join.MITER // fixed

        // for Text
        if (mode == MediaEditMode.TEXT) {
            paint.typeface = fontFamily
            paint.textSize = fontSize
            paint.textAlign = textAlign
            paint.style = Paint.Style.FILL
            paint.strokeWidth = 0f
            textRect = TextRect(paint)
        }
        if (mode == MediaEditMode.ERASER) {
            // Eraser
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
            paint.setARGB(0, 0, 0, 0)

            // paint.setColor(this.baseColor);
            // paint.setShadowLayer(this.blur, 0F, 0F, this.baseColor);
        } else {
            // Otherwise
            paint.color = paintStrokeColor
            paint.setShadowLayer(blur, 0f, 0f, paintStrokeColor)
            paint.alpha = opacity
        }
        return paint
    }

    /**
     * This method initialize Path.
     * Namely, this method creates the instance of Path,
     * and moves current position.
     *
     * @param event This is argument of onTouchEvent method
     * @return path This is returned as the instance of Path
     */
    private fun createPath(event: MotionEvent): Path {
        val path = Path()

        // Save for ACTION_MOVE
        startX = event.x
        startY = event.y
        path.moveTo(startX, startY)
        return path
    }

    /**
     * This method updates the lists for the instance of Path and Paint.
     * "Undo" and "Redo" are enabled by this method.
     *
     * @param path the instance of Path
     * @param paint the instance of Paint
     */
    private fun updateHistory(path: Path) {
        if (historyPointer == pathLists.size) {
            pathLists.add(path)
            paintLists.add(createPaint())
            historyPointer++
        } else {
            // On the way of Undo or Redo
            pathLists[historyPointer] = path
            paintLists[historyPointer] = createPaint()
            historyPointer++
            var i = historyPointer
            val size = paintLists.size
            while (i < size) {
                pathLists.removeAt(historyPointer)
                paintLists.removeAt(historyPointer)
                i++
            }
        }
    }

    /**
     * This method gets the instance of Path that pointer indicates.
     *
     * @return the instance of Path
     */
    private val currentPath: Path
        get() = pathLists[historyPointer - 1]

    /**
     * This method draws text.
     *
     * @param canvas the instance of Canvas
     */
    private fun drawText(canvas: Canvas) {
        if (text.isEmpty()) {
            return
        }
        if (mode == MediaEditMode.TEXT) {
            textX = startX
            textY = startY
            textPaint = createPaint()
        }

        val textX = this.textX
        val textY = this.textY

        textPaint.style = Paint.Style.FILL

        textRect?.prepare(
            text,
            width,
            height
        )
        if (textToCenter) {
            textRect?.draw(canvas, width / 2, height / 2)
            textToCenter = false
        } else {
            textRect?.draw(canvas, textX.toInt(), y.toInt())
        }
        textRect?.setBounds(textX, y)
        canvas.translate(-textX, -textY)
    }

    /**
     * This method defines processes on MotionEvent.ACTION_DOWN
     *
     * @param event This is argument of onTouchEvent method
     */
    private fun onActionDown(event: MotionEvent) {
//        val x = event.x
//        val y = event.y
        when (mode) {
            MediaEditMode.DRAW, MediaEditMode.ERASER -> {
                updateHistory(createPath(event))
                isDown = true
                canvasListener?.onDrawStart()
            }
//            CanvasMode.TEXT -> {
//                if (textRect?.containsXY(x.toInt(), y.toInt()) == true) {
//                    startX = event.x
//                    startY = event.y
//                    Log.e("onActionDown", "true")
//                } else {
//                    Log.e("onActionDown", "false")
//                }
//            }
            else -> {}
        }
    }

    /**
     * This method defines processes on MotionEvent.ACTION_MOVE
     *
     * @param event This is argument of onTouchEvent method
     */
    private fun onActionMove(event: MotionEvent) {
        val x = event.x
        val y = event.y
        when (mode) {
            MediaEditMode.DRAW, MediaEditMode.ERASER -> {
                if (!isDown) {
                    return
                }
                val path = currentPath
                path.lineTo(x, y)
            }
            MediaEditMode.TEXT -> {
                if (x.toInt() in 0..this.width && y.toInt() in 0..this.height) {
                    startX = x
                    startY = y
                }
            }

            else -> {}
        }
    }

    /**
     * This method defines processes on MotionEvent.ACTION_DOWN
     */
    private fun onActionUp() {
        if (isDown) {
            startX = 0f
            startY = 0f
            isDown = false
            canvasListener?.onDrawEnd()
            canvasListener?.onUndoAvailable(historyPointer > 0)
        }
    }

    /**
     * This method updates the instance of Canvas (View)
     *
     * @param canvas the new instance of Canvas
     */
    val paint = Paint()
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Before "drawPath"
        canvas.drawColor(Color.TRANSPARENT)
        if (bitmap != null) {
            val centreX = (this.width - bitmap!!.width) / 2f
            val centreY = (this.height - bitmap!!.height) / 2f
            canvas.drawBitmap(bitmap!!, centreX, centreY, paint)
        }
        for (i in 0 until historyPointer) {
            val path = pathLists[i]
            val paint = paintLists[i]
            canvas.drawPath(path, paint)
        }
        drawText(canvas)
        this.canvas = canvas
    }

    /**
     * This method set event listener for drawing.
     *
     * @param event the instance of MotionEvent
     * @return
     */
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (mode == MediaEditMode.TEXT) {
            rotationGestureDetector.onTouchEvent(event)
            canvasListener?.onCanvasClick()
        }
        when (event.action) {
            MotionEvent.ACTION_DOWN -> onActionDown(event)
            MotionEvent.ACTION_MOVE -> onActionMove(event)
            MotionEvent.ACTION_UP -> onActionUp()
        }

        this.invalidate()
        return true
    }

    /**
     * This method draws canvas again for Undo.
     *
     * @return If Undo is enabled, this is returned as true. Otherwise, this is returned as false.
     */
    fun undo(): Boolean {
        return if (historyPointer > 0) {
            historyPointer--
            this.invalidate()
            canvasListener?.onUndoAvailable(historyPointer > 0)
            true
        } else {
            false
        }
    }

    /**
     * This method draws canvas again for Redo.
     *
     * @return If Redo is enabled, this is returned as true. Otherwise, this is returned as false.
     */
    fun redo(): Boolean {
        return if (historyPointer < pathLists.size) {
            historyPointer++
            this.invalidate()
            true
        } else {
            false
        }
    }

    /**
     * This method is getter for font size,
     *
     * @return
     */
    fun getFontSize(): Float {
        return fontSize
    }

    /**
     * This method is setter for font size.
     * The 1st argument is greater than or equal to 0.0.
     *
     * @param size
     */
    fun setFontSize(size: Float) {
        fontSize = if (size >= 0f) {
            size
        } else {
            ViewUtils.spToPx(20)
        }
        if (text.isNotEmpty()) {
            this.invalidate()
        }
    }

    fun setPaintColor(color: Int) {
        this.paintStrokeColor = color
        if (text.isNotEmpty()) {
            this.invalidate()
        }
    }

    fun setTypeface(typeface: Typeface) {
        this.fontFamily = typeface
        if (text.isNotEmpty()) {
            this.invalidate()
        }
    }

    /**
     * This method initializes canvas.
     *
     * @return
     */
    fun clear() {
        val paint = Paint()
        paint.color = Color.TRANSPARENT
        paint.style = Paint.Style.FILL
        historyPointer = 0
        pathLists.clear()
        paintLists.clear()
        text = ""
        this.invalidate()
    }

    /**
     * This method gets current canvas as bitmap.
     *
     * @return This is returned as bitmap.
     */
    fun getBitmap(): Bitmap {
        this.isDrawingCacheEnabled = false
        this.isDrawingCacheEnabled = true
        return Bitmap.createBitmap(this.drawingCache)
    }

    /**
     * This method draws the designated bitmap to canvas.
     *
     * @param bitmap
     */
    fun drawBitmap(bitmap: Bitmap?) {
        this.bitmap = bitmap
        this.invalidate()
    }

    override fun onRotation(rotationDetector: RotationGestureDetector?) {
        val angle = rotationDetector?.angle ?: return
        canvas?.rotate(angle)
    }

}