/*
 * AI Assistance Disclosure
 *
 * Company: OpenAI
 * Tool: ChatGPT (GPT-6)
 *
 * Subjects:
 * 2026-11-25: Admin profile browsing, search, and UI states (US 03.05.01).
 *
 * Scope: AI-assisted implementation, debugging, and code review.
 */

package com.turing.campsite.ui.screens.admin

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.turing.campsite.PrimaryColor
import com.turing.campsite.data.model.User

private val PageBackground = Color(0xFFF6F4ED)
private val CardBackground = Color(0xFFFCFBF7)
private val CardBorder = Color(0xFFDFE2D8)
private val AvatarBackground = Color(0xFFE8EFE3)
private val SecondaryText = Color(0xFF7A8079)

/**
 * Displays registered user profiles for administrators.
 *
 * Filters the supplied profiles locally by name or email and provides loading,
 * empty-database, and no-search-results states. The caller loads the profiles.
 * Profile deletion belongs to a separate admin feature.
 */
@Composable
fun AdminBrowseProfilesScreen(
    onBackClick: () -> Unit,
    profiles: List<User> = emptyList(),
    isLoading: Boolean = false
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }

    val query = searchQuery.trim()
    val filteredProfiles = if (query.isEmpty()) {
        profiles
    } else {
        profiles.filter { profile ->
            profile.name.contains(query, ignoreCase = true) ||
                    profile.email.contains(query, ignoreCase = true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBackground)
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
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
                    tint = PrimaryColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "Browse Profiles",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryColor
            )
        }

        Spacer(modifier = Modifier.height(26.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = "Search by name or email",
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

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "All profiles",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryColor
            )

            Text(
                text = "${filteredProfiles.size} profiles",
                fontSize = 11.sp,
                color = SecondaryText
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryColor)
                }
            }

            filteredProfiles.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (profiles.isEmpty()) {
                            "No profiles registered yet."
                        } else {
                            "No profiles match your search."
                        },
                        fontSize = 14.sp,
                        color = SecondaryText
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = filteredProfiles,
                        key = { it.id }
                    ) { profile ->
                        AdminProfileCard(profile)
                    }
                }
            }
        }
    }
}

/** Displays one user's name, email, and assigned roles. */
@Composable
private fun AdminProfileCard(profile: User) {
    val displayName = profile.name.ifBlank { "Unnamed profile" }
    val initials = displayName.trim()
        .split(Regex("\\s+"))
        .mapNotNull { it.firstOrNull()?.uppercase() }
        .joinToString("")
        .take(2)

    val roleText = profile.roles.joinToString(", ") {
        it.name.lowercase().replaceFirstChar { char -> char.uppercase() }
    }.ifBlank { "No roles assigned" }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = CardBackground,
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(AvatarBackground, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = PrimaryColor
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = displayName,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = profile.email.ifBlank { "No email provided" },
                    fontSize = 11.sp,
                    color = SecondaryText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = roleText,
                    fontSize = 11.sp,
                    color = PrimaryColor
                )
            }
        }
    }
}