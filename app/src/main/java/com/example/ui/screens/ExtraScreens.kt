package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.i18n.AppStrings
import com.example.ui.theme.*
import com.example.ui.viewmodel.GramVyaparViewModel

// 1. DELIVERY PARTNER SCREEN
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryPartnerScreen(
    viewModel: GramVyaparViewModel
) {
    val lang by viewModel.language.collectAsState()
    val orders by viewModel.orders.collectAsState()
    var enteredOtp by remember { mutableStateOf("") }
    var activeOrderToDeliver by remember { mutableStateOf<Order?>(null) }
    var showOtpError by remember { mutableStateOf(false) }

    Scaffold(
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
                Column {
                    Text(
                        text = "Rural Delivery Hub 🚚",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = AgriGreenDark
                    )
                    Text(
                        text = "Village Farm-to-Buyer Express Logistics",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }

            items(orders) { order ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = RuralSurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(order.id, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = AgriGreenDark)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        if (order.orderStatus == OrderStatus.DELIVERED) AgriGreenContainer else SaffronContainer
                                    )
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    order.orderStatus.title.split("/")[0].trim(),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (order.orderStatus == OrderStatus.DELIVERED) OnAgriGreenContainer else OnSaffronContainer
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Pickup point
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(Icons.Default.Store, contentDescription = null, tint = AgriGreenPrimary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Pickup from Farmer:", fontSize = 11.sp, color = TextSecondary)
                                Text("Kisan Baburao (Pimpalgaon Baswant, Nashik)", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Delivery point
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(Icons.Default.Home, contentDescription = null, tint = SaffronAccent, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Deliver to Buyer:", fontSize = 11.sp, color = TextSecondary)
                                Text("${order.buyerName} • ${order.buyerPhone}", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                Text(order.deliveryAddress, fontSize = 11.sp, color = TextSecondary)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (order.orderStatus != OrderStatus.DELIVERED) {
                            Button(
                                onClick = { activeOrderToDeliver = order },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .testTag("delivery_complete_action_btn"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
                            ) {
                                Text("Enter Customer OTP & Deliver", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    if (activeOrderToDeliver != null) {
        AlertDialog(
            onDismissRequest = { activeOrderToDeliver = null },
            title = { Text("Complete Delivery Hand-off", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "Ask customer for the 4-digit Delivery OTP sent to their phone.\n(Demo Hint: ${activeOrderToDeliver?.deliveryOtp})",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = enteredOtp,
                        onValueChange = { enteredOtp = it; showOtpError = false },
                        label = { Text("Enter OTP") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (showOtpError) {
                        Text("Invalid OTP. Please re-check with buyer.", color = RateDownRed, fontSize = 11.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (enteredOtp.trim() == activeOrderToDeliver?.deliveryOtp) {
                            activeOrderToDeliver?.let {
                                viewModel.updateOrderStatus(it.id, OrderStatus.DELIVERED)
                            }
                            activeOrderToDeliver = null
                            enteredOtp = ""
                        } else {
                            showOtpError = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
                ) {
                    Text("Verify & Deliver")
                }
            },
            dismissButton = {
                TextButton(onClick = { activeOrderToDeliver = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// 2. RURAL ARTISANS GUILD SCREEN
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
                title = { Text("Rural Artisans Guild 🏺", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
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
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SaffronContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Vocal for Local • Preserving Indian Heritage",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = OnSaffronContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Buy directly from traditional pottery masters, Warli tribal painters, and handloom weavers across Indian villages. 100% of proceeds reach rural craft clusters.",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            items(artisans) { artisan ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = RuralSurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(SaffronContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🏺", fontSize = 28.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(artisan.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text(artisan.craftType, fontSize = 12.sp, color = SaffronAccent, fontWeight = FontWeight.SemiBold)
                                Text("📍 ${artisan.village}, ${artisan.district}", fontSize = 11.sp, color = TextSecondary)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(AgriGreenContainer)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("⭐ ${artisan.rating}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AgriGreenDark)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = artisan.story,
                            fontSize = 12.sp,
                            color = TextSecondary,
                            lineHeight = 17.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text("Specialties:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            artisan.specialties.take(3).forEach { spec ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(RuralBackground)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(spec, fontSize = 10.sp, color = TextPrimary)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {},
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(42.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
                        ) {
                            Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Contact Artisan / Custom Order", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// 3. TRAINING & SKILL DEVELOPMENT SCREEN
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainingAcademyScreen(
    viewModel: GramVyaparViewModel
) {
    val modules by viewModel.trainingModules.collectAsState()
    var selectedForCertificate by remember { mutableStateOf<TrainingModule?>(null) }

    Scaffold(
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
                Column {
                    Text(
                        text = "Rural Entrepreneur Academy 🎓",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = AgriGreenDark
                    )
                    Text(
                        text = "Master Online E-Commerce & Sell to Millions Nationwide",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }

            items(modules) { module ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = RuralSurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(SaffronContainer)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    module.platformTag,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OnSaffronContainer
                                )
                            }
                            Text(module.duration, fontSize = 11.sp, color = TextSecondary)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = module.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = module.description,
                            fontSize = 12.sp,
                            color = TextSecondary,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text("Key Practical Learnings:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        module.keyTakeaways.forEach { takeaway ->
                            Text("• $takeaway", fontSize = 11.sp, color = TextSecondary, modifier = Modifier.padding(vertical = 1.dp))
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            if (!module.isCompleted) {
                                Button(
                                    onClick = {
                                        viewModel.markTrainingComplete(module.id)
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(42.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Complete Lesson", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                OutlinedButton(
                                    onClick = { selectedForCertificate = module },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(42.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    border = ButtonDefaults.outlinedButtonBorder.copy(
                                        brush = Brush.horizontalGradient(listOf(AgriGreenPrimary, CropGold))
                                    )
                                ) {
                                    Icon(Icons.Default.CardMembership, contentDescription = null, tint = AgriGreenPrimary, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("View Certificate 🎖️", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AgriGreenPrimary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Certificate Dialog
    if (selectedForCertificate != null) {
        AlertDialog(
            onDismissRequest = { selectedForCertificate = null },
            title = {
                Text("Certificate of Completion 🎖️", fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Government & GramVyapar Rural Entrepreneurship Initiative",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(2.dp, CropGold, RoundedCornerShape(12.dp))
                            .background(Color(0xFFFFFDE7))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("CERTIFICATE AWARDED TO", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                            Text("Ramesh Tukaram Patil", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = AgriGreenDark)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("For successfully completing the verified course:", fontSize = 10.sp, color = TextSecondary)
                            Text(selectedForCertificate?.certificateTitle ?: "", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SaffronAccent, textAlign = TextAlign.Center)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Authorized by GramVyapar Rural Digital Guild", fontSize = 9.sp, color = TextMuted)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { selectedForCertificate = null },
                    colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
                ) {
                    Text("Close")
                }
            }
        )
    }
}

// 4. ADMIN SCREEN
@Composable
fun AdminOverviewScreen(
    viewModel: GramVyaparViewModel
) {
    var broadcastText by remember { mutableStateOf("") }
    var isBroadcastSent by remember { mutableStateOf(false) }

    Scaffold(
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
                Column {
                    Text(
                        text = "Platform Administration Console ⚙️",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = AgriGreenDark
                    )
                    Text("GramVyapar E-Commerce Network Governance", fontSize = 12.sp, color = TextSecondary)
                }
            }

            // High level Metrics
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = AgriGreenContainer),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Gross GMV", fontSize = 11.sp, color = TextSecondary)
                            Text("₹14.8 Lakhs", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AgriGreenDark)
                        }
                    }
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = SaffronContainer),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Active Farmers", fontSize = 11.sp, color = TextSecondary)
                            Text("2,480 FPOs", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = OnSaffronContainer)
                        }
                    }
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = RuralSurface),
                        shape = RoundedCornerShape(12.dp),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Dispute Rate", fontSize = 11.sp, color = TextSecondary)
                            Text("0.08%", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = RateUpGreen)
                        }
                    }
                }
            }

            // Pending KYC Verifications
            item {
                Text("Pending Farmer & Artisan KYC Approvals", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }

            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = RuralSurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Ganesh Gaikwad (Farmer Producer)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("7/12 Land Record Attached • Narayangaon, Pune", fontSize = 11.sp, color = TextSecondary)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(SaffronContainer)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("Pending Review", fontSize = 10.sp, color = SaffronAccent, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {},
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
                            ) {
                                Text("Approve KYC", fontSize = 12.sp)
                            }
                            OutlinedButton(
                                onClick = {},
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Request Re-upload", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Broadcast Advisory
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = RuralSurface),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Broadcast Emergency Rural Advisory / Mandi Notice", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = broadcastText,
                            onValueChange = { broadcastText = it },
                            placeholder = { Text("Enter weather emergency, crop pest advisory or government subsidy update...") },
                            minLines = 2,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = { isBroadcastSent = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
                        ) {
                            Text("Send Push Broadcast to All Users", fontWeight = FontWeight.Bold)
                        }
                        if (isBroadcastSent) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("✓ Broadcast successfully delivered to 2,480 rural members.", color = AgriGreenPrimary, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

// 5. PROFILE SCREEN
@Composable
fun ProfileScreen(
    viewModel: GramVyaparViewModel,
    onNavigateToLanguage: () -> Unit
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
                .padding(16.dp)
        ) {
            // User Header
            Card(
                shape = RoundedCornerShape(16.dp),
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
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(AgriGreenContainer)
                            .border(2.dp, AgriGreenPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🌾", fontSize = 32.sp)
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(user.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(user.phone, fontSize = 12.sp, color = TextSecondary)
                        Text("📍 ${user.village}, ${user.district}, ${user.state}", fontSize = 11.sp, color = TextMuted)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // In-App Rural Wallet Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AgriGreenDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("GramVyapar Kisan & Buyer Wallet", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("₹${user.walletBalance.toInt()}", color = CropGold, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {},
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = SaffronAccent)
                        ) {
                            Text("Add Funds", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        OutlinedButton(
                            onClick = {},
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                        ) {
                            Text("Withdraw to Bank", fontSize = 12.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Switch Role Quick Section
            Text("Switch Role (Experience all facets):", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(6.dp))
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
                                    UserRole.SELLER -> "Farmer"
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

            Spacer(modifier = Modifier.height(16.dp))

            // Settings & Preferences
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = RuralSurface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column {
                    ListItem(
                        headlineContent = { Text("Language / भाषा") },
                        supportingContent = { Text(lang.nativeName + " (" + lang.englishName + ")") },
                        leadingContent = { Icon(Icons.Default.Language, contentDescription = null, tint = AgriGreenPrimary) },
                        trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
                        modifier = Modifier.clickable { onNavigateToLanguage() }
                    )
                    HorizontalDivider()
                    ListItem(
                        headlineContent = { Text("KYC & Land Verification") },
                        supportingContent = { Text("Aadhaar: ${user.aadhaarMasked} (Verified)") },
                        leadingContent = { Icon(Icons.Default.Verified, contentDescription = null, tint = AgriGreenPrimary) }
                    )
                    HorizontalDivider()
                    ListItem(
                        headlineContent = { Text("Direct Benefit Transfer (DBT) Bank Account") },
                        supportingContent = { Text("State Bank of India (A/C ending in 4109)") },
                        leadingContent = { Icon(Icons.Default.AccountBalance, contentDescription = null, tint = SaffronAccent) }
                    )
                    HorizontalDivider()
                    ListItem(
                        headlineContent = { Text("Log Out") },
                        leadingContent = { Icon(Icons.Default.Logout, contentDescription = null, tint = RateDownRed) },
                        modifier = Modifier.clickable { viewModel.navigateTo(com.example.ui.viewmodel.AppDestination.LOGIN) }
                    )
                }
            }
        }
    }
}

// 6. NOTIFICATIONS SCREEN
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    viewModel: GramVyaparViewModel,
    onBack: () -> Unit
) {
    val notifications by viewModel.notifications.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Real-Time Alerts & Advisories", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(notifications) { notif ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = RuralSurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    when (notif.type) {
                                        "RATE" -> SaffronContainer
                                        "WEATHER" -> AgriGreenContainer
                                        else -> Color(0xFFE1F5FE)
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = when (notif.type) {
                                    "RATE" -> "📈"
                                    "WEATHER" -> "🌤️"
                                    else -> "🏛️"
                                },
                                fontSize = 18.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(notif.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(notif.timestamp, fontSize = 10.sp, color = TextMuted)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(notif.message, fontSize = 12.sp, color = TextSecondary, lineHeight = 16.sp)
                        }
                    }
                }
            }
        }
    }
}
