package com.turing.campsite.ui.screens.entrant

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Terrain
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.turing.campsite.ui.components.matchesSearch
import com.turing.campsite.ui.components.SearchBar
import com.turing.campsite.data.model.Campsite
import com.turing.campsite.PrimaryColor

import com.turing.campsite.ui.components.CampsiteCard
import com.turing.campsite.ui.viewmodel.CampsiteViewModel

private val PageBackground = Color(0xFFF6F4ED)
private val CardBackground = Color(0xFFFCFBF7)
private val CardBorder = Color(0xFFDFE2D8)
private val SecondaryText = Color(0xFF7A8079)

@Composable
fun EntrantHomeScreen(
    campsiteViewModel: CampsiteViewModel,
    onCampsiteClick: (String) -> Unit
) {
    val campsites = campsiteViewModel.campsites

    LaunchedEffect(Unit) {
        campsiteViewModel.loadAll()
    }

    // Paused campsites are marked by the active field in the database.
    val activeCampsites = campsites.filter { it.active }

    EntrantHomeContent(
        activeCampsites = activeCampsites,
        onCampsiteClick = onCampsiteClick
    )
}

/**
 * Draws the entrant home screen for a given list of campsites.
 *
 * Kept apart from the view model so it can be shown and tested
 * without the database.
 */
@Composable
fun EntrantHomeContent(
    activeCampsites: List<Campsite>,
    onCampsiteClick: (String) -> Unit
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }

    // Campsites whose name, location or a tag contains what was typed.
    val shownCampsites = activeCampsites.filter {
        matchesSearch(searchQuery, it.name, it.location, *it.tags.toTypedArray())
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBackground),
        contentPadding = PaddingValues(
            horizontal = 20.dp,
            vertical = 20.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Campsites",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryColor
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "A little closer to outside.",
                            fontSize = 12.sp,
                            color = SecondaryText
                        )
                    }

                    Icon(
                        imageVector = Icons.Outlined.Terrain,
                        contentDescription = null,
                        modifier = Modifier.size(42.dp),
                        tint = PrimaryColor
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SearchBar(
                        query = searchQuery,
                        onQueryChange = { searchQuery = it },
                        placeholder = "Search campsites",
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        iconTint = SecondaryText
                    )

                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = PrimaryColor
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Outlined.Tune,
                                contentDescription = "Filters",
                                tint = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Currently active",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryColor
                    )

                    Text(
                        text = "${shownCampsites.size} campsites",
                        fontSize = 11.sp,
                        color = SecondaryText
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))
            }
        }

        items(
            items = shownCampsites,
            key = { it.id }
        ) { campsite ->
            CampsiteCard(
                campsite = campsite,
                onClick = {
                    onCampsiteClick(campsite.id)
                }
            )
        }
    }
}