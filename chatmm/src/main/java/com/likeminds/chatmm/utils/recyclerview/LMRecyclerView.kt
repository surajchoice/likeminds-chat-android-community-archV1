package com.likeminds.chatmm.utils.recyclerview

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

class LMRecyclerView : RecyclerView {
    constructor(context: Context) : super(context) {
        if (!isInEditMode) {
            init(context)
        }
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        if (!isInEditMode) {
            init(context)
        }
    }

    fun init(context: Context) {

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return false
    }
}