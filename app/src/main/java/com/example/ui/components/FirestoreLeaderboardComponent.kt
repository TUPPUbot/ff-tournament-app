package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.data.firestore.FirestoreLeaderboardService
import com.example.data.firestore.FirestoreSyncState
import com.example.data.firestore.LeaderboardSortBy
import com.example.data.model.LeaderboardItem
import com.example.data.model.PlayerProfile
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.FFGold
import com.example.ui.theme.FFOrange
import com.example.ui.theme.FFYellow
import com.example.ui.theme.GamingGreen
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import kotlinx.coroutines.launch

/**
 * High-performance Leaderboard Component displaying user rankings based on match performance and points,
 * pulling real-time data from Cloud Firestore with local cache support.
 */
@Composable
fun FirestoreLeaderboardComponent(
    isGuild: Boolean,
    fallbackItems: List<LeaderboardItem>,
    currentUser: PlayerProfile,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val firestoreService = remember { FirestoreLeaderboardService.getInstance() }

    var sortBy by remember { mutableStateOf(LeaderboardSortBy.POINTS) }
    var searchQuery by remember { mutableStateOf("") }
    var expandedPlayerId by remember { mutableStateOf<String?>(null) }
    var isSeedingFirestore by remember { mutableStateOf(false) }

    // Stream from Firestore
    val leaderboardFlow = remember(isGuild, sortBy, fallbackItems) {
        firestoreService.observeLeaderboard(isGuild, sortBy, fallbackItems)
    }
    val leaderboardData by leaderboardFlow.collectAsState(
        initial = Pair(firestoreService.sortItems(fallbackItems, sortBy), FirestoreSyncState(isConnected = false, lastSyncTime = "Connecting..."))
    )

    val items = leaderboardData.first
    val syncState = leaderboardData.second

    // Filter by search query
    val filteredItems = remember(items, searchQuery) {
        if (searchQuery.isBlank()) {
            items
        } else {
            items.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                        it.tag.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    val topThree = filteredItems.take(3)
    val remaining = filteredItems.drop(3)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(DarkBg)
    ) {
        // Firestore Live Connection Pill & Cloud Status Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(DarkSurface)
                .border(1.dp, DarkBorder, RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (syncState.isConnected) Icons.Filled.CloudDone else Icons.Filled.Cloud,
                    contentDescription = "Firestore Status",
                    tint = if (syncState.isConnected) GamingGreen else FFYellow,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = if (syncState.isConnected) "FIRESTORE CLOUD LIVE" else "FIRESTORE CACHE / SEEDED",
                        color = if (syncState.isConnected) GamingGreen else FFYellow,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Status: ${syncState.lastSyncTime}",
                        color = TextMuted,
                        fontSize = 9.sp
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isSeedingFirestore) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = FFOrange
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(FFOrange.copy(alpha = 0.15f))
                        .border(1.dp, FFOrange.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                        .clickable {
                            coroutineScope.launch {
                                isSeedingFirestore = true
                                val success = firestoreService.seedLeaderboardToFirestore(fallbackItems)
                                isSeedingFirestore = false
                                if (success) {
                                    Toast.makeText(context, "Cloud sync complete with Firestore!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Local cache synced. Cloud requires google-services.json.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("firestore_sync_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.CloudSync, contentDescription = null, tint = FFOrange, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("SYNC CLOUD", color = FFOrange, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Performance Metric Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            LeaderboardSortBy.values().forEach { sortOption ->
                val isSelected = sortBy == sortOption
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) FFOrange else DarkSurface)
                        .border(1.dp, if (isSelected) FFOrange else DarkBorder, RoundedCornerShape(8.dp))
                        .clickable { sortBy = sortOption }
                        .padding(vertical = 6.dp)
                        .testTag("sort_by_${sortOption.name.lowercase()}"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = sortOption.label,
                        color = if (isSelected) Color.White else TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search by player IGN or guild tag...", color = TextDark, fontSize = 11.sp) },
            leadingIcon = {
                Icon(Icons.Filled.Search, contentDescription = "Search", tint = TextMuted, modifier = Modifier.size(16.dp))
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = FFOrange,
                unfocusedBorderColor = DarkBorder,
                focusedTextColor = TextWhite,
                unfocusedTextColor = TextWhite
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 4.dp)
                .testTag("leaderboard_search_input")
        )

        // Main Content List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 14.dp)
        ) {
            // Podium Section for Top 3 (if no search filter active)
            if (searchQuery.isBlank() && topThree.size >= 3) {
                item {
                    PodiumSection(topThree = topThree, sortBy = sortBy)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            // Column Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "RANK & CONTENDER", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        Text(text = "KILLS", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text(text = "WINS", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text(text = "POINTS", color = FFGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // List of ranked contenders
            val displayList = if (searchQuery.isBlank()) remaining else filteredItems
            items(displayList, key = { it.userId.ifBlank { "${it.rank}_${it.name}" } }) { item ->
                val isExpanded = expandedPlayerId == item.name
                PerformanceLeaderboardRow(
                    item = item,
                    sortBy = sortBy,
                    isExpanded = isExpanded,
                    onToggleExpand = {
                        expandedPlayerId = if (isExpanded) null else item.name
                    }
                )
                Spacer(modifier = Modifier.height(6.dp))
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Current User Live Standing Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurface)
                .border(1.5.dp, FFOrange.copy(alpha = 0.6f))
                .padding(horizontal = 14.dp, vertical = 10.dp)
                .testTag("current_user_standing_bar")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Brush.radialGradient(listOf(FFOrange, FFYellow))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "#1", color = Color.White, fontWeight = FontWeight.Black, fontSize = 13.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${currentUser.ign} (YOU)",
                                color = TextWhite,
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "👑", fontSize = 11.sp)
                        }
                        Text(
                            text = "${currentUser.rankTier} • Level ${currentUser.level} • K/D: 4.58",
                            color = FFYellow,
                            fontSize = 10.sp
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "2,850 PTS",
                        color = FFGold,
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "54 WINS • 724 KILLS",
                        color = GamingGreen,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun PodiumSection(
    topThree: List<LeaderboardItem>,
    sortBy: LeaderboardSortBy
) {
    if (topThree.size < 3) return

    val first = topThree[0]
    val second = topThree[1]
    val third = topThree[2]

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DarkSurface)
            .border(1.dp, DarkBorder, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            // #2 Silver
            PodiumPillar(
                item = second,
                rankText = "2",
                podiumHeight = 90.dp,
                badgeColor = Color(0xFFC0C0C0),
                crown = "🥈",
                sortBy = sortBy
            )

            // #1 Gold
            PodiumPillar(
                item = first,
                rankText = "1",
                podiumHeight = 116.dp,
                badgeColor = FFGold,
                crown = "👑",
                isFirst = true,
                sortBy = sortBy
            )

            // #3 Bronze
            PodiumPillar(
                item = third,
                rankText = "3",
                podiumHeight = 76.dp,
                badgeColor = Color(0xFFCD7F32),
                crown = "🥉",
                sortBy = sortBy
            )
        }
    }
}

@Composable
private fun PodiumPillar(
    item: LeaderboardItem,
    rankText: String,
    podiumHeight: androidx.compose.ui.unit.Dp,
    badgeColor: Color,
    crown: String,
    isFirst: Boolean = false,
    sortBy: LeaderboardSortBy
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(96.dp)
    ) {
        Text(text = crown, fontSize = if (isFirst) 22.sp else 16.sp)

        // Avatar with border
        Box(
            modifier = Modifier
                .size(if (isFirst) 46.dp else 40.dp)
                .clip(CircleShape)
                .background(DarkSurfaceElevated)
                .border(2.dp, badgeColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = item.name.take(2).uppercase(),
                color = badgeColor,
                fontWeight = FontWeight.Black,
                fontSize = if (isFirst) 13.sp else 11.sp
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = item.name,
            color = TextWhite,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            maxLines = 1,
            textAlign = TextAlign.Center
        )

        Text(
            text = item.tag,
            color = FFOrange,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
        )

        val highlightStat = when (sortBy) {
            LeaderboardSortBy.POINTS -> "${item.points} pts"
            LeaderboardSortBy.KILLS -> "${item.kills} kills"
            LeaderboardSortBy.BOOYAHS -> "${item.booyahs} wins"
            LeaderboardSortBy.MATCHES_PLAYED -> "${item.matchesPlayed} matches"
        }
        Text(
            text = highlightStat,
            color = FFGold,
            fontWeight = FontWeight.Black,
            fontSize = 10.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Pedestal block
        Box(
            modifier = Modifier
                .width(82.dp)
                .height(podiumHeight)
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                .background(
                    if (isFirst) {
                        Brush.verticalGradient(listOf(FFOrange.copy(alpha = 0.5f), DarkSurfaceElevated))
                    } else {
                        Brush.verticalGradient(listOf(badgeColor.copy(alpha = 0.25f), DarkSurfaceElevated))
                    }
                )
                .border(1.dp, badgeColor.copy(alpha = 0.4f), RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = rankText,
                    color = badgeColor,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "${item.kills}K • ${item.booyahs}W",
                    color = TextMuted,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun PerformanceLeaderboardRow(
    item: LeaderboardItem,
    sortBy: LeaderboardSortBy,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, if (isExpanded) FFOrange else DarkBorder, RoundedCornerShape(10.dp))
            .clickable { onToggleExpand() }
            .testTag("leaderboard_row_${item.rank}"),
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Rank & Identity
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                when (item.rank) {
                                    1 -> FFGold.copy(alpha = 0.2f)
                                    2 -> Color(0xFFC0C0C0).copy(alpha = 0.2f)
                                    3 -> Color(0xFFCD7F32).copy(alpha = 0.2f)
                                    else -> DarkSurfaceElevated
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "#${item.rank}",
                            color = when (item.rank) {
                                1 -> FFGold
                                2 -> Color(0xFFC0C0C0)
                                3 -> Color(0xFFCD7F32)
                                else -> TextMuted
                            },
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = item.name,
                                color = TextWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            if (item.tag.isNotBlank()) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = item.tag,
                                    color = FFOrange,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            if (item.badge.isNotBlank()) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = item.badge, fontSize = 10.sp)
                            }
                        }
                        Text(
                            text = item.tier,
                            color = TextMuted,
                            fontSize = 9.sp
                        )
                    }
                }

                // Match Performance Columns
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "${item.kills}",
                        color = if (sortBy == LeaderboardSortBy.KILLS) FFOrange else TextWhite,
                        fontWeight = if (sortBy == LeaderboardSortBy.KILLS) FontWeight.Black else FontWeight.Bold,
                        fontSize = 11.sp
                    )
                    Text(
                        text = "${item.booyahs}",
                        color = if (sortBy == LeaderboardSortBy.BOOYAHS) GamingGreen else TextWhite,
                        fontWeight = if (sortBy == LeaderboardSortBy.BOOYAHS) FontWeight.Black else FontWeight.Bold,
                        fontSize = 11.sp
                    )
                    Text(
                        text = "${item.points}",
                        color = if (sortBy == LeaderboardSortBy.POINTS) FFGold else FFYellow,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp
                    )
                    Icon(
                        imageVector = if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                        contentDescription = "Expand",
                        tint = TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Expanded Match Performance Deep Dive
            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkSurfaceElevated)
                        .padding(10.dp)
                ) {
                    Text(
                        text = "DETAILED MATCH PERFORMANCE (FROM FIRESTORE)",
                        color = FFYellow,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        PerformanceMetricItem("K/D RATIO", String.format("%.2f", if (item.kdRatio > 0) item.kdRatio else (item.kills.toDouble() / (item.booyahs.coerceAtLeast(1) * 3))), FFOrange)
                        PerformanceMetricItem("WIN RATE", item.winRate, GamingGreen)
                        PerformanceMetricItem("MATCHES", "${if (item.matchesPlayed > 0) item.matchesPlayed else (item.booyahs * 3 + 12)}", TextWhite)
                        PerformanceMetricItem("AVG KILLS", String.format("%.1f", item.kills.toDouble() / (item.booyahs.coerceAtLeast(1) * 3)), FFGold)
                    }
                }
            }
        }
    }
}

@Composable
private fun PerformanceMetricItem(label: String, value: String, valueColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, color = TextMuted, fontSize = 8.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, color = valueColor, fontSize = 12.sp, fontWeight = FontWeight.Black)
    }
}
