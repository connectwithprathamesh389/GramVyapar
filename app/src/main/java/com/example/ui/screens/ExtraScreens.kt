package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.i18n.AppStrings
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppDestination
import com.example.ui.viewmodel.GramVyaparViewModel

// 1. SECTION 17: DELIVERY BOY EXPERIENCE
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryPartnerScreen(
    viewModel: GramVyaparViewModel,
    onBack: () -> Unit
) {
    val orders by viewModel.orders.collectAsState()
    val user by viewModel.user.collectAsState()

    var selectedOrderForOtp by remember { mutableStateOf<Order?>(null) }
    var enteredOtp by remember { mutableStateOf("") }
    var otpError by remember { mutableStateOf<String?>(null) }
    var deliverySuccessMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("🚚 Delivery Hub - Buldhana", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                        Text("Partner: ${user.name} • ${user.serviceArea ?: user.village}", fontSize = 11.sp, color = TextSecondary)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = RuralSurface)
            )
        },
        containerColor = RuralBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SaffronContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "📍 Buldhana District Local Routing",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = OnSaffronContainer
                        )
                        Text(
                            text = "Service Area: ${user.serviceArea ?: "Buldhana City & Talukas"} • Active Orders: ${orders.count { it.orderStatus != OrderStatus.DELIVERED }}",
                            fontSize = 12.sp,
                            color = OnSaffronContainer
                        )
                    }
                }
            }

            if (deliverySuccessMessage != null) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = AgriGreenContainer),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("✅", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = deliverySuccessMessage ?: "",
                                color = AgriGreenDark,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            items(orders, key = { it.id }) { order ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = RuralSurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Order #${order.id}", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        when (order.orderStatus) {
                                            OrderStatus.DELIVERED -> AgriGreenContainer
                                            OrderStatus.OUT_FOR_DELIVERY -> SaffronContainer
                                            else -> Color(0xFFF1F5F9)
                                        }
                                    )
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = order.orderStatus.titleEn,
                                    color = when (order.orderStatus) {
                                        OrderStatus.DELIVERED -> AgriGreenDark
                                        OrderStatus.OUT_FOR_DELIVERY -> OnSaffronContainer
                                        else -> TextPrimary
                                    },
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Text("👤 Customer: ${order.buyerName} (${order.buyerPhone})", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        Text("📍 Address: ${order.deliveryAddress}", fontSize = 12.sp, color = TextSecondary)
                        Text(
                            text = "💰 Amount: ₹${order.totalAmount.toInt()} (${if (order.isCod) "Cash on Delivery" else "UPI Paid"})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (order.isCod) SaffronAccent else AgriGreenDark
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Stage Progression buttons
                        when (order.orderStatus) {
                            OrderStatus.PLACED, OrderStatus.CONFIRMED -> {
                                Button(
                                    onClick = {
                                        viewModel.updateOrderStatus(order.id, OrderStatus.PREPARING)
                                        deliverySuccessMessage = "Order #${order.id} accepted for pickup."
                                    },
                                    modifier = Modifier.fillMaxWidth().height(40.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = AgriGreenContainer, contentColor = AgriGreenDark)
                                ) {
                                    Text("Accept & Pick Up from Farmer", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            OrderStatus.PREPARING -> {
                                Button(
                                    onClick = {
                                        viewModel.updateOrderStatus(order.id, OrderStatus.OUT_FOR_DELIVERY)
                                        deliverySuccessMessage = "Order #${order.id} is now Out for Delivery."
                                    },
                                    modifier = Modifier.fillMaxWidth().height(40.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = SaffronContainer, contentColor = OnSaffronContainer)
                                ) {
                                    Text("Start Route: Out for Delivery", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            OrderStatus.OUT_FOR_DELIVERY -> {
                                Button(
                                    onClick = {
                                        selectedOrderForOtp = order
                                        enteredOtp = ""
                                        otpError = null
                                    },
                                    modifier = Modifier.fillMaxWidth().height(42.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
                                ) {
                                    Icon(Icons.Default.Verified, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Enter Customer OTP & Deliver", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            OrderStatus.DELIVERED -> {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AgriGreenDark, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Delivered successfully to customer", fontSize = 12.sp, color = AgriGreenDark, fontWeight = FontWeight.SemiBold)
                                }
                            }
                            OrderStatus.CANCELLED -> {
                                Text("Order was cancelled", color = RateDownRed, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }
    }

    // OTP Verification Dialog
    selectedOrderForOtp?.let { order ->
        AlertDialog(
            onDismissRequest = { selectedOrderForOtp = null },
            title = {
                Text("Verify Customer OTP", fontWeight = FontWeight.Bold, fontSize = 17.sp)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Ask customer ${order.buyerName} for the 4-digit Delivery OTP generated on their order screen.",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )

                    OutlinedTextField(
                        value = enteredOtp,
                        onValueChange = {
                            if (it.length <= 4 && it.all { char -> char.isDigit() }) {
                                enteredOtp = it
                                otpError = null
                            }
                        },
                        label = { Text("4-Digit Delivery OTP") },
                        placeholder = { Text("e.g. ${order.deliveryOtp}") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Demo helper hint for testing
                    Text(
                        text = "💡 For testing: Customer's OTP is: ${order.deliveryOtp}",
                        fontSize = 11.sp,
                        color = AgriGreenDark,
                        fontWeight = FontWeight.SemiBold
                    )

                    if (otpError != null) {
                        Text(
                            text = otpError ?: "",
                            color = RateDownRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val isVerified = viewModel.verifyDeliveryOtp(order.id, enteredOtp)
                        if (isVerified) {
                            selectedOrderForOtp = null
                            deliverySuccessMessage = "Order #${order.id} delivered successfully with verified OTP!"
                        } else {
                            otpError = "Incorrect OTP! Please check with customer."
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Confirm Delivery", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedOrderForOtp = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// 2. SECTION 14: SIMPLE PROFILE SCREEN
@Composable
fun ProfileScreen(
    viewModel: GramVyaparViewModel,
    onNavigateToLanguage: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToLearnAndGrow: () -> Unit,
    onNavigateToArtisans: () -> Unit,
    onNavigateToSellerDashboard: () -> Unit,
    onNavigateToDeliveries: () -> Unit,
    onNavigateToAdmin: () -> Unit
) {
    val lang by viewModel.language.collectAsState()
    val user by viewModel.user.collectAsState()

    Scaffold(
        containerColor = RuralBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // User Information Header
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = RuralSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(AgriGreenContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("👤", fontSize = 28.sp)
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(user.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(user.phone, fontSize = 12.sp, color = TextSecondary)
                        Text("📍 ${user.village}, Buldhana, Maharashtra", fontSize = 11.sp, color = AgriGreenDark, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // Authenticated Role Card (Read-only security badge)
            Card(
                colors = CardDefaults.cardColors(containerColor = AgriGreenContainer),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Security,
                        contentDescription = null,
                        tint = AgriGreenDark,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Account Role: ${user.role.displayName}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = AgriGreenDark
                        )
                        Text(
                            text = "Role is authenticated & system-managed (Read-Only)",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            // Role-Specific Navigation Buttons (Only shown for user's assigned role)
            if (user.role == UserRole.SELLER) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToSellerDashboard() },
                    colors = CardDefaults.cardColors(containerColor = AgriGreenContainer),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Storefront, contentDescription = null, tint = AgriGreenDark)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Open Seller Dashboard (Buldhana)", fontWeight = FontWeight.Bold, color = AgriGreenDark, fontSize = 14.sp)
                    }
                }
            }

            if (user.role == UserRole.DELIVERY) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToDeliveries() },
                    colors = CardDefaults.cardColors(containerColor = SaffronContainer),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.LocalShipping, contentDescription = null, tint = OnSaffronContainer)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Open My Deliveries Hub", fontWeight = FontWeight.Bold, color = OnSaffronContainer, fontSize = 14.sp)
                    }
                }
            }

            if (user.role == UserRole.ADMIN) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToAdmin() },
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEDE7F6)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = Color(0xFF4A148C))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Open Admin Governance Dashboard", fontWeight = FontWeight.Bold, color = Color(0xFF4A148C), fontSize = 14.sp)
                    }
                }
            }

            // Section 14: Main Options List
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = RuralSurface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column {
                    ListItem(
                        headlineContent = { Text("My Orders") },
                        leadingContent = { Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = AgriGreenPrimary) },
                        trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
                        modifier = Modifier.clickable { onNavigateToOrders() }
                    )
                    HorizontalDivider()
                    ListItem(
                        headlineContent = { Text("My Addresses") },
                        supportingContent = { Text("${user.village}, Buldhana District") },
                        leadingContent = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = AgriGreenPrimary) }
                    )
                    HorizontalDivider()
                    ListItem(
                        headlineContent = { Text("Language / भाषा") },
                        supportingContent = { Text("${lang.nativeName} (${lang.englishName})") },
                        leadingContent = { Icon(Icons.Default.Language, contentDescription = null, tint = AgriGreenPrimary) },
                        trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
                        modifier = Modifier.clickable { onNavigateToLanguage() }
                    )
                    HorizontalDivider()
                    ListItem(
                        headlineContent = { Text("📚 Learn & Grow") },
                        supportingContent = { Text("Direct farm selling, branding & packaging") },
                        leadingContent = { Icon(Icons.Default.School, contentDescription = null, tint = SaffronAccent) },
                        trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
                        modifier = Modifier.clickable { onNavigateToLearnAndGrow() }
                    )
                    HorizontalDivider()
                    ListItem(
                        headlineContent = { Text("🏺 Rural Artisans") },
                        supportingContent = { Text("Pottery & handicrafts from Buldhana villages") },
                        leadingContent = { Icon(Icons.Default.Palette, contentDescription = null, tint = SaffronAccent) },
                        trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
                        modifier = Modifier.clickable { onNavigateToArtisans() }
                    )
                    HorizontalDivider()
                    ListItem(
                        headlineContent = { Text("Help & Support") },
                        supportingContent = { Text("Buldhana Kisan Helpline: 1800-180-1551") },
                        leadingContent = { Icon(Icons.Default.Help, contentDescription = null, tint = TextSecondary) }
                    )
                    HorizontalDivider()
                    ListItem(
                        headlineContent = { Text("Logout", color = RateDownRed, fontWeight = FontWeight.Bold) },
                        leadingContent = { Icon(Icons.Default.Logout, contentDescription = null, tint = RateDownRed) },
                        modifier = Modifier.clickable {
                            viewModel.logout()
                            viewModel.navigateTo(AppDestination.LOGIN)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

// 3. SECTION 22: SIMPLE NOTIFICATIONS
@Composable
fun NotificationsScreen(
    viewModel: GramVyaparViewModel
) {
    val notifications by viewModel.notifications.collectAsState()

    Scaffold(
        containerColor = RuralBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Text(
                    text = "Notifications",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = AgriGreenDark
                )
            }

            items(notifications, key = { it.id }) { notif ->
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = RuralSurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🔔", fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(notif.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(notif.message, fontSize = 11.sp, color = TextSecondary)
                            Text(notif.timestamp, fontSize = 9.sp, color = TextMuted)
                        }
                    }
                }
            }
        }
    }
}

// 4. SECTION 24: RURAL ARTISAN DIRECTORY (Buldhana)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RuralArtisansScreen(
    viewModel: GramVyaparViewModel,
    onBack: () -> Unit
) {
    val artisans by viewModel.artisans.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rural Artisans (Buldhana)", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = RuralSurface)
            )
        },
        containerColor = RuralBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(artisans, key = { it.id }) { artisan ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = RuralSurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🏺", fontSize = 32.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(artisan.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text(artisan.craftType, fontSize = 12.sp, color = SaffronAccent, fontWeight = FontWeight.SemiBold)
                                Text("📍 ${artisan.village}, Buldhana", fontSize = 11.sp, color = TextSecondary)
                            }
                        }
                        Text(artisan.story, fontSize = 12.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Button(
                            onClick = {},
                            modifier = Modifier.fillMaxWidth().height(38.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
                        ) {
                            Text("Contact Artisan: ${artisan.contactPhone}", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

// 5. SECTION 25: LEARN & GROW
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainingAcademyScreen(
    viewModel: GramVyaparViewModel,
    onBack: () -> Unit
) {
    val modules by viewModel.trainingModules.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("📚 Learn & Grow", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = RuralSurface)
            )
        },
        containerColor = RuralBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(modules, key = { it.id }) { module ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = RuralSurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(module.title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = AgriGreenDark)
                        Text("Category: ${module.category} • ${module.duration}", fontSize = 11.sp, color = TextSecondary)
                        Text(module.description, fontSize = 12.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        module.keyTakeaways.forEach { takeaway ->
                            Text("• $takeaway", fontSize = 11.sp, color = TextPrimary)
                        }
                    }
                }
            }
        }
    }
}

// 6. SECTION 18: ADMIN DASHBOARD (ADMIN-001 Governance)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminOverviewScreen(
    viewModel: GramVyaparViewModel,
    onBack: () -> Unit,
    initialTab: Int = 0
) {
    val products by viewModel.allProducts.collectAsState()
    val orders by viewModel.orders.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()

    var adminTab by remember(initialTab) { mutableStateOf(initialTab) }
    var selectedOrderForAssign by remember { mutableStateOf<Order?>(null) }
    var selectedUserForPermissions by remember { mutableStateOf<UserProfile?>(null) }
    var assignSuccessMsg by remember { mutableStateOf<String?>(null) }

    val deliveryBoys = allUsers.filter { it.role == UserRole.DELIVERY }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("🛡️ Admin Console (ADMIN-001)", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                        Text("Buldhana District Rural Marketplace", fontSize = 11.sp, color = TextSecondary)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = RuralSurface)
            )
        },
        containerColor = RuralBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Admin Subtabs: Overview | Users | Orders | Payments
            TabRow(
                selectedTabIndex = adminTab,
                containerColor = RuralSurface,
                contentColor = AgriGreenPrimary
            ) {
                Tab(
                    selected = adminTab == 0,
                    onClick = { adminTab = 0 },
                    text = { Text("📊 Overview", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = adminTab == 1,
                    onClick = { adminTab = 1 },
                    text = { Text("👥 Users (${allUsers.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = adminTab == 2,
                    onClick = { adminTab = 2 },
                    text = { Text("📦 Orders (${orders.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = adminTab == 3,
                    onClick = { adminTab = 3 },
                    text = { Text("💳 Payments", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                )
            }

            if (assignSuccessMsg != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = AgriGreenContainer),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = assignSuccessMsg ?: "",
                        color = AgriGreenDark,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            when (adminTab) {
                // Tab 0: Overview & Stats
                0 -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            Text("Buldhana District Summary", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }

                        item {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = AgriGreenContainer)) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text("Products", fontSize = 11.sp, color = TextSecondary)
                                        Text("${products.size}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AgriGreenDark)
                                    }
                                }
                                Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = SaffronContainer)) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text("Orders", fontSize = 11.sp, color = TextSecondary)
                                        Text("${orders.size}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = OnSaffronContainer)
                                    }
                                }
                                Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = Color(0xFFEDE7F6))) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text("Users", fontSize = 11.sp, color = TextSecondary)
                                        Text("${allUsers.size}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4A148C))
                                    }
                                }
                            }
                        }

                        item {
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = RuralSurface),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text("Buldhana APMC Mandis Status", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("• Buldhana Mandi: Live Rates Synced ✅", fontSize = 12.sp, color = AgriGreenDark)
                                    Text("• Khamgaon Mandi: Live Rates Synced ✅", fontSize = 12.sp, color = AgriGreenDark)
                                    Text("• Malkapur Mandi: Live Rates Synced ✅", fontSize = 12.sp, color = AgriGreenDark)
                                    Text("• Mehkar Mandi: Live Rates Synced ✅", fontSize = 12.sp, color = AgriGreenDark)
                                    Text("• Shegaon Mandi: Live Rates Synced ✅", fontSize = 12.sp, color = AgriGreenDark)
                                }
                            }
                        }
                    }
                }

                // Tab 1: Users Management
                1 -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(allUsers, key = { it.id }) { userItem ->
                            Card(
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = RuralSurface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(userItem.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(4.dp))
                                                        .background(
                                                            when (userItem.role) {
                                                                UserRole.ADMIN -> Color(0xFFEDE7F6)
                                                                UserRole.SELLER -> AgriGreenContainer
                                                                UserRole.DELIVERY -> SaffronContainer
                                                                else -> Color(0xFFF1F5F9)
                                                            }
                                                        )
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text(
                                                        text = userItem.role.name,
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = when (userItem.role) {
                                                            UserRole.ADMIN -> Color(0xFF4A148C)
                                                            UserRole.SELLER -> AgriGreenDark
                                                            UserRole.DELIVERY -> OnSaffronContainer
                                                            else -> TextPrimary
                                                        }
                                                    )
                                                }
                                            }
                                            Text("📞 ${userItem.phone} • ✉️ ${userItem.email}", fontSize = 11.sp, color = TextSecondary)
                                            Text("📍 ${userItem.village}, Buldhana", fontSize = 11.sp, color = TextMuted)
                                        }

                                        if (userItem.role != UserRole.ADMIN) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Switch(
                                                    checked = userItem.isActive,
                                                    onCheckedChange = { viewModel.toggleUserStatus(userItem.id) },
                                                    colors = SwitchDefaults.colors(checkedThumbColor = AgriGreenPrimary)
                                                )
                                                Text(
                                                    if (userItem.isActive) "Active" else "Blocked",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (userItem.isActive) AgriGreenDark else RateDownRed
                                                )
                                            }
                                        }
                                    }

                                    if (userItem.role != UserRole.ADMIN) {
                                        Spacer(modifier = Modifier.height(10.dp))
                                        OutlinedButton(
                                            onClick = { selectedUserForPermissions = userItem },
                                            modifier = Modifier.fillMaxWidth().height(36.dp),
                                            shape = RoundedCornerShape(6.dp),
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = AgriGreenDark)
                                        ) {
                                            Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(15.dp), tint = AgriGreenDark)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Manage Permissions", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AgriGreenDark)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Tab 2: Orders & Delivery Boy Assignment
                2 -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(orders, key = { it.id }) { order ->
                            Card(
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = RuralSurface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Order #${order.id}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text("Status: ${order.orderStatus.titleEn}", color = AgriGreenDark, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
                                    Text("Customer: ${order.buyerName} (${order.buyerPhone})", fontSize = 12.sp)
                                    Text("Address: ${order.deliveryAddress}", fontSize = 11.sp, color = TextSecondary)
                                    Text("Total: ₹${order.totalAmount.toInt()} (${if (order.isCod) "COD" else "UPI"})", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)

                                    if (order.deliveryBoyName != null) {
                                        Text("🚚 Assigned to: ${order.deliveryBoyName}", fontSize = 11.sp, color = AgriGreenDark, fontWeight = FontWeight.Bold)
                                    } else {
                                        OutlinedButton(
                                            onClick = { selectedOrderForAssign = order },
                                            modifier = Modifier.fillMaxWidth().height(36.dp),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text("Assign Delivery Partner", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Tab 3: Payments Audit
                3 -> {
                    val codOrders = orders.filter { it.isCod }
                    val upiOrders = orders.filter { !it.isCod }
                    val codTotal = codOrders.sumOf { it.totalAmount }
                    val upiTotal = upiOrders.sumOf { it.totalAmount }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            Text("Buldhana Payment Settlements", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }

                        item {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = SaffronContainer)) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text("Cash on Delivery", fontSize = 11.sp, color = OnSaffronContainer)
                                        Text("₹${codTotal.toInt()}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = OnSaffronContainer)
                                        Text("${codOrders.size} orders", fontSize = 10.sp, color = OnSaffronContainer)
                                    }
                                }
                                Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = AgriGreenContainer)) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text("Online UPI (Razorpay)", fontSize = 11.sp, color = AgriGreenDark)
                                        Text("₹${upiTotal.toInt()}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AgriGreenDark)
                                        Text("${upiOrders.size} orders", fontSize = 10.sp, color = AgriGreenDark)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Delivery Boy Assignment Dialog
    selectedOrderForAssign?.let { order ->
        AlertDialog(
            onDismissRequest = { selectedOrderForAssign = null },
            title = { Text("Assign Order #${order.id}", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Select a Delivery Boy for Buldhana area delivery:")
                    if (deliveryBoys.isEmpty()) {
                        Text("No delivery partners registered yet.", color = TextSecondary, fontSize = 12.sp)
                    } else {
                        deliveryBoys.forEach { db ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.assignDeliveryBoy(order.id, db.id, db.name)
                                        assignSuccessMsg = "Assigned Order #${order.id} to ${db.name}"
                                        selectedOrderForAssign = null
                                    },
                                colors = CardDefaults.cardColors(containerColor = RuralSurface),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("🚚", fontSize = 20.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(db.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text("Area: ${db.serviceArea ?: db.village} • 📞 ${db.phone}", fontSize = 11.sp, color = TextSecondary)
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { selectedOrderForAssign = null }) {
                    Text("Close")
                }
            }
        )
    }

    // User Permissions Management Dialog (Admin Access Control)
    selectedUserForPermissions?.let { targetUser ->
        // Track local editing state for permissions
        var pShopping by remember(targetUser) { mutableStateOf(targetUser.permissions.shopping) }
        var pCart by remember(targetUser) { mutableStateOf(targetUser.permissions.cart) }
        var pCheckout by remember(targetUser) { mutableStateOf(targetUser.permissions.checkout) }
        var pOrders by remember(targetUser) { mutableStateOf(targetUser.permissions.orders) }
        var pPayments by remember(targetUser) { mutableStateOf(targetUser.permissions.payments) }
        var pMarketRates by remember(targetUser) { mutableStateOf(targetUser.permissions.marketRates) }
        var pNotifications by remember(targetUser) { mutableStateOf(targetUser.permissions.notifications) }
        var pSellerDashboard by remember(targetUser) { mutableStateOf(targetUser.permissions.sellerDashboard) }
        var pManageProducts by remember(targetUser) { mutableStateOf(targetUser.permissions.manageProducts) }
        var pAddProduct by remember(targetUser) { mutableStateOf(targetUser.permissions.addProduct) }
        var pDeliveryManagement by remember(targetUser) { mutableStateOf(targetUser.permissions.deliveryManagement) }
        var pDeliveryOtp by remember(targetUser) { mutableStateOf(targetUser.permissions.deliveryOtp) }

        AlertDialog(
            onDismissRequest = { selectedUserForPermissions = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = AgriGreenDark, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("User Permissions & Access", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // USER DETAILS CARD
                    Card(
                        colors = CardDefaults.cardColors(containerColor = RuralBackground),
                        border = CardDefaults.outlinedCardBorder(),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            Text("USER DETAILS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AgriGreenDark)
                            Text("Name: ${targetUser.name}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("Mobile: ${targetUser.phone}", fontSize = 12.sp, color = TextSecondary)
                            Text("Email: ${targetUser.email}", fontSize = 12.sp, color = TextSecondary)
                            Text("Location: ${targetUser.village}, Buldhana", fontSize = 12.sp, color = TextSecondary)
                            Text("Role: ${targetUser.role.displayName} (Permanent)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        }
                    }

                    // ACCOUNT STATUS
                    Card(
                        colors = CardDefaults.cardColors(containerColor = RuralBackground),
                        border = CardDefaults.outlinedCardBorder(),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("ACCOUNT STATUS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AgriGreenDark)
                                Text(
                                    if (targetUser.isActive) "Status: Active" else "Status: Inactive / Blocked",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (targetUser.isActive) AgriGreenDark else RateDownRed
                                )
                            }
                            Switch(
                                checked = targetUser.isActive,
                                onCheckedChange = { viewModel.toggleUserStatus(targetUser.id) },
                                colors = SwitchDefaults.colors(checkedThumbColor = AgriGreenPrimary)
                            )
                        }
                    }

                    // PERMISSIONS HEADER
                    Text("PERMISSIONS (ACCESS CONTROL)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)

                    // Buyer Permissions Group
                    Card(
                        colors = CardDefaults.cardColors(containerColor = RuralSurface),
                        border = CardDefaults.outlinedCardBorder(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Buyer Shopping Permissions", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                            PermissionToggleRow("Products Shopping", pShopping) { pShopping = it }
                            PermissionToggleRow("Cart", pCart) { pCart = it }
                            PermissionToggleRow("Checkout", pCheckout) { pCheckout = it }
                            PermissionToggleRow("Orders History", pOrders) { pOrders = it }
                            PermissionToggleRow("Payments", pPayments) { pPayments = it }
                        }
                    }

                    // Market & Notifications
                    Card(
                        colors = CardDefaults.cardColors(containerColor = RuralSurface),
                        border = CardDefaults.outlinedCardBorder(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Information & Alerts", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                            PermissionToggleRow("Market Rates (APMC)", pMarketRates) { pMarketRates = it }
                            PermissionToggleRow("Notifications", pNotifications) { pNotifications = it }
                        }
                    }

                    // Seller Permissions Group
                    Card(
                        colors = CardDefaults.cardColors(containerColor = RuralSurface),
                        border = CardDefaults.outlinedCardBorder(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Farmer & Seller Permissions", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                            PermissionToggleRow("Seller Dashboard", pSellerDashboard) { pSellerDashboard = it }
                            PermissionToggleRow("Manage Products", pManageProducts) { pManageProducts = it }
                            PermissionToggleRow("Add Product", pAddProduct) { pAddProduct = it }
                        }
                    }

                    // Delivery Permissions Group
                    Card(
                        colors = CardDefaults.cardColors(containerColor = RuralSurface),
                        border = CardDefaults.outlinedCardBorder(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Delivery Partner Permissions", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                            PermissionToggleRow("Delivery Hub", pDeliveryManagement) { pDeliveryManagement = it }
                            PermissionToggleRow("Delivery OTP Verification", pDeliveryOtp) { pDeliveryOtp = it }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newPerms = targetUser.permissions.copy(
                            shopping = pShopping,
                            cart = pCart,
                            checkout = pCheckout,
                            orders = pOrders,
                            payments = pPayments,
                            marketRates = pMarketRates,
                            notifications = pNotifications,
                            sellerDashboard = pSellerDashboard,
                            manageProducts = pManageProducts,
                            addProduct = pAddProduct,
                            deliveryManagement = pDeliveryManagement,
                            deliveryOtp = pDeliveryOtp
                        )
                        viewModel.updateUserPermissions(targetUser.id, newPerms)
                        assignSuccessMsg = "Permissions updated successfully for ${targetUser.name}"
                        selectedUserForPermissions = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
                ) {
                    Text("Save Permissions", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { selectedUserForPermissions = null }) {
                    Text("Cancel", color = TextPrimary)
                }
            }
        )
    }
}

@Composable
private fun PermissionToggleRow(label: String, isChecked: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
        Switch(
            checked = isChecked,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(checkedThumbColor = AgriGreenPrimary)
        )
    }
}
