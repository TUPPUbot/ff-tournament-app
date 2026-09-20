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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LeaderboardItem
import com.example.data.model.PlayerProfile
import com.example.ui.components.FirestoreLeaderboardComponent
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.FFAmber
import com.example.ui.theme.FFFlame
import com.example.ui.theme.FFGold
import com.example.ui.theme.FFOrange
import com.example.ui.theme.FFYellow
import com.example.ui.theme.GamingGreen
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@Composable
fun LeaderboardScreen(
    playerLeaderboard: List<LeaderboardItem>,
    guildLeaderboard: List<LeaderboardItem>,
    currentUser: PlayerProfile,
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableIntStateOf(0) } // 0 = Players, 1 = Guilds

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        // Top Title
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onBack != null) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("leaderboard_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextWhite
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
            }

            Icon(
                imageVector = Icons.Filled.EmojiEvents,
                contentDescription = null,
                tint = FFYellow,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "ESPORTS LEADERBOARD",
                    color = TextWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "POWERED BY FIRESTORE • REAL-TIME RANKINGS",
                    color = FFOrange,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }
        }

        // Category Tabs (Top Players vs Top Guilds)
        TabRow(
            selectedTabIndex = selectedCategory,
            containerColor = DarkSurface,
            contentColor = FFOrange,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedCategory]),
                    color = FFOrange
                )
            }
        ) {
            Tab(
                selected = selectedCategory == 0,
                onClick = { selectedCategory = 0 },
                text = {
                    Text(
                        "TOP PLAYERS",
                        color = if (selectedCategory == 0) FFYellow else TextMuted,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                },
                modifier = Modifier.testTag("leaderboard_tab_players")
            )
            Tab(
                selected = selectedCategory == 1,
                onClick = { selectedCategory = 1 },
                text = {
                    Text(
                        "TOP GUILDS / CLANS",
                        color = if (selectedCategory == 1) FFYellow else TextMuted,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                },
                modifier = Modifier.testTag("leaderboard_tab_guilds")
            )
        }

        // Full Firestore Leaderboard Component
        FirestoreLeaderboardComponent(
            isGuild = selectedCategory == 1,
            fallbackItems = if (selectedCategory == 0) playerLeaderboard else guildLeaderboard,
            currentUser = currentUser,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        )
    }
}

