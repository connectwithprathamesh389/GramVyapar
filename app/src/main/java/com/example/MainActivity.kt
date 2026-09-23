package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.AppDatabase
import com.example.data.model.UserRole
import com.example.data.repository.GramVyaparRepository
import com.example.ui.components.GramVyaparBottomNavBar
import com.example.ui.components.GramVyaparTopAppBar
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.RuralBackground
import com.example.ui.viewmodel.AppDestination
import com.example.ui.viewmodel.GramVyaparViewModel
import com.example.ui.viewmodel.GramVyaparViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(applicationContext)
        val repository = GramVyaparRepository(database, applicationContext)
        val factory = GramVyaparViewModelFactory(repository)

        setContent {
            MyApplicationTheme {
                val viewModel: GramVyaparViewModel = viewModel(factory = factory)
                GramVyaparApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun GramVyaparApp(viewModel: GramVyaparViewModel) {
    val destination by viewModel.currentDestination.collectAsState()
    val user by viewModel.user.collectAsState()
    val lang by viewModel.language.collectAsState()
    val cart by viewModel.cart.collectAsState()
    val orders by viewModel.orders.collectAsState()
    val activeTab by viewModel.activeTab.collectAsState()
    val selectedProduct by viewModel.selectedProduct.collectAsState()
    val lastOrder by viewModel.lastPlacedOrder.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = RuralBackground
    ) {
        AnimatedContent(
            targetState = destination,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "AppNavigation"
        ) { dest ->
            when (dest) {
                AppDestination.SPLASH -> {
                    SplashScreen(
                        viewModel = viewModel,
                        onFinish = {
                            if (viewModel.isLoggedIn()) {
                                viewModel.navigateTo(AppDestination.MAIN_APP)
                            } else {
                                viewModel.navigateTo(AppDestination.LANGUAGE_SELECT)
                            }
                        }
                    )
                }
                AppDestination.LANGUAGE_SELECT -> {
                    LanguageScreen(
                        viewModel = viewModel,
                        onLanguageSelected = {
                            if (viewModel.isLoggedIn()) {
                                viewModel.navigateTo(AppDestination.MAIN_APP)
                            } else {
                                viewModel.navigateTo(AppDestination.LOGIN)
                            }
                        },
                        onBack = {
                            if (viewModel.isLoggedIn()) {
                                viewModel.navigateTo(AppDestination.MAIN_APP)
                            } else {
                                viewModel.navigateTo(AppDestination.LOGIN)
                            }
                        }
                    )
                }
                AppDestination.LOGIN -> {
                    LoginScreen(
                        viewModel = viewModel,
                        onLoginSuccess = { viewModel.navigateTo(AppDestination.MAIN_APP) },
                        onRegisterClick = { viewModel.navigateTo(AppDestination.REGISTER) },
                        onForgotPasswordClick = { viewModel.navigateTo(AppDestination.FORGOT_PASSWORD) }
                    )
                }
                AppDestination.REGISTER -> {
                    RegisterScreen(
                        viewModel = viewModel,
                        onRegisterSuccess = { viewModel.navigateTo(AppDestination.MAIN_APP) },
                        onBackToLogin = { viewModel.navigateTo(AppDestination.LOGIN) }
                    )
                }
                AppDestination.FORGOT_PASSWORD -> {
                    ForgotPasswordScreen(
                        viewModel = viewModel,
                        onBackToLogin = { viewModel.navigateTo(AppDestination.LOGIN) }
                    )
                }
                AppDestination.PRODUCT_DETAIL -> {
                    selectedProduct?.let { product ->
                        ProductDetailScreen(
                            product = product,
                            viewModel = viewModel,
                            onBack = { viewModel.navigateTo(AppDestination.MAIN_APP) },
                            onNavigateToCheckout = { viewModel.navigateTo(AppDestination.CHECKOUT) }
                        )
                    } ?: run {
                        viewModel.navigateTo(AppDestination.MAIN_APP)
                    }
                }
                AppDestination.CHECKOUT -> {
                    CheckoutScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateTo(AppDestination.MAIN_APP) }
                    )
                }
                AppDestination.ORDER_SUCCESS -> {
                    lastOrder?.let { order ->
                        OrderSuccessScreen(
                            order = order,
                            onBackHome = {
                                viewModel.setActiveTab(0)
                                viewModel.navigateTo(AppDestination.MAIN_APP)
                            },
                            onViewOrders = {
                                viewModel.setActiveTab(2) // Orders tab
                                viewModel.navigateTo(AppDestination.MAIN_APP)
                            }
                        )
                    } ?: run {
                        viewModel.navigateTo(AppDestination.MAIN_APP)
                    }
                }
                AppDestination.TRAINING_DETAIL -> {
                    TrainingAcademyScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateTo(AppDestination.MAIN_APP) }
                    )
                }
                AppDestination.ARTISAN_DETAIL -> {
                    RuralArtisansScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateTo(AppDestination.MAIN_APP) }
                    )
                }
                AppDestination.SELLER_DASHBOARD -> {
                    SellerDashboardScreen(
                        viewModel = viewModel,
                        onNavigateToAddProduct = { viewModel.navigateTo(AppDestination.ADD_PRODUCT) },
                        onBack = { viewModel.navigateTo(AppDestination.MAIN_APP) }
                    )
                }
                AppDestination.ADD_PRODUCT -> {
                    AddProductScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateTo(AppDestination.MAIN_APP) }
                    )
                }
                AppDestination.DELIVERY_DASHBOARD -> {
                    DeliveryPartnerScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateTo(AppDestination.MAIN_APP) }
                    )
                }
                AppDestination.ADMIN_DASHBOARD -> {
                    AdminOverviewScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateTo(AppDestination.MAIN_APP) }
                    )
                }
                AppDestination.MANDI_RATES -> {
                    MandiRatesScreen(
                        viewModel = viewModel,
                        onSellProduceClick = {
                            viewModel.navigateTo(AppDestination.ADD_PRODUCT)
                        },
                        onBack = {
                            viewModel.navigateTo(AppDestination.MAIN_APP)
                        }
                    )
                }
                AppDestination.MAIN_APP -> {
                    Scaffold(
                        topBar = {
                            GramVyaparTopAppBar(
                                lang = lang,
                                user = user,
                                onLanguageClick = { viewModel.navigateTo(AppDestination.LANGUAGE_SELECT) },
                                cartItemCount = cart.sumOf { it.quantity.toInt() },
                                onCartClick = {
                                    if (user.role == UserRole.BUYER) {
                                        viewModel.setActiveTab(1)
                                    }
                                }
                            )
                        },
                        bottomBar = {
                            GramVyaparBottomNavBar(
                                role = user.role,
                                activeTab = activeTab,
                                cartItemCount = cart.sumOf { it.quantity.toInt() },
                                lang = lang,
                                onTabSelected = { viewModel.setActiveTab(it) }
                            )
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            when (user.role) {
                                UserRole.SELLER -> {
                                    when (activeTab) {
                                        // Seller Tab 0: Home (My Products & Dashboard)
                                        0 -> SellerDashboardScreen(
                                            viewModel = viewModel,
                                            onNavigateToAddProduct = { viewModel.setActiveTab(1) },
                                            onBack = { }
                                        )
                                        // Seller Tab 1: ➕ Add Product
                                        1 -> AddProductScreen(
                                            viewModel = viewModel,
                                            onBack = { viewModel.setActiveTab(0) }
                                        )
                                        // Seller Tab 2: 📦 Orders
                                        2 -> MyOrdersScreen(
                                            orders = orders,
                                            lang = lang,
                                            onShopNow = { viewModel.setActiveTab(0) }
                                        )
                                        // Seller Tab 3: 👤 Profile
                                        else -> ProfileScreen(
                                            viewModel = viewModel,
                                            onNavigateToLanguage = { viewModel.navigateTo(AppDestination.LANGUAGE_SELECT) },
                                            onNavigateToOrders = { viewModel.setActiveTab(2) },
                                            onNavigateToLearnAndGrow = { viewModel.navigateTo(AppDestination.TRAINING_DETAIL) },
                                            onNavigateToArtisans = { viewModel.navigateTo(AppDestination.ARTISAN_DETAIL) },
                                            onNavigateToSellerDashboard = { viewModel.setActiveTab(0) },
                                            onNavigateToDeliveries = { viewModel.navigateTo(AppDestination.DELIVERY_DASHBOARD) },
                                            onNavigateToAdmin = { viewModel.navigateTo(AppDestination.ADMIN_DASHBOARD) }
                                        )
                                    }
                                }
                                UserRole.DELIVERY -> {
                                    when (activeTab) {
                                        // Delivery Tab 0: 🚚 Deliveries (Today's deliveries & OTP flow)
                                        0 -> DeliveryPartnerScreen(
                                            viewModel = viewModel,
                                            onBack = { }
                                        )
                                        // Delivery Tab 1: 🔔 Notifications
                                        1 -> NotificationsScreen(
                                            viewModel = viewModel
                                        )
                                        // Delivery Tab 2: 👤 Profile
                                        else -> ProfileScreen(
                                            viewModel = viewModel,
                                            onNavigateToLanguage = { viewModel.navigateTo(AppDestination.LANGUAGE_SELECT) },
                                            onNavigateToOrders = { },
                                            onNavigateToLearnAndGrow = { viewModel.navigateTo(AppDestination.TRAINING_DETAIL) },
                                            onNavigateToArtisans = { viewModel.navigateTo(AppDestination.ARTISAN_DETAIL) },
                                            onNavigateToSellerDashboard = { },
                                            onNavigateToDeliveries = { viewModel.setActiveTab(0) },
                                            onNavigateToAdmin = { }
                                        )
                                    }
                                }
                                UserRole.ADMIN -> {
                                    when (activeTab) {
                                        // Admin Tab 0: 📊 Dashboard & Governance
                                        0 -> AdminOverviewScreen(
                                            viewModel = viewModel,
                                            onBack = { }
                                        )
                                        // Admin Tab 1: 👥 Users Management
                                        1 -> AdminOverviewScreen(
                                            viewModel = viewModel,
                                            onBack = { }
                                        )
                                        // Admin Tab 2: 📦 Orders & Delivery Assignment
                                        2 -> AdminOverviewScreen(
                                            viewModel = viewModel,
                                            onBack = { }
                                        )
                                        // Admin Tab 3: 👤 Console Profile
                                        else -> ProfileScreen(
                                            viewModel = viewModel,
                                            onNavigateToLanguage = { viewModel.navigateTo(AppDestination.LANGUAGE_SELECT) },
                                            onNavigateToOrders = { viewModel.setActiveTab(2) },
                                            onNavigateToLearnAndGrow = { viewModel.navigateTo(AppDestination.TRAINING_DETAIL) },
                                            onNavigateToArtisans = { viewModel.navigateTo(AppDestination.ARTISAN_DETAIL) },
                                            onNavigateToSellerDashboard = { viewModel.navigateTo(AppDestination.SELLER_DASHBOARD) },
                                            onNavigateToDeliveries = { viewModel.navigateTo(AppDestination.DELIVERY_DASHBOARD) },
                                            onNavigateToAdmin = { viewModel.setActiveTab(0) }
                                        )
                                    }
                                }
                                UserRole.BUYER -> {
                                    when (activeTab) {
                                        // Buyer Tab 0: 🏠 Home
                                        0 -> BuyerHomeScreen(
                                            viewModel = viewModel,
                                            onProductClick = { viewModel.selectProduct(it) },
                                            onRateClick = { viewModel.navigateTo(AppDestination.MANDI_RATES) },
                                            onCartClick = { viewModel.setActiveTab(1) }
                                        )
                                        // Buyer Tab 1: 🛒 Cart
                                        1 -> CartScreen(
                                            viewModel = viewModel,
                                            onProceedCheckout = { viewModel.navigateTo(AppDestination.CHECKOUT) },
                                            onShopMore = { viewModel.setActiveTab(0) }
                                        )
                                        // Buyer Tab 2: 📦 Orders
                                        2 -> MyOrdersScreen(
                                            orders = orders,
                                            lang = lang,
                                            onShopNow = { viewModel.setActiveTab(0) }
                                        )
                                        // Buyer Tab 3: 👤 Profile
                                        else -> ProfileScreen(
                                            viewModel = viewModel,
                                            onNavigateToLanguage = { viewModel.navigateTo(AppDestination.LANGUAGE_SELECT) },
                                            onNavigateToOrders = { viewModel.setActiveTab(2) },
                                            onNavigateToLearnAndGrow = { viewModel.navigateTo(AppDestination.TRAINING_DETAIL) },
                                            onNavigateToArtisans = { viewModel.navigateTo(AppDestination.ARTISAN_DETAIL) },
                                            onNavigateToSellerDashboard = { viewModel.navigateTo(AppDestination.SELLER_DASHBOARD) },
                                            onNavigateToDeliveries = { viewModel.navigateTo(AppDestination.DELIVERY_DASHBOARD) },
                                            onNavigateToAdmin = { viewModel.navigateTo(AppDestination.ADMIN_DASHBOARD) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}
