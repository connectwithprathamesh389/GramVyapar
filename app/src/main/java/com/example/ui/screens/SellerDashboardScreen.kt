package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
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
import com.example.data.model.AppLanguage
import com.example.data.model.OrderStatus
import com.example.data.model.ProductCategory
import com.example.ui.i18n.AppStrings
import com.example.ui.theme.*
import com.example.ui.viewmodel.GramVyaparViewModel
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerDashboardScreen(
    viewModel: GramVyaparViewModel,
    onNavigateToAddProduct: () -> Unit,
    onBack: () -> Unit
) {
    val lang by viewModel.language.collectAsState()
    val user by viewModel.user.collectAsState()
    val products by viewModel.allProducts.collectAsState()
    val orders by viewModel.orders.collectAsState()

    var showAdvancedAnalytics by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(AppStrings.get("seller_dashboard", lang), fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = RuralSurface)
            )
        },
        containerColor = RuralBackground,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToAddProduct,
                containerColor = AgriGreenPrimary,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.Add, contentDescription = "Add") },
                text = { Text("Add Product", fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("seller_fab_add_product")
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Location Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Farmer: ${user.name}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = "📍 ${user.village}, Buldhana District",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(AgriGreenContainer)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text("KYC Verified", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AgriGreenDark)
                    }
                }
            }

            // Section 16: Today's Summary (Products: 12, Orders: 5, Sales: ₹4,500)
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = RuralSurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Today's Summary", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = AgriGreenDark)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Products", fontSize = 11.sp, color = TextSecondary)
                                Text("${products.size}", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
                            }
                            Column {
                                Text("Orders", fontSize = 11.sp, color = TextSecondary)
                                Text("${orders.size}", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = SaffronAccent)
                            }
                            Column {
                                Text("Sales", fontSize = 11.sp, color = TextSecondary)
                                Text("₹4,500", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = AgriGreenDark)
                            }
                        }
                    }
                }
            }

            // Section 16: Main Action Buttons
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onNavigateToAddProduct,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Product", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { showAdvancedAnalytics = !showAdvancedAnalytics },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(if (showAdvancedAnalytics) "Hide Details" else "View More", fontSize = 12.sp)
                    }
                }
            }

            // Optional Advanced Analytics under View More
            if (showAdvancedAnalytics) {
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = AgriGreenContainer),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Buldhana Mandi Price Intelligence", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = AgriGreenDark)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("• Tomato wholesale rate in Buldhana Mandi: ₹25/kg (Steady)", fontSize = 11.sp)
                            Text("• Soybean rate in Khamgaon Mandi: ₹4,800/quintal (+2.4%)", fontSize = 11.sp)
                            Text("• Malkapur Cotton mandi arrival: Moderate demand", fontSize = 11.sp)
                        }
                    }
                }
            }

            // Recent Orders for Pickup
            item {
                Text("Orders Ready for Pickup", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }

            items(orders) { order ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = RuralSurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Order ${order.id}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("OTP: ${order.deliveryOtp}", fontWeight = FontWeight.Bold, color = SaffronAccent, fontSize = 13.sp)
                        }
                        Text("Buyer: ${order.buyerName} • ${order.deliveryAddress}", fontSize = 12.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(6.dp))
                        order.items.forEach { item ->
                            Text("• ${item.quantity.toInt()} ${item.product.unit} of ${item.product.name}", fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Total: ₹${order.totalAmount.toInt()}", fontWeight = FontWeight.Bold, color = AgriGreenDark)
                            if (order.orderStatus != OrderStatus.DELIVERED) {
                                Button(
                                    onClick = { viewModel.updateOrderStatus(order.id, OrderStatus.OUT_FOR_DELIVERY) },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.height(34.dp)
                                ) {
                                    Text("Mark Handed Over", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }

            // My Listed Products
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Text("My Products in Buldhana", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }

            items(products) { product ->
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = RuralSurface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(product.category.iconEmoji, fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(product.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("₹${product.price.toInt()}/${product.unit} • Stock: ${product.stock.toInt()} ${product.unit}", fontSize = 11.sp, color = TextSecondary)
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }
}

/**
 * Section 8 & 9: Simple Add Product with Automatic Buldhana Market Rate Comparison
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    viewModel: GramVyaparViewModel,
    onBack: () -> Unit
) {
    val lang by viewModel.language.collectAsState()
    val mandiRates by viewModel.mandiRates.collectAsState()

    var name by remember { mutableStateOf("Tomato") }
    var quantityText by remember { mutableStateOf("100") }
    var unit by remember { mutableStateOf("kg") }
    var priceText by remember { mutableStateOf("22") }
    var selectedCategory by remember { mutableStateOf(ProductCategory.VEGETABLES) }

    // Automatic Buldhana Market Rate lookup from backend
    val currentBuldhanaMarketRate = remember(name, selectedCategory, mandiRates) {
        val match = mandiRates.firstOrNull { rate ->
            rate.district.equals("Buldhana", ignoreCase = true) &&
            (rate.commodity.contains(name, ignoreCase = true) ||
             rate.commodityMr.contains(name, ignoreCase = true) ||
             rate.commodityHi.contains(name, ignoreCase = true) ||
             rate.category == selectedCategory)
        }
        match?.modalPrice ?: 25.0
    }

    val sellerPrice = priceText.toDoubleOrNull() ?: 0.0

    // Section 9: Simple Price Comparison computation
    val comparisonMessage = remember(sellerPrice, currentBuldhanaMarketRate) {
        val diff = sellerPrice - currentBuldhanaMarketRate
        when {
            sellerPrice <= 0.0 -> ""
            diff < 0 -> "🟢 Good Deal (₹${abs(diff).toInt()} lower than Buldhana mandi rate)"
            diff > 0 -> "⚠️ Warning: ₹${diff.toInt()} higher than Buldhana mandi rate"
            else -> "🟢 Fair Deal (Same as Buldhana mandi rate)"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Product", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Product Name
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Product Name") },
                placeholder = { Text("e.g. Tomato, Soybean, Cotton, Tur Dal") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("add_product_name_input")
            )

            // Quantity & Unit
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = quantityText,
                    onValueChange = { quantityText = it },
                    label = { Text("Quantity") },
                    placeholder = { Text("100") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = unit,
                    onValueChange = { unit = it },
                    label = { Text("Unit") },
                    placeholder = { Text("kg / quintal") },
                    modifier = Modifier.weight(1f)
                )
            }

            // Section 8: Current Buldhana Market Rate display (automatic)
            Card(
                colors = CardDefaults.cardColors(containerColor = AgriGreenContainer),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = AgriGreenDark)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Current Buldhana Market Rate: ₹${currentBuldhanaMarketRate.toInt()}/$unit",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = AgriGreenDark
                        )
                        Text(
                            text = "Based on official Buldhana APMC Mandi rates",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            // Your Selling Price
            OutlinedTextField(
                value = priceText,
                onValueChange = { priceText = it },
                label = { Text("Your Selling Price (₹/$unit)") },
                placeholder = { Text("22") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("add_product_price_input")
            )

            // Section 9: Seller Price Comparison box
            if (comparisonMessage.isNotEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (sellerPrice <= currentBuldhanaMarketRate) Color(0xFFE8F5E9) else SaffronContainer
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Buldhana Market Rate: ₹${currentBuldhanaMarketRate.toInt()}/$unit",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = "Your Price: ₹${sellerPrice.toInt()}/$unit",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = comparisonMessage,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (sellerPrice <= currentBuldhanaMarketRate) AgriGreenDark else SaffronAccent
                        )
                    }
                }
            }

            // Photo Placeholder
            OutlinedButton(
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.PhotoCamera, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Product Photo: [ Add Photo ]")
            }

            Spacer(modifier = Modifier.weight(1f))

            // Add Product Button
            Button(
                onClick = {
                    val price = priceText.toDoubleOrNull() ?: 22.0
                    val qty = quantityText.toDoubleOrNull() ?: 100.0
                    viewModel.addProduct(
                        name = name.ifEmpty { "Farm Fresh Produce" },
                        category = selectedCategory,
                        price = price,
                        unit = unit.ifEmpty { "kg" },
                        stock = qty,
                        description = "Direct farm harvest from Buldhana."
                    )
                    onBack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("publish_produce_button"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
            ) {
                Text("Add Product", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
