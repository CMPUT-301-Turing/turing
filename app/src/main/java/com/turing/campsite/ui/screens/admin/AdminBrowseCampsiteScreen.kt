package com.turing.campsite.ui.screens.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.turing.campsite.ui.components.CampsiteCard
import com.turing.campsite.data.model.Campsite

private val PageBackground = Color(0xFFF6F4ED)
private val CardBackground = Color(0xFFFCFBF7)
private val CardBorder = Color(0xFFDFE2D8)
private val SecondaryText = Color(0xFF7A8079)

/**
 * Temporary campsite data used by the admin browse screen.
 */
private val sampleCampsites = listOf(

    Campsite(
        id = "1",
        name = "Oak Hollow",
        location = "Brecon, Powys",
        description = "Woodland",
        maxOccupancy = 2,
        ratingAverage = 0.0,
        ratingCount = 0
    ),
    Campsite(
        id = "2",
        name = "Pinewood Retreat",
        location = "New Forest, Hampshire",
        description = "Woodland",
        maxOccupancy = 7,
        ratingAverage = 4.8,
        ratingCount = 12
    ),
    Campsite(
        id = "3",
        name = "Willowbank Meadow",
        location = "Bakewell, Peak District",
        description = "Riverside",
        maxOccupancy = 5,
        ratingAverage = 4.7,
        ratingCount = 8
    ),
    Campsite(
        id = "4",
        name = "Heather Ridge",
        location = "Whitby, North Yorkshire",
        description = "Hill views",
        maxOccupancy = 12,
        ratingAverage = 0.0,
        ratingCount = 0
    ),
    Campsite(
        id = "5",
        name = "Fellside Farm",
        location = "Keswick, Lake District",
        description = "Hill views",
        maxOccupancy = 10,
        ratingAverage = 4.6,
        ratingCount = 5
    )
)

/**
 * Admin browse campsites screen.
 *
 * Displays a static list of campsites using the shared
 * CampsiteCard component with an admin delete button.
 *
 * Search and deletion are visual placeholders.
 * Back navigation is handled through a callback.
 */
@Composable
fun AdminBrowseCampsiteScreen(
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBackground)
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        // Header and back navigation
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back to admin home",
                    modifier = Modifier.size(22.dp),
                    tint = PrimaryColor
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "Browse Campsites",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryColor
            )
        }

        Spacer(modifier = Modifier.height(26.dp))

        // Search bar (visual placeholder)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(10.dp),
            color = CardBackground,
            border = BorderStroke(1.dp, CardBorder)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = PrimaryColor
                )

                Text(
                    text = "Search by name or location",
                    fontSize = 12.sp,
                    color = SecondaryText
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Campsite list heading
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "All campsites",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryColor
            )

            Text(
                text = "${sampleCampsites.size} campsites",
                fontSize = 11.sp,
                color = SecondaryText
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Scrollable campsite list
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(
                items = sampleCampsites,
                key = { it.id }
            ) { campsite ->
                CampsiteCard(
                    campsite = campsite,
                    showDeleteButton = true
                )
            }
        }
    }
}