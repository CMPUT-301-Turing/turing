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
import androidx.compose.runtime.remember
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
private val ErrorColor = Color(0xFFB45345)

/**
 * Edit profile screen for entrants and campsite owners (US 01.07.03).
 *
 * The fields start with the user's saved details. Save checks the fields
 * with validateProfile() and only calls onSaveClick if they are valid.
 * Saving is handled by the caller, so the screen can be tested without Firestore.
 *
 * @param initialName the saved name to start with
 * @param initialEmail the saved email to start with
 * @param initialPhone the saved phone number to start with
 * @param isSaving true while the save is running, disables the save button
 * @param errorMessage what went wrong when loading or saving, or null
 * @param onSaveClick called with the trimmed name, email and phone when they are valid
 * @param onBackClick called when back or cancel is pressed
 */
@Composable
fun UserEditProfileScreen(
    initialName: String,
    initialEmail: String,
    initialPhone: String,
    isSaving: Boolean,
    errorMessage: String?,
    onSaveClick: (name: String, email: String, phone: String) -> Unit,
    onBackClick: () -> Unit
) {
    // Keyed on the initial values so the fields fill in once the profile has loaded
    var name by rememberSaveable(initialName) {
        mutableStateOf(initialName)
    }
    var email by rememberSaveable(initialEmail) {
        mutableStateOf(initialEmail)
    }
    var phoneNumber by rememberSaveable(initialPhone) {
        mutableStateOf(initialPhone)
    }
    var errors by remember {
        mutableStateOf(ProfileErrors())
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
                label = "Name",
                value = name,
                error = errors.name,
                onValueChange = { name = it }
            )

            Spacer(modifier = Modifier.height(18.dp))

            ProfileInput(
                label = "Email",
                value = email,
                error = errors.email,
                onValueChange = { email = it }
            )

            Spacer(modifier = Modifier.height(18.dp))

            ProfileInput(
                label = "Phone number",
                value = phoneNumber,
                error = errors.phone,
                onValueChange = { phoneNumber = it }
            )
        }

        // Error from the last save, if it failed
        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Something went wrong: $errorMessage",
                fontSize = 12.sp,
                color = ErrorColor
            )
        }

        // Bottom action buttons
        Spacer(modifier = Modifier.height(12.dp))

        // Save button, only saves when every field is valid
        Surface(
            onClick = {
                errors = validateProfile(name, email, phoneNumber)
                if (errors.isValid) {
                    onSaveClick(name.trim(), email.trim(), phoneNumber.trim())
                }
            },
            enabled = !isSaving,
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp),
            shape = RoundedCornerShape(8.dp),
            color = PrimaryColor
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = if (isSaving) "Saving..." else "Save changes",
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
 *
 * Shows the error under the field when there is one.
 */
@Composable
private fun ProfileInput(
    label: String,
    value: String,
    error: String?,
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
            isError = error != null,
            supportingText = error?.let { message ->
                { Text(text = message, fontSize = 11.sp, color = ErrorColor) }
            },
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
