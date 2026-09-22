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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.i18n.AppStrings
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppDestination
import com.example.ui.viewmodel.GramVyaparViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    product: Product,
    viewModel: GramVyaparViewModel,
    onBack: () -> Unit,
    onNavigateToCheckout: () -> Unit
) {
    val lang by viewModel.language.collectAsState()
    var quantity by remember { mutableStateOf(1.0) }
    var showAddedSnackbar by remember { mutableStateOf(false) }

    val name = when (lang) {
        AppLanguage.HINDI -> product.nameHi
        AppLanguage.MARATHI -> product.nameMr
        else -> product.name
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Produce Details", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = RuralSurface)
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 8.dp,
                color = RuralSurface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = {
                            viewModel.addToCart(product, quantity)
                            showAddedSnackbar = true
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("detail_add_to_cart_button"),
                        shape = RoundedCornerShape(12.dp),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = Brush.horizontalGradient(listOf(AgriGreenPrimary, AgriGreenLight))
                        )
                    ) {
                        Icon(Icons.Default.AddShoppingCart, contentDescription = null, tint = AgriGreenPrimary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = AppStrings.get("add_to_cart", lang),
                            color = AgriGreenPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = {
                            viewModel.addToCart(product, quantity)
                            onNavigateToCheckout()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("detail_buy_now_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SaffronAccent)
                    ) {
                        Icon(Icons.Default.FlashOn, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = AppStrings.get("buy_now", lang),
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Visual Banner with category emoji & badges
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                if (product.isArtisanCraft) SaffronContainer else AgriGreenContainer,
                                RuralSurface
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = product.category.iconEmoji, fontSize = 72.sp)

                if (product.badge != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(12.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (product.isArtisanCraft) Terracotta else AgriGreenPrimary)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = product.badge,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Pricing & Mandi Benchmark Comparison
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "₹${product.price.toInt()}",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = AgriGreenDark
                        )
                        Text(
                            text = " / ${product.unit}",
                            fontSize = 14.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(bottom = 3.dp)
                        )
                    }
                    if (product.marketMandiRate > 0) {
                        val diff = product.marketMandiRate - product.price
                        if (diff > 0) {
                            Text(
                                text = "₹${diff.toInt()}/${product.unit} lower than Mandi wholesale rate",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = RateUpGreen
                            )
                        }
                    }
                }

                // Quantity selector
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(RuralBackground)
                        .border(1.dp, RuralCardBorder, RoundedCornerShape(12.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    IconButton(
                        onClick = { if (quantity > 1.0) quantity -= 1.0 },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Decrease")
                    }
                    Text(
                        text = "${quantity.toInt()} ${product.unit}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    IconButton(
                        onClick = { quantity += 1.0 },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Increase")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Live Mandi Benchmark Card
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = AgriGreenContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.TrendingUp, contentDescription = null, tint = AgriGreenDark)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Real-Time Mandi Benchmark Rate",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = OnAgriGreenContainer
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "APMC Mandi Modal Rate: ₹${product.marketMandiRate.toInt()}/${product.unit}. Buy direct from farm to support rural producers.",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Farmer / Artisan profile card
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = RuralSurface),
                border = CardDefaults.outlinedCardBorder(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(if (product.isArtisanCraft) SaffronContainer else AgriGreenContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (product.isArtisanCraft) Icons.Default.Palette else Icons.Default.Agriculture,
                            contentDescription = null,
                            tint = if (product.isArtisanCraft) SaffronAccent else AgriGreenPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = product.sellerName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.CheckCircle, contentDescription = "Verified", tint = AgriGreenPrimary, modifier = Modifier.size(16.dp))
                        }
                        Text(
                            text = "Village: ${product.village}, ${product.district}",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = "Rating: ⭐ ${product.rating} (${product.reviewCount} farm reviews)",
                            fontSize = 11.sp,
                            color = CropGold,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Description
            Text(
                text = "Product Details & Origin",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = product.description,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                color = TextSecondary
            )

            if (showAddedSnackbar) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = AgriGreenDark),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "✓ Added ${quantity.toInt()} ${product.unit} to cart",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CartScreen(
    viewModel: GramVyaparViewModel,
    onProceedCheckout: () -> Unit,
    onShopMore: () -> Unit
) {
    val lang by viewModel.language.collectAsState()
    val cartItems by viewModel.cart.collectAsState()

    val subtotal = cartItems.sumOf { it.totalPrice }
    val deliveryFee = if (subtotal >= 499 || subtotal == 0.0) 0.0 else 40.0
    val discount = if (subtotal > 300) 30.0 else 0.0
    val total = (subtotal + deliveryFee - discount).coerceAtLeast(0.0)

    Scaffold(
        bottomBar = {
            if (cartItems.isNotEmpty()) {
                Surface(
                    tonalElevation = 8.dp,
                    color = RuralSurface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Total Payable",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                                Text(
                                    text = "₹${total.toInt()}",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = AgriGreenDark
                                )
                            }
                            Button(
                                onClick = onProceedCheckout,
                                modifier = Modifier
                                    .height(48.dp)
                                    .testTag("cart_proceed_checkout_btn"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
                            ) {
                                Text(AppStrings.get("proceed_checkout", lang), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🛒", fontSize = 54.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = AppStrings.get("cart_empty", lang),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Explore farm produce directly from rural producers",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onShopMore,
                        colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
                    ) {
                        Text("Shop Farm Produce")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = AppStrings.get("my_cart", lang),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    // Free delivery indicator
                    if (subtotal < 499) {
                        val needed = 499 - subtotal
                        Text(
                            text = "Add ₹${needed.toInt()} more for FREE Farm-to-Home Delivery",
                            fontSize = 12.sp,
                            color = SaffronAccent,
                            fontWeight = FontWeight.SemiBold
                        )
                    } else {
                        Text(
                            text = "✓ Qualified for FREE Rural Delivery",
                            fontSize = 12.sp,
                            color = AgriGreenPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                items(cartItems) { item ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = RuralSurface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(AgriGreenContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(item.product.category.iconEmoji, fontSize = 28.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.product.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "₹${item.product.price.toInt()} / ${item.product.unit}",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                                Text(
                                    text = "Total: ₹${item.totalPrice.toInt()}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AgriGreenDark
                                )
                            }
                            // Qty controls
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { viewModel.updateCartQuantity(item.product.id, -1.0) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = TextSecondary)
                                }
                                Text(
                                    text = "${item.quantity.toInt()}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(horizontal = 4.dp)
                                )
                                IconButton(
                                    onClick = { viewModel.updateCartQuantity(item.product.id, 1.0) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Increase", tint = AgriGreenPrimary)
                                }
                            }
                        }
                    }
                }

                // Bill Details
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = RuralSurface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Bill Summary", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(AppStrings.get("subtotal", lang), color = TextSecondary, fontSize = 13.sp)
                                Text("₹${subtotal.toInt()}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(AppStrings.get("delivery_fee", lang), color = TextSecondary, fontSize = 13.sp)
                                Text(
                                    if (deliveryFee == 0.0) AppStrings.get("free_delivery", lang) else "₹${deliveryFee.toInt()}",
                                    color = if (deliveryFee == 0.0) AgriGreenPrimary else TextPrimary,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                            }
                            if (discount > 0) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Rural Producer Cess Discount", color = SaffronAccent, fontSize = 13.sp)
                                    Text("-₹${discount.toInt()}", color = SaffronAccent, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Grand Total", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("₹${total.toInt()}", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = AgriGreenDark)
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    viewModel: GramVyaparViewModel,
    onBack: () -> Unit
) {
    val lang by viewModel.language.collectAsState()
    val user by viewModel.user.collectAsState()
    val cartItems by viewModel.cart.collectAsState()
    var address by remember { mutableStateOf("House 14, Near Gram Panchayat, ${user.village}, Tal. ${user.taluka}, Dist. ${user.district} - ${user.pincode}") }
    var selectedPayment by remember { mutableStateOf("UPI (Google Pay / PhonePe)") }
    var isProcessing by remember { mutableStateOf(false) }

    val subtotal = cartItems.sumOf { it.totalPrice }
    val total = (subtotal - 30.0).coerceAtLeast(0.0)

    val paymentOptions = listOf(
        "UPI (Google Pay / PhonePe / Paytm / BHIM)",
        "RuPay & Kisan Credit Card (Zero Surcharge)",
        "Net Banking (State Bank of India / HDFC / BOB)",
        "Wallets (Paytm / Amazon Pay)",
        "Cash on Delivery (COD) (₹25 verification charge)"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout & Payment", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = RuralSurface)
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 8.dp,
                color = RuralSurface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        isProcessing = true
                        viewModel.placeOrder(selectedPayment, address)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(52.dp)
                        .testTag("confirm_order_payment_btn"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary),
                    enabled = !isProcessing
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Pay ₹${total.toInt()} & Confirm Order", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Delivery Address Box
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = RuralSurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = AgriGreenPrimary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Delivery Address", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Payment Methods
            Text("Select Payment Method (Razorpay Gateway)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Spacer(modifier = Modifier.height(8.dp))

            paymentOptions.forEach { method ->
                val isSel = selectedPayment == method
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { selectedPayment = method },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSel) AgriGreenContainer else RuralSurface
                    ),
                    border = if (isSel) CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.horizontalGradient(listOf(AgriGreenPrimary, AgriGreenLight)),
                        width = 2.dp
                    ) else null
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSel,
                            onClick = { selectedPayment = method },
                            colors = RadioButtonDefaults.colors(selectedColor = AgriGreenPrimary)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = method,
                            fontSize = 13.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                            color = TextPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Rural Guarantee Badge
            Card(
                colors = CardDefaults.cardColors(containerColor = SaffronContainer),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = SaffronAccent)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "100% Secure Transaction • Escrow Protected: Farmer receives payment upon delivery confirmation.",
                        fontSize = 11.sp,
                        color = OnSaffronContainer
                    )
                }
            }
        }
    }
}

@Composable
fun OrderSuccessScreen(
    order: Order,
    onBackHome: () -> Unit,
    onViewOrders: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RuralBackground)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(AgriGreenContainer)
                .border(3.dp, AgriGreenPrimary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Success",
                tint = AgriGreenPrimary,
                modifier = Modifier.size(48.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Order Placed Successfully!",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = AgriGreenDark,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Your farm produce is directly dispatched from the rural grower",
            fontSize = 13.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = RuralSurface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Order ID:", color = TextSecondary, fontSize = 13.sp)
                    Text(order.id, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Delivery OTP:", color = TextSecondary, fontSize = 13.sp)
                    Text(order.deliveryOtp, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = SaffronAccent)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total Amount Paid:", color = TextSecondary, fontSize = 13.sp)
                    Text("₹${order.totalAmount.toInt()}", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = AgriGreenDark)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Assigned Partner:", color = TextSecondary, fontSize = 13.sp)
                    Text("Suresh Shinde (Rural Express)", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onBackHome,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("success_back_home_btn"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
        ) {
            Text("Continue Shopping", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = onViewOrders,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("success_view_orders_btn"),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("View My Orders", color = AgriGreenPrimary, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun MyOrdersScreen(
    orders: List<Order>,
    lang: AppLanguage,
    onShopNow: () -> Unit
) {
    Scaffold(
        containerColor = RuralBackground
    ) { innerPadding ->
        if (orders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📦", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No orders placed yet", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onShopNow,
                        colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
                    ) {
                        Text("Browse Fresh Harvest")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "My Order History",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
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
                                Text(
                                    text = order.id,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = AgriGreenDark
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(
                                            if (order.orderStatus == OrderStatus.DELIVERED) AgriGreenContainer else SaffronContainer
                                        )
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = order.orderStatus.title.split("/")[0].trim(),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (order.orderStatus == OrderStatus.DELIVERED) OnAgriGreenContainer else OnSaffronContainer
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            order.items.forEach { item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "${item.quantity.toInt()} ${item.product.unit} × ${item.product.name}",
                                        fontSize = 13.sp,
                                        color = TextSecondary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = "₹${item.totalPrice.toInt()}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Delivery OTP: ${order.deliveryOtp}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SaffronAccent
                                    )
                                    Text(
                                        text = order.orderDate,
                                        fontSize = 11.sp,
                                        color = TextMuted
                                    )
                                }
                                Text(
                                    text = "Total: ₹${order.totalAmount.toInt()}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = AgriGreenDark
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
