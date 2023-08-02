package com.likeminds.chatmm.utils.actionmode

import android.view.Menu
import android.view.MenuItem

internal interface ActionModeListener<T> {
    fun onActionItemClick(item: MenuItem?)
    fun onActionItemUpdate(item: Menu?, actionModeData: T?) {}
    fun onActionModeDestroyed()
}