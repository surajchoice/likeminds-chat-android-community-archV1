package com.likeminds.chatmm.utils.actionmode

import android.view.Menu
import android.view.MenuItem
import androidx.annotation.MenuRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode

/**
 *  This class will be used to implement action mode in any activity
 *  @property T is the data class used to invalidate the menu items conditionally
 * */

internal class ActionModeCallback<T> : ActionMode.Callback {

    var actionModeListener: ActionModeListener<T>? = null

    private var mode: ActionMode? = null

    @MenuRes
    private var menuResId: Int = 0
    private var title: String? = null

    private var actionModeData: T? = null
    private var isVisible: Boolean = false

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        mode?.let {
            this.mode = mode
            mode.menuInflater.inflate(menuResId, menu)
            mode.title = title
        }
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        actionModeListener?.onActionItemUpdate(menu, actionModeData)
        return true
    }

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        actionModeListener?.onActionItemClick(item)
        mode?.finish()
        return true
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        isVisible = false
        this.mode = null
        actionModeListener?.onActionModeDestroyed()
    }

    fun startActionMode(
        actionModeListener: ActionModeListener<T>,
        view: AppCompatActivity,
        @MenuRes menuResId: Int,
        title: String? = null
    ) {
        this.actionModeListener = actionModeListener
        this.menuResId = menuResId
        this.title = title
        view.startSupportActionMode(this)
        isVisible = true
    }

    fun finishActionMode() {
        isVisible = false
        mode?.finish()
    }

    fun updateTitle(updatedTitle: String) {
        mode?.title = updatedTitle
    }

    fun invalidate(actionModeData: T) {
        this.actionModeData = actionModeData
        mode?.invalidate()
    }

    fun isActionModeEnabled(): Boolean {
        return isVisible
    }
}