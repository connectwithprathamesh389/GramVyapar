package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.AppLanguage
import com.example.data.model.ProductCategory
import com.example.ui.components.MandiLiveRateTicker
import com.example.ui.components.ProductCard
import com.example.ui.i18n.AppStrings
import com.example.ui.theme.*
import com.example.ui.viewmodel.GramVyaparViewModel
import com.example.ui.viewmodel.SortBy

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyerHomeScreen(
    viewModel: GramVyaparViewModel,
    onProductClick: (com.example.data.model.Product) -> Unit,
    onRateClick: (com.example.data.model.MandiRate) -> Unit,
    onArtisansClick: () -> Unit
) {
    val lang by viewModel.language.collectAsState()
    val mandiRates by viewModel.mandiRates.collectAsState()
    val products by viewModel.filteredProducts.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCat by viewModel.selectedCategory.collectAsState()
    val isOrganicOnly by viewModel.isOrganicOnly.collectAsState()
    val maxPrice by viewModel.maxPriceFilter.collectAsState()
    val selectedSort by viewModel.selectedSort.collectAsState()
    var showFilterSheet by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RuralBackground)
    ) {
        // 1. Scrolling Mandi Rate Ticker
        MandiLiveRateTicker(
            rates = mandiRates,
            lang = lang,
            onRateClick = onRateClick
        )

        // Main content in LazyVerticalGrid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Header Section: Search & Filter
            item(span = { GridItemSpan(2) }) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.searchQuery.value = it },
                            placeholder = {
                                Text(
                                    text = AppStrings.get("search_placeholder", lang),
                                    fontSize = 13.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = "Search", tint = AgriGreenPrimary)
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                                        Icon(Icons.Default.Close, contentDescription = "Clear")
                                    }
                                } else {
                                    Icon(
                                        Icons.Default.Mic,
                                        contentDescription = "Voice Search",
                                        tint = SaffronAccent,
                                        modifier = Modifier.padding(end = 4.dp)
                                    )
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(24.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = RuralSurface,
                                unfocusedContainerColor = RuralSurface,
                                focusedBorderColor = AgriGreenPrimary,
                                unfocusedBorderColor = RuralCardBorder
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("search_input_field")
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        FilledIconButton(
                            onClick = { showFilterSheet = true },
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("filter_button"),
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = if (isOrganicOnly || selectedCat != ProductCategory.ALL) SaffronAccent else AgriGreenPrimary
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(Icons.Default.Tune, contentDescription = "Filter", tint = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Hero Banner Carousel
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = RuralSurface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Image(
                                painter = painterResource(id = R.drawable.img_hero_banner),
                                contentDescription = "Hero Harvest Banner",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(
                                                AgriGreenDark.copy(alpha = 0.88f),
                                                Color.Transparent
                                            )
                                        )
                                    )
                            )
                            Column(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(CropGold)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "100% DIRECT FROM FARMERS",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Fresh Harvest\n& Artisan Guild",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    lineHeight = 22.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Zero Middlemen • Transparent Mandi Rates",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Rural Weather Advisory Widget
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = AgriGreenContainer),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "🌤️", fontSize = 26.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Rural Weather: Nashik 28°C • Sunny",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OnAgriGreenContainer
                                )
                                Text(
                                    text = "Advisory: Ideal dry weather for onion & grain harvesting.",
                                    fontSize = 10.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Categories Horizontal Bar
                    Text(
                        text = AppStrings.get("categories", lang),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val scrollState = rememberScrollState()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(scrollState),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ProductCategory.values().forEach { cat ->
                            val isSelected = selectedCat == cat
                            val label = when (lang) {
                                AppLanguage.HINDI -> cat.titleHi
                                AppLanguage.MARATHI -> cat.titleMr
                                else -> cat.titleEn
                            }
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.selectedCategory.value = cat },
                                label = {
                                    Text(
                                        text = "${cat.iconEmoji} $label",
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
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

                    // Rural Artisans Guild Callout
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SaffronContainer),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onArtisansClick() }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🏺", fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = AppStrings.get("support_artisans", lang),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = OnSaffronContainer
                                    )
                                    Text(
                                        text = "Pottery, Warli Art, Paithani Weaving • Direct Support",
                                        fontSize = 10.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                            Icon(
                                Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = OnSaffronContainer
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = AppStrings.get("featured_farm_fresh", lang),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "${products.size} Items",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            // Product Cards Grid
            if (products.isEmpty()) {
                item(span = { GridItemSpan(2) }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🌾", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No produce found matching your filters",
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Button(
                                onClick = {
                                    viewModel.searchQuery.value = ""
                                    viewModel.selectedCategory.value = ProductCategory.ALL
                                    viewModel.isOrganicOnly.value = false
                                    viewModel.maxPriceFilter.value = 1500.0
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
                            ) {
                                Text("Reset Filters")
                            }
                        }
                    }
                }
            } else {
                items(products) { product ->
                    ProductCard(
                        product = product,
                        lang = lang,
                        onProductClick = { onProductClick(product) },
                        onAddToCart = { viewModel.addToCart(product, 1.0) }
                    )
                }
            }
        }
    }

    // Filter Bottom Sheet
    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            containerColor = RuralSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Refine Farm Produce",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    TextButton(
                        onClick = {
                            viewModel.selectedCategory.value = ProductCategory.ALL
                            viewModel.isOrganicOnly.value = false
                            viewModel.maxPriceFilter.value = 1500.0
                            viewModel.selectedSort.value = SortBy.POPULARITY
                        }
                    ) {
                        Text("Reset", color = SaffronAccent)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Organic Filter Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "🌱 Certified Organic Only",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Show 100% chemical & pesticide free farm produce",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                    Switch(
                        checked = isOrganicOnly,
                        onCheckedChange = { viewModel.isOrganicOnly.value = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = AgriGreenPrimary)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Price Range Slider
                Text(
                    text = "Max Price: ₹${maxPrice.toInt()}",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Slider(
                    value = maxPrice.toFloat(),
                    onValueChange = { viewModel.maxPriceFilter.value = it.toDouble() },
                    valueRange = 20f..1500f,
                    colors = SliderDefaults.colors(
                        thumbColor = AgriGreenPrimary,
                        activeTrackColor = AgriGreenPrimary
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Sorting options
                Text(
                    text = "Sort By",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        SortBy.POPULARITY to "Popular",
                        SortBy.PRICE_LOW_HIGH to "Price: Low→High",
                        SortBy.PRICE_HIGH_LOW to "Price: High→Low",
                        SortBy.RATING to "Rating"
                    ).forEach { (sort, title) ->
                        val isSel = selectedSort == sort
                        FilterChip(
                            selected = isSel,
                            onClick = { viewModel.selectedSort.value = sort },
                            label = { Text(title, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AgriGreenPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { showFilterSheet = false },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
                ) {
                    Text("Apply Filters", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
