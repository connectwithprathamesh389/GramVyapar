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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.i18n.AppStrings
import com.example.ui.theme.*
import com.example.ui.viewmodel.GramVyaparViewModel

/**
 * Section 12: Simple Product Details
 * Shows: Image, Product Name, Seller, Price, Available Quantity, Location, Description, Market Rate, Rating
 * Actions: Add to Cart, Buy Now
 */
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
    var showAddedMessage by remember { mutableStateOf(false) }

    val name = when (lang) {
        AppLanguage.HINDI -> product.nameHi
        AppLanguage.MARATHI -> product.nameMr
        else -> product.name
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(name, fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = RuralSurface)
            )
        },
        containerColor = RuralBackground,
        bottomBar = {
            Surface(
                color = RuralSurface,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            viewModel.addToCart(product, quantity)
                            showAddedMessage = true
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("detail_add_to_cart_btn"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(AppStrings.get("add_to_cart", lang), fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            viewModel.addToCart(product, quantity)
                            onNavigateToCheckout()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("detail_buy_now_btn"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
                    ) {
                        Text(AppStrings.get("buy_now", lang), fontWeight = FontWeight.Bold)
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Product Image/Emoji Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (product.isArtisanCraft) SaffronContainer else AgriGreenContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(product.category.iconEmoji, fontSize = 72.sp)
            }

            if (showAddedMessage) {
                Text("✓ Added to your cart successfully!", color = AgriGreenDark, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            // Name & Rating
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(AgriGreenContainer)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("⭐ ${product.rating}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AgriGreenDark)
                }
            }

            // Price
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
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary
                )
            }

            // Buldhana Mandi Rate Benchmark (Section 12)
            if (product.marketMandiRate > 0) {
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = AgriGreenContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.TrendingUp, contentDescription = null, tint = AgriGreenDark)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Buldhana Mandi Rate: ₹${product.marketMandiRate.toInt()}/${product.unit} (Save ₹${(product.marketMandiRate - product.price).coerceAtLeast(0.0).toInt()}/${product.unit})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = OnAgriGreenContainer
                        )
                    }
                }
            }

            // Seller, Location & Available Quantity
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = RuralSurface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Seller: ${product.sellerName}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Text("📍 Location: ${product.village}, Buldhana District", fontSize = 12.sp, color = TextSecondary)
                    Text("Available Quantity: ${product.stock.toInt()} ${product.unit}", fontSize = 12.sp, color = TextSecondary)
                }
            }

            // Short Description
            Column {
                Text("Description", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = product.description,
                    fontSize = 13.sp,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )
            }

            // Quantity selector
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Select Quantity:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                IconButton(
                    onClick = { if (quantity > 1) quantity-- },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(RuralSurface)
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "Decrease")
                }
                Text("${quantity.toInt()} ${product.unit}", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                IconButton(
                    onClick = { quantity++ },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(RuralSurface)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Increase")
                }
            }

            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

/**
 * Simple Cart Screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    viewModel: GramVyaparViewModel,
    onProceedCheckout: () -> Unit,
    onShopMore: () -> Unit
) {
    val lang by viewModel.language.collectAsState()
    val cart by viewModel.cart.collectAsState()
    val totalAmount = cart.sumOf { it.totalPrice }

    Scaffold(
        containerColor = RuralBackground,
        bottomBar = {
            if (cart.isNotEmpty()) {
                Surface(
                    color = RuralSurface,
                    shadowElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Total", fontSize = 12.sp, color = TextSecondary)
                            Text("₹${totalAmount.toInt()}", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = AgriGreenDark)
                        }
                        Button(
                            onClick = onProceedCheckout,
                            modifier = Modifier
                                .height(46.dp)
                                .testTag("cart_proceed_checkout_btn"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
                        ) {
                            Text(AppStrings.get("proceed_checkout", lang), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        if (cart.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🛒", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(AppStrings.get("cart_empty", lang), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onShopMore,
                        colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
                    ) {
                        Text("Browse Buldhana Produce")
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
                        text = AppStrings.get("my_cart", lang) + " (${cart.size} items)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = AgriGreenDark
                    )
                }

                items(cart, key = { it.product.id }) { item ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = RuralSurface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(item.product.category.iconEmoji, fontSize = 32.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.product.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("₹${item.product.price.toInt()}/${item.product.unit}", fontSize = 12.sp, color = TextSecondary)
                                Text("📍 ${item.product.village}", fontSize = 10.sp, color = TextMuted)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { viewModel.updateCartQuantity(item.product.id, -1.0) },
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = "Decrease")
                                }
                                Text("${item.quantity.toInt()}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                IconButton(
                                    onClick = { viewModel.updateCartQuantity(item.product.id, 1.0) },
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Increase")
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

/**
 * Sections 19 & 20: Simple 3-Step Checkout
 * Step 1: Delivery Address
 * Step 2: Order Summary
 * Step 3: Payment (UPI, Card, Net Banking, Cash on Delivery)
 * [ Place Order ]
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    viewModel: GramVyaparViewModel,
    onBack: () -> Unit
) {
    val lang by viewModel.language.collectAsState()
    val user by viewModel.user.collectAsState()
    val cart by viewModel.cart.collectAsState()
    val subtotal = cart.sumOf { it.totalPrice }

    var address by remember { mutableStateOf("${user.village}, Buldhana District, Maharashtra - ${user.pincode}") }
    var selectedPayment by remember { mutableStateOf("UPI") }

    val paymentOptions = listOf("UPI", "Card", "Net Banking", "Cash on Delivery")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = RuralSurface)
            )
        },
        containerColor = RuralBackground,
        bottomBar = {
            Surface(
                color = RuralSurface,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total Payable", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("₹${subtotal.toInt()}", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = AgriGreenDark)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            viewModel.placeOrder(selectedPayment, address)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("checkout_place_order_btn"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
                    ) {
                        Text("Place Order", fontSize = 16.sp, fontWeight = FontWeight.Bold)
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Step 1: Delivery Address
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = RuralSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Step 1: Delivery Address (Buldhana)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = AgriGreenDark)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = false,
                        minLines = 2
                    )
                }
            }

            // Step 2: Order Summary
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = RuralSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Step 2: Order Summary", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = AgriGreenDark)
                    Spacer(modifier = Modifier.height(4.dp))
                    cart.forEach { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${item.product.name} x ${item.quantity.toInt()} ${item.product.unit}", fontSize = 12.sp)
                            Text("₹${item.totalPrice.toInt()}", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        }
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Delivery in Buldhana", fontSize = 12.sp, color = TextSecondary)
                        Text("FREE (Local Farm Direct)", fontSize = 12.sp, color = RateUpGreen, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Step 3: Payment Method (Section 19: UPI, Card, Net Banking, Cash on Delivery)
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = RuralSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Step 3: Payment", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = AgriGreenDark)
                    Spacer(modifier = Modifier.height(8.dp))
                    paymentOptions.forEach { method ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedPayment = method }
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedPayment == method,
                                onClick = { selectedPayment = method },
                                colors = RadioButtonDefaults.colors(selectedColor = AgriGreenPrimary)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(method, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

/**
 * Order Success Screen
 */
@Composable
fun OrderSuccessScreen(
    order: Order,
    onBackHome: () -> Unit,
    onViewOrders: () -> Unit
) {
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
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(AgriGreenContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Check, contentDescription = null, tint = AgriGreenPrimary, modifier = Modifier.size(40.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Order Placed Successfully! 🎉", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = AgriGreenDark)
            Spacer(modifier = Modifier.height(6.dp))
            Text("Order ID: ${order.id}", fontSize = 14.sp, color = TextSecondary)
            Text("Delivery in Buldhana: ${order.deliveryAddress}", fontSize = 12.sp, color = TextSecondary, textAlign = TextAlign.Center)

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onViewOrders,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
            ) {
                Text("Track Order", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = onBackHome,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Back to Home")
            }
        }
    }
}

/**
 * Section 21: Simple Orders Screen
 * Simple Statuses: Order Placed → Confirmed → Preparing → Out for Delivery → Delivered
 */
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
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("No orders placed yet", fontSize = 15.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = onShopNow,
                        colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
                    ) {
                        Text("Order Fresh Harvest")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Text("My Orders in Buldhana", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = AgriGreenDark)
                }

                items(orders, key = { it.id }) { order ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = RuralSurface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Order ${order.id}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                val statusText = if (lang == AppLanguage.MARATHI) order.orderStatus.titleMr else order.orderStatus.titleEn
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (order.orderStatus == OrderStatus.DELIVERED) AgriGreenContainer else SaffronContainer)
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = statusText,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (order.orderStatus == OrderStatus.DELIVERED) AgriGreenDark else OnSaffronContainer
                                    )
                                }
                            }

                            order.items.forEach { item ->
                                Text("• ${item.product.name} (${item.quantity.toInt()} ${item.product.unit})", fontSize = 12.sp)
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Amount: ₹${order.totalAmount.toInt()} (${order.paymentMethod})", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("OTP: ${order.deliveryOtp}", fontWeight = FontWeight.Bold, color = SaffronAccent, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
