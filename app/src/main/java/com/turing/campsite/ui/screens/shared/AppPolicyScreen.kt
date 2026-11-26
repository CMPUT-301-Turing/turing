package com.turing.campsite.ui.screens.shared

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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

private val PageBackground = Color(0xFFF6F4ED)
private val CardBackground = Color(0xFFFCFBF7)
private val CardBorder = Color(0xFFDFE2D8)
private val SecondaryText = Color(0xFF7A8079)

/**
 * One section of the app policy and guidelines.
 *
 * @property title the section heading
 * @property body the text shown under the heading
 */
data class PolicySection(
    val title: String,
    val body: String
)

/**
 * The app policy and guidelines shown on [AppPolicyScreen].
 *
 * Kept as plain text in the app so it can be read without a connection.
 */
val appPolicySections = listOf(
    PolicySection(
        title = "Your profile",
        body = "You are identified by this device, so there is no username or password. " +
            "Your profile keeps your name, email and phone number so campsite owners " +
            "can contact you about a reservation. You can edit these details at any time."
    ),
    PolicySection(
        title = "Lotteries",
        body = "Joining a campsite lottery puts you on its waitlist. When registration " +
            "closes, winners are picked at random and everyone else stays on the waitlist. " +
            "Joining more than once does not improve your chances."
    ),
    PolicySection(
        title = "Reservations",
        body = "If you win, accept or decline the reservation before the deadline. " +
            "Each campsite has its own cancellation policy, shown on its details page."
    ),
    PolicySection(
        title = "Notifications",
        body = "We notify you when you win or lose a lottery and when an owner sends an " +
            "update about a campsite you joined. You can opt out of notifications from " +
            "your profile."
    ),
    PolicySection(
        title = "Community guidelines",
        body = "Be respectful in messages and reviews. Campsite owners must post accurate " +
            "details and photos. Administrators can remove campsites, images or profiles " +
            "that break these guidelines."
    ),
    PolicySection(
        title = "Deleting your profile",
        body = "You can delete your profile from the profile screen. This removes your " +
            "details from the app and cannot be undone."
    )
)

/**
 * Shows the app policy and guidelines (US 01.11.01).
 *
 * Opened from the profile screen. Navigation is handled through callbacks.
 *
 * @param onBackClick called when the back button is pressed
 * @param sections the policy sections to show, defaults to [appPolicySections]
 */
@Composable
fun AppPolicyScreen(
    onBackClick: () -> Unit,
    sections: List<PolicySection> = appPolicySections
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Back navigation
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back to profile",
                    tint = PrimaryColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = "Profile and settings",
                fontSize = 12.sp,
                color = PrimaryColor
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Heading
        Text(
            text = "App policy and guidelines",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryColor
        )

        Text(
            text = "How the app works and what we expect from everyone.",
            fontSize = 12.sp,
            color = SecondaryText
        )

        Spacer(modifier = Modifier.height(4.dp))

        sections.forEach { section ->
            PolicyCard(section = section)
        }
    }
}

/**
 * Displays one policy section in a card.
 */
@Composable
private fun PolicyCard(section: PolicySection) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = CardBackground,
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = section.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryColor
            )

            Text(
                text = section.body,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                color = SecondaryText
            )
        }
    }
}
