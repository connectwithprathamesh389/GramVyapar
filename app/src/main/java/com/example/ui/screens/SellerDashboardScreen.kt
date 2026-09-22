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
import androidx.compose.ui.graphics.Brush
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerDashboardScreen(
    viewModel: GramVyaparViewModel,
    onNavigateToAddProduct: () -> Unit,
    onNavigateToTraining: () -> Unit
) {
    val lang by viewModel.language.collectAsState()
    val user by viewModel.user.collectAsState()
    val products by viewModel.allProducts.collectAsState()
    val orders by viewModel.orders.collectAsState()

    val myProducts = products.filter { it.sellerName == user.name || it.id in listOf("p1", "p2") }

    Scaffold(
        containerColor = RuralBackground,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToAddProduct,
                containerColor = AgriGreenPrimary,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.Add, contentDescription = "Add") },
                text = { Text("List Farm Produce", fontWeight = FontWeight.Bold) },
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
            // Header Profile & Greeting
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Namaste, ${user.name.split(" ")[0]}! 🌾",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = AgriGreenDark
                        )
                        Text(
                            text = "Farmer & Producer Center • ${user.village}, ${user.district}",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(AgriGreenContainer)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("KYC Verified", fontSize = 11.sp, color = AgriGreenPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Metric Summary Cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = AgriGreenContainer),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(AppStrings.get("total_sales", lang), fontSize = 11.sp, color = TextSecondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("₹52,480", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = AgriGreenDark)
                            Text("+18% this harvest", fontSize = 9.sp, color = RateUpGreen)
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = SaffronContainer),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(AppStrings.get("orders_received", lang), fontSize = 11.sp, color = TextSecondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("28 Orders", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = OnSaffronContainer)
                            Text("6 awaiting dispatch", fontSize = 9.sp, color = SaffronAccent)
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = RuralSurface),
                        shape = RoundedCornerShape(14.dp),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(AppStrings.get("pending_payout", lang), fontSize = 11.sp, color = TextSecondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("₹14,200", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
                            Text("DBT Transfer", fontSize = 9.sp, color = TextMuted)
                        }
                    }
                }
            }

            // Quick Banner: Online Selling Academy
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = RuralSurface),
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToTraining() }
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🎓", fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Selling on Amazon Kisan & eNAM",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "Learn packaging, GST exemptions, and bulk interstate trade.",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = AgriGreenPrimary)
                    }
                }
            }

            // Section: Orders to Dispatch
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Orders Awaiting Dispatch",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text("3 Pending", fontSize = 12.sp, color = SaffronAccent, fontWeight = FontWeight.SemiBold)
                }
            }

            items(orders) { order ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = RuralSurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Order ${order.id}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("OTP: ${order.deliveryOtp}", fontWeight = FontWeight.Bold, color = SaffronAccent, fontSize = 13.sp)
                        }
                        Text("Buyer: ${order.buyerName} • ${order.buyerPhone}", fontSize = 12.sp, color = TextSecondary)
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
                            Text("Amount: ₹${order.totalAmount.toInt()}", fontWeight = FontWeight.Bold, color = AgriGreenDark, fontSize = 14.sp)
                            Button(
                                onClick = {
                                    viewModel.updateOrderStatus(order.id, OrderStatus.PICKED_UP)
                                },
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                modifier = Modifier.height(34.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
                            ) {
                                Text("Ready for Pickup", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Section: My Listed Produce
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "My Active Produce Listings",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text("${myProducts.size} Items", fontSize = 12.sp, color = TextSecondary)
                }
            }

            items(myProducts) { product ->
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
                        Text(product.category.iconEmoji, fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(product.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Price: ₹${product.price.toInt()}/${product.unit} • Stock: ${product.stock.toInt()} ${product.unit}", fontSize = 11.sp, color = TextSecondary)
                            Text("Benchmark Mandi Rate: ₹${product.marketMandiRate.toInt()}/${product.unit}", fontSize = 10.sp, color = TextMuted)
                        }
                        IconButton(onClick = {}) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = TextSecondary, modifier = Modifier.size(20.dp))
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    viewModel: GramVyaparViewModel,
    onBack: () -> Unit
) {
    val lang by viewModel.language.collectAsState()
    var name by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(ProductCategory.VEGETABLES) }
    var priceText by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("kg") }
    var stockText by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isOrganic by remember { mutableStateOf(true) }
    var isSuccess by remember { mutableStateOf(false) }

    val currentBenchmark = when (selectedCategory) {
        ProductCategory.VEGETABLES -> 26.0
        ProductCategory.GRAINS -> 48.0
        ProductCategory.PULSES -> 140.0
        ProductCategory.SPICES -> 70.0
        ProductCategory.DAIRY -> 1200.0
        else -> 350.0
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(AppStrings.get("add_new_produce", lang), fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = RuralSurface)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(18.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(AppStrings.get("product_name", lang)) },
                placeholder = { Text("e.g. Fresh Red Onions, A2 Cow Ghee, Warli Vase") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("add_product_name_input")
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text("Category", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(ProductCategory.VEGETABLES, ProductCategory.GRAINS, ProductCategory.PULSES, ProductCategory.HANDICRAFTS).forEach { cat ->
                    val isSel = selectedCategory == cat
                    FilterChip(
                        selected = isSel,
                        onClick = { selectedCategory = cat },
                        label = { Text("${cat.iconEmoji} ${cat.titleEn}", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AgriGreenPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it },
                    label = { Text(AppStrings.get("price_per_unit", lang)) },
                    placeholder = { Text("₹ Price") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("add_product_price_input")
                )
                OutlinedTextField(
                    value = unit,
                    onValueChange = { unit = it },
                    label = { Text(AppStrings.get("unit_type", lang)) },
                    placeholder = { Text("kg / quintal / pc") },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Real-time Mandi Rate benchmark notification
            Card(
                colors = CardDefaults.cardColors(containerColor = AgriGreenContainer),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.TrendingUp, contentDescription = null, tint = AgriGreenDark)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Live APMC Benchmark for ${selectedCategory.titleEn}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = OnAgriGreenContainer
                        )
                        Text(
                            text = "Wholesale rate is approx. ₹${currentBenchmark.toInt()}/$unit. Pricing competitively boosts sales by 3x.",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = stockText,
                onValueChange = { stockText = it },
                label = { Text("Stock Quantity Available") },
                placeholder = { Text("e.g. 500") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Produce Description & Harvest Date") },
                placeholder = { Text("Harvested yesterday, sun-cured, natural pesticide-free...") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = isOrganic,
                    onCheckedChange = { isOrganic = it },
                    colors = CheckboxDefaults.colors(checkedColor = AgriGreenPrimary)
                )
                Text(
                    text = "🌱 Certified Organic / Naturally Grown without chemicals",
                    fontSize = 13.sp,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    val price = priceText.toDoubleOrNull() ?: 30.0
                    val stock = stockText.toDoubleOrNull() ?: 100.0
                    viewModel.addProduct(
                        name = name.ifEmpty { "Fresh Farm Produce" },
                        category = selectedCategory,
                        price = price,
                        unit = unit.ifEmpty { "kg" },
                        stock = stock,
                        description = description.ifEmpty { "Freshly harvested produce directly from farm." },
                        isOrganic = isOrganic
                    )
                    isSuccess = true
                    onBack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("publish_produce_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
            ) {
                Text(AppStrings.get("publish_product", lang), fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
