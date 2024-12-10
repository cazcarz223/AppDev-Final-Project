package com.eventapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.eventapp.model.Event
import com.eventapp.viewmodel.EventViewModel
import kotlinx.coroutines.flow.StateFlow

@Composable
fun EventListItem(
    event: Event,
    onEventClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onEventClick)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = event.name,
                style = MaterialTheme.typography.headlineSmall
            )
            IconButton(onClick = onFavoriteClick) {
                Icon(
                    imageVector = Icons.Outlined.FavoriteBorder,
                    contentDescription = "Favorite"
                )
            }
        }
        Text(
            text = event.description,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "Date: ${event.dateTime}",
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = "Price: $${event.price}",
            style = MaterialTheme.typography.bodySmall
        )

        // Add a "Purchase Ticket" button
        Button(
            onClick = { /* No action */ },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Purchase Ticket")
        }

        // Add a "Share" button
        IconButton(
            onClick = { /* No action */ },
            modifier = Modifier.align(Alignment.End)
        ) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "Share"
            )
        }
    }
}
/**
 * Composable for displaying a list of events with infinite scrolling
 * @param viewModel ViewModel to manage event-related operations
 * @param eventsState StateFlow representing the current state of events
 * @param onEventClick Callback when an event is clicked
 * @param onFavoriteClick Callback when favorite button is clicked
 */
@Composable
fun EventListScreen(
    viewModel: EventViewModel,
    eventsState: StateFlow<EventViewModel.EventsUiState>,
    onEventClick: (Event) -> Unit,
    onFavoriteClick: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

        val events by remember {
            derivedStateOf {
                when (val state = eventsState.value) {
                    is EventViewModel.EventsUiState.Success -> state.events
                    is EventViewModel.EventsUiState.Append -> state.events
                    else -> emptyList()
                }
            }
        }


        // Observe UI state
        val uiState by eventsState.collectAsState()
    Column {
        // Add a search bar
        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = { Text("Search events...") },
            trailingIcon = {
                IconButton(onClick = { /* No action */ }) {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                }
            }
        )
        Box(modifier = Modifier.fillMaxSize()) {
            when (val state = uiState) {
                is EventViewModel.EventsUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is EventViewModel.EventsUiState.Success -> {
                    LazyColumn {
                        items(state.events) { event ->
                            EventListItem(
                                event = event,
                                onEventClick = { onEventClick(event) },
                                onFavoriteClick = { onFavoriteClick(event.id) }
                            )
                        }
                    }
                }

                is EventViewModel.EventsUiState.Error -> {
                    Text(
                        text = "Error: ${state.message}",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is EventViewModel.EventsUiState.Empty -> {
                    Text(
                        text = "No events found",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }


                else -> {
                    LazyColumn(
                        state = listState,
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(events) { event ->
                            EventListItem(
                                event = event,
                                onEventClick = { onEventClick(event) },
                                onFavoriteClick = { onFavoriteClick(event.id) }
                            )
                            Divider()
                        }

                        // Loading indicator at the bottom
                        if (uiState is EventViewModel.EventsUiState.Append) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}




