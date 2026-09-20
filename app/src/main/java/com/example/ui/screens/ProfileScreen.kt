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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AddCard
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.foundation.layout.PaddingValues
import com.example.ui.components.PaymentSheetModal
import com.example.ui.components.WithdrawSheetModal
import com.example.util.NotificationHelper
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MatchHistoryItem
import com.example.data.model.PlayerProfile
import com.example.data.model.Tournament
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
fun ProfileScreen(
    currentUser: PlayerProfile,
    tournaments: List<Tournament>,
    onUpdateProfile: (String, String, String, String) -> Unit,
    onToggleAdmin: (Boolean) -> Unit,
    onLogout: () -> Unit,
    onNavigate: (Screen) -> Unit,
    onDepositCoins: (amountCoins: Int, amountInr: Int, paymentMethod: String) -> Unit = { _, _, _ -> },
    onWithdrawCoins: (amountCoins: Int, amountInr: Int, upiId: String, holderName: String) -> Unit = { _, _, _, _ -> },
    onUpdateUpiId: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showEditDialog by remember { mutableStateOf(false) }
    var showPaymentModal by remember { mutableStateOf(false) }
    var showWithdrawModal by remember { mutableStateOf(false) }
    var matchAlertsEnabled by remember { mutableStateOf(true) }
    var adminAlertsEnabled by remember { mutableStateOf(true) }
    var hasNotifPermission by remember {
        mutableStateOf(NotificationHelper.hasNotificationPermission(context))
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasNotifPermission = isGranted
        if (isGranted) {
            Toast.makeText(context, "Notifications active! You will receive match alerts.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Notification permission denied.", Toast.LENGTH_SHORT).show()
        }
    }

    val registeredTournaments = tournaments.filter { tourney ->
        tourney.id in currentUser.registeredTournamentIds || tourney.registeredTeams.any {
            it.leaderUid == currentUser.uid || it.leaderIgn.equals(currentUser.ign, ignoreCase = true)
        }
    }

    fun copyUid() {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Free Fire UID", currentUser.uid)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Free Fire UID copied to clipboard!", Toast.LENGTH_SHORT).show()
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Esports ID Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(DarkSurfaceElevated, DarkSurface)
                        )
                    )
                    .border(1.dp, FFOrange.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Avatar
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(DarkBg)
                                    .border(2.dp, FFYellow, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = currentUser.ign.take(1).uppercase(),
                                    color = FFOrange,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 24.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = currentUser.ign,
                                        color = TextWhite,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Filled.Verified,
                                        contentDescription = "Verified Player",
                                        tint = FFYellow,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable { copyUid() }
                                ) {
                                    Text(
                                        text = "UID: ${currentUser.uid}",
                                        color = TextMuted,
                                        fontSize = 11.sp
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Filled.ContentCopy,
                                        contentDescription = "Copy UID",
                                        tint = FFOrange,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(2.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(FFOrange.copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = currentUser.rankTier.uppercase(),
                                            color = FFGold,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 9.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Level ${currentUser.level}",
                                        color = TextMuted,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        IconButton(
                            onClick = { showEditDialog = true },
                            modifier = Modifier.testTag("edit_profile_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = "Edit Profile",
                                tint = FFYellow
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = currentUser.bio,
                        color = TextWhite.copy(alpha = 0.9f),
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Real Wallet Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(DarkSurface)
                    .border(1.dp, DarkBorder, RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Wallet,
                                contentDescription = null,
                                tint = FFYellow,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "REAL WALLET",
                                color = TextWhite,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(GamingGreen.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(text = "REAL CASH & UPI ACTIVE", color = GamingGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(DarkSurfaceElevated)
                                .padding(10.dp)
                        ) {
                            Column {
                                Text(text = "FF COINS (INR VALUE)", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text(
                                    text = "🪙 ${currentUser.coins}",
                                    color = FFGold,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp
                                )
                                Text(text = "≈ ₹${currentUser.coins}.00 Real Cash (1₹ = 1🪙)", color = GamingGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(DarkSurfaceElevated)
                                .padding(10.dp)
                        ) {
                            Column {
                                Text(text = "PLATFORM DIAMONDS", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text(
                                    text = "💎 ${currentUser.diamonds}",
                                    color = Color(0xFF64B5F6),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp
                                )
                                Text(text = "For esports pass rewards", color = TextDark, fontSize = 9.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { showPaymentModal = true },
                            colors = ButtonDefaults.buttonColors(containerColor = FFOrange),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("wallet_top_up_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AddCard,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "+ TOP-UP COINS",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp
                            )
                        }

                        Button(
                            onClick = { showWithdrawModal = true },
                            colors = ButtonDefaults.buttonColors(containerColor = GamingGreen),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("wallet_withdraw_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AccountBalanceWallet,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "WITHDRAW TO UPI",
                                color = Color.Black,
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "ℹ️ Real Esports Wallet: Instant top-up via UPI (Google Pay, PhonePe, Paytm) & Razorpay Test Gateway. Withdraw earnings anytime directly to your UPI ID.",
                        color = TextMuted,
                        fontSize = 10.sp,
                        lineHeight = 14.sp
                    )
                }
            }
        }

        // Linked UPI ID for Cashouts Card
        item {
            var editingUpi by remember { mutableStateOf(false) }
            var upiText by remember(currentUser.upiId) { mutableStateOf(currentUser.upiId) }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(DarkSurface)
                    .border(1.dp, DarkBorder, RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.PhoneAndroid,
                                contentDescription = null,
                                tint = GamingGreen,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "LINKED UPI ID FOR CASHOUTS",
                                color = TextWhite,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (!editingUpi) {
                            Button(
                                onClick = { editingUpi = true },
                                colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceElevated),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier
                                    .height(26.dp)
                                    .testTag("edit_upi_id_btn")
                            ) {
                                Text("EDIT UPI", color = FFYellow, fontSize = 9.sp, fontWeight = FontWeight.Black)
                            }
                        }
                    }

                    if (editingUpi) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = upiText,
                                onValueChange = { upiText = it.trim() },
                                placeholder = { Text("e.g. mobile@upi / yourname@oksbi", color = TextDark, fontSize = 11.sp) },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GamingGreen,
                                    unfocusedBorderColor = DarkBorder,
                                    focusedTextColor = TextWhite,
                                    unfocusedTextColor = TextWhite
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("profile_upi_input")
                            )

                            Button(
                                onClick = {
                                    if (upiText.isNotBlank()) {
                                        onUpdateUpiId(upiText)
                                        editingUpi = false
                                        Toast.makeText(context, "UPI ID saved successfully!", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = GamingGreen),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("save_upi_id_btn")
                            ) {
                                Text("SAVE", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 11.sp)
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(DarkSurfaceElevated)
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = currentUser.upiId.ifBlank { "No UPI ID Linked (Tap Edit to Add)" },
                                        color = if (currentUser.upiId.isNotBlank()) TextWhite else TextMuted,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = "Official tournament winnings & withdrawals sent to this UPI ID",
                                        color = TextDark,
                                        fontSize = 9.sp
                                    )
                                }

                                if (currentUser.upiId.isNotBlank()) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(GamingGreen.copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("VERIFIED", color = GamingGreen, fontSize = 9.sp, fontWeight = FontWeight.Black)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Wallet Transactions & Payment Activity
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(DarkSurface)
                    .border(1.dp, DarkBorder, RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.ReceiptLong,
                                contentDescription = null,
                                tint = FFYellow,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "PAYMENT & ENTRY FEE TRANSACTIONS",
                                color = TextWhite,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        Text(
                            text = "${currentUser.walletTransactions.size} Records",
                            color = TextMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (currentUser.walletTransactions.isEmpty()) {
                        Text(
                            text = "No wallet activity yet. Top-up coins or register for tournaments to see records here.",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            currentUser.walletTransactions.take(8).forEach { txn ->
                                val isPositive = txn.type == com.example.data.model.TransactionType.DEPOSIT ||
                                        txn.type == com.example.data.model.TransactionType.PRIZE_REWARD ||
                                        txn.type == com.example.data.model.TransactionType.KILL_BONUS
                                val sign = if (isPositive) "+" else "-"
                                val amountColor = if (isPositive) GamingGreen else GamingRed

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(DarkSurfaceElevated)
                                        .padding(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(28.dp)
                                                    .clip(CircleShape)
                                                    .background(amountColor.copy(alpha = 0.15f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = if (isPositive) Icons.Filled.ArrowDownward else Icons.Filled.ArrowUpward,
                                                    contentDescription = null,
                                                    tint = amountColor,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column {
                                                Text(
                                                    text = txn.title,
                                                    color = TextWhite,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    maxLines = 1
                                                )
                                                Text(
                                                    text = "${txn.date} • ${txn.paymentMethod} • ${txn.receiptId}",
                                                    color = TextDark,
                                                    fontSize = 9.sp,
                                                    maxLines = 1
                                                )
                                            }
                                        }

                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(
                                                text = "$sign 🪙 ${txn.amountCoins}",
                                                color = amountColor,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Black
                                            )
                                            Text(
                                                text = "$sign₹${txn.amountCoins}",
                                                color = amountColor.copy(alpha = 0.85f),
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Career Esports Performance Stats
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(DarkSurface)
                    .border(1.dp, DarkBorder, RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.MilitaryTech,
                            contentDescription = null,
                            tint = FFOrange,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "CAREER BATTLE STATS",
                            color = TextWhite,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatTile(title = "MATCHES", value = "${currentUser.matchesPlayed}", modifier = Modifier.weight(1f))
                        StatTile(title = "BOOYAHS", value = "${currentUser.booyahs}", color = GamingGreen, modifier = Modifier.weight(1f))
                        StatTile(title = "TOTAL KILLS", value = "${currentUser.kills}", color = FFYellow, modifier = Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatTile(title = "K/D RATIO", value = "${currentUser.kdRatio}", color = FFOrange, modifier = Modifier.weight(1f))
                        StatTile(title = "WIN RATE", value = "${currentUser.winRate}%", color = GamingGreen, modifier = Modifier.weight(1f))
                        StatTile(title = "HEADSHOT", value = "${currentUser.headshotRate}%", color = FFFlame, modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        // My Registered Tournaments Section
        item {
            Column {
                Text(
                    text = "MY REGISTERED TOURNAMENTS (${registeredTournaments.size})",
                    color = TextWhite,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (registeredTournaments.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(DarkSurface)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "You haven't registered for any tournaments yet.", color = TextMuted, fontSize = 12.sp)
                    }
                } else {
                    registeredTournaments.forEach { tourney ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(DarkSurface)
                                .border(1.dp, DarkBorder, RoundedCornerShape(10.dp))
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = tourney.title, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(
                                        text = "${tourney.scheduleTime} • ${tourney.mode.label}",
                                        color = TextMuted,
                                        fontSize = 11.sp
                                    )
                                }

                                Button(
                                    onClick = { onNavigate(Screen.MatchRoom(tourney.id)) },
                                    colors = ButtonDefaults.buttonColors(containerColor = FFOrange),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("ROOM", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Match History
        item {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.History,
                        contentDescription = null,
                        tint = FFYellow,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "RECENT MATCH HISTORY",
                        color = TextWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                currentUser.matchHistory.forEach { item ->
                    MatchHistoryRow(item = item)
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }
        }

        // Local Notifications & Match Alerts Center
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkSurfaceElevated)
                    .border(1.dp, FFOrange.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.NotificationsActive,
                                contentDescription = null,
                                tint = FFOrange,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "MATCH ALERTS & NOTIFICATIONS",
                                color = TextWhite,
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp
                            )
                        }

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (hasNotifPermission) GamingGreen.copy(alpha = 0.2f) else FFYellow.copy(alpha = 0.2f))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = if (hasNotifPermission) "ACTIVE" else "PERMISSION REQ",
                                    color = if (hasNotifPermission) GamingGreen else FFYellow,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Receive high-priority system alerts when registered matches are starting, room credentials unlock, or admins issue tournament updates.",
                        color = TextMuted,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasNotifPermission) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = FFOrange),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().testTag("grant_notif_permission_btn")
                        ) {
                            Icon(Icons.Filled.Notifications, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("ENABLE SYSTEM NOTIFICATIONS", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = DarkBorder, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(10.dp))

                    // Match Starting Alert Switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Match Starting Alert",
                                color = TextWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "Pushes Room ID & Password before match launch",
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }
                        Switch(
                            checked = matchAlertsEnabled,
                            onCheckedChange = { matchAlertsEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = FFYellow,
                                checkedTrackColor = FFOrange
                            ),
                            modifier = Modifier.testTag("match_alerts_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Admin Broadcasts Switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Admin Broadcasts & Updates",
                                color = TextWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "Urgent referee notices, schedule revisions & room alerts",
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }
                        Switch(
                            checked = adminAlertsEnabled,
                            onCheckedChange = { adminAlertsEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = FFYellow,
                                checkedTrackColor = FFOrange
                            ),
                            modifier = Modifier.testTag("admin_alerts_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Test Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val targetTourney = registeredTournaments.firstOrNull() ?: tournaments.firstOrNull()
                                NotificationHelper.sendMatchStartingAlert(
                                    context = context,
                                    tournamentId = targetTourney?.id ?: "tourney_1",
                                    tournamentTitle = targetTourney?.title ?: "FF Pro Championship S4",
                                    matchTime = "Starting in 5 minutes",
                                    roomId = targetTourney?.roomId ?: "8374921",
                                    password = targetTourney?.roomPassword ?: "booyah99"
                                )
                                Toast.makeText(context, "Match Starting Alert dispatched!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkSurface),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, FFOrange),
                            modifier = Modifier.weight(1f).testTag("test_match_alert_btn")
                        ) {
                            Icon(Icons.Filled.SportsEsports, contentDescription = null, tint = FFOrange, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("TEST MATCH ALERT", color = FFOrange, fontSize = 10.sp, fontWeight = FontWeight.Black)
                        }

                        Button(
                            onClick = {
                                NotificationHelper.sendAdminUpdateAlert(
                                    context = context,
                                    author = "Chief Referee",
                                    message = "Official Notice: Custom lobby starting in 5 minutes! Check your assigned team slot.",
                                    tournamentTitle = registeredTournaments.firstOrNull()?.title
                                )
                                Toast.makeText(context, "Admin Broadcast alert dispatched!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkSurface),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, FFYellow),
                            modifier = Modifier.weight(1f).testTag("test_admin_alert_btn")
                        ) {
                            Icon(Icons.Filled.Campaign, contentDescription = null, tint = FFYellow, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("TEST BROADCAST", color = FFYellow, fontSize = 10.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }

        // Admin Mode Toggle & Actions
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkSurfaceElevated)
                    .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.AdminPanelSettings,
                                    contentDescription = null,
                                    tint = FFYellow,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Admin Mode Privilege",
                                    color = TextWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                            Text(
                                text = "Enable to manage tournaments & rooms",
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }

                        Switch(
                            checked = currentUser.isAdmin,
                            onCheckedChange = { onToggleAdmin(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = FFYellow,
                                checkedTrackColor = FFOrange
                            ),
                            modifier = Modifier.testTag("admin_privilege_switch")
                        )
                    }

                    if (currentUser.isAdmin) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = { onNavigate(Screen.AdminDashboard) },
                            colors = ButtonDefaults.buttonColors(containerColor = FFOrange),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().testTag("open_admin_hub_btn")
                        ) {
                            Text("OPEN ADMIN DASHBOARD", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Sign Out Button
        item {
            OutlinedButton(
                onClick = onLogout,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = GamingRed),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().testTag("sign_out_btn")
            ) {
                Icon(
                    imageVector = Icons.Filled.Logout,
                    contentDescription = null,
                    tint = GamingRed,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("SIGN OUT / SWITCH USER", color = GamingRed, fontWeight = FontWeight.Bold)
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // Edit Profile Modal
    if (showEditDialog) {
        EditProfileModal(
            currentProfile = currentUser,
            onDismiss = { showEditDialog = false },
            onSave = { ign, uid, bio, upiId ->
                onUpdateProfile(ign, uid, bio, upiId)
                Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                showEditDialog = false
            }
        )
    }

    if (showPaymentModal) {
        PaymentSheetModal(
            currentCoins = currentUser.coins,
            onDismiss = { showPaymentModal = false },
            onPaymentSuccess = { amountCoins, amountInr, paymentMethod ->
                onDepositCoins(amountCoins, amountInr, paymentMethod)
                Toast.makeText(context, "Deposit success: +🪙 $amountCoins Coins via $paymentMethod!", Toast.LENGTH_SHORT).show()
                showPaymentModal = false
            }
        )
    }

    if (showWithdrawModal) {
        WithdrawSheetModal(
            currentCoins = currentUser.coins,
            savedUpiId = currentUser.upiId,
            onDismiss = { showWithdrawModal = false },
            onWithdrawSuccess = { amountCoins, amountInr, upiId, holderName ->
                onWithdrawCoins(amountCoins, amountInr, upiId, holderName)
                showWithdrawModal = false
            }
        )
    }
}

@Composable
private fun StatTile(
    title: String,
    value: String,
    color: Color = TextWhite,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(DarkSurfaceElevated)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = title, color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, color = color, fontSize = 14.sp, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun MatchHistoryRow(item: MatchHistoryItem) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(DarkSurface)
            .border(1.dp, DarkBorder, RoundedCornerShape(10.dp))
            .padding(12.dp)
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
                        .background(if (item.rank == 1) FFGold.copy(alpha = 0.2f) else DarkSurfaceElevated),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "#${item.rank}",
                        color = if (item.rank == 1) FFGold else TextWhite,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = item.tournamentTitle,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "${item.date} • ${item.mode} • ${item.kills} Kills",
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(DarkSurfaceElevated)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "+🪙 ${item.coinsEarned}",
                    color = FFGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun EditProfileModal(
    currentProfile: PlayerProfile,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String) -> Unit
) {
    var ign by remember { mutableStateOf(currentProfile.ign) }
    var uid by remember { mutableStateOf(currentProfile.uid) }
    var bio by remember { mutableStateOf(currentProfile.bio) }
    var upiId by remember { mutableStateOf(currentProfile.upiId) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        shape = RoundedCornerShape(16.dp),
        title = {
            Text(
                text = "EDIT ESPORTS PROFILE",
                color = FFYellow,
                fontWeight = FontWeight.Black,
                fontSize = 16.sp
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = ign,
                    onValueChange = { ign = it },
                    label = { Text("Free Fire IGN", color = TextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FFOrange,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("edit_ign_input")
                )

                OutlinedTextField(
                    value = uid,
                    onValueChange = { uid = it },
                    label = { Text("Free Fire UID", color = TextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FFOrange,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("edit_uid_input")
                )

                OutlinedTextField(
                    value = upiId,
                    onValueChange = { upiId = it.trim() },
                    label = { Text("UPI ID for Cashouts", color = TextMuted) },
                    placeholder = { Text("e.g. yourname@okaxis / 9876543210@paytm", color = TextDark) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GamingGreen,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("edit_upi_input")
                )

                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Bio / Clan Information", color = TextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FFOrange,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("edit_bio_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(ign, uid, bio, upiId) },
                colors = ButtonDefaults.buttonColors(containerColor = FFOrange),
                modifier = Modifier.testTag("save_profile_btn")
            ) {
                Text("Save Changes", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel", color = TextMuted)
            }
        }
    )
}
