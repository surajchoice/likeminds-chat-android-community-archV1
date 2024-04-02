package com.likeminds.chatmm.search.view.adapter.databinder

import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.likeminds.chatmm.R
import com.likeminds.chatmm.chatroom.detail.util.ChatroomUtil
import com.likeminds.chatmm.databinding.ItemSearchChatroomTitleBinding
import com.likeminds.chatmm.member.util.MemberUtil
import com.likeminds.chatmm.member.util.UserPreferences
import com.likeminds.chatmm.search.model.SearchChatroomTitleViewData
import com.likeminds.chatmm.search.util.SearchUtils
import com.likeminds.chatmm.search.view.adapter.SearchAdapterListener
import com.likeminds.chatmm.utils.TimeUtil
import com.likeminds.chatmm.utils.ViewUtils.hide
import com.likeminds.chatmm.utils.ViewUtils.show
import com.likeminds.chatmm.utils.customview.ViewDataBinder
import com.likeminds.chatmm.utils.membertagging.MemberTaggingDecoder
import com.likeminds.chatmm.utils.model.ITEM_SEARCH_TITLE

class SearchChatroomTitleViewDataBinder(
    private val listener: SearchAdapterListener,
    private var userPreferences: UserPreferences
) : ViewDataBinder<ItemSearchChatroomTitleBinding, SearchChatroomTitleViewData>() {

    override val viewType: Int
        get() = ITEM_SEARCH_TITLE

    override fun createBinder(parent: ViewGroup): ItemSearchChatroomTitleBinding {
        val binding = ItemSearchChatroomTitleBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        setListener(binding)
        return binding
    }

    override fun bindData(
        binding: ItemSearchChatroomTitleBinding,
        data: SearchChatroomTitleViewData,
        position: Int
    ) {
        binding.apply {
            searchChatroomTitleViewData = data
            hideBottomLine = data.isLast
            hideNotFollowed = data.followStatus == true

            val chatroomViewData = data.chatroom

            //highlight keyword matched in chatroom name
            tvChatroomName.text =
                if (!data.keywordMatchedInChatroomName.isNullOrEmpty()) {
                    data.chatroom.header?.let {
                        SearchUtils.getHighlightedText(
                            it,
                            data.keywordMatchedInChatroomName,
                            ContextCompat.getColor(root.context, R.color.lm_chat_black)
                        )
                    }
                } else {
                    data.chatroom.header
                }

            val chatTypeDrawable =
                ChatroomUtil.getTypeDrawable(root.context, chatroomViewData.type)
            if (chatTypeDrawable == null) {
                ivChatroomType.hide()
            } else {
                ivChatroomType.show()
                ivChatroomType.setImageDrawable(chatTypeDrawable)
            }


            val updatedTitle = data.chatroom.title.replace("\n", " ")

            val senderName = MemberUtil.getFirstNameToShow(
                userPreferences,
                data.chatroom.memberViewData
            )

            //decode member tagging
            val answerWithNonHighlightedTags = MemberTaggingDecoder.decode(updatedTitle)

            val tvConversationText = SpannableStringBuilder("$senderName ")

            //trimming of the text to  be shown
            if ((data.keywordMatchedInMessageText?.size ?: 0) > 0) {
                tvConversationText.append(data.keywordMatchedInMessageText?.let {
                    SearchUtils.getTrimmedText(
                        answerWithNonHighlightedTags,
                        it,
                        ContextCompat.getColor(root.context, R.color.lm_chat_black)
                    )
                })
            } else {
                tvConversationText.append(answerWithNonHighlightedTags)
            }

            tvConversation.setText(tvConversationText, TextView.BufferType.SPANNABLE)

            val time = data.chatroom.createdAt

            if (time != null) {
                tvTimestamp.show()
                tvTimestamp.text = TimeUtil.getLastConversationTime(time)
            } else {
                tvTimestamp.hide()
            }
        }
    }

    private fun setListener(binding: ItemSearchChatroomTitleBinding) {
        binding.root.setOnClickListener {
            val titleViewData = binding.searchChatroomTitleViewData ?: return@setOnClickListener
            listener.onTitleClicked(titleViewData)
        }
    }
}