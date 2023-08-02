package com.likeminds.chatmm.polls.view

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.util.*

class DatePickerFragment(
    private val onDateSetListener: DatePickerDialog.OnDateSetListener,
    private val minimumDate: Long? = null,
) : DialogFragment() {

    companion object {
        const val TAG = "DatePickerFragment"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current date as the default date in the picker
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        // Create a new instance of DatePickerDialog
        val datePickerDialog =
            DatePickerDialog(requireContext(), onDateSetListener, year, month, day)

        // Set the minimum date to be able to select in date picker
        if (minimumDate != null) {
            datePickerDialog.datePicker.minDate = minimumDate
        }
        return datePickerDialog
    }
}