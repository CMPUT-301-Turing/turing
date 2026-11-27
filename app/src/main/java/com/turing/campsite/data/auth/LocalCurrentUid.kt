package com.turing.campsite.data.auth

import androidx.compose.runtime.staticCompositionLocalOf

/**
 * The uid of the signed-in phone, available to every screen.
 *
 * TuringCampsiteApp provides it once sign-in has finished, so screens read
 * LocalCurrentUid.current instead of asking Firebase themselves. It is an
 * empty string when nothing provides it, for example in screen tests.
 */
val LocalCurrentUid = staticCompositionLocalOf { "" }
