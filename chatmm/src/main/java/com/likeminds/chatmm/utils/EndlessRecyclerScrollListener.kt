package com.likeminds.chatmm.utils

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

internal abstract class EndlessRecyclerScrollListener(private val mLinearLayoutManager: LinearLayoutManager) :
    RecyclerView.OnScrollListener() {

    var overallXScroll = 0

    // The first item to be visible on top of current state
    private var firstVisibleItem: Int = 0

    // All the items visible in current state
    private var visibleItemCount: Int = 0

    // Total number of items present in the data set
    private var totalItemCount: Int = 0

    // Total number of items in the data set after the last load
    private var previousTotal = 0

    // True if we are still waiting for the last set of data to load.
    private var loading = true

    // The minimum amount of items to have below your current scroll position before loading more.
    private val visibleThreshold = 5

    // Specify the page on which the user is
    private var currentPage = 1

    companion object {
        const val DEFAULT_PAGE = 1
    }

    //Reset variables
    fun resetData() {
        this.previousTotal = 0
        this.loading = true
        this.currentPage = DEFAULT_PAGE
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        overallXScroll += dx
        val reverseLayout = mLinearLayoutManager.reverseLayout
        if (!reverseLayout && dy >= 0 || reverseLayout && dy <= 0) {
            visibleItemCount = recyclerView.childCount
            totalItemCount = mLinearLayoutManager.itemCount
            firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition()
            if (loading) {
                val diff = totalItemCount - visibleThreshold - visibleItemCount
                if (firstVisibleItem > diff) {
                    //User has scrolled and passed the visible threshold
                    previousTotal = totalItemCount
                    loading = false
                    currentPage++
                    onLoadMore(currentPage)
                }
            } else {
                if (totalItemCount > previousTotal) {
                    //Data fetched
                    loading = true
                } else if (mLinearLayoutManager.findLastVisibleItemPosition() == mLinearLayoutManager.itemCount - 1) {
                    //End of the list
                    onLoadMore(-100)
                }
            }
        }
    }

    abstract fun onLoadMore(currentPage: Int)

}