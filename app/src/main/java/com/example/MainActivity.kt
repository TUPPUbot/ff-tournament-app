package com.example

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.data.model.TournamentStatus
import com.example.data.repository.TournamentRepository
import com.example.ui.components.FFBottomBar
import com.example.ui.components.FFTopBar
import com.example.ui.navigation.BottomNavTab
import com.example.ui.navigation.Screen
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LeaderboardScreen
import com.example.ui.screens.MatchRoomScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.TournamentDetailsScreen
import com.example.ui.screens.TournamentListScreen
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.FFOrange
import com.example.ui.theme.FFYellow
import com.example.ui.theme.GamingGreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.util.InAppAlert
import com.example.util.NotificationHelper
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        NotificationHelper.createNotificationChannels(this)

        val navTarget = intent?.getStringExtra("EXTRA_NAVIGATE_TO")
        val tourneyId = intent?.getStringExtra("EXTRA_TOURNAMENT_ID")

        setContent {
            MyApplicationTheme {
                FFTournamentApp(initialNavTarget = navTarget, initialTourneyId = tourneyId)
            }
        }
    }
}

@Composable
fun FFTournamentApp(
    initialNavTarget: String? = null,
    initialTourneyId: String? = null
) {
    val context = LocalContext.current
    val repository = remember { TournamentRepository.getInstance() }
    val tournaments by repository.tournaments.collectAsState()
    val currentUser by repository.currentUser.collectAsState()
    val announcements by repository.announcements.collectAsState()
    val playerLeaderboard = repository.playerLeaderboard
    val guildLeaderboard = repository.guildLeaderboard

    val initialScreen = if (initialNavTarget == "match_room") {
        Screen.MatchRoom(initialTourneyId)
    } else {
        Screen.Home
    }

    var currentScreen by remember { mutableStateOf<Screen>(initialScreen) }
    var previousScreen by remember { mutableStateOf<Screen>(Screen.Home) }
    var selectedTournamentId by remember { mutableStateOf<String?>(initialTourneyId) }

    // In-app alert notification banner state
    var activeInAppAlert by remember { mutableStateOf<InAppAlert?>(null) }

    LaunchedEffect(Unit) {
        NotificationHelper.inAppAlerts.collect { alert ->
            activeInAppAlert = alert
            delay(6500)
            if (activeInAppAlert?.id == alert.id) {
                activeInAppAlert = null
            }
        }
    }

    // Permission launcher for Android 13+
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!NotificationHelper.hasNotificationPermission(context)) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    fun navigateTo(screen: Screen) {
        if (screen != currentScreen) {
            previousScreen = currentScreen
            currentScreen = screen
        }
    }

    fun navigateBack() {
        if (previousScreen != currentScreen) {
            currentScreen = previousScreen
        } else {
            currentScreen = Screen.Home
        }
    }

    // Handle back button
    BackHandler(enabled = currentScreen !is Screen.Home) {
        navigateBack()
    }

    val showBottomBar = currentScreen !is Screen.Auth

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg),
        topBar = {
            if (currentScreen !is Screen.Auth && currentScreen !is Screen.AdminDashboard && currentScreen !is Screen.TournamentDetails) {
                FFTopBar(
                    currentUser = currentUser,
                    currentScreen = currentScreen,
                    onNavigate = { navigateTo(it) }
                )
            }
        },
        bottomBar = {
            if (showBottomBar) {
                val currentTab = when (currentScreen) {
                    is Screen.Home -> BottomNavTab.HOME
                    is Screen.TournamentList, is Screen.TournamentDetails -> BottomNavTab.TOURNAMENTS
                    is Screen.MatchRoom -> BottomNavTab.MY_MATCHES
                    is Screen.Profile -> BottomNavTab.PROFILE
                    else -> BottomNavTab.HOME
                }
                FFBottomBar(
                    currentTab = currentTab,
                    onTabSelected = { tab ->
                        val target = when (tab) {
                            BottomNavTab.HOME -> Screen.Home
                            BottomNavTab.TOURNAMENTS -> Screen.TournamentList
                            BottomNavTab.MY_MATCHES -> Screen.MatchRoom()
                            BottomNavTab.PROFILE -> Screen.Profile
                        }
                        navigateTo(target)
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Real-Time In-App Alert Floating Banner
            AnimatedVisibility(
                visible = activeInAppAlert != null,
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .zIndex(100f)
            ) {
                val alert = activeInAppAlert
                if (alert != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(DarkSurfaceElevated)
                            .border(1.5.dp, if (alert.isMatchAlert) FFOrange else FFYellow, RoundedCornerShape(14.dp))
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (alert.isMatchAlert) FFOrange.copy(alpha = 0.2f) else FFYellow.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (alert.isMatchAlert) Icons.Filled.NotificationsActive else Icons.Filled.Campaign,
                                    contentDescription = null,
                                    tint = if (alert.isMatchAlert) FFOrange else FFYellow,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = alert.title,
                                    color = TextWhite,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = alert.message,
                                    color = TextMuted,
                                    fontSize = 10.sp,
                                    lineHeight = 14.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            if (alert.tournamentId != null) {
                                Button(
                                    onClick = {
                                        activeInAppAlert = null
                                        navigateTo(Screen.MatchRoom(alert.tournamentId))
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = GamingGreen),
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Text("ROOM", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Black)
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                            }

                            IconButton(
                                onClick = { activeInAppAlert = null },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Dismiss",
                                    tint = TextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            when (val screen = currentScreen) {
                is Screen.Home -> {
                    HomeScreen(
                        tournaments = tournaments,
                        currentUser = currentUser,
                        onNavigate = { navigateTo(it) },
                        onSelectTournament = { tourneyId ->
                            selectedTournamentId = tourneyId
                            navigateTo(Screen.TournamentDetails(tourneyId))
                        },
                        onRegisterTeam = { tourneyId, teamName, leaderIgn, leaderUid, members ->
                            val result = repository.registerTeam(tourneyId, teamName, leaderIgn, leaderUid, members)
                            if (result.first) {
                                val t = tournaments.find { it.id == tourneyId }
                                NotificationHelper.sendMatchStartingAlert(
                                    context = context,
                                    tournamentId = tourneyId,
                                    tournamentTitle = t?.title ?: "Registered Match",
                                    matchTime = t?.scheduleTime ?: "Scheduled Time",
                                    roomId = t?.roomId,
                                    password = t?.roomPassword
                                )
                            }
                            result
                        }
                    )
                }
                is Screen.TournamentList -> {
                    TournamentListScreen(
                        tournaments = tournaments,
                        onSelectTournament = { tourneyId ->
                            selectedTournamentId = tourneyId
                            navigateTo(Screen.TournamentDetails(tourneyId))
                        },
                        onNavigate = { navigateTo(it) }
                    )
                }
                is Screen.TournamentDetails -> {
                    val tournament = tournaments.firstOrNull { it.id == screen.tournamentId }
                        ?: tournaments.firstOrNull()

                    if (tournament != null) {
                        TournamentDetailsScreen(
                            tournament = tournament,
                            currentUser = currentUser,
                            onBack = { navigateBack() },
                            onNavigate = { navigateTo(it) },
                            onRegisterTeam = { teamName, leaderIgn, leaderUid, members ->
                                val result = repository.registerTeam(tournament.id, teamName, leaderIgn, leaderUid, members)
                                if (result.first) {
                                    NotificationHelper.sendMatchStartingAlert(
                                        context = context,
                                        tournamentId = tournament.id,
                                        tournamentTitle = tournament.title,
                                        matchTime = tournament.scheduleTime,
                                        roomId = tournament.roomId,
                                        password = tournament.roomPassword
                                    )
                                }
                                result
                            }
                        )
                    } else {
                        navigateTo(Screen.TournamentList)
                    }
                }
                is Screen.MatchRoom -> {
                    val currentTourney = tournaments.firstOrNull { it.id == (screen.tournamentId ?: selectedTournamentId) }
                        ?: tournaments.firstOrNull()
                    val slots = if (currentTourney != null) repository.getRoomSlots(currentTourney) else emptyList()

                    MatchRoomScreen(
                        tournaments = tournaments,
                        selectedTournamentId = screen.tournamentId ?: selectedTournamentId,
                        currentUser = currentUser,
                        announcements = announcements,
                        roomSlots = slots,
                        onSelectTournament = { tourneyId ->
                            selectedTournamentId = tourneyId
                            navigateTo(Screen.MatchRoom(tourneyId))
                        },
                        onSubmitProof = { tourneyId, kills, placement, notes ->
                            repository.submitMatchProof(tourneyId, kills, placement, notes)
                        }
                    )
                }
                is Screen.Leaderboard -> {
                    LeaderboardScreen(
                        playerLeaderboard = playerLeaderboard,
                        guildLeaderboard = guildLeaderboard,
                        currentUser = currentUser,
                        onBack = { navigateBack() }
                    )
                }
                is Screen.Profile -> {
                    ProfileScreen(
                        currentUser = currentUser,
                        tournaments = tournaments,
                        onUpdateProfile = { ign, uid, bio, upiId ->
                            repository.updateProfile(ign, uid, bio, upiId)
                        },
                        onToggleAdmin = { isAdmin ->
                            repository.toggleAdminMode(isAdmin)
                        },
                        onLogout = {
                            navigateTo(Screen.Auth)
                        },
                        onNavigate = { navigateTo(it) },
                        onDepositCoins = { coins, inr, method ->
                            repository.depositCoins(coins, inr, method)
                            NotificationHelper.sendNotification(
                                context = context,
                                title = "💰 Deposit Successful (+🪙 $coins)",
                                message = "Credited ₹$inr to Real Wallet via $method. Good luck in your matches!",
                                isMatchAlert = false,
                                notificationId = (System.currentTimeMillis() % 100000).toInt()
                            )
                        },
                        onWithdrawCoins = { coins, inr, upiId, holderName ->
                            repository.withdrawCoins(coins, inr, upiId, holderName)
                            NotificationHelper.sendNotification(
                                context = context,
                                title = "💸 Cashout Transferred (₹$inr)",
                                message = "Payout dispatched to UPI ID $upiId ($holderName). Processing via IMPS.",
                                isMatchAlert = false,
                                notificationId = (System.currentTimeMillis() % 100000).toInt()
                            )
                        },
                        onUpdateUpiId = { newUpi ->
                            repository.updateUpiId(newUpi)
                        }
                    )
                }
                is Screen.Auth -> {
                    AuthScreen(
                        onLogin = { identifier, uid, isAdmin ->
                            repository.login(identifier, uid, isAdmin)
                        },
                        onSignup = { ign, uid, email ->
                            repository.signup(ign, uid, email)
                        },
                        onClose = { navigateBack() }
                    )
                }
                is Screen.AdminDashboard -> {
                    AdminDashboardScreen(
                        tournaments = tournaments,
                        onCreateTournament = { newTourney ->
                            repository.addTournament(newTourney)
                            NotificationHelper.sendAdminUpdateAlert(
                                context = context,
                                author = "Tournament Organizers",
                                message = "New Esports Tournament Added: ${newTourney.title} with ₹${newTourney.prizePoolCoins} prize pool!",
                                tournamentTitle = newTourney.title
                            )
                            if (!newTourney.roomId.isNullOrBlank()) {
                                NotificationHelper.sendRoomIdUpdatedAlert(
                                    context = context,
                                    tournamentId = newTourney.id,
                                    tournamentTitle = newTourney.title,
                                    roomId = newTourney.roomId,
                                    password = newTourney.roomPassword ?: "None",
                                    joinedPlayerCount = newTourney.currentSlots
                                )
                            }
                        },
                        onEditTournament = { updatedTourney ->
                            val (success, roomChanged) = repository.updateTournament(updatedTourney)
                            if (success && roomChanged && !updatedTourney.roomId.isNullOrBlank()) {
                                NotificationHelper.sendRoomIdUpdatedAlert(
                                    context = context,
                                    tournamentId = updatedTourney.id,
                                    tournamentTitle = updatedTourney.title,
                                    roomId = updatedTourney.roomId,
                                    password = updatedTourney.roomPassword ?: "None",
                                    joinedPlayerCount = updatedTourney.currentSlots
                                )
                            }
                        },
                        onDeleteTournament = { tourneyId ->
                            repository.deleteTournament(tourneyId)
                        },
                        onUpdateRoomCredentials = { tourneyId, roomId, pass ->
                            val updated = repository.updateRoomCredentials(tourneyId, roomId, pass, isLive = true)
                            val t = updated ?: tournaments.find { it.id == tourneyId }
                            NotificationHelper.sendRoomIdUpdatedAlert(
                                context = context,
                                tournamentId = tourneyId,
                                tournamentTitle = t?.title ?: "Custom Room Match",
                                roomId = roomId,
                                password = pass,
                                joinedPlayerCount = t?.currentSlots ?: 0
                            )
                        },
                        onUpdateTournamentStatus = { tourneyId, status ->
                            repository.updateTournamentStatus(tourneyId, status)
                            val t = tournaments.find { it.id == tourneyId }
                            if (status == TournamentStatus.LIVE || status == TournamentStatus.STARTING_SOON) {
                                NotificationHelper.sendMatchStartingAlert(
                                    context = context,
                                    tournamentId = tourneyId,
                                    tournamentTitle = t?.title ?: "Tournament Match",
                                    matchTime = if (status == TournamentStatus.LIVE) "LIVE NOW" else "In 5 minutes",
                                    roomId = t?.roomId,
                                    password = t?.roomPassword
                                )
                            }
                        },
                        onPostAnnouncement = { message, author, isImportant ->
                            repository.postAnnouncement(message, author, isImportant)
                            NotificationHelper.sendAdminUpdateAlert(
                                context = context,
                                author = author,
                                message = message
                            )
                        },
                        onBack = { navigateBack() },
                        onNavigate = { navigateTo(it) }
                    )
                }
            }
        }
    }
}


