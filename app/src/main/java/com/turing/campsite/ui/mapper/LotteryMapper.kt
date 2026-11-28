package com.turing.campsite.ui.mapper

import com.turing.campsite.data.model.Campsite
import com.turing.campsite.data.model.Lottery
import com.turing.campsite.ui.components.LotteryFormData

fun LotteryFormData.toLottery(ownerId: String, campsites: List<String>): Lottery{
    return Lottery(
        name = name,
        ownerId = ownerId,
        campsiteID = campsites,
        registrationStart = registrationStart,
        registrationEnd = registrationEnd,
        bookingStart = bookingStart,
        bookingEnd = bookingEnd,
        winnerCount = numberOfWinners.toInt(),
        lotteryCap = lotteryCapacity.toIntOrNull() ?: 0,
        capOn = maximumCapacityEnabled
    )
}