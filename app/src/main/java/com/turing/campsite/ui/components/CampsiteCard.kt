package com.turing.campsite.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Terrain
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
import androidx.compose.foundation.clickable

import com.turing.campsite.PrimaryColor

import com.turing.campsite.data.model.Campsite // real model
import java.util.Locale

private val CardBackground = Color(0xFFFCFBF7)
private val CardBorder = Color(0xFFDFE2D8)
private val ChipBackground = Color(0xFFE8EFE3)
private val SecondaryText = Color(0xFF7A8079)
private val DeleteColor = Color(0xFFB45345)

/**
 * Displays a preview of one campsite.
 *
 * An optional delete icon can be displayed for administrators.
 * Deletion functionality will be added later.
 */
@Composable
fun CampsiteCard(
    campsite: Campsite,
    showDeleteButton: Boolean = false,
    onClick: (() -> Unit)? = null
) {

    // A campsite the owner has paused is shown as inactive.
    val statusColor = if (campsite.active) {
        PrimaryColor
    } else {
        SecondaryText
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(10.dp),
        color = CardBackground,
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {


            Box(
                modifier = Modifier
                    .width(84.dp)
                    .height(104.dp)
                    .background(
                        Color(0xFF82967A),
                        RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Terrain,
                    contentDescription = null,
                    modifier = Modifier.size(36.dp),
                    tint = Color.White
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                // Campsite status and optional admin delete icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(statusColor, CircleShape)
                        )

                        Text(
                            text = if (campsite.active) "Active" else "Inactive",
                            fontSize = 10.sp,
                            color = statusColor
                        )
                    }

                    // Admin delete icon (visual placeholder)
                    if (showDeleteButton) {
                        Icon(
                            imageVector = Icons.Outlined.DeleteOutline,
                            contentDescription = "Delete campsite",
                            modifier = Modifier.size(20.dp),
                            tint = DeleteColor
                        )
                    }
                }

                Text(
                    text = campsite.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = campsite.location,
                    fontSize = 11.sp,
                    color = SecondaryText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Tags are shown when there are any, otherwise the description.
                Text(
                    text = if (campsite.tags.isEmpty()) {
                        campsite.description
                    } else {
                        campsite.tags.joinToString(" · ")
                    },
                    fontSize = 11.sp,
                    color = PrimaryColor,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {

                    InfoChip("Max ${campsite.maxOccupancy} people")
                }


                val ratingText = if (campsite.ratingCount == 0) {
                    "Not rated"
                } else {
                    String.format(Locale.US, "%.1f/5", campsite.ratingAverage)
                }
                InfoChip(ratingText)
            }
        }
    }
}

/**
 * Small information label used in campsite cards.
 */
@Composable
private fun InfoChip(text: String) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = ChipBackground
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(
                horizontal = 7.dp,
                vertical = 3.dp
            ),
            fontSize = 10.sp,
            color = PrimaryColor,
            lineHeight = 12.sp
        )
    }
}