package com.eventapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.eventapp.ui.components.EventListScreen
import com.eventapp.ui.theme.EventAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EventAppTheme {
                EventListScreen(
                    viewModel = TODO(),
                    eventsState = TODO(),
                    onEventClick = TODO(),
                    onFavoriteClick = TODO()
                )
            }
        }
    }
}
                