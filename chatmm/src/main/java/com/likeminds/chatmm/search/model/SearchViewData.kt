package com.likeminds.chatmm.search.model

import com.likeminds.chatmm.utils.model.BaseViewType

class SearchViewData private constructor(
    val disablePagination: Boolean,
    val dataList: List<BaseViewType>,
    val keyword: String,
    val checkForSeparator: Boolean
) {
    class Builder {
        private var disablePagination: Boolean = false
        private var dataList: List<BaseViewType> = emptyList()
        private var keyword: String = ""
        private var checkForSeparator: Boolean = false

        fun disablePagination(disablePagination: Boolean) =
            apply { this.disablePagination = disablePagination }

        fun dataList(dataList: List<BaseViewType>) = apply { this.dataList = dataList }
        fun keyword(keyword: String) = apply { this.keyword = keyword }
        fun checkForSeparator(checkForSeparator: Boolean) =
            apply { this.checkForSeparator = checkForSeparator }

        fun build() = SearchViewData(
            disablePagination,
            dataList,
            keyword,
            checkForSeparator
        )
    }

    fun toBuilder(): Builder {
        return Builder().disablePagination(disablePagination)
            .dataList(dataList)
            .keyword(keyword)
            .checkForSeparator(checkForSeparator)
    }
}