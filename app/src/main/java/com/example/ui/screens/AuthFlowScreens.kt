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
    onFinish: (isLoggedIn: Boolean) -> Unit
) {
    val alphaAnim = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        alphaAnim.animateTo(1f, animationSpec = tween(1000))
        delay(600)
        onFinish(viewModel.isLoggedIn())
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
    onLanguageSelected: () -> Unit,
    onBack: (() -> Unit)? = null
) {
    val currentLang by viewModel.language.collectAsState()
    var selected by remember { mutableStateOf(currentLang) }
    val isLoggedIn = viewModel.isLoggedIn()

    Scaffold(
        containerColor = RuralBackground,
        topBar = {
            if (isLoggedIn || onBack != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { onBack?.invoke() ?: onLanguageSelected() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                    Text(
                        text = "Change Language",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextPrimary
                    )
                }
            }
        }
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
                Triple(AppLanguage.MARATHI, "मराठी", "महाराष्ट्रातील शेतकरी व कारागीर बांधवांसाठी (Default)"),
                Triple(AppLanguage.HINDI, "हिंदी", "भारत के किसानों, कारीगरों और ग्राहकों के लिए"),
                Triple(AppLanguage.ENGLISH, "English", "For Farmers, Buyers, Artisans & Delivery Partners")
            )

            languages.forEach { (lang, native, desc) ->
                val isChosen = selected == lang
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable {
                            selected = lang
                            viewModel.setLanguage(lang)
                        }
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
                            onClick = {
                                selected = lang
                                viewModel.setLanguage(lang)
                            },
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
                    text = "Continue / पुढे चला",
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
                        onClick = {
                            selectedRole = role
                            username = when (role) {
                                UserRole.BUYER -> "9822945678"
                                UserRole.SELLER -> "9822054321"
                                UserRole.DELIVERY -> "9822112233"
                                UserRole.ADMIN -> "ADMIN-001"
                            }
                            password = when (role) {
                                UserRole.ADMIN -> "admin123"
                                else -> "gram1234"
                            }
                            errorMsg = null
                        },
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
                label = { Text(if (selectedRole == UserRole.ADMIN) "Admin ID (ADMIN-001) / Mobile" else AppStrings.get("email_or_phone", lang)) },
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
                        viewModel.login(username, password, selectedRole)
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
    
    // Step 1: Full Name
    var fullName by remember { mutableStateOf("") }
    
    // Step 2: Mobile Number
    var mobile by remember { mutableStateOf("") }
    
    // Step 3 & 4: Mobile OTP verification
    var isOtpSent by remember { mutableStateOf(false) }
    var generatedOtp by remember { mutableStateOf("4829") }
    var enteredOtp by remember { mutableStateOf("") }
    var isMobileVerified by remember { mutableStateOf(false) }
    var otpErrorMsg by remember { mutableStateOf<String?>(null) }
    var otpSuccessBanner by remember { mutableStateOf<String?>(null) }
    
    // Step 5: Email ID (No OTP required)
    var email by remember { mutableStateOf("") }
    
    // Step 6: Password
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    
    // Step 7: Select User Role (Only Buyer, Farmer/Seller, Delivery Boy - NEVER Admin!)
    var selectedRole by remember { mutableStateOf(UserRole.BUYER) }
    
    // Step 8: Location / Address (Buldhana District only)
    var village by remember { mutableStateOf("Chikhli") }
    val buldhanaTalukas = listOf(
        "Buldhana", "Chikhli", "Deulgaon Raja", "Jalgaon (Jamod)", "Khamgaon",
        "Lonar", "Malkapur", "Mehkar", "Motala", "Nandura", "Sangrampur", "Shegaon", "Sindkhed Raja"
    )
    var taluka by remember { mutableStateOf("Chikhli") }
    var talukaExpanded by remember { mutableStateOf(false) }
    var pincode by remember { mutableStateOf("443201") }
    var serviceArea by remember { mutableStateOf("Khamgaon") }
    var formError by remember { mutableStateOf<String?>(null) }

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
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = AgriGreenDark
            )
            Text(
                text = "Buldhana District Rural Marketplace Registration",
                fontSize = 13.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ================= STEP 1: Full Name =================
            Text(
                text = "Step 1: Full Name",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = AgriGreenDark
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it; formError = null },
                label = { Text("Enter Full Name") },
                placeholder = { Text("e.g. Gajanan Patil") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("register_fullname_input")
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ================= STEP 2: Mobile Number =================
            Text(
                text = "Step 2: Mobile Number",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = AgriGreenDark
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = mobile,
                onValueChange = {
                    if (it.length <= 10 && it.all { ch -> ch.isDigit() }) {
                        mobile = it
                        formError = null
                        if (isMobileVerified) isMobileVerified = false
                    }
                },
                label = { Text("10-Digit Mobile Number") },
                placeholder = { Text("98220XXXXX") },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                trailingIcon = {
                    if (isMobileVerified) {
                        Icon(Icons.Default.CheckCircle, contentDescription = "Verified", tint = AgriGreenPrimary)
                    }
                },
                singleLine = true,
                enabled = !isMobileVerified,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("register_mobile_input")
            )

            // ================= STEP 3: Send Mobile OTP Button =================
            Spacer(modifier = Modifier.height(8.dp))
            if (!isMobileVerified) {
                Button(
                    onClick = {
                        if (mobile.length == 10) {
                            generatedOtp = (1000..9999).random().toString()
                            isOtpSent = true
                            enteredOtp = ""
                            otpErrorMsg = null
                            otpSuccessBanner = "OTP sent to +91 $mobile: $generatedOtp (Test OTP: $generatedOtp)"
                        } else {
                            otpErrorMsg = "Please enter a valid 10-digit mobile number first"
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = if (isOtpSent) Color(0xFF0288D1) else AgriGreenPrimary)
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isOtpSent) "Resend Mobile OTP" else "Step 3: Send Mobile OTP",
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Banner showing OTP feedback
            if (otpSuccessBanner != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE1F5FE)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Sms, contentDescription = null, tint = Color(0xFF0288D1))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = otpSuccessBanner ?: "",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF01579B)
                        )
                    }
                }
            }

            // ================= STEP 4: Enter Mobile OTP =================
            if (isOtpSent && !isMobileVerified) {
                Spacer(modifier = Modifier.height(14.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = AgriGreenContainer),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Step 4: Enter Mobile OTP",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = AgriGreenDark
                        )
                        Text(
                            text = "Enter the 4-digit code sent to +91 $mobile",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = enteredOtp,
                            onValueChange = {
                                if (it.length <= 4) enteredOtp = it
                                otpErrorMsg = null
                            },
                            label = { Text("4-Digit OTP") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("register_otp_input")
                        )

                        if (otpErrorMsg != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = otpErrorMsg ?: "", color = RateDownRed, fontSize = 12.sp)
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                if (enteredOtp.trim() == generatedOtp.trim() || enteredOtp.trim() == "4829" || enteredOtp.trim() == "1234") {
                                    isMobileVerified = true
                                    otpErrorMsg = null
                                    otpSuccessBanner = null
                                } else {
                                    otpErrorMsg = "Invalid OTP. Please enter $generatedOtp"
                                }
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
                        ) {
                            Text("Verify Mobile OTP", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            if (isMobileVerified) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AgriGreenPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "✓ Mobile Verified: +91 $mobile",
                            color = AgriGreenDark,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                // ================= STEP 5: Email ID (No OTP required) =================
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Step 5: Email ID (Optional, No OTP required)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = AgriGreenDark
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    placeholder = { Text("e.g. gajanan@gmail.com") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // ================= STEP 6: Password =================
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Step 6: Password",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = AgriGreenDark
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; formError = null },
                    label = { Text("Password") },
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
                    modifier = Modifier.fillMaxWidth()
                )

                // ================= STEP 7: Select User Role (Buyer, Seller, Delivery ONLY - NO ADMIN) =================
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Step 7: Select User Role",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = AgriGreenDark
                )
                Text(
                    text = "Public registration allows Buyer, Farmer/Seller, and Delivery Boy.",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val publicRoles = listOf(UserRole.BUYER, UserRole.SELLER, UserRole.DELIVERY)
                    publicRoles.forEach { role ->
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
                                        UserRole.BUYER -> "🛒 Buyer\nखरेदीदार"
                                        UserRole.SELLER -> "🌾 Farmer\nशेतकरी"
                                        UserRole.DELIVERY -> "🚚 Delivery\nडिलिव्हरी"
                                        else -> ""
                                    },
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                // ================= STEP 8: Address & Service Area =================
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Step 8: Rural Address Details",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = AgriGreenDark
                )
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = village,
                    onValueChange = { village = it },
                    label = { Text("Village / Town / City") },
                    placeholder = { Text("e.g. Chikhli, Bibi, Undri, Khamgaon") },
                    leadingIcon = { Icon(Icons.Default.Home, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

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

                OutlinedTextField(
                    value = pincode,
                    onValueChange = { if (it.length <= 6) pincode = it },
                    label = { Text("PIN Code") },
                    leadingIcon = { Icon(Icons.Default.PinDrop, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Fixed District Card
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
                                text = "📍 Buldhana District, Maharashtra (Fixed)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = AgriGreenDark
                            )
                            Text(
                                text = "Platform operates exclusively within Buldhana District",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }

                // If Delivery Boy: Collect Service Area
                if (selectedRole == UserRole.DELIVERY) {
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = serviceArea,
                        onValueChange = { serviceArea = it },
                        label = { Text("Delivery Boy Service Area / Town") },
                        placeholder = { Text("e.g. Khamgaon, Buldhana, Chikhli, Shegaon") },
                        leadingIcon = { Icon(Icons.Default.TwoWheeler, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (formError != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = formError ?: "", color = RateDownRed, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Complete Registration Button
                Button(
                    onClick = {
                        if (fullName.isBlank()) {
                            formError = "Please enter your Full Name"
                        } else if (password.length < 4) {
                            formError = "Please enter a password with at least 4 characters"
                        } else {
                            try {
                                viewModel.registerUser(
                                    name = fullName,
                                    phone = mobile,
                                    email = email,
                                    role = selectedRole,
                                    village = village,
                                    taluka = taluka,
                                    pincode = pincode,
                                    serviceArea = serviceArea
                                )
                                onRegisterSuccess()
                            } catch (e: Exception) {
                                formError = e.message ?: "Registration error"
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("register_submit_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
                ) {
                    Text(
                        text = "Complete Registration",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
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
