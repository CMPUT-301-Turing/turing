package com.turing.campsite.ui.screens.entrant


import android.util.Log//
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Terrain
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Button//
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox//
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api//
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme//
import androidx.compose.material3.ModalBottomSheet//
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState//
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope//
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.turing.campsite.PrimaryColor

import com.turing.campsite.ui.components.CampsiteCard
import com.turing.campsite.ui.viewmodel.CampsiteViewModel
import kotlinx.coroutines.launch//

private val PageBackground = Color(0xFFF6F4ED)
private val CardBackground = Color(0xFFFCFBF7)
private val CardBorder = Color(0xFFDFE2D8)
private val SecondaryText = Color(0xFF7A8079)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntrantHomeScreen(
    campsiteViewModel: CampsiteViewModel,
    onCampsiteClick: (String) -> Unit,
) {
    val campsites = campsiteViewModel.campsites
    var searchQuery by remember { mutableStateOf("") }
    var filterLocation by remember { mutableStateOf("") }//
    var tempFilterLocation by remember { mutableStateOf("") }//
    var expanded by remember { mutableStateOf(false) }//
    var guestCountParameter by remember { mutableStateOf("") }//
    var tempFilterGuestCount by remember { mutableStateOf("") }
    var tempFilterGuestCountInt by remember { mutableStateOf(0) }
    var filterActive by remember { mutableStateOf(false) }//

    var filterGuestCount by remember { mutableStateOf(0) }
    var showFilter by remember { mutableStateOf(false) }//
    val scope = rememberCoroutineScope()//
    val sheetState = rememberModalBottomSheetState()//

    LaunchedEffect(Unit) {
        campsiteViewModel.loadAll()
    }

    // get active campsites
    val activeCampsites = campsites.filter { it.statusLabel.equals("Active", ignoreCase = true) }
    var filteredCampsites = activeCampsites
    // filter them based on search

    if (guestCountParameter=="=" && !showFilter){
        filteredCampsites = activeCampsites.filter {
            (it.name.contains(searchQuery, ignoreCase = true) ||
            (it.location.contains(searchQuery, ignoreCase = true))) && (
            it.location.contains(filterLocation, ignoreCase = true) &&(
            it.maxOccupancy == filterGuestCount))
        }
    }else if (guestCountParameter==">=" && !showFilter) {
         filteredCampsites = activeCampsites.filter {
            (it.name.contains(searchQuery, ignoreCase = true) ||
            (it.location.contains(searchQuery, ignoreCase = true))) && (
            it.location.contains(filterLocation, ignoreCase = true) && (
            it.maxOccupancy >= filterGuestCount))
        }
    }else if((guestCountParameter=="<=") && !showFilter){
         filteredCampsites = activeCampsites.filter {
            (it.name.contains(searchQuery, ignoreCase = true) ||
            (it.location.contains(searchQuery, ignoreCase = true))) && (
            it.location.contains(filterLocation, ignoreCase = true) && (
            it.maxOccupancy <= filterGuestCount))
        }
    }else{
         filteredCampsites = activeCampsites.filter {
            (it.name.contains(searchQuery, ignoreCase = true) ||
            (it.location.contains(searchQuery, ignoreCase = true)))&& (
            it.location.contains(filterLocation, ignoreCase = true))
        }
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


                // THE SEARCH BAR IS HERE xd
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        placeholder = {
                            Text(
                                text = "Search campsites",
                                fontSize = 12.sp,
                                color = SecondaryText
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = null,
                                tint = SecondaryText,
                                modifier = Modifier.size(20.dp)
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

                    Surface(
                        modifier = Modifier.size(56.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = PrimaryColor,
                        onClick = {
                            showFilter = true//
                            filterActive=false
                        }//
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
                        text = "${filteredCampsites.size} campsites",
                        fontSize = 11.sp,
                        color = SecondaryText
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))
            }
        }

        // show the filtered list
        items(
            items = filteredCampsites,
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
    if (showFilter){
        ModalBottomSheet(
            onDismissRequest = {showFilter=false},
            sheetState = sheetState,
            containerColor = PageBackground,
            contentColor = SecondaryText
        ) { Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Filter Options",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryColor
                )
                Box(contentAlignment = Alignment.CenterEnd,
                ) {
                    Surface(
                        modifier = Modifier.size(40.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = Color.Red,
                        onClick = {
                            filterActive = false
                            tempFilterGuestCount = ""
                            tempFilterGuestCountInt = 0
                            tempFilterLocation = ""
                            guestCountParameter=""
                            filterLocation = tempFilterLocation
                            filterGuestCount = 0
                        }//
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "trashFilter",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Location: ")
                OutlinedTextField(
                    value = tempFilterLocation,
                    onValueChange = { tempFilterLocation = it },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    placeholder = {
                        Text(
                            text = "Location",
                            fontSize = 12.sp,
                            color = SecondaryText
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
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Guest count: ")
                Box {
                    OutlinedButton(
                        onClick = { expanded = true },
                        shape = RectangleShape,
                        modifier = Modifier.width(70.dp).height(50.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Box(contentAlignment = Alignment.CenterStart,
                            modifier = Modifier.fillMaxWidth().weight(1f),) {
                        Text(guestCountParameter)
                        }
                        Box(contentAlignment = Alignment.CenterEnd,
                            modifier = Modifier.fillMaxWidth().weight(2f),
                            ){
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Open Dropdown",
                                modifier = Modifier.size(20.dp),
                                //contentAlignment = Alignment.Center
                            )
                        }

                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(">=") },
                            onClick = {
                                expanded = false
                                guestCountParameter = ">="

                            }
                        )
                        DropdownMenuItem(
                            text = { Text("=") },
                            onClick = {
                                expanded = false
                                guestCountParameter = "="

                            }
                        )
                        DropdownMenuItem(
                            text = { Text("<=") },
                            onClick = {
                                expanded = false
                                guestCountParameter = "<="

                            }
                        )
                        DropdownMenuItem(
                            text = { Text("") },
                            onClick = {
                                expanded = false
                                guestCountParameter = ""

                            }
                        )
                    }
                }
                OutlinedTextField(
                    value = tempFilterGuestCount,
                    onValueChange = { newText ->
                        if (newText.all { it.isDigit() }) {
                        tempFilterGuestCount = newText
                        tempFilterGuestCountInt = newText.toIntOrNull() ?: 0
                    } },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    placeholder = {
                        Text(
                            text = "Max number of people",
                            fontSize = 12.sp,
                            color = SecondaryText
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

            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = true, onCheckedChange = { /* Update state */ })
                Text("Show only active items")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Apply Button
            Button(
                onClick = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            showFilter = false
                        }
                    }
                    filterLocation=tempFilterLocation
                    filterGuestCount=tempFilterGuestCountInt
                    filterActive= true

                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryColor,
                    contentColor = Color.White
                )
            ) {
                Text("Apply Filters")
            }
            }
        }
    }
}//end of function