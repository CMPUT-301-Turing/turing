package com.turing.campsite.ui.screens.owner

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.turing.campsite.PrimaryColor

private val PageBackground = Color(0xFFF6F4ED)
private val CardBackground = Color(0xFFFCFBF7)
private val CardBorder = Color(0xFFDFE2D8)
private val ChipBackground = Color(0xFFE8EFE3)
private val SecondaryText = Color(0xFF7A8079)

/**
 * Displays a campsite's QR code.
 *
 * Campsite details and the QR image are temporary.
 * Share and Download will be implemented later.
 */
@Composable
fun OwnerCampsiteQRCodeScreen(
    onBackClick: () -> Unit,
    onShareClick: () -> Unit = {},
    onDownloadClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        // Header
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
                    contentDescription = "Back to campsite details",
                    modifier = Modifier.size(22.dp),
                    tint = PrimaryColor
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "QR Code",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryColor
            )
        }

        Spacer(modifier = Modifier.height(64.dp))

        // Campsite QR card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            color = CardBackground,
            border = BorderStroke(1.dp, CardBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Riverbend Glade",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryColor
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = "Dartmoor, Devon",
                    fontSize = 12.sp,
                    color = SecondaryText
                )

                Spacer(modifier = Modifier.height(20.dp))

                // QR-style placeholder
                QRCodePlaceholder()

                Spacer(modifier = Modifier.height(20.dp))

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = ChipBackground
                ) {
                    Text(
                        text = "Preview QR",
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 6.dp
                        ),
                        fontSize = 11.sp,
                        color = PrimaryColor
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Share and Download actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            QRCodeAction(
                label = "Share",
                icon = Icons.Outlined.Share,
                onClick = onShareClick
            )

            QRCodeAction(
                label = "Download",
                icon = Icons.Outlined.Download,
                onClick = onDownloadClick
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Instructions
        Text(
            text = "Opens this campsite's details in App.\n" +
                    "QR code is generated automatically upon visiting this page.",
            modifier = Modifier.fillMaxWidth(),
            fontSize = 12.sp,
            lineHeight = 18.sp,
            color = SecondaryText,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Displays a temporary QR-style pattern.
 *
 * This is not a scannable QR code. Actual QR generation
 * will be implemented when campsite URLs are available.
 */
@Composable
private fun QRCodePlaceholder() {
    val gridSize = 29
    val quietZone = 3

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val totalModules = gridSize + quietZone * 2
            val moduleSize = size.minDimension / totalModules

            val startX =
                (size.width - moduleSize * totalModules) / 2f

            val startY =
                (size.height - moduleSize * totalModules) / 2f

            for (row in 0 until gridSize) {
                for (column in 0 until gridSize) {
                    if (isDarkQRModule(row, column, gridSize)) {
                        drawRect(
                            color = Color.Black,
                            topLeft = Offset(
                                x = startX +
                                        (column + quietZone) * moduleSize,
                                y = startY +
                                        (row + quietZone) * moduleSize
                            ),
                            size = Size(
                                width = moduleSize,
                                height = moduleSize
                            )
                        )
                    }
                }
            }
        }
    }
}

/**
 * Generates a deterministic QR-like pattern
 * with three standard finder patterns.
 *
 * Visual placeholder only.
 */
private fun isDarkQRModule(
    row: Int,
    column: Int,
    gridSize: Int
): Boolean {
    val finderPositions = listOf(
        0 to 0,
        0 to gridSize - 7,
        gridSize - 7 to 0
    )

    for ((finderRow, finderColumn) in finderPositions) {
        val localRow = row - finderRow
        val localColumn = column - finderColumn

        if (localRow in -1..7 && localColumn in -1..7) {
            if (localRow !in 0..6 || localColumn !in 0..6) {
                return false
            }

            val outerBorder =
                localRow == 0 || localRow == 6 ||
                        localColumn == 0 || localColumn == 6

            val center =
                localRow in 2..4 && localColumn in 2..4

            return outerBorder || center
        }
    }

    // Timing patterns
    if (row == 6) {
        return column % 2 == 0
    }

    if (column == 6) {
        return row % 2 == 0
    }

    // Temporary data modules
    return (
            row * 17 +
                    column * 31 +
                    row * column * 7
            ) % 11 < 5
}

/**
 * Circular action button used for Share and Download.
 */
@Composable
private fun QRCodeAction(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Surface(
            onClick = onClick,
            modifier = Modifier.size(52.dp),
            shape = CircleShape,
            color = ChipBackground
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    modifier = Modifier.size(23.dp),
                    tint = PrimaryColor
                )
            }
        }

        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = PrimaryColor
        )
    }
}
