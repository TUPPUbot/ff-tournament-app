package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
import com.example.ui.theme.GamingRed
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class WithdrawPreset(
    val amountInr: Int,
    val requiredCoins: Int
)

val WITHDRAW_PRESETS = listOf(
    WithdrawPreset(100, 100),
    WithdrawPreset(200, 200),
    WithdrawPreset(500, 500)
)

@Composable
fun WithdrawSheetModal(
    currentCoins: Int,
    savedUpiId: String,
    onDismiss: () -> Unit,
    onWithdrawSuccess: (amountCoins: Int, amountInr: Int, upiId: String, holderName: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current

    var selectedPreset by remember { mutableStateOf<WithdrawPreset?>(WITHDRAW_PRESETS[0]) }
    var customInrText by remember { mutableStateOf("") }
    var upiIdInput by remember { mutableStateOf(if (savedUpiId.isNotBlank()) savedUpiId else "soul.mortal@okaxis") }
    var holderNameInput by remember { mutableStateOf("Esports Champion") }

    var isProcessing by remember { mutableStateOf(false) }
    var processingMessage by remember { mutableStateOf("Processing Payout...") }
    var payoutSuccessResult by remember { mutableStateOf<Triple<Int, Int, String>?>(null) } // Coins, INR, Reference

    val selectedInr = if (selectedPreset != null) {
        selectedPreset!!.amountInr
    } else {
        customInrText.toIntOrNull() ?: 0
    }

    val requiredCoins = selectedInr // 1 Rupee = 1 Coin
    val isBalanceSufficient = currentCoins >= requiredCoins && requiredCoins > 0
    val isValidUpi = upiIdInput.contains("@") && upiIdInput.length >= 5
    val canSubmit = isBalanceSufficient && isValidUpi && selectedInr >= 10

    AlertDialog(
        onDismissRequest = {
            if (!isProcessing) onDismiss()
        },
        containerColor = DarkSurface,
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, GamingGreen.copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
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
                            .background(GamingGreen.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AccountBalanceWallet,
                            contentDescription = null,
                            tint = GamingGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "WITHDRAW TO UPI",
                            color = TextWhite,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Lock, contentDescription = null, tint = GamingGreen, modifier = Modifier.size(11.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "Instant IMPS Bank Clearing (0% Fee)",
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
            if (payoutSuccessResult != null) {
                // Success View
                val (coinsDeducted, inrAmount, payoutRef) = payoutSuccessResult!!
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
                        text = "WITHDRAWAL DISPATCHED!",
                        color = TextWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black
                    )

                    Text(
                        text = "₹$inrAmount.00 TRANSFER INITIATED",
                        color = GamingGreen,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    Text(
                        text = "Funds sent to $upiIdInput. Clearing within 5-15 minutes.",
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
                                Text("Payout Ref", color = TextMuted, fontSize = 11.sp)
                                Text(payoutRef, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Coins Redeemed", color = TextMuted, fontSize = 11.sp)
                                Text("🪙 $coinsDeducted Coins", color = FFGold, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Beneficiary UPI", color = TextMuted, fontSize = 11.sp)
                                Text(upiIdInput, color = GamingGreen, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Balance Header
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(DarkSurfaceElevated)
                                .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("YOUR WITHDRAWABLE BALANCE", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    Text(
                                        text = "🪙 $currentCoins FF Coins",
                                        color = FFGold,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                    Text(
                                        text = "≈ ₹$currentCoins.00 Real Cash (1 Coin = ₹1)",
                                        color = TextDark,
                                        fontSize = 9.sp
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(GamingGreen.copy(alpha = 0.15f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("0% FEE", color = GamingGreen, fontSize = 10.sp, fontWeight = FontWeight.Black)
                                }
                            }
                        }
                    }

                    // Choose Preset Amount
                    item {
                        Text(
                            text = "SELECT CASHOUT AMOUNT (INR)",
                            color = TextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            WITHDRAW_PRESETS.forEach { preset ->
                                val isSelected = selectedPreset == preset
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) GamingGreen.copy(alpha = 0.15f) else DarkSurfaceElevated)
                                        .border(
                                            1.5.dp,
                                            if (isSelected) GamingGreen else DarkBorder,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable {
                                            selectedPreset = preset
                                            customInrText = ""
                                        }
                                        .padding(vertical = 10.dp, horizontal = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = "₹${preset.amountInr}",
                                            color = if (isSelected) GamingGreen else TextWhite,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                        Text(
                                            text = "🪙 ${preset.requiredCoins}",
                                            color = if (isSelected) FFGold else TextMuted,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Custom Amount Input
                    item {
                        OutlinedTextField(
                            value = customInrText,
                            onValueChange = {
                                customInrText = it.filter { ch -> ch.isDigit() }
                                if (customInrText.isNotEmpty()) {
                                    selectedPreset = null
                                }
                            },
                            label = { Text("Or Custom Amount (₹)", color = TextMuted, fontSize = 11.sp) },
                            placeholder = { Text("Min ₹10 (10 coins)", color = TextDark, fontSize = 11.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GamingGreen,
                                unfocusedBorderColor = DarkBorder,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite
                            ),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("custom_withdraw_inr_input")
                        )
                    }

                    // UPI ID Input
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "TRANSFER DESTINATION (UPI ID)",
                                color = TextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )

                            OutlinedTextField(
                                value = upiIdInput,
                                onValueChange = { upiIdInput = it.trim() },
                                label = { Text("Receiver UPI ID / VPA", color = TextMuted, fontSize = 11.sp) },
                                placeholder = { Text("e.g. mobile@upi / yourname@oksbi", color = TextDark) },
                                isError = upiIdInput.isNotEmpty() && !isValidUpi,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GamingGreen,
                                    unfocusedBorderColor = DarkBorder,
                                    focusedTextColor = TextWhite,
                                    unfocusedTextColor = TextWhite
                                ),
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("withdraw_upi_input")
                            )

                            if (upiIdInput.isNotEmpty() && !isValidUpi) {
                                Text("Please enter a valid UPI ID (e.g. name@bank)", color = GamingRed, fontSize = 10.sp)
                            }
                        }
                    }

                    // Beneficiary Name Input
                    item {
                        OutlinedTextField(
                            value = holderNameInput,
                            onValueChange = { holderNameInput = it },
                            label = { Text("Account Holder Name", color = TextMuted, fontSize = 11.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GamingGreen,
                                unfocusedBorderColor = DarkBorder,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite
                            ),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("withdraw_holder_input")
                        )
                    }

                    // Summary Calculation & Sufficiency Notice
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(DarkSurfaceElevated)
                                .border(
                                    1.dp,
                                    if (!isBalanceSufficient && selectedInr > 0) GamingRed.copy(alpha = 0.5f) else DarkBorder,
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(10.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Amount to Cash Out", color = TextMuted, fontSize = 10.sp)
                                    Text("₹$selectedInr.00", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Coins Required", color = TextMuted, fontSize = 10.sp)
                                    Text("🪙 $requiredCoins Coins", color = FFGold, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Transfer Fee", color = TextMuted, fontSize = 10.sp)
                                    Text("₹0.00 (FREE)", color = GamingGreen, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                }

                                if (!isBalanceSufficient && selectedInr > 0) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "⚠️ Insufficient balance! You need 🪙 ${requiredCoins - currentCoins} more coins to withdraw ₹$selectedInr.",
                                        color = GamingRed,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (payoutSuccessResult != null) {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = GamingGreen),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("withdraw_done_btn")
                ) {
                    Text("DONE & CLOSE", color = Color.Black, fontWeight = FontWeight.Black)
                }
            } else {
                Button(
                    onClick = {
                        isProcessing = true
                        processingMessage = "Connecting to NPCI UPI Clearing..."
                        coroutineScope.launch {
                            delay(900)
                            processingMessage = "Verifying UPI VPA & Dispatching ₹$selectedInr..."
                            delay(800)
                            val payoutRef = "PO_UPI_${System.currentTimeMillis() % 10000000}"
                            isProcessing = false
                            payoutSuccessResult = Triple(requiredCoins, selectedInr, payoutRef)
                            onWithdrawSuccess(requiredCoins, selectedInr, upiIdInput, holderNameInput)
                            Toast.makeText(context, "Withdrawal of ₹$selectedInr to $upiIdInput initiated!", Toast.LENGTH_LONG).show()
                        }
                    },
                    enabled = canSubmit && !isProcessing,
                    colors = ButtonDefaults.buttonColors(containerColor = GamingGreen),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("submit_withdraw_btn")
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(
                            color = Color.Black,
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(processingMessage, color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    } else {
                        Icon(Icons.Filled.FlashOn, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (selectedInr > 0) "WITHDRAW ₹$selectedInr TO UPI" else "ENTER WITHDRAW AMOUNT",
                            color = Color.Black,
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        },
        dismissButton = {
            if (payoutSuccessResult == null && !isProcessing) {
                OutlinedButton(onClick = onDismiss) {
                    Text("Cancel", color = TextMuted)
                }
            }
        }
    )
}
