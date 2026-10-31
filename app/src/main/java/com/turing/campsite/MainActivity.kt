package com.turing.campsite

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.turing.campsite.ui.theme.TuringTheme

/**
 * Single activity that hosts the whole Compose UI.
 *
 * Enables edge-to-edge drawing and renders the app content inside [TuringTheme].
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TuringTheme {
                TuringCampsiteApp()
            }
        }
    }
}

val PrimaryColor = Color(0xFF354F45)
val CardColor = Color(0xFFFBFAF6)
val BackgroundColor = Color(0xFFF5F3EA)
val BorderColor = Color(0xFFD7DACA)

/** Primary colour of the dark Material 3 scheme. */
val Purple80 = Color(0xFFD0BCFF)
/** Secondary colour of the dark Material 3 scheme. */
val PurpleGrey80 = Color(0xFFCCC2DC)
/** Tertiary colour of the dark Material 3 scheme. */
val Pink80 = Color(0xFFEFB8C8)

/** Primary colour of the light Material 3 scheme. */
val Purple40 = Color(0xFF6650a4)
/** Secondary colour of the light Material 3 scheme. */
val PurpleGrey40 = Color(0xFF625b71)
/** Tertiary colour of the light Material 3 scheme. */
val Pink40 = Color(0xFF7D5260)