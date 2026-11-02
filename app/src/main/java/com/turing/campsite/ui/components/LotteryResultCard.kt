package com.turing.campsite.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material.icons.outlined.Terrain
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.turing.campsite.PrimaryColor

private val CardBackground = Color(0xFFFCFBF7)
private val CardBorder = Color(0xFFDFE2D8)
private val ChipBackground = Color(0xFFE8EFE3)
private val SecondaryText = Color(0xFF7A8079)
private val PendingBackground = Color(0xFFF3ECE0)
private val PendingText = Color(0xFF927A57)

/**
 * Possible lottery result states.
 */
enum class LotteryResultStatus(val label: String) {
    WON("Won"),
    PENDING("Pending"),
    LOST("Lost")
}

/**
 * Temporary data used to display lottery results.
 */
data class LotteryResultPreview(
    val id: String,
    val campsiteName: String,
    val status: LotteryResultStatus,
    val message: String,
    val timestamp: String,
    val dateRange: String? = null,
    val deadline: String? = null,
    val imageColor: Color = Color(0xFF82967A)
)

/**
 * Displays a lottery result.
 *
 * Winning results use an expanded card with action buttons.
 * Other results use a compact card.
 */
@Composable
fun LotteryResultCard(result: LotteryResultPreview) {
    when (result.status) {
        LotteryResultStatus.WON -> WinningLotteryCard(result)
        LotteryResultStatus.PENDING,
        LotteryResultStatus.LOST -> OtherLotteryResultCard(result)
    }
}

/**
 * Displays a winning lottery result.
 *
 * Accept and Decline are visual placeholders for now.
 */
@Composable
private fun WinningLotteryCard(result: LotteryResultPreview) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = CardBackground,
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Winning status
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ResultIcon(Icons.Outlined.EmojiEvents)

                Text(
                    text = "You won the lottery!",
                    modifier = Modifier.weight(1f),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryColor
                )

                ResultStatusChip(result.status)
            }

            // Campsite information
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Placeholder campsite image
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .background(
                            result.imageColor,
                            RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Terrain,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = Color.White
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = result.campsiteName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryColor
                    )

                    result.dateRange?.let { dateRange ->
                        Text(
                            text = dateRange,
                            fontSize = 12.sp,
                            color = PrimaryColor
                        )
                    }

                    Text(
                        text = result.message,
                        fontSize = 11.sp,
                        color = SecondaryText
                    )
                }
            }

            Text(
                text = result.timestamp,
                fontSize = 11.sp,
                color = SecondaryText
            )

            // Acceptance deadline
            result.deadline?.let { deadline ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            ChipBackground,
                            RoundedCornerShape(7.dp)
                        )
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AccessTime,
                        contentDescription = null,
                        modifier = Modifier.size(17.dp),
                        tint = PrimaryColor
                    )

                    Text(
                        text = deadline,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = PrimaryColor
                    )
                }
            }

            // Action buttons (visual only)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LotteryResultAction(
                    text = "Accept",
                    filled = true,
                    modifier = Modifier.weight(1f)
                )

                LotteryResultAction(
                    text = "Decline",
                    filled = false,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

/**
 * Displays a pending or unsuccessful lottery result.
 */
@Composable
private fun OtherLotteryResultCard(result: LotteryResultPreview) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = CardBackground,
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Top
        ) {
            val icon = when (result.status) {
                LotteryResultStatus.PENDING ->
                    Icons.Outlined.HourglassEmpty

                else -> Icons.Outlined.ConfirmationNumber
            }

            ResultIcon(icon)

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = result.campsiteName,
                        modifier = Modifier.weight(1f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryColor
                    )

                    ResultStatusChip(result.status)
                }

                val details = if (result.dateRange != null) {
                    "${result.dateRange} · ${result.message}"
                } else {
                    result.message
                }

                Text(
                    text = details,
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    color = SecondaryText
                )

                Text(
                    text = result.timestamp,
                    fontSize = 10.sp,
                    color = SecondaryText
                )
            }
        }
    }
}

/**
 * Small circular icon used by lottery result cards.
 */
@Composable
private fun ResultIcon(icon: ImageVector) {
    Box(
        modifier = Modifier
            .size(30.dp)
            .background(ChipBackground, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(17.dp),
            tint = PrimaryColor
        )
    }
}

/**
 * Status label for lottery results.
 */
@Composable
private fun ResultStatusChip(status: LotteryResultStatus) {
    val backgroundColor = when (status) {
        LotteryResultStatus.PENDING -> PendingBackground
        else -> ChipBackground
    }

    val textColor = when (status) {
        LotteryResultStatus.PENDING -> PendingText
        else -> PrimaryColor
    }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = backgroundColor
    ) {
        Text(
            text = status.label,
            modifier = Modifier.padding(
                horizontal = 8.dp,
                vertical = 4.dp
            ),
            fontSize = 10.sp,
            color = textColor
        )
    }
}

/**
 * Visual placeholder for lottery result actions.
 */
@Composable
private fun LotteryResultAction(
    text: String,
    filled: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.height(38.dp),
        shape = RoundedCornerShape(7.dp),
        color = if (filled) PrimaryColor else CardBackground,
        border = if (filled) null else BorderStroke(1.dp, CardBorder)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                fontSize = 12.sp,
                color = if (filled) Color.White else PrimaryColor
            )
        }
    }
}
