package com.likeminds.chatmm.overflowmenu.view

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import androidx.constraintlayout.widget.ConstraintLayout
import com.likeminds.chatmm.databinding.PopupOverflowMenuBinding
import com.likeminds.chatmm.overflowmenu.model.OverflowMenuItemViewData
import com.likeminds.chatmm.overflowmenu.view.adapter.OverflowMenuAdapter
import com.likeminds.chatmm.overflowmenu.view.adapter.OverflowMenuAdapterListener
import com.likeminds.chatmm.utils.ValueUtils.isValidIndex
import com.likeminds.chatmm.utils.ViewUtils

class OverflowMenuPopup(
    private val context: Context,
    private val overflowMenuAdapter: OverflowMenuAdapter
) : PopupWindow(context) {

    companion object {

        fun create(
            context: Context,
            listener: OverflowMenuAdapterListener
        ): OverflowMenuPopup {
            val overflowMenu = OverflowMenuPopup(
                context,
                OverflowMenuAdapter(listener)
            )
            overflowMenu.contentView = null
            return overflowMenu
        }

    }

    override fun setContentView(contentView: View?) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = PopupOverflowMenuBinding.inflate(inflater, null, false)
        setBackgroundDrawable(null)
        binding.recyclerView.adapter = overflowMenuAdapter
        super.setContentView(binding.root)

        height = ConstraintLayout.LayoutParams.WRAP_CONTENT
        width = ViewUtils.dpToPx(200)
        elevation = 10f
        isFocusable = true
        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    fun setItems(items: List<OverflowMenuItemViewData>) {
        overflowMenuAdapter.replace(items)
    }

    fun update(item: OverflowMenuItemViewData) {
        val index = overflowMenuAdapter.items().indexOfFirst {
            (it as OverflowMenuItemViewData).title == item.title
        }
        if (index.isValidIndex()) {
            overflowMenuAdapter.update(index, item)
        }
    }
}