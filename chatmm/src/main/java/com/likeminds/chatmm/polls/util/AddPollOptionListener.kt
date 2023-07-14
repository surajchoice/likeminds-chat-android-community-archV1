package com.likeminds.chatmm.polls.util

import com.likeminds.chatmm.polls.model.AddPollOptionExtras

interface AddPollOptionListener {
    fun newPollOptionEntered(addPollOptionExtras: AddPollOptionExtras)
}