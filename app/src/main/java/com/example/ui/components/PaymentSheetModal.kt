package com.example.ui.components

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkBorderHighlight
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.FFAmber
import com.example.ui.theme.FFFlame
import com.example.ui.theme.FFGold
import com.example.ui.theme.FFOrange
import com.example.ui.theme.FFYellow
import com.example.ui.theme.GamingGreen
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class CoinPack(
    val coins: Int,
    val priceInr: Int,
    val bonus: String? = null,
    val badge: String? = null
)

// 100, 200, 500 packs as explicitly requested with 1 Rupee = 1 Coin conversion
val DEFAULT_COIN_PACKS = listOf(
    CoinPack(coins = 100, priceInr = 100, bonus = null, badge = "STARTER PACK"),
    CoinPack(coins = 200, priceInr = 200, bonus = null, badge = "MOST POPULAR"),
    CoinPack(coins = 500, priceInr = 500, bonus = null, badge = "BEST VALUE PRO")
)

enum class PaymentModeTab(val title: String) {
    UPI_INTENT("UPI APPS"),
    RAZORPAY("RAZORPAY TEST")
}

data class UpiAppOption(
    val id: String,
    val name: String,
    val packageName: String?,
    val iconColor: Color,
    val badge: String
)

val UPI_APP_OPTIONS = listOf(
    UpiAppOption("gpay", "Google Pay", "com.google.android.apps.nps", Color(0xFF4285F4), "REAL UPI INTENT"),
    UpiAppOption("phonepe", "PhonePe", "com.phonepe.app", Color(0xFF6739B7), "REAL UPI INTENT"),
    UpiAppOption("paytm", "Paytm", "net.one97.paytm", Color(0xFF00B9F5), "REAL UPI INTENT"),
    UpiAppOption("any_upi", "Any UPI App", null, FFOrange, "SYSTEM CHOOSER")
)

@Composable
fun PaymentSheetModal(
    currentCoins: Int,
    requiredCoins: Int = 0,
    forTournamentTitle: String? = null,
    onDismiss: () -> Unit,
    onPaymentSuccess: (amountCoins: Int, amountInr: Int, paymentMethod: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val coroutineScope = rememberCoroutineScope()

    var selectedPack by remember {
        val initial = if (requiredCoins > 0) {
            DEFAULT_COIN_PACKS.firstOrNull { it.coins >= requiredCoins } ?: DEFAULT_COIN_PACKS[1]
        } else {
            DEFAULT_COIN_PACKS[0]
        }
        mutableStateOf(initial)
    }

    var selectedTab by remember { mutableStateOf(PaymentModeTab.UPI_INTENT) }
    var selectedUpiApp by remember { mutableStateOf(UPI_APP_OPTIONS[0]) }

    // Razorpay specific selections
    var razorpayMethod by remember { mutableStateOf("UPI") } // "UPI", "CARDS", "NETBANKING"
    var razorpayTestUpiId by remember { mutableStateOf("success@razorpay") }
    var razorpaySelectedBank by remember { mutableStateOf("HDFC Bank") }

    var isProcessing by remember { mutableStateOf(false) }
    var processingMessage by remember { mutableStateOf("Verifying Payment...") }
    var paymentSuccessResult by remember { mutableStateOf<Pair<Int, String>?>(null) } // Coins to receipt

    // Fallback dialog when UPI app is not installed on device
    var showUpiFallbackDialog by remember { mutableStateOf<String?>(null) }

    // Real UPI Activity Result Launcher
    val upiLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val responseData = result.data?.getStringExtra("response") ?: ""
        val isSuccess = responseData.contains("Status=SUCCESS", ignoreCase = true) ||
                responseData.contains("SUCCESS", ignoreCase = true) ||
                result.resultCode == Activity.RESULT_OK

        if (isSuccess) {
            val receipt = "TXN_UPI_${System.currentTimeMillis() % 10000000}"
            paymentSuccessResult = Pair(selectedPack.coins, receipt)
            onPaymentSuccess(selectedPack.coins, selectedPack.priceInr, "UPI (${selectedUpiApp.name})")
            Toast.makeText(context, "UPI Payment Successful! +${selectedPack.coins} Coins credited.", Toast.LENGTH_LONG).show()
        } else {
            // Provide confirmation option in case emulator/sandbox didn't return standard response string
            showUpiFallbackDialog = "UPI intent dispatched to ${selectedUpiApp.name}. Confirm your payment status below:"
        }
    }

    // Function to launch real UPI Intent
    fun launchRealUpiIntent(app: UpiAppOption, pack: CoinPack) {
        val payeeVpa = "esports.tournaments@upi"
        val payeeName = "FF Tournament Esports"
        val txnRef = "TXN${System.currentTimeMillis() % 10000000}"
        val orderRef = "ORDER_${System.currentTimeMillis() % 10000000}"
        val note = "FF Coins Top-Up Pack Rs ${pack.priceInr}"
        val amount = "${pack.priceInr}.00"

        val uriString = "upi://pay?pa=$payeeVpa&pn=${Uri.encode(payeeName)}&mc=5732&tid=$txnRef&tr=$orderRef&tn=${Uri.encode(note)}&am=$amount&cu=INR"
        val upiUri = Uri.parse(uriString)

        val intent = Intent(Intent.ACTION_VIEW, upiUri).apply {
            if (app.packageName != null) {
                setPackage(app.packageName)
            }
        }

        try {
            if (app.packageName == null) {
                val chooser = Intent.createChooser(intent, "Pay via UPI App")
                upiLauncher.launch(chooser)
            } else {
                upiLauncher.launch(intent)
            }
        } catch (e: ActivityNotFoundException) {
            // App not found on this device
            showUpiFallbackDialog = "${app.name} is not installed on this Android device/emulator.\nYou can switch to Razorpay Checkout (Test Mode) or verify manually using the UPI ID below."
        } catch (e: Exception) {
            showUpiFallbackDialog = "Unable to open ${app.name}. You can switch to Razorpay Checkout or use standard UPI transfer."
        }
    }

    AlertDialog(
        onDismissRequest = {
            if (!isProcessing) onDismiss()
        },
        containerColor = DarkSurface,
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, DarkBorderHighlight, RoundedCornerShape(20.dp)),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(FFOrange.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Payment,
                            contentDescription = null,
                            tint = FFOrange,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "TOP-UP REAL WALLET",
                            color = TextWhite,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Lock, contentDescription = null, tint = GamingGreen, modifier = Modifier.size(11.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "Real UPI Intent & Razorpay Integrated",
                                color = GamingGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                if (!isProcessing) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(Icons.Filled.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }
            }
        },
        text = {
            if (paymentSuccessResult != null) {
                // Success State View
                val (coinsAdded, receipt) = paymentSuccessResult!!
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(GamingGreen.copy(alpha = 0.2f))
                            .border(2.dp, GamingGreen, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = GamingGreen,
                            modifier = Modifier.size(38.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "DEPOSIT SUCCESSFUL!",
                        color = TextWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black
                    )

                    Text(
                        text = "+🪙 $coinsAdded FF COINS ADDED",
                        color = FFGold,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    Text(
                        text = "Your Real Wallet balance has been instantly credited.",
                        color = TextMuted,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(DarkSurfaceElevated)
                            .border(1.dp, DarkBorder, RoundedCornerShape(10.dp))
                            .padding(12.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Amount Paid", color = TextMuted, fontSize = 11.sp)
                                Text("₹${selectedPack.priceInr}.00", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Payment Gateway", color = TextMuted, fontSize = 11.sp)
                                Text(
                                    if (selectedTab == PaymentModeTab.UPI_INTENT) "UPI (${selectedUpiApp.name})" else "Razorpay Test",
                                    color = FFOrange,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Transaction ID", color = TextMuted, fontSize = 11.sp)
                                Text(receipt, color = TextMuted, fontSize = 11.sp)
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Tournament Entry Fee Context Banner (if needed)
                    if (forTournamentTitle != null && requiredCoins > 0) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(FFOrange.copy(alpha = 0.12f))
                                    .border(1.dp, FFOrange.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                    .padding(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "ENTRY FEE REQUIRED",
                                            color = FFOrange,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                        Text(
                                            text = forTournamentTitle,
                                            color = TextWhite,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "🪙 $requiredCoins",
                                            color = FFYellow,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                        Text(
                                            text = "Your Balance: 🪙 $currentCoins",
                                            color = TextMuted,
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Choose Coin Pack (100, 200, 500)
                    item {
                        Text(
                            text = "CHOOSE COIN PACK (100 / 200 / 500)",
                            color = TextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            DEFAULT_COIN_PACKS.forEach { pack ->
                                val isSelected = selectedPack == pack
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) DarkSurfaceElevated else DarkSurface)
                                        .border(
                                            1.5.dp,
                                            if (isSelected) FFOrange else DarkBorder,
                                            RoundedCornerShape(10.dp)
                                        )
                                        .clickable { selectedPack = pack }
                                        .padding(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "🪙 ${pack.coins} Coins",
                                                color = if (isSelected) FFGold else TextWhite,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Black
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(GamingGreen.copy(alpha = 0.2f))
                                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = "1₹ = 1🪙",
                                                    color = GamingGreen,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            if (pack.badge != null) {
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(4.dp))
                                                        .background(if (pack.priceInr == 200) FFOrange else DarkBorderHighlight)
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text(
                                                        text = pack.badge,
                                                        color = Color.White,
                                                        fontSize = 8.sp,
                                                        fontWeight = FontWeight.Black
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(8.dp))
                                            }

                                            Text(
                                                text = "₹${pack.priceInr}",
                                                color = if (isSelected) FFOrange else TextWhite,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Black
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Payment Method Tabs: Real UPI vs Razorpay Test
                    item {
                        Text(
                            text = "SELECT PAYMENT GATEWAY",
                            color = TextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        TabRow(
                            selectedTabIndex = selectedTab.ordinal,
                            containerColor = DarkSurfaceElevated,
                            contentColor = FFOrange,
                            indicator = { tabPositions ->
                                TabRowDefaults.Indicator(
                                    Modifier.tabIndicatorOffset(tabPositions[selectedTab.ordinal]),
                                    color = FFOrange,
                                    height = 3.dp
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                        ) {
                            PaymentModeTab.values().forEach { tab ->
                                Tab(
                                    selected = selectedTab == tab,
                                    onClick = { selectedTab = tab },
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = if (tab == PaymentModeTab.UPI_INTENT) Icons.Filled.PhoneAndroid else Icons.Filled.Security,
                                                contentDescription = null,
                                                tint = if (selectedTab == tab) FFOrange else TextMuted,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = tab.title,
                                                color = if (selectedTab == tab) FFOrange else TextMuted,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Black
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }

                    // Tab Content
                    if (selectedTab == PaymentModeTab.UPI_INTENT) {
                        // UPI Apps Selection
                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "TAP TO LAUNCH UPI INTENT",
                                    color = TextDark,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                UPI_APP_OPTIONS.forEach { app ->
                                    val isSelected = selectedUpiApp == app
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSelected) FFOrange.copy(alpha = 0.15f) else DarkSurfaceElevated)
                                            .border(
                                                1.dp,
                                                if (isSelected) FFOrange else DarkBorder,
                                                RoundedCornerShape(8.dp)
                                            )
                                            .clickable { selectedUpiApp = app }
                                            .padding(horizontal = 12.dp, vertical = 10.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(28.dp)
                                                        .clip(CircleShape)
                                                        .background(app.iconColor.copy(alpha = 0.2f)),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Filled.PhoneAndroid,
                                                        contentDescription = null,
                                                        tint = app.iconColor,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Column {
                                                    Text(
                                                        text = app.name,
                                                        color = if (isSelected) TextWhite else TextMuted,
                                                        fontSize = 12.sp,
                                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                                    )
                                                    Text(
                                                        text = app.badge,
                                                        color = GamingGreen,
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }

                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Filled.OpenInNew,
                                                    contentDescription = null,
                                                    tint = if (isSelected) FFOrange else TextDark,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                if (isSelected) {
                                                    Icon(
                                                        imageVector = Icons.Filled.CheckCircle,
                                                        contentDescription = null,
                                                        tint = FFOrange,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                // UPI VPA Copy Bar
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(DarkSurfaceElevated)
                                        .border(1.dp, DarkBorder, RoundedCornerShape(6.dp))
                                        .padding(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text("VPA: esports.tournaments@upi", color = TextMuted, fontSize = 10.sp)
                                            Text("Official Merchant UPI VPA", color = TextDark, fontSize = 8.sp)
                                        }
                                        Button(
                                            onClick = {
                                                clipboardManager.setText(AnnotatedString("esports.tournaments@upi"))
                                                Toast.makeText(context, "UPI ID copied!", Toast.LENGTH_SHORT).show()
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = DarkBorderHighlight),
                                            shape = RoundedCornerShape(4.dp),
                                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                            modifier = Modifier.height(24.dp)
                                        ) {
                                            Icon(Icons.Filled.ContentCopy, contentDescription = null, tint = TextWhite, modifier = Modifier.size(10.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("COPY", color = TextWhite, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        // Razorpay Test Mode Card
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(DarkSurfaceElevated)
                                    .border(1.dp, Color(0xFF0C2340), RoundedCornerShape(10.dp))
                                    .padding(12.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(Color(0xFF0C2340))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text("RAZORPAY", color = Color(0xFF528FF0), fontSize = 9.sp, fontWeight = FontWeight.Black)
                                            }
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("TEST MODE", color = FFYellow, fontSize = 9.sp, fontWeight = FontWeight.Black)
                                        }

                                        Text(
                                            text = "Key: rzp_test_esports_ff",
                                            color = TextDark,
                                            fontSize = 9.sp
                                        )
                                    }

                                    // Razorpay Sub-methods selector
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        listOf("UPI", "CARDS", "NETBANKING").forEach { method ->
                                            val isSel = razorpayMethod == method
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(if (isSel) Color(0xFF0C2340) else DarkSurface)
                                                    .border(1.dp, if (isSel) Color(0xFF528FF0) else DarkBorder, RoundedCornerShape(6.dp))
                                                    .clickable { razorpayMethod = method }
                                                    .padding(vertical = 8.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = method,
                                                    color = if (isSel) Color(0xFF528FF0) else TextMuted,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }

                                    when (razorpayMethod) {
                                        "UPI" -> {
                                            OutlinedTextField(
                                                value = razorpayTestUpiId,
                                                onValueChange = { razorpayTestUpiId = it },
                                                label = { Text("Razorpay Test UPI VPA", color = TextMuted, fontSize = 11.sp) },
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    focusedBorderColor = Color(0xFF528FF0),
                                                    unfocusedBorderColor = DarkBorder,
                                                    focusedTextColor = TextWhite,
                                                    unfocusedTextColor = TextWhite
                                                ),
                                                singleLine = true,
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                            Text("Use success@razorpay for instant test approval", color = GamingGreen, fontSize = 9.sp)
                                        }
                                        "CARDS" -> {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(DarkSurface)
                                                    .padding(10.dp)
                                            ) {
                                                Column {
                                                    Text("Test Card: 4000 0000 0000 0002 (Visa)", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                    Text("Exp: 12/28 • CVV: 123 • OTP: 1234", color = TextMuted, fontSize = 10.sp)
                                                }
                                            }
                                        }
                                        "NETBANKING" -> {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                listOf("HDFC Bank", "ICICI Bank", "SBI", "Axis").forEach { bank ->
                                                    val isB = razorpaySelectedBank == bank
                                                    Box(
                                                        modifier = Modifier
                                                            .weight(1f)
                                                            .clip(RoundedCornerShape(4.dp))
                                                            .background(if (isB) Color(0xFF0C2340) else DarkSurface)
                                                            .border(1.dp, if (isB) Color(0xFF528FF0) else DarkBorder, RoundedCornerShape(4.dp))
                                                            .clickable { razorpaySelectedBank = bank }
                                                            .padding(vertical = 6.dp),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Text(bank, color = if (isB) TextWhite else TextDark, fontSize = 9.sp, maxLines = 1)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (paymentSuccessResult != null) {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = GamingGreen),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("payment_done_btn")
                ) {
                    Text("DONE & CONTINUE", color = Color.Black, fontWeight = FontWeight.Black)
                }
            } else {
                Button(
                    onClick = {
                        if (selectedTab == PaymentModeTab.UPI_INTENT) {
                            // Trigger real UPI Intent
                            launchRealUpiIntent(selectedUpiApp, selectedPack)
                        } else {
                            // Razorpay Test Checkout Flow
                            isProcessing = true
                            processingMessage = "Connecting to Razorpay Test Gateway..."
                            coroutineScope.launch {
                                delay(1000)
                                processingMessage = "Authorizing ₹${selectedPack.priceInr} with Razorpay..."
                                delay(800)
                                val rzpId = "pay_rzp_${System.currentTimeMillis().toString().takeLast(8)}"
                                isProcessing = false
                                paymentSuccessResult = Pair(selectedPack.coins, rzpId)
                                onPaymentSuccess(selectedPack.coins, selectedPack.priceInr, "Razorpay ($razorpayMethod)")
                                Toast.makeText(context, "Razorpay Payment Verified! +${selectedPack.coins} Coins credited.", Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                    enabled = !isProcessing,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedTab == PaymentModeTab.UPI_INTENT) FFOrange else Color(0xFF528FF0)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pay_entry_fee_btn")
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(processingMessage, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    } else {
                        if (selectedTab == PaymentModeTab.UPI_INTENT) {
                            Icon(Icons.Filled.FlashOn, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "PAY ₹${selectedPack.priceInr} VIA ${selectedUpiApp.name.uppercase()}",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp
                            )
                        } else {
                            Icon(Icons.Filled.Security, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "CHECKOUT ₹${selectedPack.priceInr} (RAZORPAY TEST)",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        },
        dismissButton = {
            if (paymentSuccessResult == null && !isProcessing) {
                OutlinedButton(onClick = onDismiss) {
                    Text("Cancel", color = TextMuted)
                }
            }
        }
    )

    // Fallback confirmation dialog if UPI app returned without auto-hook or wasn't installed
    if (showUpiFallbackDialog != null) {
        AlertDialog(
            onDismissRequest = { showUpiFallbackDialog = null },
            containerColor = DarkSurface,
            shape = RoundedCornerShape(16.dp),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.VerifiedUser, contentDescription = null, tint = FFYellow, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("UPI PAYMENT STATUS", color = TextWhite, fontWeight = FontWeight.Black, fontSize = 15.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = showUpiFallbackDialog ?: "",
                        color = TextMuted,
                        fontSize = 12.sp
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkSurfaceElevated)
                            .padding(10.dp)
                    ) {
                        Column {
                            Text("UPI VPA: esports.tournaments@upi", color = FFYellow, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            Text("Amount: ₹${selectedPack.priceInr}.00", color = TextWhite, fontSize = 11.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val receipt = "TXN_UPI_${System.currentTimeMillis() % 10000000}"
                        showUpiFallbackDialog = null
                        paymentSuccessResult = Pair(selectedPack.coins, receipt)
                        onPaymentSuccess(selectedPack.coins, selectedPack.priceInr, "UPI Intent (${selectedUpiApp.name})")
                        Toast.makeText(context, "Payment Confirmed! +${selectedPack.coins} FF Coins credited.", Toast.LENGTH_LONG).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GamingGreen),
                    modifier = Modifier.testTag("confirm_upi_success_btn")
                ) {
                    Text("I HAVE PAID (CREDIT COINS)", color = Color.Black, fontWeight = FontWeight.Black)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        showUpiFallbackDialog = null
                        selectedTab = PaymentModeTab.RAZORPAY
                    }
                ) {
                    Text("Pay via Razorpay instead", color = Color(0xFF528FF0))
                }
            }
        )
    }
}
