package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GameMap
import com.example.data.model.GameMode
import com.example.data.model.PrizeBreakdown
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
import com.example.ui.theme.FFFlame
import com.example.ui.theme.FFGold
import com.example.ui.theme.FFOrange
import com.example.ui.theme.FFYellow
import com.example.ui.theme.GamingGreen
import com.example.ui.theme.GamingRed
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    tournaments: List<Tournament>,
    onCreateTournament: (Tournament) -> Unit,
    onEditTournament: (Tournament) -> Unit,
    onDeleteTournament: (String) -> Unit,
    onUpdateRoomCredentials: (String, String, String) -> Unit,
    onUpdateTournamentStatus: (String, TournamentStatus) -> Unit,
    onPostAnnouncement: (String, String, Boolean) -> Unit,
    onBack: () -> Unit,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Manage, 1 = Create Match, 2 = Broadcast

    // State for Quick Room Credentials modal
    var roomCredsTourney by remember { mutableStateOf<Tournament?>(null) }
    var inputRoomId by remember { mutableStateOf("") }
    var inputPassword by remember { mutableStateOf("") }

    // State for Edit Tournament dialog
    var editingTourney by remember { mutableStateOf<Tournament?>(null) }

    // State for Delete Confirmation dialog
    var deletingTourney by remember { mutableStateOf<Tournament?>(null) }

    // Announcement state
    var announceMsg by remember { mutableStateOf("") }
    var announceAuthor by remember { mutableStateOf("Head Referee") }
    var announceImportant by remember { mutableStateOf(true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        // Admin Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurface)
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("admin_back_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextWhite
                )
            }
            Icon(
                imageVector = Icons.Filled.AdminPanelSettings,
                contentDescription = null,
                tint = FFYellow,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "ESPORTS ADMIN CONTROL PANEL",
                    color = TextWhite,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Full Tournament Creator, Editor, Room Dispenser & Cashout",
                    color = TextMuted,
                    fontSize = 10.sp
                )
            }
        }

        // Summary Metric Cards
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AdminMetricCard(
                title = "TOURNAMENTS",
                value = "${tournaments.size}",
                color = FFOrange,
                modifier = Modifier.weight(1f)
            )
            AdminMetricCard(
                title = "LIVE ROOMS",
                value = "${tournaments.count { it.status == TournamentStatus.LIVE || it.roomActive }}",
                color = GamingGreen,
                modifier = Modifier.weight(1f)
            )
            AdminMetricCard(
                title = "JOINED TEAMS",
                value = "${tournaments.sumOf { it.currentSlots }}",
                color = FFGold,
                modifier = Modifier.weight(1f)
            )
        }

        // Navigation Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = DarkSurface,
            contentColor = FFOrange,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = FFOrange
                )
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Text(
                        "MANAGE (${tournaments.size})",
                        color = if (selectedTab == 0) FFYellow else TextMuted,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                },
                modifier = Modifier.testTag("admin_tab_manage")
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Text(
                        "+ CREATE TOURNAMENT",
                        color = if (selectedTab == 1) FFYellow else TextMuted,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                },
                modifier = Modifier.testTag("admin_tab_create")
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = {
                    Text(
                        "BROADCAST",
                        color = if (selectedTab == 2) FFYellow else TextMuted,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                },
                modifier = Modifier.testTag("admin_tab_broadcast")
            )
        }

        when (selectedTab) {
            0 -> {
                // Manage existing tournaments: Edit, Delete, Room ID, Live Toggle
                if (tournaments.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Filled.SportsEsports,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("No tournaments yet", color = TextWhite, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                            Button(
                                onClick = { selectedTab = 1 },
                                colors = ButtonDefaults.buttonColors(containerColor = FFOrange)
                            ) {
                                Text("Create First Tournament", color = Color.White)
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(tournaments, key = { it.id }) { tourney ->
                            AdminTournamentCard(
                                tournament = tourney,
                                onEdit = { editingTourney = tourney },
                                onDelete = { deletingTourney = tourney },
                                onEditRoomCreds = {
                                    roomCredsTourney = tourney
                                    inputRoomId = tourney.roomId ?: "9482710"
                                    inputPassword = tourney.roomPassword ?: "booyah99"
                                },
                                onToggleLive = {
                                    val nextStatus = if (tourney.status == TournamentStatus.LIVE) TournamentStatus.COMPLETED else TournamentStatus.LIVE
                                    onUpdateTournamentStatus(tourney.id, nextStatus)
                                    Toast.makeText(context, "Status set to ${nextStatus.label}", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                }
            }
            1 -> {
                // Create New Tournament Form with Full Controls
                TournamentFormScreen(
                    initialTournament = null,
                    submitButtonText = "PUBLISH TOURNAMENT",
                    onSubmit = { newTourney ->
                        onCreateTournament(newTourney)
                        Toast.makeText(context, "Tournament published successfully!", Toast.LENGTH_SHORT).show()
                        selectedTab = 0
                    }
                )
            }
            2 -> {
                // Post Announcement to match rooms
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "BROADCAST REFEREE ALERT",
                        color = TextWhite,
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp
                    )

                    Text(
                        text = "This alert instantly appears on all contender match room screens and notification trays.",
                        color = TextMuted,
                        fontSize = 11.sp
                    )

                    OutlinedTextField(
                        value = announceAuthor,
                        onValueChange = { announceAuthor = it },
                        label = { Text("Sender Title / Authority", color = TextMuted) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FFOrange,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = announceMsg,
                        onValueChange = { announceMsg = it },
                        label = { Text("Announcement Message", color = TextMuted) },
                        placeholder = { Text("e.g. Custom room created. Check Room ID tab and enter within 5 mins!", color = TextDark) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FFOrange,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("announcement_msg_input")
                    )

                    Button(
                        onClick = {
                            if (announceMsg.isNotBlank()) {
                                onPostAnnouncement(announceMsg, announceAuthor, announceImportant)
                                Toast.makeText(context, "Broadcast dispatched to all contenders!", Toast.LENGTH_SHORT).show()
                                announceMsg = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = FFOrange),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("send_broadcast_btn")
                    ) {
                        Icon(Icons.Filled.Campaign, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("BROADCAST ANNOUNCEMENT", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Modal: Quick Set Room ID & Password (with notification dispatch)
    if (roomCredsTourney != null) {
        val target = roomCredsTourney!!
        AlertDialog(
            onDismissRequest = { roomCredsTourney = null },
            containerColor = DarkSurface,
            shape = RoundedCornerShape(16.dp),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Key, contentDescription = null, tint = FFGold, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("PUBLISH ROOM ID & PASS", color = FFYellow, fontWeight = FontWeight.Black, fontSize = 14.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Tournament: ${target.title}",
                        color = TextWhite,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Saving will send an immediate high-priority notification to all ${target.currentSlots} registered contenders with the room credentials.",
                        color = TextMuted,
                        fontSize = 11.sp
                    )

                    OutlinedTextField(
                        value = inputRoomId,
                        onValueChange = { inputRoomId = it },
                        label = { Text("Free Fire Custom Room ID", color = TextMuted) },
                        placeholder = { Text("e.g. 74829103", color = TextDark) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FFOrange,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_room_id_input")
                    )

                    OutlinedTextField(
                        value = inputPassword,
                        onValueChange = { inputPassword = it },
                        label = { Text("Room Password", color = TextMuted) },
                        placeholder = { Text("e.g. booyah99", color = TextDark) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FFOrange,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_room_pass_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (inputRoomId.isNotBlank()) {
                            onUpdateRoomCredentials(target.id, inputRoomId.trim(), inputPassword.trim())
                            Toast.makeText(context, "Room credentials published & notified to joined players!", Toast.LENGTH_LONG).show()
                            roomCredsTourney = null
                        } else {
                            Toast.makeText(context, "Please enter a valid Room ID", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FFOrange),
                    modifier = Modifier.testTag("admin_save_room_creds_btn")
                ) {
                    Icon(Icons.Filled.NotificationsActive, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Publish & Notify All", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { roomCredsTourney = null }) {
                    Text("Cancel", color = TextMuted)
                }
            }
        )
    }

    // Dialog: Delete Confirmation
    if (deletingTourney != null) {
        val target = deletingTourney!!
        AlertDialog(
            onDismissRequest = { deletingTourney = null },
            containerColor = DarkSurface,
            shape = RoundedCornerShape(16.dp),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Warning, contentDescription = null, tint = GamingRed, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("DELETE TOURNAMENT", color = GamingRed, fontWeight = FontWeight.Black, fontSize = 15.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Are you sure you want to permanently delete \"${target.title}\"?",
                        color = TextWhite,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "This will remove the bracket, prize pool of ₹${target.prizePoolCoins}, and all ${target.currentSlots} registered teams. This action cannot be undone.",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteTournament(target.id)
                        Toast.makeText(context, "Tournament \"${target.title}\" deleted", Toast.LENGTH_SHORT).show()
                        deletingTourney = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GamingRed),
                    modifier = Modifier.testTag("confirm_delete_tourney_btn")
                ) {
                    Icon(Icons.Filled.Delete, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Delete", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { deletingTourney = null }) {
                    Text("Cancel", color = TextMuted)
                }
            }
        )
    }

    // Full Modal: Edit Tournament Dialog
    if (editingTourney != null) {
        val target = editingTourney!!
        AlertDialog(
            onDismissRequest = { editingTourney = null },
            containerColor = DarkBg,
            shape = RoundedCornerShape(16.dp),
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("EDIT TOURNAMENT", color = FFYellow, fontWeight = FontWeight.Black, fontSize = 15.sp)
                    IconButton(onClick = { editingTourney = null }, modifier = Modifier.size(24.dp)) {
                        Text("✕", color = TextMuted, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            },
            text = {
                Box(modifier = Modifier.height(480.dp)) {
                    TournamentFormScreen(
                        initialTournament = target,
                        submitButtonText = "SAVE CHANGES",
                        onSubmit = { updatedTourney ->
                            onEditTournament(updatedTourney)
                            Toast.makeText(context, "Tournament updated successfully!", Toast.LENGTH_SHORT).show()
                            editingTourney = null
                        }
                    )
                }
            },
            confirmButton = {},
            dismissButton = {}
        )
    }
}

@Composable
private fun AdminMetricCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(DarkSurface)
            .border(1.dp, DarkBorder, RoundedCornerShape(10.dp))
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = title, color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, color = color, fontSize = 16.sp, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun AdminTournamentCard(
    tournament: Tournament,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onEditRoomCreds: () -> Unit,
    onToggleLive: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, DarkBorder, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Mode, Map, Status, and Action Icons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    ModeChip(mode = tournament.mode)
                    MapChip(map = tournament.map)
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    StatusBadge(status = tournament.status)
                    // Edit Button
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier
                            .size(28.dp)
                            .testTag("admin_edit_tourney_${tournament.id}")
                    ) {
                        Icon(Icons.Filled.Edit, contentDescription = "Edit", tint = FFOrange, modifier = Modifier.size(16.dp))
                    }
                    // Delete Button
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier
                            .size(28.dp)
                            .testTag("admin_delete_tourney_${tournament.id}")
                    ) {
                        Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = GamingRed, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title
            Text(
                text = tournament.title,
                color = TextWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Info row: Slots, Prize Pool, Entry Fee
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Slots: ${tournament.currentSlots}/${tournament.maxSlots}",
                    color = TextMuted,
                    fontSize = 11.sp
                )
                Text(
                    text = "Prize: ₹${tournament.prizePoolCoins} (🪙 ${tournament.prizePoolCoins})",
                    color = GamingGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
                Text(
                    text = "Fee: ${if (tournament.entryFeeCoins == 0) "FREE" else "₹${tournament.entryFeeCoins} (🪙 ${tournament.entryFeeCoins})"}",
                    color = if (tournament.entryFeeCoins == 0) GamingGreen else FFOrange,
                    fontSize = 11.sp
                )
            }

            // Prize Distribution Summary (1st, 2nd, 3rd)
            val p1 = tournament.prizeBreakdown.getOrNull(0)?.prizeCoins ?: (tournament.prizePoolCoins * 0.5).toInt()
            val p2 = tournament.prizeBreakdown.getOrNull(1)?.prizeCoins ?: (tournament.prizePoolCoins * 0.3).toInt()
            val p3 = tournament.prizeBreakdown.getOrNull(2)?.prizeCoins ?: (tournament.prizePoolCoins * 0.2).toInt()
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(DarkSurfaceElevated)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "🥇 1st: ₹$p1", color = FFGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text(text = "🥈 2nd: ₹$p2", color = TextWhite, fontSize = 10.sp)
                Text(text = "🥉 3rd: ₹$p3", color = TextMuted, fontSize = 10.sp)
                Text(text = "Schedule: ${tournament.scheduleTime}", color = FFYellow, fontSize = 10.sp)
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Room Credentials Box
            if (!tournament.roomId.isNullOrBlank()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(FFOrange.copy(alpha = 0.15f))
                        .border(1.dp, FFOrange.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🔑 Room: ${tournament.roomId} • Pass: ${tournament.roomPassword ?: "None"}",
                        color = FFGold,
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp
                    )
                    Text(
                        text = "LIVE TO PLAYERS",
                        color = GamingGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp
                    )
                }
            } else {
                Text(
                    text = "Room ID not yet set (Contenders awaiting credentials)",
                    color = TextMuted,
                    fontSize = 10.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onEditRoomCreds,
                    colors = ButtonDefaults.buttonColors(containerColor = FFOrange),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1.2f).testTag("admin_set_room_btn_${tournament.id}")
                ) {
                    Icon(Icons.Filled.Key, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (tournament.roomId.isNullOrBlank()) "SET ROOM ID" else "UPDATE ROOM ID",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                OutlinedButton(
                    onClick = onToggleLive,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = if (tournament.status == TournamentStatus.LIVE) "END MATCH" else "START LIVE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (tournament.status == TournamentStatus.LIVE) GamingRed else GamingGreen
                    )
                }
            }
        }
    }
}

/**
 * Reusable Tournament Form used for both Creating and Editing tournaments.
 * Allows setting:
 * - Tournament Name
 * - Prize Pool (custom rupees, 1=1)
 * - Entry Fee (custom coins, 1=1)
 * - Slots (custom number)
 * - Map (Bermuda / Purgatory / Kalahari)
 * - Mode (Solo / Duo / Squad)
 * - Match Time
 * - Room ID and Room Password
 * - Prize Pool distribution: 1st, 2nd, 3rd prize split (custom amounts with quick split presets)
 */
@Composable
private fun TournamentFormScreen(
    initialTournament: Tournament?,
    submitButtonText: String,
    onSubmit: (Tournament) -> Unit
) {
    var title by remember { mutableStateOf(initialTournament?.title ?: "") }
    var selectedMode by remember { mutableStateOf(initialTournament?.mode ?: GameMode.SQUAD) }
    var selectedMap by remember { mutableStateOf(initialTournament?.map ?: GameMap.BERMUDA) }
    var prizeRupees by remember {
        mutableStateOf((initialTournament?.prizePoolCoins ?: 2000).toString())
    }
    var entryFeeCoins by remember {
        mutableStateOf((initialTournament?.entryFeeCoins ?: 20).toString())
    }
    var maxSlots by remember {
        mutableStateOf((initialTournament?.maxSlots ?: 12).toString())
    }
    var scheduleTime by remember {
        mutableStateOf(initialTournament?.scheduleTime ?: "Today, 09:30 PM IST")
    }
    var roomId by remember { mutableStateOf(initialTournament?.roomId ?: "") }
    var roomPassword by remember { mutableStateOf(initialTournament?.roomPassword ?: "") }

    // Prize Split State (1st, 2nd, 3rd)
    val totalPrize = prizeRupees.toIntOrNull() ?: 2000
    var prize1st by remember {
        mutableStateOf(
            (initialTournament?.prizeBreakdown?.getOrNull(0)?.prizeCoins ?: (totalPrize * 0.5).toInt()).toString()
        )
    }
    var prize2nd by remember {
        mutableStateOf(
            (initialTournament?.prizeBreakdown?.getOrNull(1)?.prizeCoins ?: (totalPrize * 0.3).toInt()).toString()
        )
    }
    var prize3rd by remember {
        mutableStateOf(
            (initialTournament?.prizeBreakdown?.getOrNull(2)?.prizeCoins ?: (totalPrize * 0.2).toInt()).toString()
        )
    }

    // Quick helper to recalculate split based on percentages
    fun applySplitPreset(pct1: Double, pct2: Double, pct3: Double) {
        val curTotal = prizeRupees.toIntOrNull() ?: 2000
        prize1st = (curTotal * pct1).toInt().toString()
        prize2nd = (curTotal * pct2).toInt().toString()
        prize3rd = (curTotal * pct3).toInt().toString()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = if (initialTournament == null) "CREATE ESPORTS TOURNAMENT" else "EDIT TOURNAMENT DETAILS",
                color = TextWhite,
                fontWeight = FontWeight.Black,
                fontSize = 14.sp
            )
        }

        // 1. Tournament Name
        item {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Tournament Name / Title", color = TextMuted) },
                placeholder = { Text("e.g. Bermuda Friday Night Clash", color = TextDark) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FFOrange,
                    unfocusedBorderColor = DarkBorder,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_tourney_title_input")
            )
        }

        // 2. Select Mode (Solo / Duo / Squad)
        item {
            Text(text = "Game Mode", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(GameMode.SOLO, GameMode.DUO, GameMode.SQUAD, GameMode.CLASH_SQUAD).forEach { mode ->
                    val isSelected = selectedMode == mode
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) FFOrange else DarkSurface)
                            .border(1.dp, if (isSelected) FFOrange else DarkBorder, RoundedCornerShape(8.dp))
                            .clickable {
                                selectedMode = mode
                                // Automatically update default max slots if user hasn't heavily customized
                                maxSlots = when (mode) {
                                    GameMode.SOLO -> "48"
                                    GameMode.DUO -> "24"
                                    GameMode.SQUAD -> "12"
                                    GameMode.CLASH_SQUAD -> "8"
                                }
                            }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = mode.label,
                            color = if (isSelected) Color.White else TextMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // 3. Select Map (Bermuda / Purgatory / Kalahari)
        item {
            Text(text = "Map", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(GameMap.BERMUDA, GameMap.PURGATORY, GameMap.KALAHARI).forEach { map ->
                    val isSelected = selectedMap == map
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) FFOrange else DarkSurface)
                            .border(1.dp, if (isSelected) FFOrange else DarkBorder, RoundedCornerShape(8.dp))
                            .clickable { selectedMap = map }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = map.label,
                            color = if (isSelected) Color.White else TextMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // 4. Prize Pool (Custom Rupees) & Entry Fee (Custom Coins, 1=1)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = prizeRupees,
                    onValueChange = { input ->
                        val digits = input.filter { it.isDigit() }
                        prizeRupees = digits
                        // Auto update split when total prize changes
                        val newTotal = digits.toIntOrNull() ?: 0
                        prize1st = (newTotal * 0.50).toInt().toString()
                        prize2nd = (newTotal * 0.30).toInt().toString()
                        prize3rd = (newTotal * 0.20).toInt().toString()
                    },
                    label = { Text("Prize Pool (₹ Rupees)", color = TextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FFOrange,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("admin_prize_input")
                )

                OutlinedTextField(
                    value = entryFeeCoins,
                    onValueChange = { entryFeeCoins = it.filter { char -> char.isDigit() } },
                    label = { Text("Entry Fee (🪙 = ₹)", color = TextMuted) },
                    placeholder = { Text("0 = Free", color = TextDark) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FFOrange,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("admin_entry_input")
                )
            }
        }

        // 5. Total Slots & Match Schedule Time
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = maxSlots,
                    onValueChange = { maxSlots = it.filter { char -> char.isDigit() } },
                    label = { Text("Max Slots", color = TextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FFOrange,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    singleLine = true,
                    modifier = Modifier
                        .weight(0.8f)
                        .testTag("admin_slots_input")
                )

                OutlinedTextField(
                    value = scheduleTime,
                    onValueChange = { scheduleTime = it },
                    label = { Text("Match Time", color = TextMuted) },
                    placeholder = { Text("e.g. Today, 09:30 PM", color = TextDark) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FFOrange,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    singleLine = true,
                    modifier = Modifier
                        .weight(1.2f)
                        .testTag("admin_schedule_input")
                )
            }
        }

        // 6. Prize Pool Distribution (1st, 2nd, 3rd Prize Split)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(DarkSurface)
                    .border(1.dp, DarkBorder, RoundedCornerShape(10.dp))
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "PRIZE POOL DISTRIBUTION",
                        color = FFYellow,
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp
                    )
                    val sum = (prize1st.toIntOrNull() ?: 0) + (prize2nd.toIntOrNull() ?: 0) + (prize3rd.toIntOrNull() ?: 0)
                    Text(
                        text = "Total: ₹$sum / ₹${prizeRupees.ifBlank { "0" }}",
                        color = if (sum <= (prizeRupees.toIntOrNull() ?: 0)) GamingGreen else GamingRed,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }

                // Split Preset Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(DarkSurfaceElevated)
                            .border(1.dp, DarkBorder, RoundedCornerShape(6.dp))
                            .clickable { applySplitPreset(0.50, 0.30, 0.20) }
                            .padding(vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("50/30/20%", color = TextWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(DarkSurfaceElevated)
                            .border(1.dp, DarkBorder, RoundedCornerShape(6.dp))
                            .clickable { applySplitPreset(0.60, 0.25, 0.15) }
                            .padding(vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("60/25/15%", color = TextWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(DarkSurfaceElevated)
                            .border(1.dp, DarkBorder, RoundedCornerShape(6.dp))
                            .clickable { applySplitPreset(0.70, 0.20, 0.10) }
                            .padding(vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("70/20/10%", color = TextWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // 1st, 2nd, 3rd custom inputs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OutlinedTextField(
                        value = prize1st,
                        onValueChange = { prize1st = it.filter { char -> char.isDigit() } },
                        label = { Text("🥇 1st (₹)", color = FFGold, fontSize = 10.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FFGold,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = prize2nd,
                        onValueChange = { prize2nd = it.filter { char -> char.isDigit() } },
                        label = { Text("🥈 2nd (₹)", color = TextWhite, fontSize = 10.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FFOrange,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = prize3rd,
                        onValueChange = { prize3rd = it.filter { char -> char.isDigit() } },
                        label = { Text("🥉 3rd (₹)", color = TextMuted, fontSize = 10.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FFOrange,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 7. Room ID & Room Password (Optional upfront or edit)
        item {
            Text(text = "Custom Room Credentials (Optional / Live)", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = roomId,
                    onValueChange = { roomId = it },
                    label = { Text("Room ID", color = TextMuted) },
                    placeholder = { Text("e.g. 8492019", color = TextDark) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FFOrange,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = roomPassword,
                    onValueChange = { roomPassword = it },
                    label = { Text("Room Password", color = TextMuted) },
                    placeholder = { Text("e.g. booyah7", color = TextDark) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FFOrange,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }
            if (roomId.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "🔔 If Room ID is set/changed, notification will be dispatched to all registered contenders automatically.",
                    color = FFGold,
                    fontSize = 10.sp
                )
            }
        }

        // Submit Button
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val finalPrize = prizeRupees.toIntOrNull() ?: 2000
                        val p1Val = prize1st.toIntOrNull() ?: (finalPrize * 0.50).toInt()
                        val p2Val = prize2nd.toIntOrNull() ?: (finalPrize * 0.30).toInt()
                        val p3Val = prize3rd.toIntOrNull() ?: (finalPrize * 0.20).toInt()

                        val breakdown = listOf(
                            PrizeBreakdown("1st Place (BOOYAH)", p1Val, 100, "Championship Trophy + ₹$p1Val Cash"),
                            PrizeBreakdown("2nd Place", p2Val, 70, "Silver Contender + ₹$p2Val Cash"),
                            PrizeBreakdown("3rd Place", p3Val, 50, "Bronze Contender + ₹$p3Val Cash")
                        )

                        val finalTourney = if (initialTournament != null) {
                            initialTournament.copy(
                                title = title.trim(),
                                mode = selectedMode,
                                map = selectedMap,
                                prizePoolCoins = finalPrize,
                                entryFeeCoins = entryFeeCoins.toIntOrNull() ?: 0,
                                maxSlots = maxSlots.toIntOrNull() ?: initialTournament.maxSlots,
                                scheduleTime = scheduleTime.trim(),
                                roomId = roomId.trim().ifBlank { initialTournament.roomId },
                                roomPassword = roomPassword.trim().ifBlank { initialTournament.roomPassword },
                                roomActive = roomId.isNotBlank() || initialTournament.roomActive,
                                prizeBreakdown = breakdown
                            )
                        } else {
                            Tournament(
                                id = "tourney_${System.currentTimeMillis()}",
                                title = title.trim(),
                                mode = selectedMode,
                                map = selectedMap,
                                status = TournamentStatus.UPCOMING,
                                prizePoolCoins = finalPrize,
                                entryFeeCoins = entryFeeCoins.toIntOrNull() ?: 0,
                                perKillBonusCoins = 10,
                                maxSlots = maxSlots.toIntOrNull() ?: 12,
                                scheduleTime = scheduleTime.trim(),
                                roomId = roomId.trim().ifBlank { null },
                                roomPassword = roomPassword.trim().ifBlank { null },
                                roomActive = roomId.isNotBlank(),
                                prizeBreakdown = breakdown
                            )
                        }
                        onSubmit(finalTourney)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = FFOrange),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_submit_tourney_btn")
            ) {
                Icon(if (initialTournament == null) Icons.Filled.Add else Icons.Filled.Edit, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(6.dp))
                Text(submitButtonText, color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}
