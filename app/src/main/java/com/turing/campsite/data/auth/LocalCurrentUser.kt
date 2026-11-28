/* AI Assistance Disclosure: OpenAI, ChatGPT (GPT-6), 2026-11-26.
 * Scope: Role-aware view switching using the signed-in profile and shared navigation guards.
 */
package com.turing.campsite.data.auth

import androidx.compose.runtime.staticCompositionLocalOf
import com.turing.campsite.data.model.User

/** Same authenticated profile in every view; selecting a view never grants roles. */
val LocalCurrentUser = staticCompositionLocalOf<User?> { null }
