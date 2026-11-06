package com.turing.campsite.ui.screens.entrant

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.ArrowOutward
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Terrain
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.turing.campsite.data.model.Campsite
import com.turing.campsite.PrimaryColor
import com.turing.campsite.ui.viewmodel.CampsiteViewModel
import com.turing.campsite.ui.viewmodel.LotteryViewModel

private val PageBackground = Color(0xFFF6F4ED)
private val CardBackground = Color(0xFFFCFBF7)
private val CardBorder = Color(0xFFDFE2D8)
private val ChipBackground = Color(0xFFE8EFE3)
private val SecondaryText = Color(0xFF7A8079)
private val StarColor = Color(0xFFC19A49)

@Composable
fun EntrantCampsiteDetailsScreen(
    campsiteViewModel: CampsiteViewModel,
    lotteryViewModel: LotteryViewModel,
    campsiteId: String,
    onBackClick: () -> Unit,
    onEnterLotteryClick: () -> Unit = {}
) {
    val campsite = campsiteViewModel.selectedCampsite

    LaunchedEffect(campsiteId) {
        campsiteViewModel.select(campsiteId)
    }

    EntrantCampsiteDetailsContent(
        campsite = campsite,
        onBackClick = onBackClick,
        onEnterLotteryClick = onEnterLotteryClick
    )
}

/**
 * Draws the campsite details for a given campsite.
 *
 * Kept apart from the view models so it can be shown and tested
 * without the database. A null campsite shows the not found text.
 */
@Composable
fun EntrantCampsiteDetailsContent(
    campsite: Campsite?,
    onBackClick: () -> Unit,
    onEnterLotteryClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBackground)
    ) {
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
                    contentDescription = "Back to campsites",
                    modifier = Modifier.size(22.dp),
                    tint = PrimaryColor
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Campsites",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryColor
            )
        }

        if (campsite == null) {
            Text(
                text = "Campsite details not found.",
                modifier = Modifier.padding(20.dp),
                color = SecondaryText
            )
        } else {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    Spacer(modifier = Modifier.height(2.dp))

                    CampsitePhotoGallery(imageColor = Color(0xFF82967A))

                    Column(
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text(
                            text = campsite.name,
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryColor
                        )

                        Text(
                            text = campsite.location,
                            fontSize = 13.sp,
                            color = SecondaryText
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(7.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Groups,
                            contentDescription = null,
                            modifier = Modifier.size(19.dp),
                            tint = PrimaryColor
                        )

                        Text(
                            text = "Capacity: ${campsite.capacity} people",
                            modifier = Modifier.weight(1f),
                            fontSize = 11.sp,
                            color = PrimaryColor
                        )

                        Column(
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = "Max ${campsite.maxOccupancy} people",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PrimaryColor
                            )
                        }
                    }

                    Text(
                        text = "Status: ${campsite.statusLabel}",
                        fontSize = 12.sp,
                        color = PrimaryColor
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(7.dp)
                    ) {
                        Text(
                            text = "About Campsite",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryColor
                        )

                        Text(
                            text = campsite.description,
                            fontSize = 13.sp,
                            lineHeight = 19.sp,
                            color = SecondaryText
                        )
                    }

                    CampsiteLocationCard(campsite.location)

                    CampsiteReviewsSection(
                        rating = campsite.ratingAverage.toString(),
                        reviewCount = campsite.ratingCount
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            EntrantLotterySection(
                onEnterLotteryClick = onEnterLotteryClick
            )
        }
    }
}

@Composable
private fun CampsitePhotoGallery(
    imageColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(148.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        CampsitePhotoPlaceholder(
            imageColor = imageColor,
            modifier = Modifier
                .weight(1.8f)
                .fillMaxHeight()
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            CampsitePhotoPlaceholder(
                imageColor = Color(0xFF718C65),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            CampsitePhotoPlaceholder(
                imageColor = Color(0xFF647E68),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        }
    }
}

@Composable
private fun CampsitePhotoPlaceholder(
    imageColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.background(
            brush = Brush.verticalGradient(
                listOf(
                    imageColor.copy(alpha = 0.75f),
                    imageColor
                )
            ),
            shape = RoundedCornerShape(8.dp)
        ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.Terrain,
            contentDescription = null,
            modifier = Modifier.size(36.dp),
            tint = Color.White.copy(alpha = 0.8f)
        )
    }
}

@Composable
private fun CampsiteLocationCard(
    location: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = ChipBackground
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Place,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = PrimaryColor
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = location,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryColor
                )

                Text(
                    text = "View location & directions",
                    fontSize = 11.sp,
                    color = SecondaryText
                )
            }

            Icon(
                imageVector = Icons.Outlined.ArrowOutward,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = PrimaryColor
            )
        }
    }
}

@Composable
private fun CampsiteReviewsSection(
    rating: String,
    reviewCount: Int
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "Reviews",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryColor
            )

            Text(
                text = "★ $rating",
                fontSize = 13.sp,
                color = PrimaryColor
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "See all $reviewCount",
                fontSize = 12.sp,
                color = PrimaryColor
            )
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(9.dp),
            color = CardBackground,
            border = BorderStroke(1.dp, CardBorder)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Ellie M. · Sep 2026",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryColor
                    )

                    Text(
                        text = "★★★★★",
                        fontSize = 12.sp,
                        color = StarColor
                    )
                }

                Text(
                    text = "\"Beautifully quiet, with lovely walks straight from our pitch. We'll be back!\"",
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    color = SecondaryText
                )
            }
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp),
            shape = RoundedCornerShape(9.dp),
            color = PageBackground,
            border = BorderStroke(1.dp, CardBorder)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(
                    8.dp,
                    Alignment.CenterHorizontally
                ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(17.dp),
                    tint = PrimaryColor
                )

                Text(
                    text = "Leave a review & rating",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = PrimaryColor
                )
            }
        }
    }
}

@Composable
private fun EntrantLotterySection(
    onEnterLotteryClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(PageBackground)
    ) {
        HorizontalDivider(color = CardBorder)

        Column(
            modifier = Modifier.padding(
                horizontal = 20.dp,
                vertical = 14.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "•  Lottery open",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryColor
                )

                Text(
                    text = "Closes 26 Oct",
                    fontSize = 11.sp,
                    color = SecondaryText
                )
            }

            Text(
                text = "Enter for a chance to book your woodland stay.",
                fontSize = 12.sp,
                color = SecondaryText
            )

            Button(
                onClick = onEnterLotteryClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(9.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryColor
                )
            ) {
                Text(
                    text = "Enter lottery",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.width(8.dp))

                Icon(
                    imageVector = Icons.Outlined.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(19.dp)
                )
            }

            Text(
                text = "No lottery open next time? Join the waitlist to hear when entries reopen.",
                modifier = Modifier.fillMaxWidth(),
                fontSize = 11.sp,
                lineHeight = 15.sp,
                textAlign = TextAlign.Center,
                color = SecondaryText
            )
        }
    }
}