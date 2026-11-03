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

private val CardBackground = Color(0xFFFCFBF7)
private val CardBorder = Color(0xFFDFE2D8)
private val ChipBackground = Color(0xFFE8EFE3)
private val SecondaryText = Color(0xFF7A8079)
private val DeleteColor = Color(0xFFB45345)

/**
 * Temporary campsite data used to populate campsite cards.
 *
 * The default status preserves existing entrant and admin
 * campsite cards that do not explicitly provide a status.
 */
data class CampsitePreview(
    val name: String,
    val location: String,
    val description: String,
    val peoplePerPitch: String,
    val maxPeople: String,
    val rating: String,
    val imageColor: Color,
    val statusLabel: String = "Active now"
)

/**
 * Displays a preview of one campsite.
 *
 * An optional delete icon can be displayed for administrators.
 * Deletion functionality will be added later.
 */
@Composable
fun CampsiteCard(
    campsite: CampsitePreview,
    showDeleteButton: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val statusColor = if (campsite.statusLabel == "Inactive") {
        SecondaryText
    } else {
        PrimaryColor
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
            // Placeholder for campsite photograph
            Box(
                modifier = Modifier
                    .width(84.dp)
                    .height(104.dp)
                    .background(
                        campsite.imageColor,
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
                            text = campsite.statusLabel,
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

                Text(
                    text = campsite.description,
                    fontSize = 11.sp,
                    color = PrimaryColor
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    InfoChip(campsite.peoplePerPitch)
                    InfoChip(campsite.maxPeople)
                }

                InfoChip(campsite.rating)
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
