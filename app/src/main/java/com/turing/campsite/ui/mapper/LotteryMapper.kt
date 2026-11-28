package com.turing.campsite.ui.mapper


import com.turing.campsite.data.model.Lottery
import com.turing.campsite.ui.components.LotteryFormData
import java.util.Date


fun LotteryFormData.toLottery(owner: String, campsites: List<String>): Lottery{
    var stat = ""
    if (registrationEnd!=null && registrationStart!=null){
        if (registrationEnd.toDate().after(Date()) && Date().after(registrationStart.toDate())){
            stat = "Active"
        }
        else{
            stat = "Closed"
        }
    }

    if (numberOfWinners == ""){
        return Lottery(
            name = name,
            owner = owner,
            campsiteID = campsites,
            registrationStart = registrationStart,
            registrationEnd = registrationEnd,
            bookingStart = bookingStart,
            bookingEnd = bookingEnd,
            winnerCount = "0".toInt(),
            lotteryCap = lotteryCapacity.toIntOrNull() ?: 0,
            capOn = maximumCapacityEnabled,
            status = stat
        )
    }

    return Lottery(
        name = name,
        owner = owner,
        campsiteID = campsites,
        registrationStart = registrationStart,
        registrationEnd = registrationEnd,
        bookingStart = bookingStart,
        bookingEnd = bookingEnd,
        winnerCount = numberOfWinners.toInt(),
        lotteryCap = lotteryCapacity.toIntOrNull() ?: 0,
        capOn = maximumCapacityEnabled,
        status = stat
    )
}