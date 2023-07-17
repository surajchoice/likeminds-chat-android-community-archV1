package com.likeminds.chatmm.reactions.util

import android.app.Application
import com.likeminds.chatmm.utils.sharedpreferences.BasePreferences
import javax.inject.Inject

class ReactionsPreferences @Inject constructor(
    application: Application,
) : BasePreferences(MESSAGE_REACTIONS_PREFS, application) {
    companion object {
        const val MESSAGE_REACTIONS_PREFS = "message_reactions_pref"
        const val HAS_USER_REACTED_ONCE = "HAS_USER_REACTED_ONCE"
        const val NO_OF_TIMES_HINT_SHOWN = "NO_OF_TIMES_HINT_SHOWN"
        const val TOTAL_NO_OF_TIMES_HINT_CAN_BE_SHOWN = "TOTAL_NO_OF_TIMES_HINT_CAN_BE_SHOWN"

    }

    fun getHasUserReactedOnce(): Boolean {
        return getPreference(HAS_USER_REACTED_ONCE, false)
    }

    fun setHasUserReactedOnce(hasUserReactedOnce: Boolean) {
        putPreference(HAS_USER_REACTED_ONCE, hasUserReactedOnce)
    }

    fun getTotalNoOfHintsAllowed(): Int {
        return getPreference(TOTAL_NO_OF_TIMES_HINT_CAN_BE_SHOWN, 5)
    }

    fun setTotalNoOfHintsAllowed(totalNoOfHintsAllowed: Int) {
        putPreference(TOTAL_NO_OF_TIMES_HINT_CAN_BE_SHOWN, totalNoOfHintsAllowed)
    }

    fun getNoOfTimesHintShown(): Int {
        return getPreference(NO_OF_TIMES_HINT_SHOWN, 0)
    }

    fun setNoOfTimesHintShown(times: Int) {
        putPreference(NO_OF_TIMES_HINT_SHOWN, times)
    }
}