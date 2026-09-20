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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Rule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SportsEsports
import com.example.ui.components.PaymentSheetModal
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GameMode
import com.example.data.model.PlayerProfile
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TournamentDetailsScreen(
    tournament: Tournament,
    currentUser: PlayerProfile,
    onBack: () -> Unit,
    onNavigate: (Screen) -> Unit,
    onRegisterTeam: (String, String, String, List<String>) -> Pair<Boolean, String>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    var showRegisterDialog by remember { mutableStateOf(false) }

    val isUserRegistered = tournament.registeredTeams.any {
        it.leaderUid == currentUser.uid || it.leaderIgn.equals(currentUser.ign, ignoreCase = true)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        // Navigation Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurface)
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("details_back_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextWhite
                )
            }
            Text(
                text = "TOURNAMENT DETAILS",
                color = TextWhite,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            // Hero Title Card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(DarkSurfaceElevated, DarkSurface)
                            )
                        )
                        .border(1.dp, DarkBorder, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column {
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

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = tournament.title,
                            color = TextWhite,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "📅 ${tournament.scheduleTime}",
                            color = FFYellow,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Stats Grid (Prize Pool, Entry, Kill Bonus, Slots)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            StatBox(
                                title = "PRIZE POOL",
                                value = "₹${tournament.prizePoolCoins}",
                                subtitle = "🪙 ${tournament.prizePoolCoins}",
                                color = GamingGreen,
                                modifier = Modifier.weight(1f)
                            )
                            StatBox(
                                title = "ENTRY FEE",
                                value = if (tournament.entryFeeCoins == 0) "FREE" else "₹${tournament.entryFeeCoins}",
                                subtitle = if (tournament.entryFeeCoins == 0) "0 Coins" else "🪙 ${tournament.entryFeeCoins}",
                                color = if (tournament.entryFeeCoins == 0) GamingGreen else FFOrange,
                                modifier = Modifier.weight(1f)
                            )
                            StatBox(
                                title = "PER KILL",
                                value = "+₹${tournament.perKillBonusCoins}",
                                subtitle = "🪙 ${tournament.perKillBonusCoins}",
                                color = FFAmber,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Slots Bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Registered Teams",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                            Text(
                                text = "${tournament.currentSlots}/${tournament.maxSlots} Slots",
                                color = if (tournament.isFull) GamingGreen else FFYellow,
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
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = FFOrange,
                            trackColor = DarkSurfaceVariant
                        )
                    }
                }
            }

            // Tabs (Overview, Slots/Teams, Rules)
            item {
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
                                "Overview",
                                color = if (selectedTab == 0) FFYellow else TextMuted,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                "Slots (${tournament.currentSlots}/${tournament.maxSlots})",
                                color = if (selectedTab == 1) FFYellow else TextMuted,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = {
                            Text(
                                "Rulebook",
                                color = if (selectedTab == 2) FFYellow else TextMuted,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    )
                }
            }

            // Tab Content
            when (selectedTab) {
                0 -> {
                    // Prize Distribution Section
                    item {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "🏆 PRIZE BREAKDOWN",
                                color = TextWhite,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            tournament.prizeBreakdown.forEach { prize ->
                                PrizeRow(prize = prize)
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "🎯 POINT SYSTEM",
                                color = TextWhite,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            Card(
                                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    PointRow("1st Place (BOOYAH)", "12 Points", FFGold)
                                    HorizontalDivider(color = DarkBorder, thickness = 0.5.dp)
                                    PointRow("2nd Place", "9 Points", TextWhite)
                                    HorizontalDivider(color = DarkBorder, thickness = 0.5.dp)
                                    PointRow("3rd Place", "8 Points", TextWhite)
                                    HorizontalDivider(color = DarkBorder, thickness = 0.5.dp)
                                    PointRow("4th Place", "7 Points", TextMuted)
                                    HorizontalDivider(color = DarkBorder, thickness = 0.5.dp)
                                    PointRow("5th Place", "6 Points", TextMuted)
                                    HorizontalDivider(color = DarkBorder, thickness = 0.5.dp)
                                    PointRow("Each Kill", "1 Point", FFFlame)
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // Registered Teams & Slot List
                    item {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "REGISTERED SQUADS & SLOTS",
                                color = TextWhite,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }

                    items(tournament.registeredTeams) { team ->
                        TeamSlotRow(
                            team = team,
                            isCurrentUser = team.leaderUid == currentUser.uid,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                    }

                    if (tournament.registeredTeams.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No squads registered yet. Be the first to grab Slot 1!",
                                    color = TextMuted,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
                2 -> {
                    // Rules
                    item {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "OFFICIAL ESPORTS RULES",
                                color = TextWhite,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            tournament.rules.forEachIndexed { index, rule ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape)
                                            .background(FFOrange.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${index + 1}",
                                            color = FFOrange,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = rule,
                                        color = TextWhite,
                                        fontSize = 13.sp,
                                        lineHeight = 18.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(DarkSurfaceElevated)
                                    .border(1.dp, FFAmber.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                    .padding(12.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.Security,
                                        contentDescription = null,
                                        tint = FFYellow,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "Anti-Cheat Active: Emulator users and third-party script injectors are automatically flagged and kicked.",
                                        color = TextMuted,
                                        fontSize = 11.sp,
                                        lineHeight = 15.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (isUserRegistered) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkSurfaceElevated)
                            .border(1.dp, FFOrange.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
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
                                Icon(
                                    imageVector = Icons.Filled.NotificationsActive,
                                    contentDescription = null,
                                    tint = FFOrange,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "MATCH ALERT ACTIVE",
                                        color = TextWhite,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                    Text(
                                        text = "You'll get an alert when room credentials unlock",
                                        color = TextMuted,
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    NotificationHelper.sendMatchStartingAlert(
                                        context = context,
                                        tournamentId = tournament.id,
                                        tournamentTitle = tournament.title,
                                        matchTime = tournament.scheduleTime,
                                        roomId = tournament.roomId ?: "8374921",
                                        password = tournament.roomPassword ?: "booyah99"
                                    )
                                    android.widget.Toast.makeText(context, "Match starting notification dispatched!", android.widget.Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = FFOrange),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                modifier = Modifier.height(28.dp).testTag("tourney_test_alert_btn")
                            ) {
                                Text("TEST ALERT", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }

        // Bottom Sticky Action Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurface)
                .border(1.dp, DarkBorder)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isUserRegistered) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "YOU ARE REGISTERED",
                            color = GamingGreen,
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "Assigned Slot in Match Room",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }

                    Button(
                        onClick = { onNavigate(Screen.MatchRoom(tournament.id)) },
                        colors = ButtonDefaults.buttonColors(containerColor = GamingGreen),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("go_to_match_room_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.MeetingRoom,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "GO TO ROOM",
                            color = Color.Black,
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp
                        )
                    }
                } else if (tournament.status == TournamentStatus.COMPLETED) {
                    Button(
                        onClick = onBack,
                        colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceElevated),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Tournament Concluded", color = TextMuted)
                    }
                } else if (tournament.isFull) {
                    Button(
                        onClick = { },
                        enabled = false,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("All Slots Filled", color = TextMuted)
                    }
                } else {
                    Button(
                        onClick = { showRegisterDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = FFOrange),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("register_now_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.SportsEsports,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (tournament.entryFeeCoins == 0) "JOIN TOURNAMENT (FREE)" else "REGISTER TEAM (₹${tournament.entryFeeCoins} / 🪙 ${tournament.entryFeeCoins})",
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }

    // Interactive Registration Dialog
    if (showRegisterDialog) {
        RegistrationModal(
            tournament = tournament,
            currentUser = currentUser,
            onDismiss = { showRegisterDialog = false },
            onConfirm = { teamName, leaderIgn, leaderUid, members ->
                val result = onRegisterTeam(teamName, leaderIgn, leaderUid, members)
                Toast.makeText(context, result.second, Toast.LENGTH_SHORT).show()
                if (result.first) {
                    showRegisterDialog = false
                }
            }
        )
    }
}

@Composable
private fun StatBox(
    title: String,
    value: String,
    color: Color,
    subtitle: String? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(DarkSurfaceElevated)
            .padding(8.dp)
    ) {
        Column {
            Text(text = title, color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, color = color, fontSize = 13.sp, fontWeight = FontWeight.Black)
            if (subtitle != null) {
                Text(text = subtitle, color = TextMuted, fontSize = 9.sp)
            }
        }
    }
}

@Composable
private fun PrizeRow(prize: com.example.data.model.PrizeBreakdown) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(DarkSurface)
            .border(1.dp, DarkBorder, RoundedCornerShape(10.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.EmojiEvents,
                contentDescription = null,
                tint = if (prize.rank.contains("1st")) FFGold else FFOrange,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = prize.rank,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                if (prize.description.isNotBlank()) {
                    Text(
                        text = prize.description,
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(DarkSurfaceElevated)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "₹${prize.prizeCoins}",
                    color = GamingGreen,
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp
                )
                Text(
                    text = "🪙 ${prize.prizeCoins}",
                    color = FFGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp
                )
            }
        }
    }
}

@Composable
private fun PointRow(title: String, points: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title, color = TextWhite, fontSize = 12.sp)
        Text(text = points, color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun TeamSlotRow(
    team: com.example.data.model.TeamRegistration,
    isCurrentUser: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (isCurrentUser) FFOrange.copy(alpha = 0.15f) else DarkSurface)
            .border(
                1.dp,
                if (isCurrentUser) FFOrange else DarkBorder,
                RoundedCornerShape(10.dp)
            )
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isCurrentUser) FFOrange else DarkSurfaceElevated),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "#${team.slotNumber}",
                        color = if (isCurrentUser) Color.Black else FFYellow,
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = team.teamName,
                            color = TextWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        if (isCurrentUser) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "• YOU",
                                color = GamingGreen,
                                fontWeight = FontWeight.Black,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Text(
                        text = "Leader: ${team.leaderIgn} (UID: ${team.leaderUid})",
                        color = TextMuted,
                        fontSize = 11.sp
                    )

                    if (team.members.isNotEmpty()) {
                        Text(
                            text = "Squad: ${team.members.joinToString(", ")}",
                            color = FFGold.copy(alpha = 0.8f),
                            fontSize = 10.sp
                        )
                    }
                }
            }

            Text(
                text = team.registeredAt,
                color = TextDark,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun RegistrationModal(
    tournament: Tournament,
    currentUser: PlayerProfile,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, List<String>) -> Unit
) {
    var teamName by remember { mutableStateOf("${currentUser.ign}'s Squad") }
    var leaderIgn by remember { mutableStateOf(currentUser.ign) }
    var leaderUid by remember { mutableStateOf(currentUser.uid) }
    var member1 by remember { mutableStateOf("") }
    var member2 by remember { mutableStateOf("") }
    var member3 by remember { mutableStateOf("") }

    val isSquadOrDuo = tournament.mode == GameMode.SQUAD || tournament.mode == GameMode.DUO || tournament.mode == GameMode.CLASH_SQUAD

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        shape = RoundedCornerShape(16.dp),
        title = {
            Column {
                Text(
                    text = "REGISTER SQUAD",
                    color = FFYellow,
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp
                )
                Text(
                    text = tournament.title,
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Team Name
                OutlinedTextField(
                    value = teamName,
                    onValueChange = { teamName = it },
                    label = { Text("Team / Squad Name", color = TextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FFOrange,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("reg_team_name_input")
                )

                // Leader IGN
                OutlinedTextField(
                    value = leaderIgn,
                    onValueChange = { leaderIgn = it },
                    label = { Text("Leader Free Fire IGN", color = TextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FFOrange,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("reg_leader_ign_input")
                )

                // Leader UID
                OutlinedTextField(
                    value = leaderUid,
                    onValueChange = { leaderUid = it },
                    label = { Text("Leader Free Fire UID", color = TextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FFOrange,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("reg_leader_uid_input")
                )

                if (isSquadOrDuo) {
                    Text(
                        text = "Squad Members (Optional/IGN)",
                        color = FFGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = member1,
                        onValueChange = { member1 = it },
                        placeholder = { Text("Player 2 IGN / UID", color = TextMuted, fontSize = 12.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FFOrange,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (tournament.mode != GameMode.DUO) {
                        OutlinedTextField(
                            value = member2,
                            onValueChange = { member2 = it },
                            placeholder = { Text("Player 3 IGN / UID", color = TextMuted, fontSize = 12.sp) },
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
                            value = member3,
                            onValueChange = { member3 = it },
                            placeholder = { Text("Player 4 IGN / UID", color = TextMuted, fontSize = 12.sp) },
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
                }

                // Balance summary
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkSurfaceElevated)
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Entry Fee:", color = TextMuted, fontSize = 11.sp)
                        Text(
                            text = if (tournament.entryFeeCoins == 0) "FREE" else "₹${tournament.entryFeeCoins} (🪙 ${tournament.entryFeeCoins})",
                            color = if (tournament.entryFeeCoins == 0) GamingGreen else FFOrange,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        teamName,
                        leaderIgn,
                        leaderUid,
                        listOfNotNull(member1.takeIf { it.isNotBlank() }, member2.takeIf { it.isNotBlank() }, member3.takeIf { it.isNotBlank() })
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = FFOrange),
                modifier = Modifier.testTag("reg_confirm_btn")
            ) {
                Text("Confirm Registration", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel", color = TextMuted)
            }
        }
    )
}
