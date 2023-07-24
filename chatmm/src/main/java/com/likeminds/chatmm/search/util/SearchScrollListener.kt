package com.likeminds.chatmm.search.util

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.likeminds.chatmm.search.viewmodel.SearchViewModel

/**
 * Scroll listener for search
 */
abstract class SearchScrollListener : RecyclerView.OnScrollListener() {

    companion object {
        //Offset for pagination to trigger, try to keep it 20% of paged size for better visual
        private const val OFFSET = SearchViewModel.PAGE_SIZE * 0.2
    }

    // Total number of items currently present in the data set
    private var totalItemCount = 0

    // True if loading is done at bottom
    private var bottomLoading = true

    /**
     * Call this once you are done adding items at bottom position of the recyclerview
     */
    fun setBottomLoadingToTrue() {
        bottomLoading = true
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if (dy <= 0) {
            return
        }

        val layoutManager = recyclerView.layoutManager as? LinearLayoutManager ?: return
        totalItemCount = layoutManager.itemCount
        val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

        if (bottomLoading) {
            if (lastVisibleItemPosition > (totalItemCount - OFFSET)) {
                bottomLoading = false
                onLoadMore()
            }
        }
    }

    abstract fun onLoadMore()
}