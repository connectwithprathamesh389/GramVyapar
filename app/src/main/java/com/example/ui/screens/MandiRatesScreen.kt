package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import com.example.data.model.MandiRate
import com.example.ui.i18n.AppStrings
import com.example.ui.theme.*
import com.example.ui.viewmodel.GramVyaparViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MandiRatesScreen(
    viewModel: GramVyaparViewModel,
    onSellProduceClick: (MandiRate) -> Unit,
    onBack: (() -> Unit)? = null
) {
    val lang by viewModel.language.collectAsState()
    val allRates by viewModel.mandiRates.collectAsState()
    val isOnline by viewModel.isMandiServiceOnline.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedMarket by remember { mutableStateOf("All Buldhana Markets") }

    // Strictly Buldhana District Markets that actually have valid data
    val availableMarkets = remember(allRates) {
        val validMandiNames = allRates
            .filter { it.district.equals("Buldhana", ignoreCase = true) }
            .map { it.mandiName }
            .distinct()
        listOf("All Buldhana Markets") + validMandiNames
    }

    // Filter rates: Only Buldhana District data
    val filteredRates = remember(allRates, searchQuery, selectedMarket) {
        allRates
            .filter { it.district.equals("Buldhana", ignoreCase = true) }
            .filter { rate ->
                if (selectedMarket == "All Buldhana Markets") true else rate.mandiName == selectedMarket
            }
            .filter { rate ->
                if (searchQuery.isBlank()) true else {
                    val q = searchQuery.trim().lowercase()
                    rate.commodity.lowercase().contains(q) ||
                    rate.commodityHi.lowercase().contains(q) ||
                    rate.commodityMr.lowercase().contains(q) ||
                    rate.mandiName.lowercase().contains(q)
                }
            }
    }

    Scaffold(
        topBar = {
            if (onBack != null) {
                TopAppBar(
                    title = {
                        Text(
                            AppStrings.get("todays_market_rates", lang),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = RuralSurface)
                )
            }
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
            // Section 6: Top Location & Title
            item {
                Column {
                    Text(
                        text = "📍 Buldhana District",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = AgriGreenDark
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = AppStrings.get("todays_market_rates", lang),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Official APMC mandi prices for Buldhana, Maharashtra",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }

            // Fallback status indicator if offline / simulated
            if (!isOnline) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = RateDownRed)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = AppStrings.get("market_rate_unavailable", lang),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = RateDownRed
                                )
                                Text(
                                    text = AppStrings.get("last_updated_prefix", lang) + " Today, 10:30 AM (Cached)",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }
            }

            // Section 6: Simple Search Product
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = AppStrings.get("search_product", lang) + " (e.g. Tomato, Soybean, Cotton...)",
                            fontSize = 13.sp
                        )
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = AgriGreenPrimary)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = RuralSurface,
                        unfocusedContainerColor = RuralSurface,
                        focusedBorderColor = AgriGreenPrimary,
                        unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("mandi_search_input")
                )
            }

            // Section 7: Optional Simple Market Filter Chips
            // All Buldhana Markets, Buldhana, Khamgaon, Malkapur, Shegaon, Chikhli, Mehkar, Deulgaon Raja
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    availableMarkets.forEach { market ->
                        val isSelected = selectedMarket == market
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedMarket = market },
                            label = {
                                Text(
                                    text = if (market == "All Buldhana Markets") {
                                        AppStrings.get("all_buldhana_markets", lang)
                                    } else market,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AgriGreenPrimary,
                                selectedLabelColor = Color.White,
                                containerColor = RuralSurface,
                                labelColor = TextPrimary
                            )
                        )
                    }
                }
            }

            // Section 5: Rate Cards - Product, Market, Today's Rate, Last Updated
            if (filteredRates.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No rate found for this search in Buldhana.",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                    }
                }
            } else {
                items(filteredRates, key = { it.id }) { rate ->
                    val commodityName = when (lang) {
                        AppLanguage.HINDI -> rate.commodityHi
                        AppLanguage.MARATHI -> rate.commodityMr
                        else -> rate.commodity
                    }

                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = RuralSurface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Emoji
                            Text(
                                text = rate.category.iconEmoji,
                                fontSize = 34.sp
                            )
                            Spacer(modifier = Modifier.width(12.dp))

                            // Details: Product, Market, Last Updated
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = commodityName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Market: ${rate.mandiName} (Buldhana)",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                                Text(
                                    text = "Updated: ${rate.lastUpdated}",
                                    fontSize = 10.sp,
                                    color = TextMuted
                                )
                            }

                            // Rate & Unit with Change Indicator
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "₹${rate.modalPrice.toInt()}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = AgriGreenDark
                                )
                                Text(
                                    text = "/ ${rate.unit}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextSecondary
                                )
                                val isUp = rate.changePercentage >= 0
                                Text(
                                    text = if (isUp) "▲ +${rate.changePercentage}%" else "▼ ${rate.changePercentage}%",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isUp) RateUpGreen else RateDownRed
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}
