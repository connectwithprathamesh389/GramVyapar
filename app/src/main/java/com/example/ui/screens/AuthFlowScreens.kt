package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.AppLanguage
import com.example.data.model.UserRole
import com.example.ui.i18n.AppStrings
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppDestination
import com.example.ui.viewmodel.GramVyaparViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    viewModel: GramVyaparViewModel,
    onFinish: () -> Unit
) {
    val alphaAnim = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        alphaAnim.animateTo(1f, animationSpec = tween(1200))
        delay(1200)
        onFinish()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        AgriGreenDark,
                        AgriGreenPrimary,
                        Color(0xFF134D17)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(24.dp)
                .alpha(alphaAnim.value)
        ) {
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(3.dp, CropGold, CircleShape)
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_app_icon),
                    contentDescription = "GramVyapar Logo",
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "GramVyapar",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )

            Text(
                text = "ग्रामव्यापार • RuralKart",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = SaffronAccentLight
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Farm-to-Market E-Commerce & Rural Entrepreneurship",
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.85f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            CircularProgressIndicator(
                color = CropGold,
                strokeWidth = 3.dp,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun LanguageScreen(
    viewModel: GramVyaparViewModel,
    onLanguageSelected: () -> Unit
) {
    val currentLang by viewModel.language.collectAsState()
    var selected by remember { mutableStateOf(currentLang) }

    Scaffold(
        containerColor = RuralBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_app_icon),
                contentDescription = null,
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Choose Your Language",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "भाषा निवडा / अपनी भाषा चुनें",
                fontSize = 15.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(28.dp))

            val languages = listOf(
                Triple(AppLanguage.MARATHI, "मराठी", "महाराष्ट्रातील शेतकरी व कारागीर बांधवांसाठी"),
                Triple(AppLanguage.HINDI, "हिंदी", "भारत के किसानों, कारीगरों और ग्राहकों के लिए"),
                Triple(AppLanguage.ENGLISH, "English", "For Farmers, Buyers, Artisans & Delivery Partners")
            )

            languages.forEach { (lang, native, desc) ->
                val isChosen = selected == lang
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable { selected = lang }
                        .testTag("lang_select_${lang.name}"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isChosen) AgriGreenContainer else RuralSurface
                    ),
                    border = if (isChosen) CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.horizontalGradient(listOf(AgriGreenPrimary, AgriGreenLight)),
                        width = 2.dp
                    ) else null
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isChosen,
                            onClick = { selected = lang },
                            colors = RadioButtonDefaults.colors(selectedColor = AgriGreenPrimary)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = native,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isChosen) AgriGreenDark else TextPrimary
                            )
                            Text(
                                text = desc,
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    viewModel.setLanguage(selected)
                    onLanguageSelected()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("continue_language_btn"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
            ) {
                Text(
                    text = AppStrings.get("continue_btn", selected),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun LoginScreen(
    viewModel: GramVyaparViewModel,
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit,
    onForgotPasswordClick: () -> Unit
) {
    val lang by viewModel.language.collectAsState()
    val user by viewModel.user.collectAsState()
    var username by remember { mutableStateOf("9822054321") }
    var password by remember { mutableStateOf("gram1234") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(true) }
    var selectedRole by remember { mutableStateOf(user.role) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    Scaffold(
        containerColor = RuralBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Image(
                painter = painterResource(id = R.drawable.img_app_icon),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(16.dp))
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = AppStrings.get("login_title", lang),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = AgriGreenDark,
                textAlign = TextAlign.Center
            )

            Text(
                text = AppStrings.get("login_subtitle", lang),
                fontSize = 13.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Quick Role Switcher demo tab for testing all 4 roles instantly
            Text(
                text = "Login as Role:",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                UserRole.values().forEach { role ->
                    val isSel = selectedRole == role
                    OutlinedButton(
                        onClick = { selectedRole = role },
                        modifier = Modifier
                            .weight(1f)
                            .padding(2.dp),
                        contentPadding = PaddingValues(horizontal = 2.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isSel) AgriGreenPrimary else Color.Transparent,
                            contentColor = if (isSel) Color.White else TextPrimary
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = Brush.horizontalGradient(listOf(AgriGreenPrimary, AgriGreenLight))
                        )
                    ) {
                        Text(
                            text = when (role) {
                                UserRole.BUYER -> "Buyer"
                                UserRole.SELLER -> "Farmer"
                                UserRole.DELIVERY -> "Delivery"
                                UserRole.ADMIN -> "Admin"
                            },
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it; errorMsg = null },
                label = { Text(AppStrings.get("email_or_phone", lang)) },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_username_input")
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it; errorMsg = null },
                label = { Text(AppStrings.get("password", lang)) },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = null
                        )
                    }
                },
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_password_input")
            )

            if (errorMsg != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = errorMsg ?: "",
                    color = RateDownRed,
                    fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.Start)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = { rememberMe = it },
                        colors = CheckboxDefaults.colors(checkedColor = AgriGreenPrimary)
                    )
                    Text(
                        text = AppStrings.get("remember_me", lang),
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
                TextButton(onClick = onForgotPasswordClick) {
                    Text(
                        text = AppStrings.get("forgot_password", lang),
                        fontSize = 12.sp,
                        color = SaffronAccent,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (username.isBlank() || password.isBlank()) {
                        errorMsg = "Please enter valid mobile/email and password"
                    } else {
                        viewModel.switchRole(selectedRole)
                        onLoginSuccess()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("login_submit_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
            ) {
                Text(
                    text = AppStrings.get("login_button", lang),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onRegisterClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("login_register_button"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = AppStrings.get("register_prompt", lang),
                    fontSize = 14.sp,
                    color = AgriGreenPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun RegisterScreen(
    viewModel: GramVyaparViewModel,
    onRegisterSuccess: () -> Unit,
    onBackToLogin: () -> Unit
) {
    val lang by viewModel.language.collectAsState()
    var fullName by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(UserRole.SELLER) }
    var village by remember { mutableStateOf("Chikhli") }
    val buldhanaTalukas = listOf(
        "Buldhana", "Chikhli", "Deulgaon Raja", "Jalgaon (Jamod)", "Khamgaon",
        "Lonar", "Malkapur", "Mehkar", "Motala", "Nandura", "Sangrampur", "Shegaon", "Sindkhed Raja"
    )
    var taluka by remember { mutableStateOf("Chikhli") }
    var talukaExpanded by remember { mutableStateOf(false) }
    val district = "Buldhana"
    val state = "Maharashtra"
    var pincode by remember { mutableStateOf("443201") }
    var aadhaarUploaded by remember { mutableStateOf(true) }
    var termsAccepted by remember { mutableStateOf(true) }
    var showOtpDialog by remember { mutableStateOf(false) }
    var otpInput by remember { mutableStateOf("4829") }

    Scaffold(
        containerColor = RuralBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            IconButton(onClick = onBackToLogin) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }

            Text(
                text = AppStrings.get("register_title", lang),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = AgriGreenDark
            )
            Text(
                text = "Join India's direct farm-to-market rural network",
                fontSize = 13.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Role selection
            Text(
                text = AppStrings.get("select_role", lang),
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(UserRole.BUYER, UserRole.SELLER, UserRole.DELIVERY).forEach { role ->
                    val isSel = selectedRole == role
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedRole = role },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSel) AgriGreenContainer else RuralSurface
                        ),
                        border = if (isSel) CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.horizontalGradient(listOf(AgriGreenPrimary, AgriGreenLight))
                        ) else null
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = when (role) {
                                    UserRole.BUYER -> "🛒 Buyer"
                                    UserRole.SELLER -> "🌾 Farmer"
                                    UserRole.DELIVERY -> "🚚 Delivery"
                                    else -> ""
                                },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text(AppStrings.get("full_name", lang)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = mobile,
                onValueChange = { mobile = it },
                label = { Text("Mobile Number (OTP)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email (Optional)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(AppStrings.get("password", lang)) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Rural Address Details",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = village,
                onValueChange = { village = it },
                label = { Text(AppStrings.get("village", lang)) },
                placeholder = { Text("e.g. Chikhli, Bibi, Undri, etc.") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Taluka selection strictly within Buldhana District
            @OptIn(ExperimentalMaterial3Api::class)
            ExposedDropdownMenuBox(
                expanded = talukaExpanded,
                onExpandedChange = { talukaExpanded = !talukaExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = taluka,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Taluka (Buldhana District)") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = talukaExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = talukaExpanded,
                    onDismissRequest = { talukaExpanded = false }
                ) {
                    buldhanaTalukas.forEach { t ->
                        DropdownMenuItem(
                            text = { Text(t) },
                            onClick = {
                                taluka = t
                                talukaExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Fixed Location: Buldhana District, Maharashtra (Section 2)
            Card(
                colors = CardDefaults.cardColors(containerColor = AgriGreenContainer),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = AgriGreenDark)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "📍 Buldhana District, Maharashtra",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = AgriGreenDark
                        )
                        Text(
                            text = "Fixed strictly for local rural marketplace in Buldhana",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            if (selectedRole == UserRole.SELLER || selectedRole == UserRole.DELIVERY) {
                Spacer(modifier = Modifier.height(14.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = SaffronContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = SaffronAccent)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Farmer / Delivery KYC Verification",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = OnSaffronContainer
                            )
                        }
                        Text(
                            text = AppStrings.get("kyc_upload_note", lang),
                            fontSize = 11.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AssistChip(
                                onClick = { aadhaarUploaded = true },
                                label = { Text(if (aadhaarUploaded) "✓ Aadhaar Verified" else "Upload Aadhaar Card") },
                                leadingIcon = { Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            AssistChip(
                                onClick = {},
                                label = { Text("7/12 Land Record (Opt)") }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = termsAccepted,
                    onCheckedChange = { termsAccepted = it },
                    colors = CheckboxDefaults.colors(checkedColor = AgriGreenPrimary)
                )
                Text(
                    text = AppStrings.get("terms_accept", lang),
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { showOtpDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("register_submit_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
            ) {
                Text(
                    text = "Verify OTP & Create Account",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    if (showOtpDialog) {
        AlertDialog(
            onDismissRequest = { showOtpDialog = false },
            title = {
                Text("Verify Mobile OTP", fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text(
                        text = "We sent a 4-digit verification code to +91 ${mobile.ifEmpty { "9822054321" }}",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = otpInput,
                        onValueChange = { otpInput = it },
                        label = { Text("Enter 4-Digit OTP") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showOtpDialog = false
                        viewModel.switchRole(selectedRole)
                        onRegisterSuccess()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
                ) {
                    Text(AppStrings.get("verify_otp", lang))
                }
            },
            dismissButton = {
                TextButton(onClick = { showOtpDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ForgotPasswordScreen(
    viewModel: GramVyaparViewModel,
    onBackToLogin: () -> Unit
) {
    val lang by viewModel.language.collectAsState()
    var mobileOrEmail by remember { mutableStateOf("") }
    var otpSent by remember { mutableStateOf(false) }
    var otp by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var isResetSuccess by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = RuralBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(
                onClick = onBackToLogin,
                modifier = Modifier.align(Alignment.Start)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Reset Your Password",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = AgriGreenDark
            )
            Text(
                text = "Enter your registered mobile or email to receive a secure OTP",
                fontSize = 13.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = mobileOrEmail,
                onValueChange = { mobileOrEmail = it },
                label = { Text(AppStrings.get("email_or_phone", lang)) },
                modifier = Modifier.fillMaxWidth()
            )

            if (otpSent) {
                Spacer(modifier = Modifier.height(14.dp))
                OutlinedTextField(
                    value = otp,
                    onValueChange = { otp = it },
                    label = { Text("Enter OTP (Default: 4829)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(14.dp))
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("New Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (!otpSent) {
                Button(
                    onClick = { otpSent = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
                ) {
                    Text("Send Verification OTP", fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = {
                        isResetSuccess = true
                        onBackToLogin()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
                ) {
                    Text("Save New Password & Login", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
