
/*
 * AI Assistance Disclosure
 *
 * Company: OpenAI
 * Tool: ChatGPT (GPT-6)
 * Date: 2026-11-24
 * Subject: Admin campsite browsing, search implementation,
 *          loading and empty states, Jetpack Compose state
 *          management and code review.
 *
 * Scope: AI-assisted implementation and debugging of this file.
 */

package com.turing.campsite.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
 * Admin browse campsites screen.
 *
 * Displays all registered campsites using the shared
 * CampsiteCard component with an admin delete button.
 * Supports searching by campsite name or location.
 *
 * Displays a loading indicator while fetching campsites,
 * and an empty-state message when no results are available.
 *
 * Deletion is currently a visual placeholder.
 * Back navigation is handled through a callback.
 */
@Composable
fun AdminBrowseCampsiteScreen(
    onBackClick: () -> Unit,
    campsites: List<Campsite>,
    isLoading: Boolean
) {
    // Search query state
    var searchQuery by rememberSaveable { mutableStateOf("") }

    // Filter by campsite name or location
    val query = searchQuery.trim()
    val filteredCampsites = if (query.isEmpty()) {
        campsites
    } else {
        campsites.filter { campsite ->
            campsite.name.contains(query, ignoreCase = true) ||
                    campsite.location.contains(query, ignoreCase = true)
        }
    }

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

        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = "Search by name or location",
                    fontSize = 12.sp,
                    color = SecondaryText
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = PrimaryColor
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = CardBackground,
                unfocusedContainerColor = CardBackground,
                focusedBorderColor = PrimaryColor,
                unfocusedBorderColor = CardBorder
            )
        )

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
                text = "${filteredCampsites.size} campsites",
                fontSize = 11.sp,
                color = SecondaryText
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Loading, empty state, or campsite list
        if (isLoading) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryColor)
            }
        } else if (filteredCampsites.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (campsites.isEmpty()) {
                        "No campsites registered yet."
                    } else {
                        "No campsites match your search."
                    },
                    fontSize = 14.sp,
                    color = SecondaryText
                )
            }
        } else {
            // Scrollable filtered campsite list
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(
                    items = filteredCampsites,
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
}