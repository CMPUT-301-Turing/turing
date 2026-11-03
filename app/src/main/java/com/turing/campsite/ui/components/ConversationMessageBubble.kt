
package com.turing.campsite.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.turing.campsite.PrimaryColor

private val CardBackground = Color(0xFFFCFBF7)
private val CardBorder = Color(0xFFDFE2D8)
private val IncomingBackground = Color(0xFFE8EFE3)
private val SecondaryText = Color(0xFF7A8079)

/**
 * Supported message presentation styles.
 *
 * AUDIT is used in the administrator's read-only history.
 * CHAT is used in owner/entrant conversations.
 */
enum class ConversationBubbleStyle {
    AUDIT,
    CHAT
}

/**
 * Displays an individual conversation message.
 *
 * alignRight determines which side the message appears on.
 *
 * In CHAT mode:
 * - Left messages are incoming.
 * - Right messages are outgoing.
 *
 * In AUDIT mode, existing sender labels and styling
 * are preserved.
 */
@Composable
fun ConversationMessageBubble(
    message: String,
    timestamp: String,
    senderLabel: String,
    alignRight: Boolean,
    style: ConversationBubbleStyle = ConversationBubbleStyle.AUDIT
) {
    val isChat = style == ConversationBubbleStyle.CHAT

    val bubbleColor = when {
        isChat && alignRight -> PrimaryColor
        isChat -> IncomingBackground
        alignRight -> IncomingBackground
        else -> CardBackground
    }

    val messageColor = if (isChat && alignRight) {
        Color.White
    } else {
        PrimaryColor
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (alignRight) {
            Arrangement.End
        } else {
            Arrangement.Start
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(0.84f),
            horizontalAlignment = if (alignRight) {
                Alignment.End
            } else {
                Alignment.Start
            },
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            // Admin audit history shows the sender above the bubble.
            if (!isChat) {
                Text(
                    text = senderLabel,
                    fontSize = 11.sp,
                    color = SecondaryText
                )
            }

            Surface(
                modifier = if (isChat) {
                    Modifier
                } else {
                    Modifier.fillMaxWidth()
                },
                shape = RoundedCornerShape(
                    topStart = 12.dp,
                    topEnd = 12.dp,
                    bottomStart = if (alignRight) 12.dp else 3.dp,
                    bottomEnd = if (alignRight) 3.dp else 12.dp
                ),
                color = bubbleColor,
                border = if (isChat) {
                    null
                } else {
                    BorderStroke(1.dp, CardBorder)
                }
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = message,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        color = messageColor
                    )

                    // Audit timestamps remain inside the bubble.
                    if (!isChat) {
                        Text(
                            text = timestamp,
                            modifier = Modifier.align(Alignment.End),
                            fontSize = 10.sp,
                            color = SecondaryText
                        )
                    }
                }
            }

            // Chat timestamps appear beneath each message.
            if (isChat) {
                Text(
                    text = timestamp,
                    fontSize = 10.sp,
                    color = SecondaryText
                )
            }
        }
    }
}