package com.likeminds.chatmm.utils.customview

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

/**
 * A generic ViewHolder that works with a [ViewDataBinding].
 *
 * @param <T> The type of the ViewDataBinding.
</T> */
class DataBoundViewHolder<T : ViewDataBinding>(val binding: T) :
    RecyclerView.ViewHolder(binding.root)