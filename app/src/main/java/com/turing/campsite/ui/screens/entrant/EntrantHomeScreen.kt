package com.turing.campsite.ui.screens.entrant

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Terrain
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Icon
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
import com.turing.campsite.ui.components.CampsitePreview

private val PageBackground = Color(0xFFF6F4ED)
private val CardBackground = Color(0xFFFCFBF7)
private val CardBorder = Color(0xFFDFE2D8)
private val SecondaryText = Color(0xFF7A8079)

private val sampleCampsites = listOf(
    CampsitePreview(
        name = "Pinewood Retreat",
        location = "New Forest, Hampshire",
        description = "12 pitches · Woodland",
        peoplePerPitch = "1–4 people/pitch",
        maxPeople = "Max 48 people",
        rating = "4.8/5",
        imageColor = Color(0xFF82967A)
    ),
    CampsitePreview(
        name = "Willowbank Meadow",
        location = "Bakewell, Peak District",
        description = "8 pitches · Riverside",
        peoplePerPitch = "1–6 people/pitch",
        maxPeople = "Max 48 people",
        rating = "4.7/5",
        imageColor = Color(0xFF9CAB91)
    ),
    CampsitePreview(
        name = "Fellside Farm",
        location = "Keswick, Lake District",
        description = "6 pitches · Hill views",
        peoplePerPitch = "1–4 people/pitch",
        maxPeople = "Max 24 people",
        rating = "4.6/5",
        imageColor = Color(0xFF8B9D91)
    )
)

/**
 * Entrant home screen.
 *
 * Displays a static campsite browsing interface.
 * Shared bottom navigation is managed by AppScaffold.
 */
@Composable
fun EntrantHomeScreen(
    onCampsiteClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBackground),
        contentPadding = PaddingValues(
            horizontal = 20.dp,
            vertical = 20.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header, search controls, and section title
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Campsites",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryColor
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "A little closer to outside.",
                            fontSize = 12.sp,
                            color = SecondaryText
                        )
                    }

                    Icon(
                        imageVector = Icons.Outlined.Terrain,
                        contentDescription = null,
                        modifier = Modifier.size(42.dp),
                        tint = PrimaryColor
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Search and filter controls (visual only)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = CardBackground,
                        border = BorderStroke(1.dp, CardBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = null,
                                tint = SecondaryText,
                                modifier = Modifier.size(20.dp)
                            )

                            Text(
                                text = "Search campsites",
                                fontSize = 12.sp,
                                color = SecondaryText
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = PrimaryColor
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Outlined.Tune,
                                contentDescription = "Filters",
                                tint = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Section heading
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Currently active",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryColor
                    )

                    Text(
                        text = "${sampleCampsites.size} campsites",
                        fontSize = 11.sp,
                        color = SecondaryText
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))
            }
        }

        // Campsite cards
        items(
            items = sampleCampsites,
            key = { it.name }
        ) { campsite ->
            CampsiteCard(
                campsite = campsite,
                onClick = {
                    onCampsiteClick(campsite.name)
                }
            )
        }
    }
}