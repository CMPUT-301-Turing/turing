package com.turing.campsite

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.navigation.NavController
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AdminBrowseImageScreen(navController: NavController){
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ){
        Text(text= "Browse Image screen")
        Row(){
            Button(
                onClick={
                    navController.popBackStack()
                }
            ) {
                Text("back")
            }

        }
    }
}