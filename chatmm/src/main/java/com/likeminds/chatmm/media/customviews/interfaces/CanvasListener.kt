package com.likeminds.chatmm.media.customviews.interfaces

interface CanvasListener {

    fun onDrawStart()

    fun onDrawEnd()

    fun onUndoAvailable(undoAvailable: Boolean)

    fun onCanvasClick()

}