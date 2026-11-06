package com.turing.campsite.utils

import com.google.firebase.Timestamp
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.Locale

fun formateDate(
    startDate: Timestamp?,
    endDate: Timestamp?
): String {
    if (startDate == null || endDate == null){
        return "Dates unavailable"
    }

    val formats = SimpleDateFormat("dd MM yyyy", Locale.getDefault())

    val start = formats.format(startDate.toDate())
    val end = formats.format(endDate.toDate())

    return "$start - $end"
}