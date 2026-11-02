package com.turing.campsite.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Terrain
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.turing.campsite.PrimaryColor

private val CardBackground = Color(0xFFFCFBF7)
private val CardBorder = Color(0xFFDFE2D8)
private val SecondaryText = Color(0xFF7A8079)

/**
 * Possible types of lottery entries.
 */
enum class LotteryEntryStatus(
    val label: String,
    val actionLabel: String
) {
    ENTERED("Lottery entered", "Leave lottery"),
    WAITLISTED("On lottery waitlist", "Leave waitlist")
}

/**
 * Temporary data used to display lottery entries.
 */
data class LotteryEntryPreview(
    val id: String,
    val campsiteName: String,
    val location: String,
    val dateRange: String,
    val nights: Int,
    val note: String,
    val status: LotteryEntryStatus,
    val imageColor: Color
)

/**
 * Displays a lottery or waitlist entry.
 *
 * Action buttons are visual placeholders for now.
 */
@Composable
fun LotteryEntryCard(entry: LotteryEntryPreview) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = CardBackground,
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Campsite information
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Placeholder campsite image
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            entry.imageColor,
                            RoundedCornerShape(7.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Terrain,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Entry status
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .background(PrimaryColor, CircleShape)
                        )

                        Text(
                            text = entry.status.label,
                            fontSize = 10.sp,
                            color = PrimaryColor
                        )
                    }

                    Text(
                        text = entry.campsiteName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = entry.location,
                        fontSize = 11.sp,
                        color = SecondaryText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            HorizontalDivider(color = CardBorder)

            // Lottery dates
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.DateRange,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp),
                        tint = PrimaryColor
                    )

                    Text(
                        text = entry.dateRange,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryColor
                    )
                }

                Text(
                    text = "${entry.nights} nights",
                    fontSize = 11.sp,
                    color = SecondaryText
                )
            }

            // Additional information
            Text(
                text = entry.note,
                fontSize = 11.sp,
                color = SecondaryText
            )

            // Leave lottery / waitlist (visual only)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp),
                shape = RoundedCornerShape(7.dp),
                color = CardBackground,
                border = BorderStroke(1.dp, CardBorder)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = entry.status.actionLabel,
                        fontSize = 11.sp,
                        color = PrimaryColor
                    )
                }
            }
        }
    }
}
