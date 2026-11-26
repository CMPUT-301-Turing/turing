package com.turing.campsite.ui.screens.owner

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.QrCode2
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Terrain
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.turing.campsite.PrimaryColor

import com.turing.campsite.data.model.Campsite
import com.turing.campsite.ui.viewmodel.CampsiteViewModel

private val PageBackground = Color(0xFFF6F4ED)
private val CardBackground = Color(0xFFFCFBF7)
private val CardBorder = Color(0xFFDFE2D8)
private val ChipBackground = Color(0xFFE8EFE3)
private val SecondaryText = Color(0xFF7A8079)


/**
 * Detailed campsite information for the owner, loaded from Firestore.
 *
 * Loads the campsite and its lottery for [campsiteId], then shows them with
 * the stateless overload below. Navigation is handled through callbacks.
 */
@Composable
fun OwnerCampsiteFullDetailScreen(
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onQRCodeClick: () -> Unit,
    onLotteryClick: () -> Unit,
    onReservationsClick: () -> Unit,
    campsiteViewModel: CampsiteViewModel,
    campsiteId: String,
    ownerId: String?
) {
    LaunchedEffect(campsiteId, ownerId) {
        if (ownerId != null) campsiteViewModel.select(campsiteId)
    }

    OwnerCampsiteFullDetailScreen(
        campsite = campsiteViewModel.selectedCampsite?.takeIf {
            it.id == campsiteId && ownerId != null && it.ownerId == ownerId
        },
        onBackClick = onBackClick,
        onEditClick = onEditClick,
        onQRCodeClick = onQRCodeClick,
        onLotteryClick = onLotteryClick,
        onReservationsClick = onReservationsClick
    )
}

/**
 * Detailed campsite information for the owner.
 *
 * Stateless: shows the given [campsite], or a "not found" message when it is
 * null. Kept separate from the ViewModel version so screen tests can pass a
 * campsite directly.
 */
@Composable
fun OwnerCampsiteFullDetailScreen(
    campsite: Campsite?,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onQRCodeClick: () -> Unit,
    onLotteryClick: () -> Unit,
    onReservationsClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBackground)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back to owner home",
                    tint = PrimaryColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Campsite Details",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryColor
            )
        }
        if (campsite == null){
            Text(
                text = "Campsite details not found.",
                modifier = Modifier.padding(20.dp)
            )
        }
        else {
            // Scrollable campsite details
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                CampsiteHeroSection(campsite.name, campsite.location)

                Column(
                    modifier = Modifier.padding(
                        horizontal = 20.dp,
                        vertical = 20.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Main campsite actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        CampsiteAction(
                            label = "Edit Campsite",
                            icon = Icons.Outlined.Edit,
                            onClick = onEditClick,
                            modifier = Modifier.weight(1f)
                        )

                        CampsiteAction(
                            label = "QR Code",
                            icon = Icons.Outlined.QrCode2,
                            onClick = onQRCodeClick,
                            modifier = Modifier.weight(1f)
                        )

                        CampsiteAction(
                            label = "View Waitlist",
                            icon = Icons.Outlined.ConfirmationNumber,
                            onClick = onLotteryClick,
                            modifier = Modifier.weight(1f)
                        )

                        CampsiteAction(
                            label = "Reservations",
                            icon = Icons.Outlined.CalendarMonth,
                            onClick = onReservationsClick,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    CampsiteDetailsSection(
                        campsite.maxOccupancy,
                        campsite.description,
                        campsite.tags
                    )

                    CampsiteRulesSection(campsite.rulesText)

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

/**
 * Campsite cover image and title.
 *
 * A gradient is used until campsite photographs
 * are loaded from storage.
 */
@Composable
private fun CampsiteHeroSection(name: String,
                                location: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF829A7A),
                        Color(0xFF59765A),
                        Color(0xFF243B30)
                    )
                )
            )
    ) {
        Icon(
            imageVector = Icons.Outlined.Terrain,
            contentDescription = null,
            modifier = Modifier
                .size(72.dp)
                .align(Alignment.Center),
            tint = Color.White.copy(alpha = 0.45f)
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = name,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Place,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = Color.White
                )

                Text(
                    text = location,
                    fontSize = 12.sp,
                    color = Color.White
                )
            }
        }
    }
}

/**
 * Circular action shortcut displayed below the cover image.
 */
@Composable
private fun CampsiteAction(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .background(ChipBackground, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = PrimaryColor
            )
        }

        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            color = PrimaryColor
        )
    }
}

/**
 * Common card layout for campsite detail sections.
 */
@Composable
private fun CampsiteDetailCard(
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = CardBackground,
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            content = content
        )
    }
}

/**
 * Campsite occupancy, description, and tags.
 */
@Composable
private fun CampsiteDetailsSection(maxOccupancy: Int,
                                   description: String,
                                   tags: List<String>) {
    CampsiteDetailCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Campsite Details",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryColor
            )

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = ChipBackground
            ) {
                Text(
                    text = "Active",
                    modifier = Modifier.padding(
                        horizontal = 10.dp,
                        vertical = 5.dp
                    ),
                    fontSize = 11.sp,
                    color = PrimaryColor
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(
                        ChipBackground,
                        RoundedCornerShape(9.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Groups,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp),
                    tint = PrimaryColor
                )
            }

            Column {
                Text(
                    text = "Maximum Occupancy",
                    fontSize = 11.sp,
                    color = SecondaryText
                )

                Text(
                    text = "$maxOccupancy people",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryColor
                )
            }
        }

        HorizontalDivider(color = CardBorder)

        Text(
            text = "Amenities / Description",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = PrimaryColor
        )

        Text(
            text = description,
            fontSize = 12.sp,
            lineHeight = 18.sp,
            color = PrimaryColor
        )

        Text(
            text = "Tags",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = PrimaryColor
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tags.forEach { tag ->
                CampsiteTag(tag)
            }
        }
    }
}

/**
 * Small label for campsite tags.
 */
@Composable
private fun CampsiteTag(label: String) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = ChipBackground
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 5.dp
            ),
            fontSize = 10.sp,
            color = PrimaryColor
        )
    }
}

/**
 * Displays the campsite's rules and policies.
 */
@Composable
private fun CampsiteRulesSection(rulesText: String) {
    CampsiteDetailCard {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Shield,
                contentDescription = null,
                modifier = Modifier.size(19.dp),
                tint = PrimaryColor
            )

            Text(
                text = "Rules & Policies",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryColor
            )
        }

        Text(
            text = "Campsite Rules",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = PrimaryColor
        )

        Text(
            text = rulesText,
            fontSize = 12.sp,
            lineHeight = 18.sp,
            color = PrimaryColor
        )

        HorizontalDivider(color = CardBorder)

        Text(
            text = "Cancellation Policy",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = PrimaryColor
        )

        Text(
            text = "Cancellations made more than 7 days before arrival " +
                    "receive a full refund. Cancellations made within " +
                    "7 days receive a 50% refund. No refunds are issued " +
                    "for same-day cancellations.",
            fontSize = 12.sp,
            lineHeight = 18.sp,
            color = PrimaryColor
        )

        HorizontalDivider(color = CardBorder)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Require entrant confirmation",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryColor
            )

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = PrimaryColor
            ) {
                Text(
                    text = "ON",
                    modifier = Modifier.padding(
                        horizontal = 10.dp,
                        vertical = 5.dp
                    ),
                    fontSize = 10.sp,
                    color = Color.White
                )
            }
        }

        Text(
            text = "Entrants must confirm they have read these rules before reserving.",
            fontSize = 11.sp,
            lineHeight = 16.sp,
            color = SecondaryText
        )
    }
}
