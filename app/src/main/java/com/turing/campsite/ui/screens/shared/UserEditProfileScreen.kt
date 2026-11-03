package com.turing.campsite.ui.screens.shared

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.turing.campsite.PrimaryColor

private val PageBackground = Color(0xFFF6F4ED)
private val FieldBackground = Color(0xFFFCFBF7)
private val FieldBorder = Color(0xFFDFE2D8)
private val SecondaryText = Color(0xFF7A8079)

/**
 * Entrant edit profile screen.
 *
 * Profile changes are stored locally until persistence
 * is implemented.
 */
@Composable
fun UserEditProfileScreen(
    onBackClick: () -> Unit
) {
    var username by rememberSaveable {
        mutableStateOf("alex_outdoors")
    }
    var email by rememberSaveable {
        mutableStateOf("alex.outdoors@example.com")
    }
    var phoneNumber by rememberSaveable {
        mutableStateOf("+44 7700 900123")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBackground)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
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

            Spacer(modifier = Modifier.height(22.dp))

            // Heading
            Text(
                text = "Edit profile",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryColor
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Keep your details up to date.",
                fontSize = 12.sp,
                color = SecondaryText
            )

            Spacer(modifier = Modifier.height(26.dp))

            // Editable profile fields
            ProfileInput(
                label = "Username",
                value = username,
                onValueChange = { username = it }
            )

            Spacer(modifier = Modifier.height(18.dp))

            ProfileInput(
                label = "Email",
                value = email,
                onValueChange = { email = it }
            )

            Spacer(modifier = Modifier.height(18.dp))

            ProfileInput(
                label = "Phone number",
                value = phoneNumber,
                onValueChange = { phoneNumber = it }
            )
        }

        // Bottom action buttons
        Spacer(modifier = Modifier.height(12.dp))

        // Save button (visual placeholder until persistence is added)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp),
            shape = RoundedCornerShape(8.dp),
            color = PrimaryColor
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "Save changes",
                    fontSize = 13.sp,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = onBackClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, FieldBorder),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = PrimaryColor
            )
        ) {
            Text(
                text = "Cancel",
                fontSize = 13.sp
            )
        }
    }
}

/**
 * Reusable labelled input field for editing profile details.
 */
@Composable
private fun ProfileInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = PrimaryColor
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            textStyle = TextStyle(
                fontSize = 13.sp,
                color = PrimaryColor
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryColor,
                unfocusedBorderColor = FieldBorder,
                focusedContainerColor = FieldBackground,
                unfocusedContainerColor = FieldBackground
            )
        )
    }
}
