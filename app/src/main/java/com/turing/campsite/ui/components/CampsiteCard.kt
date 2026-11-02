
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

import com.turing.campsite.PrimaryColor

private val CardBackground = Color(0xFFFCFBF7)
private val CardBorder = Color(0xFFDFE2D8)
private val ChipBackground = Color(0xFFE8EFE3)
private val SecondaryText = Color(0xFF7A8079)

/**
 * Temporary campsite data used to populate campsite cards.
 */
data class CampsitePreview(
    val name: String,
    val location: String,
    val description: String,
    val peoplePerPitch: String,
    val maxPeople: String,
    val rating: String,
    val imageColor: Color
)

/**
 * Displays a preview of one campsite.
 */
@Composable
fun CampsiteCard(campsite: CampsitePreview) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
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
                // Active status
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(PrimaryColor, CircleShape)
                    )

                    Text(
                        text = "Active now",
                        fontSize = 10.sp,
                        color = PrimaryColor
                    )
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