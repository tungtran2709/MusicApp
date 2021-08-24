package com.example.musicapp.utils

object TimeConvert {

    fun convertMillisecondsToMinute(milliseconds: Int): String {
        var minute = "${milliseconds / NUMBER_ONE_THOUSAND / NUMBER_SIXTY}"
        var second = "${milliseconds / NUMBER_ONE_THOUSAND % NUMBER_SIXTY}"
        if (minute.length <= NUMBER_ONE) minute = "0$minute"
        if (second.length <= NUMBER_ONE) second = "0$second"
        return "$minute:$second"
    }

    const val NUMBER_ONE = 1
    const val NUMBER_SIXTY = 60
    const val NUMBER_ONE_THOUSAND = 1000

}
