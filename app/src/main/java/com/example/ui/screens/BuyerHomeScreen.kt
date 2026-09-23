package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.example.data.model.Product
import com.example.data.model.ProductCategory
import com.example.ui.components.MandiLiveRateTicker
import com.example.ui.components.ProductCard
import com.example.ui.i18n.AppStrings
import com.example.ui.theme.*
import com.example.ui.viewmodel.GramVyaparViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyerHomeScreen(
    viewModel: GramVyaparViewModel,
    onProductClick: (Product) -> Unit,
    onRateClick: () -> Unit,
    onCartClick: () -> Unit
) {
    val lang by viewModel.language.collectAsState()
    val mandiRates by viewModel.mandiRates.collectAsState()
    val products by viewModel.filteredProducts.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCat by viewModel.selectedCategory.collectAsState()
    val notifications by viewModel.notifications.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RuralBackground)
    ) {
        // Priority 4: Today's Market Rates Ticker
        MandiLiveRateTicker(
            rates = mandiRates,
            lang = lang,
            onRateClick = { onRateClick() }
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Priority 1: Buldhana District Location
            item(span = { GridItemSpan(2) }) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "📍 Buldhana, Maharashtra",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = AgriGreenDark
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(AgriGreenContainer)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "Buldhana District",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AgriGreenDark
                        )
                    }
                }
            }

            // Priority 2: Simple Search Input
            item(span = { GridItemSpan(2) }) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = {
                        Text(
                            text = AppStrings.get("search_product", lang) + " (e.g. Tomato, Soybean, Cotton...)",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = AgriGreenPrimary)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
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
                        unfocusedBorderColor = RuralCardBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_search_input")
                )
            }

            // Priority 3: Categories List
            // Vegetables, Fruits, Grains, Pulses, Oilseeds, Cotton, Other Agriculture, Handicrafts
            item(span = { GridItemSpan(2) }) {
                Column {
                    Text(
                        text = AppStrings.get("categories", lang),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val categories = listOf(
                            ProductCategory.ALL,
                            ProductCategory.VEGETABLES,
                            ProductCategory.FRUITS,
                            ProductCategory.GRAINS,
                            ProductCategory.PULSES,
                            ProductCategory.OILSEEDS,
                            ProductCategory.COTTON,
                            ProductCategory.OTHER_AGRI,
                            ProductCategory.HANDICRAFTS
                        )

                        categories.forEach { cat ->
                            val isSelected = selectedCat == cat
                            val label = when (lang) {
                                AppLanguage.HINDI -> cat.titleHi
                                AppLanguage.MARATHI -> cat.titleMr
                                else -> cat.titleEn
                            }

                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.setCategory(cat) },
                                label = {
                                    Text(
                                        text = "${cat.iconEmoji} $label",
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
            }

            // Quick Actions: 🛒 Cart | 📊 Today's Rates
            item(span = { GridItemSpan(2) }) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onCartClick() },
                        colors = CardDefaults.cardColors(containerColor = SaffronContainer),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = OnSaffronContainer)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "🛒 " + AppStrings.get("nav_cart", lang),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = OnSaffronContainer
                            )
                        }
                    }

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onRateClick() },
                        colors = CardDefaults.cardColors(containerColor = AgriGreenContainer),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = AgriGreenDark)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "📊 " + AppStrings.get("todays_market_rates", lang),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = AgriGreenDark
                            )
                        }
                    }
                }
            }

            // Priority 6: Important notification banner if present
            notifications.firstOrNull()?.let { notif ->
                item(span = { GridItemSpan(2) }) {
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🔔", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = notif.title,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF5D4037)
                            )
                        }
                    }
                }
            }

            // Priority 5: Products Header
            item(span = { GridItemSpan(2) }) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Local Produce in Buldhana",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "${products.size} items",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }

            // Products Grid (Simple Cards: Level 1 info)
            items(products, key = { it.id }) { product ->
                ProductCard(
                    product = product,
                    lang = lang,
                    onProductClick = { onProductClick(product) },
                    onAddToCart = { viewModel.addToCart(product, 1.0) }
                )
            }

            item(span = { GridItemSpan(2) }) {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}
