package com.eventapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.eventapp.ui.components.EventListScreen
import com.eventapp.ui.theme.EventAppTheme
import com.eventapp.viewmodel.AuthViewModel
import com.eventapp.viewmodel.EventViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val eventViewModel: EventViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EventAppTheme {
                val authState by authViewModel.authState.collectAsState()
                val eventsState by eventViewModel.eventsState.collectAsState()

                when (authState) {
                    is AuthViewModel.AuthState.Authenticated -> {
                        EventListScreen(
                            eventsState = eventViewModel.eventsState,
                            onEventClick = { /* Handle event click */ },
                            onFavoriteClick = { eventId -> eventViewModel.toggleFavorite(eventId) },
                            viewModel = eventViewModel
                        )
                    }

                    else -> {
                        // Show login/signup screen
                    }
                }
            }
        }
    }
}
                