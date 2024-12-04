package com.eventapp.ui.components

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.eventapp.model.Event
import com.eventapp.viewmodel.EventViewModel
import kotlinx.coroutines.flow.StateFlow

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
    val listState = rememberLazyListState()
    val events by remember { derivedStateOf {
        when (val state = eventsState.value) {
            is EventViewModel.EventsUiState.Success -> state.events
            is EventViewModel.EventsUiState.Append -> state.events
            else -> emptyList()
        }
    }}

    // Infinite scroll logic
    val endOfListReached by remember {
        derivedStateOf {
            listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == listState.layoutInfo.totalItemsCount - 1
        }
    }

    // Trigger loading more events when reaching end of list
    LaunchedEffect(endOfListReached) {
        if (endOfListReached) {
            viewModel.fetchEvents()
        }
    }

    // Observe UI state
    val uiState by eventsState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        when (val state = uiState) {
            is EventViewModel.EventsUiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
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

/**
 * Individual Event List Item Composable
 */
@Composable
fun EventListItem(
    event: Event,
    onEventClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onEventClick)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = event.name,
                style = MaterialTheme.typography.headlineLarge
            )
            Text(
                text = event.description,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Date: ${event.dateTime}",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = "Price: $${event.price}",
                style = MaterialTheme.typography.headlineMedium
            )
        }

        // Favorite/Like Button
        IconButton(onClick = onFavoriteClick) {
            Icon(
                imageVector = if (event.isFavorite) Icons.Filled.Favorite
                else Icons.Outlined.FavoriteBorder,
                contentDescription = "Favorite"
            )
        }
    }
}

