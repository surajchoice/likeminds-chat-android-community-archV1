package com.likeminds.chatmm.polls.view

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import androidx.fragment.app.DialogFragment
import java.util.*

internal class TimePickerFragment(private val onTimeSetListener: TimePickerDialog.OnTimeSetListener) :
    DialogFragment() {

    companion object {
        const val TAG = "TimePickerFragment"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current time as the default values for the picker
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        // Create a new instance of TimePickerDialog and return it
        return TimePickerDialog(
            activity,
            onTimeSetListener,
            hour,
            minute,
            DateFormat.is24HourFormat(activity)
        )
    }
}