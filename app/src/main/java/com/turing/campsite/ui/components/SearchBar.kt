package com.turing.campsite.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.turing.campsite.PrimaryColor

private val CardBackground = Color(0xFFFCFBF7)
private val CardBorder = Color(0xFFDFE2D8)
private val SecondaryText = Color(0xFF7A8079)

/** Test tag of the text field inside [SearchBar], so tests can type into it. */
const val SEARCH_FIELD_TAG = "searchField"

/**
 * A search box with a magnifier icon, used at the top of list screens.
 *
 * The screen owns the text: it passes the current [query] in and gets every
 * change back through [onQueryChange], then filters its own list. A small
 * clear button appears once something has been typed.
 *
 * @param query the text typed so far
 * @param onQueryChange called with the new text on every keystroke
 * @param placeholder the hint shown while the box is empty
 * @param modifier size and placement of the box, full width and 48dp tall by default
 * @param iconTint colour of the magnifier icon
 */
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(48.dp),
    iconTint: Color = PrimaryColor
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = CardBackground,
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Row(
            modifier = Modifier.padding(start = 14.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = iconTint
            )

            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .weight(1f)
                    .testTag(SEARCH_FIELD_TAG),
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = 12.sp,
                    color = PrimaryColor
                ),
                cursorBrush = SolidColor(PrimaryColor),
                decorationBox = { innerTextField ->
                    Box(contentAlignment = Alignment.CenterStart) {
                        if (query.isEmpty()) {
                            Text(
                                text = placeholder,
                                fontSize = 12.sp,
                                color = SecondaryText
                            )
                        }
                        innerTextField()
                    }
                }
            )

            if (query.isNotEmpty()) {
                IconButton(
                    onClick = { onQueryChange("") },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = "Clear search",
                        modifier = Modifier.size(16.dp),
                        tint = SecondaryText
                    )
                }
            }
        }
    }
}

/**
 * Checks whether a search query matches any of the given texts.
 *
 * The check ignores case and surrounding spaces. An empty query matches everything.
 *
 * @param query what the user typed
 * @param fields the texts to look in, for example a name and a location
 * @return true if the query is blank or is found inside one of the fields
 */
fun matchesSearch(query: String, vararg fields: String): Boolean {
    val wanted = query.trim()
    if (wanted.isEmpty()) {
        return true
    }
    return fields.any { it.contains(wanted, ignoreCase = true) }
}
