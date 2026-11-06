package com.turing.campsite.ui.screens.owner

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.turing.campsite.data.model.Campsite // real model

private val PageBackground = Color(0xFFF6F4ED)
private val CardBackground = Color(0xFFFCFBF7)
private val CardBorder = Color(0xFFDFE2D8)
private val ChipBackground = Color(0xFFE8EFE3)
private val SecondaryText = Color(0xFF7A8079)

/**
 * Temporary campsites belonging to the logged-in owner.
 */
private val sampleCampsites = listOf(

    Campsite(
        name = "Pinewood Retreat",
        location = "New Forest, Hampshire",
        description = "12 pitches · Woodland",
        maxOccupancy = 48,
        ratingAverage = 4.8,
        ratingCount = 10
    ),
    Campsite(
        name = "Willowbank Meadow",
        location = "Bakewell, Peak District",
        description = "8 pitches · Riverside",
        maxOccupancy = 48,
        ratingAverage = 4.7,
        ratingCount = 15
    ),
    Campsite(
        name = "Fellside Farm",
        location = "Keswick, Lake District",
        description = "6 pitches · Hill views",
        maxOccupancy = 24,
        ratingAverage = 4.6,
        ratingCount = 8
    ),
    Campsite(
        name = "Heather Ridge",
        location = "Whitby, North Yorkshire",
        description = "10 pitches · Countryside",
        maxOccupancy = 40,
        ratingAverage = 4.5,
        ratingCount = 5
    )
)

/**
 * Campsite owner's home screen.
 *
 * Displays the owner's campsites and actions for
 * registering campsites and creating lotteries.
 *
 * The campsites to show are passed in. Sample campsites
 * are used when none are given.
 * Navigation is handled through callbacks.
 */
@Composable
fun OwnerHomeScreen(
    onNotificationsClick: () -> Unit,
    onProfileClick: () -> Unit,
    onRegisterCampsiteClick: () -> Unit,
    onCreateLotteryClick: () -> Unit,
    onCampsiteClick: () -> Unit,
    campsites: List<Campsite> = sampleCampsites
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBackground)
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        // Header with notifications and profile icons
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Campsite Owner Home",
                modifier = Modifier.weight(1f),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryColor
            )

            IconButton(
                onClick = onNotificationsClick,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.NotificationsNone,
                    contentDescription = "Notifications",
                    modifier = Modifier.size(23.dp),
                    tint = PrimaryColor
                )
            }

            IconButton(
                onClick = onProfileClick,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.PersonOutline,
                    contentDescription = "Profile",
                    modifier = Modifier.size(23.dp),
                    tint = PrimaryColor
                )
            }
        }

        Spacer(modifier = Modifier.height(26.dp))

        // Register new campsite
        Button(
            onClick = onRegisterCampsiteClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(9.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryColor
            ),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            Text(
                text = "+ Register New Campsite",
                modifier = Modifier.fillMaxWidth(),
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Create lottery
        Surface(
            onClick = onCreateLotteryClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            color = CardBackground,
            border = BorderStroke(1.dp, CardBorder)
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            ChipBackground,
                            RoundedCornerShape(9.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ConfirmationNumber,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = PrimaryColor
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Create Lottery",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryColor
                    )

                    Text(
                        text = "Start a multi-campsite lottery",
                        fontSize = 12.sp,
                        color = SecondaryText
                    )
                }

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp),
                    tint = PrimaryColor
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Owner campsite list header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "My Campsites",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryColor
            )

            Text(
                text = "${campsites.size} campsites",
                fontSize = 12.sp,
                color = SecondaryText
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Scrollable campsite list
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(
                items = campsites,
                // Sample campsites have no id, so their name is used instead.
                key = { it.id.ifEmpty { it.name } }
            ) { campsite ->
                CampsiteCard(
                    campsite = campsite,
                    onClick = onCampsiteClick
                )
            }
        }
    }
}