package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GameMap
import com.example.data.model.GameMode
import com.example.data.model.Tournament
import com.example.data.model.TournamentStatus
import com.example.ui.components.MapChip
import com.example.ui.components.ModeChip
import com.example.ui.components.StatusBadge
import com.example.ui.navigation.Screen
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkBorderHighlight
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.FFGold
import com.example.ui.theme.FFOrange
import com.example.ui.theme.FFYellow
import com.example.ui.theme.GamingGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@Composable
fun TournamentListScreen(
    tournaments: List<Tournament>,
    onSelectTournament: (String) -> Unit,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedMode by remember { mutableStateOf<GameMode?>(null) }
    var selectedStatus by remember { mutableStateOf<TournamentStatus?>(null) }

    val filteredList = tournaments.filter { tourney ->
        val matchesQuery = searchQuery.isBlank() ||
                tourney.title.contains(searchQuery, ignoreCase = true) ||
                tourney.map.label.contains(searchQuery, ignoreCase = true) ||
                tourney.mode.label.contains(searchQuery, ignoreCase = true)

        val matchesMode = selectedMode == null || tourney.mode == selectedMode
        val matchesStatus = selectedStatus == null || tourney.status == selectedStatus

        matchesQuery && matchesMode && matchesStatus
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        // Search Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search tournaments by map, title...", color = TextMuted, fontSize = 13.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search",
                        tint = FFOrange,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Filled.Clear,
                                contentDescription = "Clear",
                                tint = TextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = DarkSurface,
                    unfocusedContainerColor = DarkSurface,
                    focusedBorderColor = FFOrange,
                    unfocusedBorderColor = DarkBorder,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("tournament_search_input")
            )
        }

        // Mode Filter Pills
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterPill(
                    label = "All Modes",
                    selected = selectedMode == null,
                    onClick = { selectedMode = null }
                )
            }
            items(GameMode.values()) { mode ->
                FilterPill(
                    label = mode.label,
                    selected = selectedMode == mode,
                    onClick = { selectedMode = if (selectedMode == mode) null else mode }
                )
            }
        }

        // Status Filter Pills
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterPill(
                    label = "All Statuses",
                    selected = selectedStatus == null,
                    onClick = { selectedStatus = null }
                )
            }
            items(TournamentStatus.values()) { status ->
                FilterPill(
                    label = status.label,
                    selected = selectedStatus == status,
                    onClick = { selectedStatus = if (selectedStatus == status) null else status }
                )
            }
        }

        // List Count Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${filteredList.size} TOURNAMENTS AVAILABLE",
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            if (selectedMode != null || selectedStatus != null || searchQuery.isNotBlank()) {
                Text(
                    text = "Reset Filters",
                    color = FFYellow,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        selectedMode = null
                        selectedStatus = null
                        searchQuery = ""
                    }
                )
            }
        }

        // Tournaments List
        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "🎯", fontSize = 40.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No Tournaments Found",
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Try adjusting your filters or search terms.",
                        color = TextMuted,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredList) { tourney ->
                    TournamentRowCard(
                        tournament = tourney,
                        onCardClick = { onSelectTournament(tourney.id) },
                        onActionClick = {
                            if (tourney.status == TournamentStatus.LIVE || tourney.roomActive) {
                                onNavigate(Screen.MatchRoom(tourney.id))
                            } else {
                                onSelectTournament(tourney.id)
                            }
                        }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun FilterPill(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (selected) FFOrange else DarkSurfaceElevated)
            .border(
                1.dp,
                if (selected) FFOrange else DarkBorder,
                RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            color = if (selected) Color.White else TextMuted,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
private fun TournamentRowCard(
    tournament: Tournament,
    onCardClick: () -> Unit,
    onActionClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, DarkBorder, RoundedCornerShape(14.dp))
            .clickable(onClick = onCardClick)
            .testTag("tourney_row_${tournament.id}"),
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Badges row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    ModeChip(mode = tournament.mode)
                    MapChip(map = tournament.map)
                }
                StatusBadge(status = tournament.status)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Title
            Text(
                text = tournament.title,
                color = TextWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "📅 ${tournament.scheduleTime} • Per Kill Bonus: 🪙 ${tournament.perKillBonusCoins}",
                color = TextMuted,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Prize & Entry banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(DarkSurfaceElevated)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "PRIZE POOL", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "₹${tournament.prizePoolCoins}",
                            color = GamingGreen,
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "(🪙 ${tournament.prizePoolCoins})",
                            color = FFGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "ENTRY FEE", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = if (tournament.entryFeeCoins == 0) "FREE" else "₹${tournament.entryFeeCoins} (🪙 ${tournament.entryFeeCoins})",
                        color = if (tournament.entryFeeCoins == 0) GamingGreen else FFOrange,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Slots progress
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filled Slots: ${tournament.currentSlots}/${tournament.maxSlots}",
                    color = TextMuted,
                    fontSize = 11.sp
                )
                Text(
                    text = if (tournament.isFull) "FULL" else "${tournament.maxSlots - tournament.currentSlots} left",
                    color = if (tournament.isFull) Color.Red else FFYellow,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            LinearProgressIndicator(
                progress = {
                    if (tournament.maxSlots > 0) tournament.currentSlots.toFloat() / tournament.maxSlots.toFloat() else 0f
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = FFOrange,
                trackColor = DarkSurfaceElevated
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Action button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onActionClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (tournament.status == TournamentStatus.LIVE) GamingGreen else FFOrange
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = if (tournament.status == TournamentStatus.LIVE) "ENTER MATCH ROOM" else "DETAILS & JOIN",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if (tournament.status == TournamentStatus.LIVE) Color.Black else Color.White
                    )
                }
            }
        }
    }
}
