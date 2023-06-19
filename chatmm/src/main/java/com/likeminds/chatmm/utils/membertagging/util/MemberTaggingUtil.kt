package com.likeminds.chatmm.utils.membertagging.util

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.text.Editable
import android.util.DisplayMetrics
import android.view.WindowInsets
import androidx.annotation.FloatRange
import com.likeminds.chatmm.utils.ViewDataConverter
import com.likeminds.chatmm.utils.membertagging.model.TagViewData
import com.likeminds.likemindschat.helper.model.GroupTag
import com.likeminds.likemindschat.helper.model.UserTag

object MemberTaggingUtil {

    private const val DEFAULT_MAX_HEIGHT = 200
    const val PAGE_SIZE = 20

    /**
     * return tagging list to the view
     * @param groupTags: list og groups like: @everyone
     * @param chatroomParticipants: list of participants in the chatroom
     * @param communityMembers: list of community members containing participants
     *
     * @return list of groups and members
     * */
    fun getTaggingData(
        groupTags: List<GroupTag>,
        chatroomParticipants: List<UserTag>,
        communityMembers: List<UserTag>
    ): ArrayList<TagViewData> {
        //list send to view
        val listOfGroupAndMember = ArrayList<TagViewData>()

        //convert groups
        val groupViewData = groupTags.mapNotNull { groupTag ->
            ViewDataConverter.convertGroupTag(groupTag)
        }

        //add to result list
        listOfGroupAndMember.addAll(groupViewData)

        //convert members
        val chatroomParticipantsViewData =
            ArrayList(chatroomParticipants + communityMembers).mapNotNull { userTag ->
                ViewDataConverter.convertUserTag(userTag)
            }

        //add to result list
        listOfGroupAndMember.addAll(chatroomParticipantsViewData)

        return listOfGroupAndMember
    }

    @JvmSynthetic
    internal fun getMaxHeight(
        context: Context,
        @FloatRange(from = 0.0, to = 1.0) percentage: Float
    ): Int {
        val activity = context as? Activity ?: return dpToPx(DEFAULT_MAX_HEIGHT)
        return (getDeviceHeight(activity) * percentage).toInt()
    }

    @JvmSynthetic
    fun dpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }

    @Suppress("DEPRECATION")
    private fun getDeviceHeight(activity: Activity): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = activity.windowManager.currentWindowMetrics
            val insets =
                windowMetrics.windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
            windowMetrics.bounds.height() - insets.top - insets.bottom
        } else {
            val displayMetrics = DisplayMetrics()
            activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
            displayMetrics.heightPixels
        }
    }

    @JvmSynthetic
    internal fun getLastSpan(
        editable: Editable,
        spans: Array<MemberTaggingClickableSpan>
    ): MemberTaggingClickableSpan {
        if (spans.size == 1) {
            return spans[0]
        }
        return spans.maxByOrNull {
            editable.getSpanEnd(it)
        }!!
    }

    @JvmSynthetic
    internal fun getSortedSpan(editable: Editable): List<MemberTaggingClickableSpan> {
        return editable.getSpans(0, editable.length, MemberTaggingClickableSpan::class.java)
            .sortedBy {
                editable.getSpanStart(it)
            }
    }
}