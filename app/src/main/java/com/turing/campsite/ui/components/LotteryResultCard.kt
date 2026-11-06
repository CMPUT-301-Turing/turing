package com.turing.campsite.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.turing.campsite.PrimaryColor
import com.turing.campsite.data.model.EntryResult
import com.turing.campsite.data.model.LotteryEntry
import java.text.SimpleDateFormat
import java.util.Locale

private val CardBackground = Color(0xFFFCFBF7)
private val CardBorder = Color(0xFFDFE2D8)
private val SecondaryText = Color(0xFF7A8079)
private val ChipBackground = Color(0xFFE8EFE3)
private val WonBackground = Color(0xFFE3F0E6)
private val LostBackground = Color(0xFFF7E9E5)

@Composable
fun LotteryResultCard(
    result: LotteryEntry
) {
    val statusText = when (result.result) {
        EntryResult.WON -> "You won!"
        EntryResult.PENDING -> "Pending draw"
        EntryResult.LOST -> "Not selected"
        EntryResult.DECLINED -> "Declined"
    }

    val messageText = when (result.result) {
        EntryResult.WON -> "A pitch is yours to book."
        EntryResult.PENDING -> "Lottery open. Your entry is in the draw."
        EntryResult.LOST -> "You weren't selected this time. Try a future draw."
        EntryResult.DECLINED -> "You declined this reservation."
    }

    val badgeColor = when (result.result) {
        EntryResult.WON -> WonBackground
        EntryResult.LOST -> LostBackground
        else -> ChipBackground
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = CardBackground,
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = result.id,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryColor
                )

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = badgeColor
                ) {
                    Text(
                        text = statusText,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = PrimaryColor
                    )
                }
            }

            Text(
                text = messageText,
                fontSize = 12.sp,
                color = SecondaryText
            )
        }
    }
}