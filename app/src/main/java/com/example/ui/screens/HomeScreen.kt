package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
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

@Composable
fun HomeScreen(
    tournaments: List<Tournament>,
    currentUser: PlayerProfile? = null,
    onNavigate: (Screen) -> Unit,
    onSelectTournament: (String) -> Unit,
    onRegisterTeam: ((String, String, String, String, List<String>) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedCategory by remember { mutableStateOf<GameMode?>(null) }
    var joiningTournament by remember { mutableStateOf<Tournament?>(null) }
    var viewingRulesTournament by remember { mutableStateOf<Tournament?>(null) }
    var spectatingTournament by remember { mutableStateOf<Tournament?>(null) }
    var credentialTournament by remember { mutableStateOf<Tournament?>(null) }

    val liveTourneys = tournaments.filter { it.status == TournamentStatus.LIVE || it.roomActive }
    val primaryLiveTourney = liveTourneys.firstOrNull()

    val upcomingTournaments = tournaments.filter {
        it.status != TournamentStatus.COMPLETED && (selectedCategory == null || it.mode == selectedCategory)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(bottom = 24.dp)
    ) {
        // Hero Esports Banner
        item {
            HeroBanner(
                liveTourney = primaryLiveTourney,
                onExploreClicked = { onNavigate(Screen.TournamentList) },
                onJoinLive = {
                    if (primaryLiveTourney != null) {
                        onNavigate(Screen.MatchRoom(primaryLiveTourney.id))
                    } else {
                        onNavigate(Screen.TournamentList)
                    }
                }
            )
        }

        // Live Matches Section
        item {
            LiveMatchesSection(
                liveTourneys = liveTourneys,
                onSpectate = { spectatingTournament = it },
                onViewCredentials = { credentialTournament = it },
                onEnterRoom = { onNavigate(Screen.MatchRoom(it.id)) }
            )
        }

        // Category Filter Pills
        item {
            CategoryFilterBar(
                selectedCategory = selectedCategory,
                onSelectCategory = { selectedCategory = it }
            )
        }

        // Upcoming Tournaments Section Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Whatshot,
                        contentDescription = null,
                        tint = FFOrange,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "UPCOMING TOURNAMENTS",
                        color = TextWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(FFOrange.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${upcomingTournaments.size} ACTIVE",
                            color = FFYellow,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Text(
                    text = "All Events →",
                    color = FFYellow,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable { onNavigate(Screen.TournamentList) }
                        .testTag("view_all_tournaments_btn")
                )
            }
        }

        // Upcoming Tournaments Vertical Cards
        items(upcomingTournaments) { tourney ->
            UpcomingTournamentCard(
                tournament = tourney,
                onCardClick = { onSelectTournament(tourney.id) },
                onJoinClick = { joiningTournament = tourney },
                onRulesClick = { viewingRulesTournament = tourney }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Quick Rules & Booyah Point System Card
        item {
            BooyahPointSystemCard(
                onLeaderboardClick = { onNavigate(Screen.Leaderboard) }
            )
        }

        // Platform Highlights
        item {
            PlatformHighlightsSection(
                onRoomPassClick = { onNavigate(Screen.MatchRoom(null)) },
                onRulesClick = {
                    Toast.makeText(
                        context,
                        "Anti-Cheat 3.0 Active: Device fingerprint verified, Emulators blocked.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        }
    }

    // Dialog: Join Tournament Registration
    joiningTournament?.let { tourney ->
        JoinTournamentDialog(
            tournament = tourney,
            currentUser = currentUser,
            onDismiss = { joiningTournament = null },
            onConfirm = { teamName, ign, uid, members ->
                onRegisterTeam?.invoke(tourney.id, teamName, ign, uid, members)
                Toast.makeText(context, "Registered $teamName for ${tourney.title}!", Toast.LENGTH_LONG).show()
                joiningTournament = null
            }
        )
    }

    // Dialog: Rules & Prize Breakdown
    viewingRulesTournament?.let { tourney ->
        RulesAndPrizeDialog(
            tournament = tourney,
            onDismiss = { viewingRulesTournament = null }
        )
    }

    // Dialog: Live Match Spectate
    spectatingTournament?.let { tourney ->
        LiveSpectateDialog(
            tournament = tourney,
            onDismiss = { spectatingTournament = null },
            onOpenMatchRoom = {
                spectatingTournament = null
                onNavigate(Screen.MatchRoom(tourney.id))
            }
        )
    }

    // Dialog: Room Credentials
    credentialTournament?.let { tourney ->
        RoomCredentialsDialog(
            tournament = tourney,
            onDismiss = { credentialTournament = null }
        )
    }
}

@Composable
private fun HeroBanner(
    liveTourney: Tournament?,
    onExploreClicked: () -> Unit,
    onJoinLive: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, DarkBorderHighlight, RoundedCornerShape(16.dp))
            .background(DarkSurface)
    ) {
        // Hero Background Image with dark overlay
        Image(
            painter = painterResource(id = R.drawable.banner_ff_hero),
            contentDescription = "Esports Battle Arena Banner",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )

        // Gradient scrim for maximum readability
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.4f),
                            Color.Black.copy(alpha = 0.85f),
                            DarkSurface
                        )
                    )
                )
        )

        // Banner Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(FFOrange.copy(alpha = 0.9f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "SEASON 2026 QUALIFIERS",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black.copy(alpha = 0.6f))
                        .border(1.dp, FFYellow.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🏆", fontSize = 11.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "215,000+ COINS POOL",
                            color = FFGold,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            Text(
                text = "DOMINATE THE BATTLEFIELD",
                color = FFYellow,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp
            )

            Text(
                text = "Join daily competitive custom matches, prove your squad's skill, and claim the BOOYAH crown!",
                color = TextWhite.copy(alpha = 0.9f),
                fontSize = 12.sp,
                lineHeight = 16.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onJoinLive,
                    colors = ButtonDefaults.buttonColors(containerColor = FFOrange),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("hero_join_btn")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Whatshot,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (liveTourney != null) "ENTER LIVE ROOM" else "BROWSE MATCHES",
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }

                OutlinedButton(
                    onClick = onExploreClicked,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = FFYellow),
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(FFYellow, FFAmber))),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("hero_schedule_btn")
                ) {
                    Text(
                        text = "ALL SCHEDULES",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = FFYellow
                    )
                }
            }
        }
    }
}

@Composable
fun LiveMatchesSection(
    liveTourneys: List<Tournament>,
    onSpectate: (Tournament) -> Unit,
    onViewCredentials: (Tournament) -> Unit,
    onEnterRoom: (Tournament) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(GamingRed)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "LIVE MATCHES",
                    color = GamingRed,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(GamingRed.copy(alpha = 0.2f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (liveTourneys.isNotEmpty()) "${liveTourneys.size} ACTIVE" else "0 LIVE",
                        color = GamingRed,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (liveTourneys.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkSurface)
                    .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.Timer,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Next Match Starts at 8:00 PM IST",
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "Check upcoming tournaments below to register your squad.",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }
        } else {
            liveTourneys.forEach { tourney ->
                LiveMatchCard(
                    tournament = tourney,
                    onSpectate = { onSpectate(tourney) },
                    onCredentials = { onViewCredentials(tourney) },
                    onEnter = { onEnterRoom(tourney) }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
fun LiveMatchCard(
    tournament: Tournament,
    onSpectate: () -> Unit,
    onCredentials: () -> Unit,
    onEnter: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, GamingGreen.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
            .testTag("live_match_card_${tournament.id}"),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(GamingGreen)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "LIVE NOW",
                            color = Color.Black,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Room #${tournament.roomId ?: "8492019"}",
                        color = FFYellow,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Visibility,
                        contentDescription = null,
                        tint = FFGold,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "1,420 Spectators",
                        color = FFGold,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = tournament.title,
                color = TextWhite,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                ModeChip(mode = tournament.mode)
                Spacer(modifier = Modifier.width(6.dp))
                MapChip(map = tournament.map)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "• Phase 4 • 18 Alive",
                    color = GamingGreen,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onSpectate,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = FFYellow),
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(FFYellow, FFAmber))),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Filled.LiveTv,
                        contentDescription = null,
                        tint = FFYellow,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Spectate", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onCredentials,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(FFOrange, FFFlame))),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Key,
                        contentDescription = null,
                        tint = FFOrange,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Room Pass", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onEnter,
                    colors = ButtonDefaults.buttonColors(containerColor = GamingGreen),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Enter Room", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.Black)
                }
            }
        }
    }
}

@Composable
fun CategoryFilterBar(
    selectedCategory: GameMode?,
    onSelectCategory: (GameMode?) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Text(
            text = "FILTER BATTLE MODE",
            color = TextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                FilterPill(
                    label = "🔥 All Modes",
                    isSelected = selectedCategory == null,
                    onClick = { onSelectCategory(null) }
                )
            }
            item {
                FilterPill(
                    label = "⚔️ Squad (4v4)",
                    isSelected = selectedCategory == GameMode.SQUAD,
                    onClick = { onSelectCategory(GameMode.SQUAD) }
                )
            }
            item {
                FilterPill(
                    label = "👥 Duo (2v2)",
                    isSelected = selectedCategory == GameMode.DUO,
                    onClick = { onSelectCategory(GameMode.DUO) }
                )
            }
            item {
                FilterPill(
                    label = "🎯 Solo Rush",
                    isSelected = selectedCategory == GameMode.SOLO,
                    onClick = { onSelectCategory(GameMode.SOLO) }
                )
            }
            item {
                FilterPill(
                    label = "⚡ Clash Squad",
                    isSelected = selectedCategory == GameMode.CLASH_SQUAD,
                    onClick = { onSelectCategory(GameMode.CLASH_SQUAD) }
                )
            }
        }
    }
}

@Composable
private fun FilterPill(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) FFOrange else DarkSurfaceElevated)
            .border(
                1.dp,
                if (isSelected) FFYellow else DarkBorder,
                RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.White else TextMuted,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold
        )
    }
}

@Composable
fun UpcomingTournamentCard(
    tournament: Tournament,
    onCardClick: () -> Unit,
    onJoinClick: () -> Unit,
    onRulesClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, DarkBorder, RoundedCornerShape(14.dp))
            .clickable(onClick = onCardClick)
            .testTag("upcoming_card_${tournament.id}"),
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: status & mode & map
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StatusBadge(status = tournament.status)
                    Spacer(modifier = Modifier.width(6.dp))
                    ModeChip(mode = tournament.mode)
                }

                MapChip(map = tournament.map)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Title & Schedule Time
            Text(
                text = tournament.title,
                color = TextWhite,
                fontWeight = FontWeight.Black,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Timer,
                    contentDescription = null,
                    tint = FFAmber,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${tournament.date} • ${tournament.scheduleTime}",
                    color = FFAmber,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Prize Pool & Entry Fee Banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(DarkSurfaceElevated)
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
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
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "KILL BOUNTY", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = "₹${tournament.perKillBonusCoins} (🪙 ${tournament.perKillBonusCoins})",
                        color = FFFlame,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "ENTRY FEE", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = if (tournament.entryFeeCoins == 0) "FREE ENTRY" else "₹${tournament.entryFeeCoins} (🪙 ${tournament.entryFeeCoins})",
                        color = if (tournament.entryFeeCoins == 0) GamingGreen else FFOrange,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Slots filled progress
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${tournament.currentSlots}/${tournament.maxSlots} Slots Filled",
                    color = TextWhite,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                val slotsRemaining = tournament.maxSlots - tournament.currentSlots
                Text(
                    text = if (slotsRemaining <= 0) "FULL" else "⚡ Only $slotsRemaining spots left!",
                    color = if (slotsRemaining <= 2) FFFlame else FFYellow,
                    fontWeight = FontWeight.ExtraBold,
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
                color = if (tournament.isFull) GamingGreen else FFOrange,
                trackColor = DarkSurfaceElevated
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Clickable Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onRulesClick,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = FFYellow),
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(FFYellow, FFAmber))),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Rules & Prize", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onJoinClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (tournament.isFull) TextMuted else FFOrange
                    ),
                    enabled = !tournament.isFull,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1.2f)
                ) {
                    Icon(
                        imageVector = Icons.Filled.SportsEsports,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (tournament.isFull) "SLOTS FULL" else "JOIN MATCH",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun JoinTournamentDialog(
    tournament: Tournament,
    currentUser: PlayerProfile?,
    onDismiss: () -> Unit,
    onConfirm: (teamName: String, leaderIgn: String, leaderUid: String, members: List<String>) -> Unit
) {
    var teamName by remember { mutableStateOf("Vampire Squad") }
    var leaderIgn by remember { mutableStateOf(currentUser?.ign ?: "Vampire★FF") }
    var leaderUid by remember { mutableStateOf(currentUser?.uid ?: "182939401") }
    var member2 by remember { mutableStateOf("SniperGhost") }
    var member3 by remember { mutableStateOf("RushMaster") }
    var member4 by remember { mutableStateOf("FlankerPro") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurfaceElevated,
        title = {
            Column {
                Text(
                    text = "REGISTER FOR MATCH",
                    color = FFYellow,
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp
                )
                Text(
                    text = tournament.title,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Entry info
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(DarkSurface)
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Entry Fee:", color = TextMuted, fontSize = 12.sp)
                    Text(
                        text = if (tournament.entryFeeCoins == 0) "FREE ENTRY" else "₹${tournament.entryFeeCoins} (🪙 ${tournament.entryFeeCoins})",
                        color = if (tournament.entryFeeCoins == 0) GamingGreen else FFOrange,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                OutlinedTextField(
                    value = teamName,
                    onValueChange = { teamName = it },
                    label = { Text("Team / Squad Name", color = TextMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = FFOrange,
                        unfocusedBorderColor = DarkBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = leaderIgn,
                    onValueChange = { leaderIgn = it },
                    label = { Text("Leader Free Fire IGN", color = TextMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = FFOrange,
                        unfocusedBorderColor = DarkBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = leaderUid,
                    onValueChange = { leaderUid = it },
                    label = { Text("Leader FF UID (e.g. 182939401)", color = TextMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = FFOrange,
                        unfocusedBorderColor = DarkBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                if (tournament.mode == GameMode.SQUAD) {
                    Text(
                        text = "Squad Teammate IGNs:",
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    OutlinedTextField(
                        value = member2,
                        onValueChange = { member2 = it },
                        label = { Text("Player 2 IGN", color = TextMuted) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = FFOrange,
                            unfocusedBorderColor = DarkBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val members = if (tournament.mode == GameMode.SQUAD) listOf(member2, member3, member4) else emptyList()
                    onConfirm(teamName, leaderIgn, leaderUid, members)
                },
                colors = ButtonDefaults.buttonColors(containerColor = FFOrange)
            ) {
                Text(text = "Confirm Registration", fontWeight = FontWeight.Bold, color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel", color = TextMuted)
            }
        }
    )
}

@Composable
fun RulesAndPrizeDialog(
    tournament: Tournament,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurfaceElevated,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.EmojiEvents,
                    contentDescription = null,
                    tint = FFGold,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "PRIZES & RULES",
                    color = FFYellow,
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = tournament.title,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                // Prize Breakdown
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkSurface)
                        .padding(10.dp)
                ) {
                    val firstPlace = tournament.prizeBreakdown.getOrNull(0)?.prizeCoins ?: (tournament.prizePoolCoins * 0.5).toInt()
                    val secondPlace = tournament.prizeBreakdown.getOrNull(1)?.prizeCoins ?: (tournament.prizePoolCoins * 0.3).toInt()
                    val thirdPlace = tournament.prizeBreakdown.getOrNull(2)?.prizeCoins ?: (tournament.prizePoolCoins * 0.2).toInt()

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(text = "PRIZE DISTRIBUTION (REAL CASH)", color = FFYellow, fontWeight = FontWeight.Black, fontSize = 11.sp)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "🥇 1st (BOOYAH)", color = FFGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text(text = "₹$firstPlace (🪙 $firstPlace)", color = FFGold, fontWeight = FontWeight.Black, fontSize = 12.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "🥈 2nd Place", color = TextWhite, fontSize = 12.sp)
                            Text(text = "₹$secondPlace (🪙 $secondPlace)", color = TextWhite, fontSize = 12.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "🥉 3rd Place", color = TextMuted, fontSize = 12.sp)
                            Text(text = "₹$thirdPlace (🪙 $thirdPlace)", color = TextMuted, fontSize = 12.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "🎯 Kill Bounty", color = FFFlame, fontSize = 12.sp)
                            Text(text = "₹${tournament.perKillBonusCoins} (🪙 ${tournament.perKillBonusCoins})/kill", color = FFFlame, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }

                // Esports Match Rules
                Text(text = "MATCH RULES", color = FFOrange, fontWeight = FontWeight.Black, fontSize = 12.sp)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "• Gun Attributes: OFF (Fair play competitive)", color = TextWhite, fontSize = 11.sp)
                    Text(text = "• Platform: Mobile Only (No Emulators/PC allowed)", color = TextWhite, fontSize = 11.sp)
                    Text(text = "• Room Details: Published 15 minutes before match start", color = TextWhite, fontSize = 11.sp)
                    Text(text = "• Screenshot: Take end-game screenshot to claim kill rewards", color = TextWhite, fontSize = 11.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = FFOrange)
            ) {
                Text(text = "Got It", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    )
}

@Composable
fun LiveSpectateDialog(
    tournament: Tournament,
    onDismiss: () -> Unit,
    onOpenMatchRoom: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurfaceElevated,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(GamingGreen)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "LIVE SPECTATOR ARENA",
                    color = GamingGreen,
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = tournament.title,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )

                // Match stats box
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkSurface)
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "ALIVE TEAMS", color = TextMuted, fontSize = 10.sp)
                        Text(text = "6/12 Squads", color = FFYellow, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Column {
                        Text(text = "ALIVE PLAYERS", color = TextMuted, fontSize = 10.sp)
                        Text(text = "18 Players", color = GamingGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Column {
                        Text(text = "ZONE PHASE", color = TextMuted, fontSize = 10.sp)
                        Text(text = "Phase 4", color = FFFlame, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }

                Text(
                    text = "CURRENT KILL LEADERS",
                    color = FFGold,
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp
                )

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "1. Total Gaming Pro", color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(text = "11 Kills", color = FFGold, fontSize = 12.sp, fontWeight = FontWeight.Black)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "2. Assassin Elite", color = TextWhite, fontSize = 12.sp)
                        Text(text = "8 Kills", color = FFYellow, fontSize = 12.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "3. Booyah Kings", color = TextWhite, fontSize = 12.sp)
                        Text(text = "6 Kills", color = TextMuted, fontSize = 12.sp)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onOpenMatchRoom,
                colors = ButtonDefaults.buttonColors(containerColor = GamingGreen)
            ) {
                Text(text = "Open Match Room", fontWeight = FontWeight.Bold, color = Color.Black)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Close", color = TextMuted)
            }
        }
    )
}

@Composable
fun RoomCredentialsDialog(
    tournament: Tournament,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val roomId = tournament.roomId ?: "8492019"
    val roomPass = tournament.roomPassword ?: "FFBOOYAH"

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurfaceElevated,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Key,
                    contentDescription = null,
                    tint = FFYellow,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "CUSTOM ROOM ACCESS",
                    color = FFYellow,
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Enter these credentials in Free Fire > Custom Room to join:",
                    color = TextMuted,
                    fontSize = 12.sp
                )

                // Room ID Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkSurface)
                        .border(1.dp, DarkBorder, RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "ROOM ID", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text(text = roomId, color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Black)
                        }

                        Button(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("FF Room ID", roomId))
                                Toast.makeText(context, "Room ID copied: $roomId", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = FFOrange),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Icon(imageVector = Icons.Filled.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Copy", fontSize = 11.sp)
                        }
                    }
                }

                // Room Password Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkSurface)
                        .border(1.dp, DarkBorder, RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "PASSWORD", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text(text = roomPass, color = FFGold, fontSize = 18.sp, fontWeight = FontWeight.Black)
                        }

                        Button(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("FF Room Password", roomPass))
                                Toast.makeText(context, "Password copied: $roomPass", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Icon(imageVector = Icons.Filled.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Copy", fontSize = 11.sp, color = TextWhite)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = FFOrange)
            ) {
                Text(text = "Done", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    )
}

@Composable
private fun FeaturedTournamentCard(
    tournament: Tournament,
    onCardClick: () -> Unit,
    onActionClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(260.dp)
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, DarkBorder, RoundedCornerShape(14.dp))
            .clickable(onClick = onCardClick)
            .testTag("featured_card_${tournament.id}"),
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusBadge(status = tournament.status)
                ModeChip(mode = tournament.mode)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = tournament.title,
                color = TextWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                MapChip(map = tournament.map)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "🕒 ${tournament.scheduleTime}",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Prize and Entry Stats Row
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
                    Text(
                        text = "₹${tournament.prizePoolCoins} (🪙 ${tournament.prizePoolCoins})",
                        color = GamingGreen,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "ENTRY FEE", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = if (tournament.entryFeeCoins == 0) "FREE" else "₹${tournament.entryFeeCoins} (🪙 ${tournament.entryFeeCoins})",
                        color = if (tournament.entryFeeCoins == 0) GamingGreen else FFOrange,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Slots filled progress
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Slots",
                    color = TextMuted,
                    fontSize = 11.sp
                )
                Text(
                    text = "${tournament.currentSlots}/${tournament.maxSlots}",
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
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = FFOrange,
                trackColor = DarkSurfaceElevated
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onActionClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (tournament.status == TournamentStatus.LIVE) GamingGreen else FFOrange
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (tournament.status == TournamentStatus.LIVE) "ENTER ROOM" else "VIEW & REGISTER",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = if (tournament.status == TournamentStatus.LIVE) Color.Black else Color.White
                )
            }
        }
    }
}

@Composable
private fun BooyahPointSystemCard(onLeaderboardClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(DarkSurface)
            .border(1.dp, DarkBorder, RoundedCornerShape(14.dp))
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
                        imageVector = Icons.Filled.EmojiEvents,
                        contentDescription = null,
                        tint = FFYellow,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ESPORTS POINT SYSTEM",
                        color = TextWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(FFOrange.copy(alpha = 0.2f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(text = "STANDARD", color = FFOrange, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                PointPill(rank = "#1 BOOYAH", pts = "12 PTS", color = FFGold)
                PointPill(rank = "#2 Rank", pts = "9 PTS", color = TextWhite)
                PointPill(rank = "#3 Rank", pts = "8 PTS", color = TextMuted)
                PointPill(rank = "Per Kill", pts = "+1 PT", color = FFFlame)
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onLeaderboardClick,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = FFYellow),
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(FFYellow, FFAmber))),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("view_leaderboard_btn")
            ) {
                Icon(
                    imageVector = Icons.Filled.EmojiEvents,
                    contentDescription = null,
                    tint = FFYellow,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "VIEW FIRESTORE ESPORTS RANKINGS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun PointPill(rank: String, pts: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(DarkSurfaceElevated)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = rank, color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Medium)
            Text(text = pts, color = color, fontSize = 12.sp, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun PlatformHighlightsSection(
    onRoomPassClick: () -> Unit,
    onRulesClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "WHY PLAY ON FF TOURNAMENT",
            color = TextMuted,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            FeatureHighlightBox(
                icon = Icons.Filled.Security,
                title = "Fair Play Only",
                desc = "No Emulators, strictly anti-cheat verified.",
                onClick = onRulesClick,
                modifier = Modifier.weight(1f)
            )
            FeatureHighlightBox(
                icon = Icons.Filled.Bolt,
                title = "Instant Room Pass",
                desc = "Custom Room credentials delivered right to your app.",
                onClick = onRoomPassClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun FeatureHighlightBox(
    icon: ImageVector,
    title: String,
    desc: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurface)
            .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Column {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = FFYellow,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                color = TextWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = desc,
                color = TextMuted,
                fontSize = 10.sp,
                lineHeight = 14.sp
            )
        }
    }
}
