package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.automirrored.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.i18n.AppStrings
import com.example.ui.theme.*

@Composable
fun MandiLiveRateTicker(
    rates: List<MandiRate>,
    lang: AppLanguage,
    onRateClick: (MandiRate) -> Unit
) {
    val scrollState = rememberScrollState()

    Surface(
        color = AgriGreenDark,
        contentColor = Color.White,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .horizontalScroll(scrollState)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFFE65100))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "BULDHANA MANDI",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            rates.filter { it.district.equals("Buldhana", ignoreCase = true) }.forEach { rate ->
                val name = when (lang) {
                    AppLanguage.HINDI -> rate.commodityHi
                    AppLanguage.MARATHI -> rate.commodityMr
                    else -> rate.commodity
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { onRateClick(rate) }
                        .padding(horizontal = 8.dp)
                ) {
                    Text(
                        text = "$name (${rate.mandiName}):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "₹${rate.modalPrice.toInt()}/${rate.unit}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = CropGold
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("•", color = Color.White)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GramVyaparTopAppBar(
    lang: AppLanguage,
    user: UserProfile,
    onLanguageClick: () -> Unit,
    cartItemCount: Int,
    onCartClick: () -> Unit
) {
    TopAppBar(
        title = {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "📍 Buldhana, Maharashtra",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        color = AgriGreenDark
                    )
                }
                Text(
                    text = "${AppStrings.get("app_name", lang)} • Buldhana District",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
        },
        actions = {
            // Language selector button
            FilledTonalButton(
                onClick = onLanguageClick,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                modifier = Modifier
                    .height(32.dp)
                    .testTag("top_app_bar_language_btn"),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = AgriGreenContainer,
                    contentColor = AgriGreenDark
                )
            ) {
                Text(
                    text = lang.nativeName,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Show Cart ONLY for Buyer
            if (user.role == UserRole.BUYER) {
                IconButton(
                    onClick = onCartClick,
                    modifier = Modifier.testTag("top_app_bar_cart_btn")
                ) {
                    BadgedBox(badge = {
                        if (cartItemCount > 0) {
                            Badge(containerColor = SaffronAccent) {
                                Text("$cartItemCount")
                            }
                        }
                    }) {
                        Icon(
                            Icons.Default.ShoppingCart,
                            contentDescription = "Cart",
                            tint = AgriGreenDark
                        )
                    }
                }
            } else {
                // Role Badge indicator
                Box(
                    modifier = Modifier
                        .padding(start = 4.dp, end = 8.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(when (user.role) {
                            UserRole.SELLER -> AgriGreenContainer
                            UserRole.DELIVERY -> SaffronContainer
                            UserRole.ADMIN -> Color(0xFFEDE7F6)
                            else -> AgriGreenContainer
                        })
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = when (user.role) {
                            UserRole.SELLER -> "🌾 Farmer"
                            UserRole.DELIVERY -> "🚚 Delivery"
                            UserRole.ADMIN -> "🛡️ Admin"
                            else -> ""
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (user.role) {
                            UserRole.SELLER -> AgriGreenDark
                            UserRole.DELIVERY -> OnSaffronContainer
                            UserRole.ADMIN -> Color(0xFF4A148C)
                            else -> AgriGreenDark
                        }
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = RuralSurface)
    )
}

/**
 * Clean Bottom Navigation based on user role:
 * BUYER: 0. Home | 1. Cart | 2. Orders | 3. Profile
 * SELLER: 0. Home (My Products) | 1. Add Product | 2. Orders | 3. Profile
 * DELIVERY: 0. Deliveries | 1. Notifications | 2. Profile
 * ADMIN: 0. Dashboard | 1. Users | 2. Orders | 3. Profile
 */
@Composable
fun GramVyaparBottomNavBar(
    role: UserRole = UserRole.BUYER,
    activeTab: Int,
    cartItemCount: Int,
    lang: AppLanguage,
    onTabSelected: (Int) -> Unit
) {
    NavigationBar(
        containerColor = RuralSurface,
        tonalElevation = 8.dp
    ) {
        when (role) {
            UserRole.SELLER -> {
                // SELLER TAB 0: 🏠 Home (My Products)
                NavigationBarItem(
                    selected = activeTab == 0,
                    onClick = { onTabSelected(0) },
                    icon = {
                        Icon(
                            if (activeTab == 0) Icons.Filled.Home else Icons.Outlined.Home,
                            contentDescription = "My Products"
                        )
                    },
                    label = { Text(AppStrings.get("nav_home", lang), fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = AgriGreenContainer)
                )

                // SELLER TAB 1: ➕ Add Product
                NavigationBarItem(
                    selected = activeTab == 1,
                    onClick = { onTabSelected(1) },
                    icon = {
                        Icon(
                            if (activeTab == 1) Icons.Filled.AddCircle else Icons.Outlined.AddCircle,
                            contentDescription = "Add Product"
                        )
                    },
                    label = { Text(AppStrings.get("nav_add_product", lang), fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = AgriGreenContainer)
                )

                // SELLER TAB 2: 📦 Orders
                NavigationBarItem(
                    selected = activeTab == 2,
                    onClick = { onTabSelected(2) },
                    icon = {
                        Icon(
                            if (activeTab == 2) Icons.AutoMirrored.Filled.ReceiptLong else Icons.AutoMirrored.Outlined.ReceiptLong,
                            contentDescription = "Orders"
                        )
                    },
                    label = { Text(AppStrings.get("nav_orders", lang), fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = AgriGreenContainer)
                )

                // SELLER TAB 3: 👤 Profile
                NavigationBarItem(
                    selected = activeTab == 3,
                    onClick = { onTabSelected(3) },
                    icon = {
                        Icon(
                            if (activeTab == 3) Icons.Filled.Person else Icons.Outlined.Person,
                            contentDescription = "Profile"
                        )
                    },
                    label = { Text(AppStrings.get("nav_profile", lang), fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = AgriGreenContainer)
                )
            }
            UserRole.DELIVERY -> {
                // DELIVERY TAB 0: 🚚 Deliveries
                NavigationBarItem(
                    selected = activeTab == 0,
                    onClick = { onTabSelected(0) },
                    icon = {
                        Icon(
                            if (activeTab == 0) Icons.Filled.LocalShipping else Icons.Outlined.LocalShipping,
                            contentDescription = "Deliveries"
                        )
                    },
                    label = { Text("Deliveries", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = SaffronContainer)
                )

                // DELIVERY TAB 1: 🔔 Notifications
                NavigationBarItem(
                    selected = activeTab == 1,
                    onClick = { onTabSelected(1) },
                    icon = {
                        Icon(
                            if (activeTab == 1) Icons.Filled.Notifications else Icons.Outlined.Notifications,
                            contentDescription = "Alerts"
                        )
                    },
                    label = { Text("Alerts", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = SaffronContainer)
                )

                // DELIVERY TAB 2: 👤 Profile
                NavigationBarItem(
                    selected = activeTab == 2,
                    onClick = { onTabSelected(2) },
                    icon = {
                        Icon(
                            if (activeTab == 2) Icons.Filled.Person else Icons.Outlined.Person,
                            contentDescription = "Profile"
                        )
                    },
                    label = { Text(AppStrings.get("nav_profile", lang), fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = SaffronContainer)
                )
            }
            UserRole.ADMIN -> {
                // ADMIN TAB 0: 📊 Dashboard
                NavigationBarItem(
                    selected = activeTab == 0,
                    onClick = { onTabSelected(0) },
                    icon = {
                        Icon(
                            if (activeTab == 0) Icons.Filled.Dashboard else Icons.Outlined.Dashboard,
                            contentDescription = "Dashboard"
                        )
                    },
                    label = { Text("Dashboard", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = Color(0xFFEDE7F6))
                )

                // ADMIN TAB 1: 👥 Users
                NavigationBarItem(
                    selected = activeTab == 1,
                    onClick = { onTabSelected(1) },
                    icon = {
                        Icon(
                            if (activeTab == 1) Icons.Filled.Group else Icons.Outlined.Group,
                            contentDescription = "Users"
                        )
                    },
                    label = { Text("Users", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = Color(0xFFEDE7F6))
                )

                // ADMIN TAB 2: 📦 Orders & Deliveries
                NavigationBarItem(
                    selected = activeTab == 2,
                    onClick = { onTabSelected(2) },
                    icon = {
                        Icon(
                            if (activeTab == 2) Icons.AutoMirrored.Filled.ReceiptLong else Icons.AutoMirrored.Outlined.ReceiptLong,
                            contentDescription = "Orders"
                        )
                    },
                    label = { Text("Orders", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = Color(0xFFEDE7F6))
                )

                // ADMIN TAB 3: 👤 Profile
                NavigationBarItem(
                    selected = activeTab == 3,
                    onClick = { onTabSelected(3) },
                    icon = {
                        Icon(
                            if (activeTab == 3) Icons.Filled.Person else Icons.Outlined.Person,
                            contentDescription = "Console"
                        )
                    },
                    label = { Text("Console", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = Color(0xFFEDE7F6))
                )
            }
            UserRole.BUYER -> {
                // BUYER TAB 0: 🏠 Home
                NavigationBarItem(
                    selected = activeTab == 0,
                    onClick = { onTabSelected(0) },
                    icon = {
                        Icon(
                            if (activeTab == 0) Icons.Filled.Home else Icons.Outlined.Home,
                            contentDescription = "Home"
                        )
                    },
                    label = { Text(AppStrings.get("nav_home", lang), fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = AgriGreenContainer)
                )

                // BUYER TAB 1: 🛒 Cart
                NavigationBarItem(
                    selected = activeTab == 1,
                    onClick = { onTabSelected(1) },
                    icon = {
                        BadgedBox(badge = {
                            if (cartItemCount > 0) Badge(containerColor = SaffronAccent) { Text("$cartItemCount") }
                        }) {
                            Icon(
                                if (activeTab == 1) Icons.Filled.ShoppingCart else Icons.Outlined.ShoppingCart,
                                contentDescription = "Cart"
                            )
                        }
                    },
                    label = { Text(AppStrings.get("nav_cart", lang), fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = AgriGreenContainer)
                )

                // BUYER TAB 2: 📦 Orders
                NavigationBarItem(
                    selected = activeTab == 2,
                    onClick = { onTabSelected(2) },
                    icon = {
                        Icon(
                            if (activeTab == 2) Icons.AutoMirrored.Filled.ReceiptLong else Icons.AutoMirrored.Outlined.ReceiptLong,
                            contentDescription = "Orders"
                        )
                    },
                    label = { Text(AppStrings.get("nav_orders", lang), fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = AgriGreenContainer)
                )

                // BUYER TAB 3: 👤 Profile
                NavigationBarItem(
                    selected = activeTab == 3,
                    onClick = { onTabSelected(3) },
                    icon = {
                        Icon(
                            if (activeTab == 3) Icons.Filled.Person else Icons.Outlined.Person,
                            contentDescription = "Profile"
                        )
                    },
                    label = { Text(AppStrings.get("nav_profile", lang), fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = AgriGreenContainer)
                )
            }
        }
    }
}

/**
 * Simple Product Card following Level 1 principle:
 * Image/Emoji, Product Name, Seller price, Unit, Seller & Buldhana Location, Add to Cart.
 * No complicated metrics on the card.
 */
@Composable
fun ProductCard(
    product: Product,
    lang: AppLanguage,
    onProductClick: () -> Unit,
    onAddToCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    val name = when (lang) {
        AppLanguage.HINDI -> product.nameHi
        AppLanguage.MARATHI -> product.nameMr
        else -> product.name
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onProductClick() }
            .testTag("product_card_${product.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = RuralSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Product Icon/Emoji Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (product.isArtisanCraft) SaffronContainer else AgriGreenContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = product.category.iconEmoji,
                    fontSize = 44.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Product Name
            Text(
                text = name,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Price & Unit
            Text(
                text = "₹${product.price.toInt()}/${product.unit}",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                color = AgriGreenDark
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Seller & Buldhana Location
            Text(
                text = "Seller: ${product.sellerName}",
                fontSize = 11.sp,
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "📍 ${product.village}, Buldhana",
                fontSize = 10.sp,
                color = TextMuted,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Clean Add to Cart button
            Button(
                onClick = onAddToCart,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp)
                    .testTag("add_to_cart_btn_${product.id}"),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Icon(
                    Icons.Default.AddShoppingCart,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = AppStrings.get("add_to_cart", lang),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
