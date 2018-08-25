package moe.leer.baabaa.view

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.util.Log
import moe.leer.baabaa.addedit.AddEventActivity
import java.util.*

/**
 *
 * Created by leer on 8/22/18.
 * Copyright (c) 2017 LeeReindeer
 * https://github.com/LeeReindeer
 */
class DatePickerFragment : DialogFragment() {

  val TAG = "DatePickerFragment"

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val c = Calendar.getInstance()
    val year = c.get(Calendar.YEAR)
    val month = c.get(Calendar.MONTH)
    val day = c.get(Calendar.DAY_OF_MONTH)
    Log.d(TAG, "onCreateDialog: $year-$month-$day")
    return DatePickerDialog(activity, activity as AddEventActivity, year, month, day)
  }
}