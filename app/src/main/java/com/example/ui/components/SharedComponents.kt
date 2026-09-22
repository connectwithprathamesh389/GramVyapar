package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
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

    // Auto scrolling effect simulated or user scrollable
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
                    text = "LIVE MANDI",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            rates.forEach { rate ->
                val name = when (lang) {
                    AppLanguage.HINDI -> rate.commodityHi
                    AppLanguage.MARATHI -> rate.commodityMr
                    else -> rate.commodity
                }
                val isPositive = rate.changePercentage >= 0

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { onRateClick(rate) }
                        .padding(horizontal = 8.dp)
                ) {
                    Text(
                        text = "$name (${rate.district}):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "₹${rate.modalPrice.toInt()}/qtl",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = CropGold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = (if (isPositive) "▲ +" else "▼ ") + "${rate.changePercentage}%",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isPositive) Color(0xFF69F0AE) else Color(0xFFFF8A80)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = "•", color = Color.White.copy(alpha = 0.4f), fontSize = 12.sp)
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
    onRoleClick: () -> Unit,
    onNotificationClick: () -> Unit,
    cartItemCount: Int = 0,
    onCartClick: () -> Unit = {}
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onRoleClick() }
            ) {
                // App Logo Icon
                androidx.compose.foundation.Image(
                    painter = painterResource(id = R.drawable.img_app_icon),
                    contentDescription = "GramVyapar Logo",
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = AppStrings.get("app_name", lang),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = AgriGreenDark
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(SaffronContainer)
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = user.role.name,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = OnSaffronContainer
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "📍 ${user.village}, ${user.district}",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        },
        actions = {
            // Language Button
            OutlinedButton(
                onClick = onLanguageClick,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                modifier = Modifier.height(32.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = AgriGreenPrimary)
            ) {
                Text(
                    text = lang.nativeName,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Notifications
            IconButton(
                onClick = onNotificationClick,
                modifier = Modifier.testTag("top_bar_notifications_button")
            ) {
                BadgedBox(
                    badge = {
                        Badge(containerColor = SaffronAccent) {
                            Text("3")
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = TextPrimary
                    )
                }
            }

            // Cart icon for quick access
            if (user.role == UserRole.BUYER) {
                IconButton(
                    onClick = onCartClick,
                    modifier = Modifier.testTag("top_bar_cart_button")
                ) {
                    BadgedBox(
                        badge = {
                            if (cartItemCount > 0) {
                                Badge(containerColor = AgriGreenPrimary) {
                                    Text(cartItemCount.toString())
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Cart",
                            tint = TextPrimary
                        )
                    }
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = RuralSurface
        )
    )
}

@Composable
fun GramVyaparBottomNavBar(
    role: UserRole,
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
            UserRole.BUYER -> {
                NavigationBarItem(
                    selected = activeTab == 0,
                    onClick = { onTabSelected(0) },
                    icon = { Icon(if (activeTab == 0) Icons.Filled.Home else Icons.Outlined.Home, contentDescription = "Home") },
                    label = { Text(AppStrings.get("nav_home", lang), fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = AgriGreenContainer)
                )
                NavigationBarItem(
                    selected = activeTab == 1,
                    onClick = { onTabSelected(1) },
                    icon = {
                        BadgedBox(badge = {
                            if (cartItemCount > 0) Badge(containerColor = SaffronAccent) { Text("$cartItemCount") }
                        }) {
                            Icon(if (activeTab == 1) Icons.Filled.ShoppingCart else Icons.Outlined.ShoppingCart, contentDescription = "Cart")
                        }
                    },
                    label = { Text(AppStrings.get("nav_cart", lang), fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = AgriGreenContainer)
                )
                NavigationBarItem(
                    selected = activeTab == 2,
                    onClick = { onTabSelected(2) },
                    icon = { Icon(if (activeTab == 2) Icons.Filled.TrendingUp else Icons.Outlined.TrendingUp, contentDescription = "Mandi Rates") },
                    label = { Text(AppStrings.get("nav_mandi_rates", lang), fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = AgriGreenContainer)
                )
                NavigationBarItem(
                    selected = activeTab == 3,
                    onClick = { onTabSelected(3) },
                    icon = { Icon(if (activeTab == 3) Icons.Filled.ReceiptLong else Icons.Outlined.ReceiptLong, contentDescription = "Orders") },
                    label = { Text(AppStrings.get("nav_orders", lang), fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = AgriGreenContainer)
                )
                NavigationBarItem(
                    selected = activeTab == 4,
                    onClick = { onTabSelected(4) },
                    icon = { Icon(if (activeTab == 4) Icons.Filled.Person else Icons.Outlined.Person, contentDescription = "Profile") },
                    label = { Text(AppStrings.get("nav_profile", lang), fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = AgriGreenContainer)
                )
            }
            UserRole.SELLER -> {
                NavigationBarItem(
                    selected = activeTab == 0,
                    onClick = { onTabSelected(0) },
                    icon = { Icon(if (activeTab == 0) Icons.Filled.Dashboard else Icons.Outlined.Dashboard, contentDescription = "Dashboard") },
                    label = { Text(AppStrings.get("nav_seller_dashboard", lang), fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = AgriGreenContainer)
                )
                NavigationBarItem(
                    selected = activeTab == 1,
                    onClick = { onTabSelected(1) },
                    icon = { Icon(Icons.Default.AddCircle, contentDescription = "Add Produce", tint = SaffronAccent) },
                    label = { Text(AppStrings.get("nav_add_product", lang), fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = AgriGreenContainer)
                )
                NavigationBarItem(
                    selected = activeTab == 2,
                    onClick = { onTabSelected(2) },
                    icon = { Icon(if (activeTab == 2) Icons.Filled.TrendingUp else Icons.Outlined.TrendingUp, contentDescription = "Rates") },
                    label = { Text(AppStrings.get("nav_mandi_rates", lang), fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = AgriGreenContainer)
                )
                NavigationBarItem(
                    selected = activeTab == 3,
                    onClick = { onTabSelected(3) },
                    icon = { Icon(if (activeTab == 3) Icons.Filled.School else Icons.Outlined.School, contentDescription = "Training") },
                    label = { Text("Training", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = AgriGreenContainer)
                )
                NavigationBarItem(
                    selected = activeTab == 4,
                    onClick = { onTabSelected(4) },
                    icon = { Icon(if (activeTab == 4) Icons.Filled.Person else Icons.Outlined.Person, contentDescription = "Profile") },
                    label = { Text(AppStrings.get("nav_profile", lang), fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = AgriGreenContainer)
                )
            }
            UserRole.DELIVERY -> {
                NavigationBarItem(
                    selected = activeTab == 0,
                    onClick = { onTabSelected(0) },
                    icon = { Icon(if (activeTab == 0) Icons.Filled.LocalShipping else Icons.Outlined.LocalShipping, contentDescription = "Deliveries") },
                    label = { Text(AppStrings.get("nav_deliveries", lang), fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = AgriGreenContainer)
                )
                NavigationBarItem(
                    selected = activeTab == 1,
                    onClick = { onTabSelected(1) },
                    icon = { Icon(if (activeTab == 1) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle, contentDescription = "Status") },
                    label = { Text(AppStrings.get("nav_status", lang), fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = AgriGreenContainer)
                )
                NavigationBarItem(
                    selected = activeTab == 2,
                    onClick = { onTabSelected(2) },
                    icon = { Icon(if (activeTab == 2) Icons.Filled.Notifications else Icons.Outlined.Notifications, contentDescription = "Alerts") },
                    label = { Text(AppStrings.get("nav_notifications", lang), fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = AgriGreenContainer)
                )
                NavigationBarItem(
                    selected = activeTab == 3,
                    onClick = { onTabSelected(3) },
                    icon = { Icon(if (activeTab == 3) Icons.Filled.Person else Icons.Outlined.Person, contentDescription = "Profile") },
                    label = { Text(AppStrings.get("nav_profile", lang), fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = AgriGreenContainer)
                )
            }
            UserRole.ADMIN -> {
                NavigationBarItem(
                    selected = activeTab == 0,
                    onClick = { onTabSelected(0) },
                    icon = { Icon(if (activeTab == 0) Icons.Filled.AdminPanelSettings else Icons.Outlined.AdminPanelSettings, contentDescription = "Admin") },
                    label = { Text("Overview", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = AgriGreenContainer)
                )
                NavigationBarItem(
                    selected = activeTab == 1,
                    onClick = { onTabSelected(1) },
                    icon = { Icon(if (activeTab == 1) Icons.Filled.Group else Icons.Outlined.Group, contentDescription = "Users") },
                    label = { Text(AppStrings.get("nav_users", lang), fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = AgriGreenContainer)
                )
                NavigationBarItem(
                    selected = activeTab == 2,
                    onClick = { onTabSelected(2) },
                    icon = { Icon(if (activeTab == 2) Icons.Filled.AssignmentTurnedIn else Icons.Outlined.AssignmentTurnedIn, contentDescription = "Orders") },
                    label = { Text(AppStrings.get("nav_orders", lang), fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = AgriGreenContainer)
                )
                NavigationBarItem(
                    selected = activeTab == 3,
                    onClick = { onTabSelected(3) },
                    icon = { Icon(if (activeTab == 3) Icons.Filled.Analytics else Icons.Outlined.Analytics, contentDescription = "Reports") },
                    label = { Text(AppStrings.get("nav_reports", lang), fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = AgriGreenContainer)
                )
                NavigationBarItem(
                    selected = activeTab == 4,
                    onClick = { onTabSelected(4) },
                    icon = { Icon(if (activeTab == 4) Icons.Filled.Person else Icons.Outlined.Person, contentDescription = "Profile") },
                    label = { Text(AppStrings.get("nav_profile", lang), fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = AgriGreenContainer)
                )
            }
        }
    }
}

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
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = RuralSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header: Category emoji & Badges
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(
                                if (product.isArtisanCraft) SaffronContainer else AgriGreenContainer,
                                Color(0xFFF1F8E9)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = product.category.iconEmoji,
                    fontSize = 48.sp
                )

                // Top Badge
                if (product.badge != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(6.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (product.isArtisanCraft) Terracotta else AgriGreenPrimary
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = product.badge,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // Organic Tag
                if (product.isOrganic && !product.isArtisanCraft) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF00796B))
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "🌱 Organic",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Farmer / Artisan info
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (product.isArtisanCraft) Icons.Default.Palette else Icons.Default.Agriculture,
                    contentDescription = null,
                    modifier = Modifier.size(12.dp),
                    tint = TextSecondary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${product.sellerName} • ${product.district}",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Pricing & Mandi Rate comparison
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "₹${product.price.toInt()}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = AgriGreenDark
                        )
                        Text(
                            text = " / ${product.unit}",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                    }
                    if (product.marketMandiRate > 0) {
                        Text(
                            text = "Mandi: ₹${product.marketMandiRate.toInt()}/${product.unit}",
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                    }
                }

                // Add to Cart Button
                FilledIconButton(
                    onClick = onAddToCart,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("add_to_cart_btn_${product.id}"),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = AgriGreenPrimary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add to Cart",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun MandiTrendSparkline(
    points: List<Double>,
    isPositive: Boolean,
    modifier: Modifier = Modifier
) {
    if (points.size < 2) return

    val minVal = points.minOrNull() ?: 0.0
    val maxVal = points.maxOrNull() ?: 1.0
    val range = (maxVal - minVal).coerceAtLeast(1.0)
    val lineColor = if (isPositive) AgriGreenPrimary else RateDownRed

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val stepX = width / (points.size - 1)

        val path = Path()
        points.forEachIndexed { i, value ->
            val normY = 1.0f - ((value - minVal) / range).toFloat()
            val x = i * stepX
            val y = normY * (height - 8f) + 4f
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }

        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(width = 3.dp.toPx())
        )
    }
}
