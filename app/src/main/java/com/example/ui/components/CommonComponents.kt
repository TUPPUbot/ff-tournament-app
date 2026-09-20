package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MeetingRoom
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GameMap
import com.example.data.model.GameMode
import com.example.data.model.PlayerProfile
import com.example.data.model.TournamentStatus
import com.example.ui.navigation.BottomNavTab
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
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@Composable
fun FFTopBar(
    currentUser: PlayerProfile,
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = DarkSurface,
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .border(width = 1.dp, color = DarkBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Logo and App Name
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onNavigate(Screen.Home) }
                    .padding(4.dp)
                    .testTag("app_logo_title")
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(FFOrange, FFFlame)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.SportsEsports,
                        contentDescription = "FF Tournament Logo",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "FF",
                            color = FFYellow,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "TOURNAMENT",
                            color = TextWhite,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                            letterSpacing = 1.sp
                        )
                    }
                    Text(
                        text = "ESPORTS ARENA",
                        color = FFAmber,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                }
            }

            // Right side: Virtual Coins balance and Quick Actions
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Virtual Coins Chip
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(DarkSurfaceVariant)
                        .border(1.dp, DarkBorderHighlight, RoundedCornerShape(20.dp))
                        .clickable { onNavigate(Screen.Profile) }
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                        .testTag("header_coins_badge")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "🪙",
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${currentUser.coins}",
                            color = FFGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                // Leaderboard Trophy button
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(DarkSurfaceVariant)
                        .border(1.dp, DarkBorder, CircleShape)
                        .clickable { onNavigate(Screen.Leaderboard) }
                        .testTag("header_leaderboard_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.EmojiEvents,
                        contentDescription = "Leaderboard",
                        tint = FFYellow,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Admin Dashboard shortcut button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (currentUser.isAdmin) FFOrange.copy(alpha = 0.2f) else DarkSurfaceVariant)
                        .border(
                            1.dp,
                            if (currentUser.isAdmin) FFOrange else DarkBorder,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { onNavigate(Screen.AdminDashboard) }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                        .testTag("admin_header_button")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.AdminPanelSettings,
                            contentDescription = "Admin Dashboard",
                            tint = if (currentUser.isAdmin) FFYellow else TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Admin",
                            color = if (currentUser.isAdmin) FFYellow else TextMuted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Profile Avatar button
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .border(2.dp, FFYellow, CircleShape)
                        .background(DarkSurfaceElevated)
                        .clickable { onNavigate(Screen.Profile) }
                        .testTag("profile_header_avatar"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = currentUser.ign.take(1).uppercase(),
                        color = FFOrange,
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun FFBottomBar(
    currentTab: BottomNavTab,
    onTabSelected: (BottomNavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = DarkSurface,
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .border(width = 1.dp, color = DarkBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                label = "Home",
                selected = currentTab == BottomNavTab.HOME,
                selectedIcon = Icons.Filled.Home,
                unselectedIcon = Icons.Outlined.Home,
                onClick = { onTabSelected(BottomNavTab.HOME) },
                tag = "nav_home"
            )
            BottomNavItem(
                label = "Tournaments",
                selected = currentTab == BottomNavTab.TOURNAMENTS,
                selectedIcon = Icons.Filled.SportsEsports,
                unselectedIcon = Icons.Outlined.SportsEsports,
                onClick = { onTabSelected(BottomNavTab.TOURNAMENTS) },
                tag = "nav_tournaments"
            )
            BottomNavItem(
                label = "My Matches",
                selected = currentTab == BottomNavTab.MY_MATCHES,
                selectedIcon = Icons.Filled.MeetingRoom,
                unselectedIcon = Icons.Outlined.MeetingRoom,
                hasBadge = true,
                onClick = { onTabSelected(BottomNavTab.MY_MATCHES) },
                tag = "nav_my_matches"
            )
            BottomNavItem(
                label = "Profile",
                selected = currentTab == BottomNavTab.PROFILE,
                selectedIcon = Icons.Filled.Person,
                unselectedIcon = Icons.Outlined.Person,
                onClick = { onTabSelected(BottomNavTab.PROFILE) },
                tag = "nav_profile"
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    label: String,
    selected: Boolean,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    hasBadge: Boolean = false,
    onClick: () -> Unit,
    tag: String
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .testTag(tag),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box {
                Icon(
                    imageVector = if (selected) selectedIcon else unselectedIcon,
                    contentDescription = label,
                    tint = if (selected) FFOrange else TextMuted,
                    modifier = Modifier.size(22.dp)
                )
                if (hasBadge) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .align(Alignment.TopEnd)
                            .clip(CircleShape)
                            .background(GamingGreen)
                    )
                }
            }
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = label,
                color = if (selected) FFYellow else TextMuted,
                fontSize = 11.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
fun StatusBadge(status: TournamentStatus, modifier: Modifier = Modifier) {
    val (bgColor, textColor, borderC) = when (status) {
        TournamentStatus.LIVE -> Triple(GamingGreen.copy(alpha = 0.2f), GamingGreen, GamingGreen)
        TournamentStatus.STARTING_SOON -> Triple(FFAmber.copy(alpha = 0.2f), FFAmber, FFAmber)
        TournamentStatus.UPCOMING, TournamentStatus.REGISTRATION_OPEN -> Triple(FFOrange.copy(alpha = 0.2f), FFOrange, FFOrange)
        TournamentStatus.COMPLETED -> Triple(DarkSurfaceElevated, TextMuted, DarkBorder)
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alphaAnim by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .border(1.dp, borderC, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (status == TournamentStatus.LIVE) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(GamingGreen)
                        .alpha(alphaAnim)
                )
                Spacer(modifier = Modifier.width(5.dp))
            }
            Text(
                text = status.label.uppercase(),
                color = textColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
fun ModeChip(mode: GameMode, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(DarkSurfaceElevated)
            .border(1.dp, DarkBorder, RoundedCornerShape(4.dp))
            .padding(horizontal = 7.dp, vertical = 3.dp)
    ) {
        Text(
            text = mode.label,
            color = FFGold,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun MapChip(map: GameMap, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(DarkSurfaceVariant)
            .border(1.dp, DarkBorder, RoundedCornerShape(4.dp))
            .padding(horizontal = 7.dp, vertical = 3.dp)
    ) {
        Text(
            text = "📍 ${map.label}",
            color = TextWhite,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
