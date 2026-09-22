package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.MandiRate
import com.example.data.model.ProductCategory
import com.example.ui.i18n.AppStrings
import com.example.ui.theme.*
import com.example.ui.viewmodel.GramVyaparViewModel

@Composable
fun MandiRatesScreen(
    viewModel: GramVyaparViewModel,
    onSellProduceClick: (MandiRate) -> Unit
) {
    val lang by viewModel.language.collectAsState()
    val allRates by viewModel.mandiRates.collectAsState()
    var selectedMandi by remember { mutableStateOf("All Mandis") }
    var selectedCategory by remember { mutableStateOf(ProductCategory.ALL) }
    var alertSubscribedCommodity by remember { mutableStateOf<String?>(null) }

    val mandis = listOf("All Mandis", "Pune APMC", "Lasalgaon", "Narayangaon", "Akola APMC", "Indore Mandi", "Kolhapur Yard")

    val filteredRates = allRates.filter { rate ->
        val mandiMatch = if (selectedMandi == "All Mandis") true else rate.mandiName.contains(selectedMandi.split(" ")[0])
        val catMatch = if (selectedCategory == ProductCategory.ALL) true else rate.category == selectedCategory
        mandiMatch && catMatch
    }

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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = AppStrings.get("mandi_title", lang),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = AgriGreenDark
                        )
                        Text(
                            text = "Real-Time APMC Mandi Benchmark (Agmarknet/eNAM)",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFE8F5E9))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("Live Sync", fontSize = 10.sp, color = AgriGreenPrimary, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Mandi Filter Chips
                Text("Select APMC Mandi Yard", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                val mandiScroll = rememberScrollState()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(mandiScroll),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    mandis.forEach { mandi ->
                        val isSel = selectedMandi == mandi
                        FilterChip(
                            selected = isSel,
                            onClick = { selectedMandi = mandi },
                            label = { Text(mandi, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AgriGreenPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Category Chips
                val catScroll = rememberScrollState()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(catScroll),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ProductCategory.values().forEach { cat ->
                        val isSel = selectedCategory == cat
                        val label = when (lang) {
                            AppLanguage.HINDI -> cat.titleHi
                            AppLanguage.MARATHI -> cat.titleMr
                            else -> cat.titleEn
                        }
                        FilterChip(
                            selected = isSel,
                            onClick = { selectedCategory = cat },
                            label = { Text("${cat.iconEmoji} $label", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SaffronAccent,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            if (alertSubscribedCommodity != null) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SaffronContainer),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = SaffronAccent)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Price Alert active for $alertSubscribedCommodity! You will receive SMS & app notifications on rate swings > 5%.",
                                fontSize = 11.sp,
                                color = OnSaffronContainer
                            )
                        }
                    }
                }
            }

            items(filteredRates) { rate ->
                val name = when (lang) {
                    AppLanguage.HINDI -> rate.commodityHi
                    AppLanguage.MARATHI -> rate.commodityMr
                    else -> rate.commodity
                }
                val isPositive = rate.changePercentage >= 0

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = RuralSurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = name,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "${rate.mandiName} • ${rate.district}, ${rate.state}",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                            IconButton(
                                onClick = {
                                    alertSubscribedCommodity = if (alertSubscribedCommodity == rate.commodity) null else rate.commodity
                                },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = if (alertSubscribedCommodity == rate.commodity) Icons.Default.NotificationsActive else Icons.Default.NotificationsNone,
                                    contentDescription = "Alert",
                                    tint = if (alertSubscribedCommodity == rate.commodity) SaffronAccent else TextSecondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Modal, Min, Max Prices
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Column {
                                Text("Modal Rate (बाजार भाव)", fontSize = 11.sp, color = TextSecondary)
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = "₹${rate.modalPrice.toInt()}",
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = AgriGreenDark
                                    )
                                    Text(
                                        text = " / ${rate.unit}",
                                        fontSize = 12.sp,
                                        color = TextSecondary,
                                        modifier = Modifier.padding(bottom = 2.dp)
                                    )
                                }
                            }

                            // Price Range box
                            Column(horizontalAlignment = Alignment.End) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isPositive) AgriGreenContainer else Color(0xFFFFEBEE))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = (if (isPositive) "▲ +" else "▼ ") + "${rate.changePercentage}%",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isPositive) RateUpGreen else RateDownRed
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Range: ₹${rate.minPrice.toInt()} - ₹${rate.maxPrice.toInt()}",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // 7-Day Trend Chart
                        Text("7-Day Mandi Price Trend", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
                        Spacer(modifier = Modifier.height(6.dp))
                        MandiTrendInteractiveChart(
                            points = rate.trend,
                            isPositive = isPositive,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Updated: ${rate.lastUpdated}",
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                            OutlinedButton(
                                onClick = { onSellProduceClick(rate) },
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Sell at this Mandi", fontSize = 11.sp, color = AgriGreenPrimary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MandiTrendInteractiveChart(
    points: List<Double>,
    isPositive: Boolean,
    modifier: Modifier = Modifier
) {
    if (points.size < 2) return

    val min = points.minOrNull() ?: 0.0
    val max = points.maxOrNull() ?: 1.0
    val range = (max - min).coerceAtLeast(1.0)
    val color = if (isPositive) AgriGreenPrimary else RateDownRed

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(RuralBackground)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val stepX = width / (points.size - 1)

            val path = Path()
            val fillPath = Path()

            points.forEachIndexed { i, v ->
                val normY = 1.0f - ((v - min) / range).toFloat()
                val x = i * stepX
                val y = normY * (height - 12f) + 6f

                if (i == 0) {
                    path.moveTo(x, y)
                    fillPath.moveTo(x, height)
                    fillPath.lineTo(x, y)
                } else {
                    path.lineTo(x, y)
                    fillPath.lineTo(x, y)
                }
            }

            fillPath.lineTo(width, height)
            fillPath.close()

            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    listOf(color.copy(alpha = 0.25f), Color.Transparent)
                )
            )

            drawPath(
                path = path,
                color = color,
                style = Stroke(width = 2.5.dp.toPx())
            )

            // Draw end circle
            val lastY = (1.0f - ((points.last() - min) / range).toFloat()) * (height - 12f) + 6f
            drawCircle(
                color = color,
                radius = 4.dp.toPx(),
                center = Offset(width, lastY)
            )
        }
    }
}
