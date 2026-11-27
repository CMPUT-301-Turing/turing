package com.turing.campsite.ui.screens.owner

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.turing.campsite.PrimaryColor
import com.turing.campsite.data.model.Lottery
import com.turing.campsite.ui.components.CampsiteCard
import com.turing.campsite.ui.viewmodel.LotteryViewModel

@Composable
fun OwnerActiveLotteriesScreen(
    lotteryViewModel: LotteryViewModel,
    ownerId: String,
    onBackClick: () -> Unit,
    onLotteryClick: (String) -> Unit
){
    TODO()
    OwnerActiveLotteriesScreen(
        lotteries = lotteryViewModel.lotteries,
        onBackClick = onBackClick,
        onLotteryClick = onLotteryClick
    )
}

@Composable
fun OwnerActiveLotteriesScreen(
    lotteries: List<Lottery>,
    onBackClick: () -> Unit,
    onLotteryClick: (String) -> Unit
){
    TODO()
    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.size(36.dp)
        ){
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint =PrimaryColor
            )
        }

        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Active Lotteries",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryColor
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        //Spacer(modifer = Modifier.height(10.dp))



    }
}