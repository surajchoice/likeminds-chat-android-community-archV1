package com.likeminds.chatmm.utils.customview

import android.os.Bundle
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.likeminds.chatmm.utils.model.BaseViewType
import com.likeminds.chatmm.utils.model.ViewType

abstract class ViewDataBinder<V : ViewDataBinding, T : BaseViewType> {
    @get:ViewType
    abstract val viewType: Int

    abstract fun createBinder(parent: ViewGroup): V

    abstract fun bindData(binding: V, data: T, position: Int)

    fun bindData(binding: V, data: Bundle, position: Int) {}
}