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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Today's Deliveries", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
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
                Text(
                    text = "Buldhana District Local Deliveries",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }

            items(orders) { order ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = RuralSurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Order ${order.id}", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text("Status: ${order.orderStatus.titleEn}", color = AgriGreenDark, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Text("Customer: ${order.buyerName} • ${order.buyerPhone}", fontSize = 12.sp)
                        Text("Location: ${order.deliveryAddress}", fontSize = 12.sp, color = TextSecondary)

                        Spacer(modifier = Modifier.height(6.dp))

                        // Simple Action Buttons: Pickup | Out for Delivery | Delivered
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { viewModel.updateOrderStatus(order.id, OrderStatus.PREPARING) },
                                modifier = Modifier.weight(1f).height(38.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AgriGreenContainer, contentColor = AgriGreenDark),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("Pickup", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { viewModel.updateOrderStatus(order.id, OrderStatus.OUT_FOR_DELIVERY) },
                                modifier = Modifier.weight(1f).height(38.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = SaffronContainer, contentColor = OnSaffronContainer),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("Out for Delivery", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { viewModel.updateOrderStatus(order.id, OrderStatus.DELIVERED) },
                                modifier = Modifier.weight(1f).height(38.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("Delivered", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
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

            // Switch Role (Clean quick tester)
            Text("Switch Role (Test Buyer, Seller, Delivery, Admin):", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                UserRole.values().forEach { role ->
                    val isSel = user.role == role
                    FilterChip(
                        selected = isSel,
                        onClick = { viewModel.switchRole(role) },
                        label = {
                            Text(
                                text = when (role) {
                                    UserRole.BUYER -> "Buyer"
                                    UserRole.SELLER -> "Seller"
                                    UserRole.DELIVERY -> "Delivery"
                                    UserRole.ADMIN -> "Admin"
                                },
                                fontSize = 11.sp
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AgriGreenPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // Role-Specific Fast Entry Buttons
            if (user.role == UserRole.SELLER || user.role == UserRole.ADMIN) {
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

            if (user.role == UserRole.DELIVERY || user.role == UserRole.ADMIN) {
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
                        modifier = Modifier.clickable { viewModel.navigateTo(AppDestination.LOGIN) }
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

// 6. SECTION 18: ADMIN DASHBOARD
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminOverviewScreen(
    viewModel: GramVyaparViewModel,
    onBack: () -> Unit
) {
    val products by viewModel.allProducts.collectAsState()
    val orders by viewModel.orders.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Governance", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
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
                Text("Buldhana District Marketplace Overview", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
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
                    Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = RuralSurface), border = CardDefaults.outlinedCardBorder()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("District", fontSize = 11.sp, color = TextSecondary)
                            Text("Buldhana", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                    }
                }
            }

            item {
                Text("Active Orders across Buldhana Talukas", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            items(orders) { order ->
                Card(shape = RoundedCornerShape(10.dp), colors = CardDefaults.cardColors(containerColor = RuralSurface)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Order ${order.id} - ${order.buyerName}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Delivery: ${order.deliveryAddress}", fontSize = 11.sp, color = TextSecondary)
                        Text("Status: ${order.orderStatus.titleEn} • Total: ₹${order.totalAmount.toInt()}", fontSize = 11.sp, color = AgriGreenDark, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
