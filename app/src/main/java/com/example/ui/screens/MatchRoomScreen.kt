package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MatchAnnouncement
import com.example.data.model.PlayerProfile
import com.example.data.model.RoomSlot
import com.example.data.model.Tournament
import com.example.data.model.TournamentStatus
import com.example.ui.components.StatusBadge
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkBorderHighlight
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.FFAmber
import com.example.ui.theme.FFFlame
import com.example.ui.theme.FFGold
import com.example.ui.theme.FFOrange
import com.example.ui.theme.FFYellow
import com.example.ui.theme.GamingGreen
import com.example.ui.theme.GamingRed
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.util.NotificationHelper

@Composable
fun MatchRoomScreen(
    tournaments: List<Tournament>,
    selectedTournamentId: String?,
    currentUser: PlayerProfile,
    announcements: List<MatchAnnouncement>,
    roomSlots: List<RoomSlot>,
    onSelectTournament: (String) -> Unit,
    onSubmitProof: (String, Int, Int, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showResultDialog by remember { mutableStateOf(false) }

    // Identify current tournament
    val activeTourney = tournaments.firstOrNull { it.id == selectedTournamentId }
        ?: tournaments.firstOrNull { it.status == TournamentStatus.LIVE }
        ?: tournaments.firstOrNull { it.roomActive }
        ?: tournaments.firstOrNull()

    if (activeTourney == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(DarkBg),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "No active match rooms found", color = TextMuted)
        }
        return
    }

    val isUserRegistered = activeTourney.registeredTeams.any {
        it.leaderUid == currentUser.uid || it.leaderIgn.equals(currentUser.ign, ignoreCase = true)
    }

    val userSlot = roomSlots.firstOrNull { it.isUserSlot }

    fun copyToClipboard(text: String, label: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "$label copied to clipboard!", Toast.LENGTH_SHORT).show()
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Tournament Selector Row
        item {
            Column {
                Text(
                    text = "SELECT MATCH ROOM",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(tournaments) { tourney ->
                        val isSelected = tourney.id == activeTourney.id
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) FFOrange else DarkSurface)
                                .border(1.dp, if (isSelected) FFOrange else DarkBorder, RoundedCornerShape(10.dp))
                                .clickable { onSelectTournament(tourney.id) }
                                .padding(horizontal = 12.dp, vertical = 7.dp)
                                .testTag("room_tab_${tourney.id}")
                        ) {
                            Text(
                                text = tourney.title,
                                color = if (isSelected) Color.White else TextMuted,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // Room Header & Status Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(DarkSurfaceElevated, DarkSurface)
                        )
                    )
                    .border(1.dp, if (activeTourney.status == TournamentStatus.LIVE) GamingGreen else DarkBorder, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatusBadge(status = activeTourney.status)

                        if (userSlot != null) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(FFOrange)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "YOUR SLOT: #${userSlot.slotNumber}",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = activeTourney.title,
                        color = TextWhite,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Map: ${activeTourney.map.label} • Mode: ${activeTourney.mode.label} • Time: ${activeTourney.scheduleTime}",
                        color = TextMuted,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Countdown Timer Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkBg)
                            .border(1.dp, DarkBorder, RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.Timer,
                                    contentDescription = null,
                                    tint = FFYellow,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (activeTourney.status == TournamentStatus.LIVE) "MATCH RUNNING" else "MATCH COUNTDOWN",
                                    color = TextMuted,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Text(
                                text = if (activeTourney.status == TournamentStatus.LIVE) "IN PROGRESS" else "08m : 42s",
                                color = if (activeTourney.status == TournamentStatus.LIVE) GamingGreen else FFGold,
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        // Custom Room Credentials Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(DarkSurface)
                    .border(1.dp, FFOrange.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Key,
                                contentDescription = null,
                                tint = FFYellow,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "CUSTOM ROOM CREDENTIALS",
                                color = TextWhite,
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp
                            )
                        }

                        if (activeTourney.roomId != null) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(GamingGreen.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(text = "UNLOCKED", color = GamingGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (activeTourney.roomId != null && activeTourney.roomPassword != null) {
                        // Room ID Row
                        CredentialRow(
                            label = "CUSTOM ROOM ID",
                            value = activeTourney.roomId,
                            onCopy = { copyToClipboard(activeTourney.roomId, "Room ID") },
                            testTag = "copy_room_id_btn"
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Room Password Row
                        CredentialRow(
                            label = "ROOM PASSWORD",
                            value = activeTourney.roomPassword,
                            onCopy = { copyToClipboard(activeTourney.roomPassword, "Password") },
                            testTag = "copy_password_btn"
                        )
                    } else {
                        // Locked State
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(DarkSurfaceElevated)
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Filled.Lock,
                                    contentDescription = null,
                                    tint = TextMuted,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Room ID & Password will be revealed 15 mins before start",
                                    color = TextWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Admins will generate custom lobby code. Stay tuned!",
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Match Alert System Status Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkSurfaceElevated)
                    .border(1.dp, FFOrange.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(FFOrange.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.NotificationsActive,
                                contentDescription = null,
                                tint = FFOrange,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "MATCH ALERT ACTIVE",
                                color = TextWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "High-priority alert triggers with room credentials",
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Button(
                        onClick = {
                            NotificationHelper.sendMatchStartingAlert(
                                context = context,
                                tournamentId = activeTourney.id,
                                tournamentTitle = activeTourney.title,
                                matchTime = activeTourney.scheduleTime,
                                roomId = activeTourney.roomId ?: "8374921",
                                password = activeTourney.roomPassword ?: "booyah99"
                            )
                            Toast.makeText(context, "Match starting notification dispatched!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = FFOrange),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("test_room_alert_btn")
                    ) {
                        Icon(Icons.Filled.Notifications, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("TEST ALERT", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Black)
                    }
                }
            }
        }

        // How to Join Guide Accordion
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkSurfaceElevated)
                    .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.HelpOutline,
                            contentDescription = null,
                            tint = FFYellow,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "HOW TO ENTER FREE FIRE CUSTOM ROOM",
                            color = FFYellow,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "1. Open Free Fire App on mobile -> Tap Battle Royale Mode Selector.\n2. Tap 'Custom' tab at the bottom right.\n3. Enter Room ID in search box & tap Join.\n4. Input the Room Password.\n5. Strictly sit in your assigned Slot Number to prevent kick!",
                        color = TextWhite,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Live Referee Announcements
        item {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Campaign,
                        contentDescription = null,
                        tint = FFOrange,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "REFEREE ANNOUNCEMENTS",
                        color = TextWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                announcements.forEach { ann ->
                    AnnouncementCard(announcement = ann)
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }
        }

        // Slot Allocation Grid Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "LOBBY SLOT ASSIGNMENTS (${roomSlots.count { it.teamName != null }}/${roomSlots.size})",
                    color = TextWhite,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black
                )

                Button(
                    onClick = { showResultDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = FFOrange),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("submit_score_btn")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Publish,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "REPORT SCORE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        // Room Slot Grid Items
        items(roomSlots) { slot ->
            SlotItemRow(slot = slot)
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Submit Score Dialog
    if (showResultDialog) {
        SubmitScoreModal(
            tournament = activeTourney,
            currentUser = currentUser,
            onDismiss = { showResultDialog = false },
            onSubmit = { kills, placement, notes ->
                onSubmitProof(activeTourney.id, kills, placement, notes)
                Toast.makeText(context, "Score submission sent to Referee for verification!", Toast.LENGTH_LONG).show()
                showResultDialog = false
            }
        )
    }
}

@Composable
private fun CredentialRow(
    label: String,
    value: String,
    onCopy: () -> Unit,
    testTag: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(DarkSurfaceElevated)
            .border(1.dp, DarkBorderHighlight, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = label, color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = value,
                    color = FFGold,
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp,
                    letterSpacing = 1.sp
                )
            }

            Button(
                onClick = onCopy,
                colors = ButtonDefaults.buttonColors(containerColor = FFOrange),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.testTag(testTag)
            ) {
                Icon(
                    imageVector = Icons.Filled.ContentCopy,
                    contentDescription = "Copy",
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "COPY", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Composable
private fun AnnouncementCard(announcement: MatchAnnouncement) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (announcement.isImportant) FFOrange.copy(alpha = 0.12f) else DarkSurface)
            .border(
                1.dp,
                if (announcement.isImportant) FFOrange.copy(alpha = 0.4f) else DarkBorder,
                RoundedCornerShape(8.dp)
            )
            .padding(10.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(if (announcement.isImportant) FFOrange else TextMuted)
                    .padding(top = 4.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = announcement.author,
                        color = if (announcement.isImportant) FFYellow else TextWhite,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = announcement.time,
                        color = TextDark,
                        fontSize = 10.sp
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = announcement.message,
                    color = TextMuted,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }
        }
    }
}

@Composable
private fun SlotItemRow(slot: RoomSlot) {
    val isFilled = slot.teamName != null

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(
                if (slot.isUserSlot) FFOrange.copy(alpha = 0.18f) else DarkSurface
            )
            .border(
                1.dp,
                if (slot.isUserSlot) FFOrange else DarkBorder,
                RoundedCornerShape(10.dp)
            )
            .padding(10.dp)
            .testTag("slot_item_${slot.slotNumber}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (slot.isUserSlot) FFOrange else DarkSurfaceElevated),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "#${slot.slotNumber}",
                        color = if (slot.isUserSlot) Color.White else FFYellow,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = slot.teamName ?: "Empty Slot",
                            color = if (isFilled) TextWhite else TextDark,
                            fontWeight = if (isFilled) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                        if (slot.isUserSlot) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(GamingGreen)
                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "YOU",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 9.sp
                                )
                            }
                        }
                    }

                    if (isFilled && slot.leaderIgn != null) {
                        Text(
                            text = "Leader: ${slot.leaderIgn}",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    } else {
                        Text(
                            text = "Available for contender",
                            color = TextDark,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (isFilled) DarkSurfaceElevated else DarkSurfaceVariant)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = if (isFilled) "RESERVED" else "VACANT",
                    color = if (isFilled) FFYellow else TextDark,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun SubmitScoreModal(
    tournament: Tournament,
    currentUser: PlayerProfile,
    onDismiss: () -> Unit,
    onSubmit: (Int, Int, String) -> Unit
) {
    var killsText by remember { mutableStateOf("") }
    var placementText by remember { mutableStateOf("1") }
    var notesText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        shape = RoundedCornerShape(16.dp),
        title = {
            Text(
                text = "SUBMIT MATCH RESULT",
                color = FFYellow,
                fontWeight = FontWeight.Black,
                fontSize = 16.sp
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Report your placement and kills for verification. The tournament referee reviews all screenshot proofs before releasing prizes.",
                    color = TextMuted,
                    fontSize = 11.sp
                )

                OutlinedTextField(
                    value = killsText,
                    onValueChange = { killsText = it.filter { char -> char.isDigit() } },
                    label = { Text("Total Kills", color = TextMuted) },
                    placeholder = { Text("e.g. 7", color = TextDark) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FFOrange,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("report_kills_input")
                )

                OutlinedTextField(
                    value = placementText,
                    onValueChange = { placementText = it.filter { char -> char.isDigit() } },
                    label = { Text("Rank / Placement (#1 = Booyah)", color = TextMuted) },
                    placeholder = { Text("e.g. 1", color = TextDark) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FFOrange,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("report_placement_input")
                )

                OutlinedTextField(
                    value = notesText,
                    onValueChange = { notesText = it },
                    label = { Text("Screenshot Proof URL / Notes", color = TextMuted) },
                    placeholder = { Text("e.g. Booyah screenshot confirmed by referee", color = TextDark) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FFOrange,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val kills = killsText.toIntOrNull() ?: 0
                    val placement = placementText.toIntOrNull() ?: 1
                    onSubmit(kills, placement, notesText.ifBlank { "Result reported by ${currentUser.ign}" })
                },
                colors = ButtonDefaults.buttonColors(containerColor = FFOrange),
                modifier = Modifier.testTag("submit_result_confirm_btn")
            ) {
                Text("Send Result", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel", color = TextMuted)
            }
        }
    )
}
