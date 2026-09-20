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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkBorder
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

@Composable
fun AuthScreen(
    onLogin: (String, String, Boolean) -> Boolean,
    onSignup: (String, String, String) -> Boolean,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Login, 1 = Signup

    // Login state
    var loginIdentifier by remember { mutableStateOf("Vampire★FF") }
    var loginUid by remember { mutableStateOf("182939401") }

    // Signup state
    var signupIgn by remember { mutableStateOf("") }
    var signupUid by remember { mutableStateOf("") }
    var signupEmail by remember { mutableStateOf("") }
    var signupPassword by remember { mutableStateOf("") }
    var agreedRules by remember { mutableStateOf(true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // App Logo & Header
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.linearGradient(listOf(FFOrange, FFFlame))
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.SportsEsports,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(34.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "FF TOURNAMENT",
            color = FFYellow,
            fontSize = 22.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp
        )

        Text(
            text = "FREE FIRE ESPORTS ARENA",
            color = TextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Auth Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = DarkSurface,
            contentColor = FFOrange,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = FFOrange
                )
            },
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .border(1.dp, DarkBorder, RoundedCornerShape(10.dp))
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Text(
                        "LOGIN",
                        color = if (selectedTab == 0) FFYellow else TextMuted,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                },
                modifier = Modifier.testTag("auth_tab_login")
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Text(
                        "SIGN UP",
                        color = if (selectedTab == 1) FFYellow else TextMuted,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                },
                modifier = Modifier.testTag("auth_tab_signup")
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(DarkSurface)
                .border(1.dp, DarkBorder, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            if (selectedTab == 0) {
                // Login Form
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = loginIdentifier,
                        onValueChange = { loginIdentifier = it },
                        label = { Text("In-Game Name (IGN) / Email", color = TextMuted) },
                        leadingIcon = {
                            Icon(Icons.Filled.Person, contentDescription = null, tint = FFOrange)
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FFOrange,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("login_ign_input")
                    )

                    OutlinedTextField(
                        value = loginUid,
                        onValueChange = { loginUid = it },
                        label = { Text("Free Fire UID / Password", color = TextMuted) },
                        leadingIcon = {
                            Icon(Icons.Filled.Lock, contentDescription = null, tint = FFOrange)
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FFOrange,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("login_uid_input")
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(
                        onClick = {
                            onLogin(loginIdentifier, loginUid, false)
                            Toast.makeText(context, "Logged in as $loginIdentifier", Toast.LENGTH_SHORT).show()
                            onClose()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = FFOrange),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().testTag("login_submit_btn")
                    ) {
                        Text("SIGN IN", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    // 1-Tap Quick Logins for testing!
                    Text(
                        text = "QUICK 1-TAP DEMO ACCESS",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                onLogin("Vampire★FF", "182939401", false)
                                Toast.makeText(context, "Logged in as Player Vampire★FF", Toast.LENGTH_SHORT).show()
                                onClose()
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).testTag("demo_player_login_btn")
                        ) {
                            Text("🎮 Player Demo", color = FFYellow, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                onLogin("AdminReferee", "999999999", true)
                                Toast.makeText(context, "Logged in as Esports Admin", Toast.LENGTH_SHORT).show()
                                onClose()
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).testTag("demo_admin_login_btn")
                        ) {
                            Text("🛡️ Admin Demo", color = FFOrange, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                // Signup Form
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = signupIgn,
                        onValueChange = { signupIgn = it },
                        label = { Text("Free Fire IGN (In-Game Name)", color = TextMuted) },
                        placeholder = { Text("e.g. Thunder_FF", color = TextDark) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FFOrange,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("signup_ign_input")
                    )

                    OutlinedTextField(
                        value = signupUid,
                        onValueChange = { signupUid = it },
                        label = { Text("Free Fire 10-digit UID", color = TextMuted) },
                        placeholder = { Text("e.g. 1928374650", color = TextDark) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FFOrange,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("signup_uid_input")
                    )

                    OutlinedTextField(
                        value = signupEmail,
                        onValueChange = { signupEmail = it },
                        label = { Text("Email Address", color = TextMuted) },
                        placeholder = { Text("e.g. player@gmail.com", color = TextDark) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FFOrange,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("signup_email_input")
                    )

                    OutlinedTextField(
                        value = signupPassword,
                        onValueChange = { signupPassword = it },
                        label = { Text("Password", color = TextMuted) },
                        visualTransformation = PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FFOrange,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("signup_password_input")
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { agreedRules = !agreedRules }
                    ) {
                        Checkbox(
                            checked = agreedRules,
                            onCheckedChange = { agreedRules = it },
                            colors = CheckboxDefaults.colors(checkedColor = FFOrange)
                        )
                        Text(
                            text = "I agree to fair play rules (Mobile only, Emulators strictly prohibited).",
                            color = TextMuted,
                            fontSize = 11.sp,
                            lineHeight = 14.sp
                        )
                    }

                    Button(
                        onClick = {
                            if (signupIgn.isBlank() || signupUid.isBlank()) {
                                Toast.makeText(context, "Please enter IGN and UID", Toast.LENGTH_SHORT).show()
                            } else {
                                onSignup(signupIgn, signupUid, signupEmail.ifBlank { "$signupIgn@ff.com" })
                                Toast.makeText(context, "Account registered for $signupIgn!", Toast.LENGTH_SHORT).show()
                                onClose()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = FFOrange),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().testTag("signup_submit_btn")
                    ) {
                        Text("CREATE ACCOUNT", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Continue as Guest / Back button
        Text(
            text = "Continue to App as Guest",
            color = FFYellow,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .clickable { onClose() }
                .padding(8.dp)
                .testTag("auth_guest_continue_btn")
        )
    }
}
