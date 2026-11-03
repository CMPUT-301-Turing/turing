
package com.turing.campsite.ui.screens.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Terrain
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.turing.campsite.PrimaryColor
import com.turing.campsite.ui.components.FilterChip

private val PageBackground = Color(0xFFF6F4ED)
private val CardBackground = Color(0xFFFCFBF7)
private val CardBorder = Color(0xFFDFE2D8)
private val SecondaryText = Color(0xFF7A8079)
private val DeleteColor = Color(0xFF994E43)

/**
 * Image categories available in the admin browser.
 */
private enum class ImageType {
    CAMPSITE,
    PROFILE
}

/**
 * Available image filters.
 */
private enum class ImageFilter(val label: String) {
    ALL("All"),
    CAMPSITES("Campsites"),
    PROFILES("Profiles")
}

/**
 * Temporary image information for displaying the image grid.
 */
private data class ImagePreview(
    val id: String,
    val uploader: String,
    val type: ImageType,
    val imageColor: Color,
    val height: Dp
)

/**
 * Temporary sample images.
 */
private val sampleImages = listOf(
    ImagePreview(
        id = "image-1",
        uploader = "Priya Shah",
        type = ImageType.CAMPSITE,
        imageColor = Color(0xFF66816A),
        height = 155.dp
    ),
    ImagePreview(
        id = "image-2",
        uploader = "Maya Bennett",
        type = ImageType.PROFILE,
        imageColor = Color(0xFF9EAC91),
        height = 205.dp
    ),
    ImagePreview(
        id = "image-3",
        uploader = "Will Smith",
        type = ImageType.CAMPSITE,
        imageColor = Color(0xFFB5A17A),
        height = 175.dp
    ),
    ImagePreview(
        id = "image-4",
        uploader = "Sophie Chen",
        type = ImageType.PROFILE,
        imageColor = Color(0xFF82998A),
        height = 165.dp
    ),
    ImagePreview(
        id = "image-5",
        uploader = "Pinewood Retreat",
        type = ImageType.CAMPSITE,
        imageColor = Color(0xFF789276),
        height = 180.dp
    ),
    ImagePreview(
        id = "image-6",
        uploader = "Harry Potter",
        type = ImageType.PROFILE,
        imageColor = Color(0xFF9EA99B),
        height = 190.dp
    )
)

/**
 * Admin image browser.
 *
 * Displays a filtered image grid with local selection state.
 * Image loading and deletion will be implemented later.
 */
@Composable
fun AdminBrowseImageScreen(
    onBackClick: () -> Unit
) {
    var selectedFilter by remember {
        mutableStateOf(ImageFilter.ALL)
    }

    var selectedImageIds by remember {
        mutableStateOf(setOf<String>())
    }

    val visibleImages = sampleImages.filter { image ->
        when (selectedFilter) {
            ImageFilter.ALL -> true
            ImageFilter.CAMPSITES -> image.type == ImageType.CAMPSITE
            ImageFilter.PROFILES -> image.type == ImageType.PROFILE
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            // Header with back navigation
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

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Browse Images",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryColor
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Search bar (visual placeholder)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(9.dp),
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
                        modifier = Modifier.size(19.dp),
                        tint = PrimaryColor
                    )

                    Text(
                        text = "Search by uploader or type",
                        fontSize = 12.sp,
                        color = SecondaryText
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Image category filters
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ImageFilter.entries.forEach { filter ->
                    FilterChip(
                        label = filter.label,
                        selected = selectedFilter == filter,
                        onClick = {
                            selectedFilter = filter
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Image list heading
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "All images",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryColor
                )

                Text(
                    text = "${visibleImages.size} images",
                    fontSize = 11.sp,
                    color = SecondaryText
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Scrollable image grid
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalItemSpacing = 12.dp,
                contentPadding = PaddingValues(
                    bottom = if (selectedImageIds.isNotEmpty()) {
                        130.dp
                    } else {
                        16.dp
                    }
                )
            ) {
                items(
                    items = visibleImages,
                    key = { it.id }
                ) { image ->
                    ImagePreviewCard(
                        image = image,
                        selected = image.id in selectedImageIds,
                        onClick = {
                            selectedImageIds =
                                if (image.id in selectedImageIds) {
                                    selectedImageIds - image.id
                                } else {
                                    selectedImageIds + image.id
                                }
                        }
                    )
                }
            }
        }

        // Selection actions
        if (selectedImageIds.isNotEmpty()) {
            ImageSelectionPanel(
                selectedCount = selectedImageIds.size,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(
                        horizontal = 20.dp,
                        vertical = 16.dp
                    )
            )
        }
    }
}

/**
 * Displays an image thumbnail and its uploader information.
 *
 * Clicking the thumbnail toggles its selection.
 */
@Composable
private fun ImagePreviewCard(
    image: ImagePreview,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(image.height)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            image.imageColor,
                            image.imageColor.copy(alpha = 0.75f)
                        )
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
                .clickable(onClick = onClick)
        ) {
            // Temporary image placeholder
            Icon(
                imageVector = if (image.type == ImageType.CAMPSITE) {
                    Icons.Outlined.Terrain
                } else {
                    Icons.Outlined.PersonOutline
                },
                contentDescription = null,
                modifier = Modifier
                    .size(42.dp)
                    .align(Alignment.Center),
                tint = Color.White.copy(alpha = 0.8f)
            )

            // Image type indicator
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(6.dp)
                    .size(25.dp)
                    .background(
                        Color.White.copy(alpha = 0.9f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (image.type == ImageType.CAMPSITE) {
                        Icons.Outlined.Terrain
                    } else {
                        Icons.Outlined.PersonOutline
                    },
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = PrimaryColor
                )
            }

            // Selection checkbox
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .size(25.dp)
                    .background(
                        if (selected) PrimaryColor else Color.White,
                        RoundedCornerShape(5.dp)
                    )
                    .border(
                        1.dp,
                        if (selected) PrimaryColor else CardBorder,
                        RoundedCornerShape(5.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (selected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(17.dp),
                        tint = Color.White
                    )
                }
            }
        }

        Text(
            text = image.uploader,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = PrimaryColor
        )

        Text(
            text = if (image.type == ImageType.CAMPSITE) {
                "Campsite photos"
            } else {
                "Profile photo"
            },
            fontSize = 11.sp,
            color = SecondaryText
        )
    }
}

/**
 * Displays selection information and the delete action.
 *
 * Deletion is a visual placeholder until backend
 * functionality is implemented.
 */
@Composable
private fun ImageSelectionPanel(
    selectedCount: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = CardBackground,
        border = BorderStroke(1.dp, CardBorder),
        shadowElevation = 6.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = PrimaryColor
            ) {
                Text(
                    text = "$selectedCount selected",
                    modifier = Modifier.padding(
                        horizontal = 10.dp,
                        vertical = 5.dp
                    ),
                    fontSize = 11.sp,
                    color = Color.White
                )
            }

            // Delete action (visual placeholder)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                shape = RoundedCornerShape(7.dp),
                color = DeleteColor
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(
                        8.dp,
                        Alignment.CenterHorizontally
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.DeleteOutline,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = Color.White
                    )

                    Text(
                        text = "Delete Selected",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }
        }
    }
}
