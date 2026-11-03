
package com.turing.campsite.ui.screens.entrant

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.turing.campsite.PrimaryColor

private val PageBackground = Color(0xFFF6F4ED)
private val SecondaryText = Color(0xFF7A8079)

/**
 * Entrant QR code scanner screen.
 *
 * Currently displays a static scanner interface.
 * Camera access and QR detection will be implemented later.
 * Bottom navigation is managed by AppScaffold.
 */
@Composable
fun EntrantQRCodeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        // Header
        Text(
            text = "Scan QR code",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryColor
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "A little closer to your next stay.",
            fontSize = 12.sp,
            color = SecondaryText
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Scanner preview placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.7f)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF394F40),
                            Color(0xFF22392F),
                            Color(0xFF182A23)
                        )
                    )
                )
        ) {
            // Scanner label
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.CameraAlt,
                    contentDescription = null,
                    modifier = Modifier.size(15.dp),
                    tint = Color.White
                )

                Text(
                    text = "QR scanner",
                    fontSize = 12.sp,
                    color = Color.White
                )
            }

            // Scanner frame markers
            ScannerFrame(
                modifier = Modifier.fillMaxSize()
            )

            // Scanner status
            Text(
                text = "•  Looking for a QR code...",
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp),
                fontSize = 12.sp,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Scanning instructions
        Text(
            text = "Align a QR code within the frame.",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontSize = 13.sp,
            color = PrimaryColor
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Hold steady. It will scan automatically.",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontSize = 11.sp,
            color = SecondaryText
        )
    }
}

/**
 * Draws the four corner markers and centre guide line
 * of the QR scanner frame.
 */
@Composable
private fun ScannerFrame(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val left = size.width * 0.14f
        val right = size.width * 0.86f
        val top = size.height * 0.23f
        val bottom = size.height * 0.72f
        val cornerLength = 20.dp.toPx()

        val framePath = Path().apply {
            // Top-left
            moveTo(left, top + cornerLength)
            lineTo(left, top)
            lineTo(left + cornerLength, top)

            // Top-right
            moveTo(right - cornerLength, top)
            lineTo(right, top)
            lineTo(right, top + cornerLength)

            // Bottom-left
            moveTo(left, bottom - cornerLength)
            lineTo(left, bottom)
            lineTo(left + cornerLength, bottom)

            // Bottom-right
            moveTo(right - cornerLength, bottom)
            lineTo(right, bottom)
            lineTo(right, bottom - cornerLength)
        }

        drawPath(
            path = framePath,
            color = Color.White,
            style = Stroke(
                width = 2.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        // Static horizontal scan guide
        val centreY = (top + bottom) / 2f

        drawLine(
            color = Color.White.copy(alpha = 0.25f),
            start = Offset(left, centreY),
            end = Offset(right, centreY),
            strokeWidth = 1.dp.toPx()
        )
    }
}
