package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
        val repository = GramVyaparRepository(database)
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
    val activeTab by viewModel.activeTab.collectAsState()
    val selectedProduct by viewModel.selectedProduct.collectAsState()
    val lastOrder by viewModel.lastPlacedOrder.collectAsState()

    var showNotificationsScreen by remember { mutableStateOf(false) }
    var showArtisansScreen by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = RuralBackground
    ) {
        if (showNotificationsScreen) {
            NotificationsScreen(
                viewModel = viewModel,
                onBack = { showNotificationsScreen = false }
            )
            return@Surface
        }

        if (showArtisansScreen) {
            RuralArtisansScreen(
                viewModel = viewModel,
                onBack = { showArtisansScreen = false }
            )
            return@Surface
        }

        AnimatedContent(
            targetState = destination,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "AppNavigation"
        ) { dest ->
            when (dest) {
                AppDestination.SPLASH -> {
                    SplashScreen(
                        viewModel = viewModel,
                        onFinish = { viewModel.navigateTo(AppDestination.LANGUAGE_SELECT) }
                    )
                }
                AppDestination.LANGUAGE_SELECT -> {
                    LanguageScreen(
                        viewModel = viewModel,
                        onLanguageSelected = { viewModel.navigateTo(AppDestination.LOGIN) }
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
                                viewModel.setActiveTab(3)
                                viewModel.navigateTo(AppDestination.MAIN_APP)
                            }
                        )
                    } ?: run {
                        viewModel.navigateTo(AppDestination.MAIN_APP)
                    }
                }
                AppDestination.TRAINING_DETAIL -> {
                    TrainingAcademyScreen(viewModel = viewModel)
                }
                AppDestination.ARTISAN_DETAIL -> {
                    RuralArtisansScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateTo(AppDestination.MAIN_APP) }
                    )
                }
                AppDestination.MAIN_APP -> {
                    Scaffold(
                        topBar = {
                            GramVyaparTopAppBar(
                                lang = lang,
                                user = user,
                                onLanguageClick = { viewModel.navigateTo(AppDestination.LANGUAGE_SELECT) },
                                onRoleClick = {
                                    val nextRole = when (user.role) {
                                        UserRole.BUYER -> UserRole.SELLER
                                        UserRole.SELLER -> UserRole.DELIVERY
                                        UserRole.DELIVERY -> UserRole.ADMIN
                                        UserRole.ADMIN -> UserRole.BUYER
                                    }
                                    viewModel.switchRole(nextRole)
                                },
                                onNotificationClick = { showNotificationsScreen = true },
                                cartItemCount = cart.sumOf { it.quantity.toInt() },
                                onCartClick = { viewModel.setActiveTab(1) }
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
                                UserRole.BUYER -> {
                                    when (activeTab) {
                                        0 -> BuyerHomeScreen(
                                            viewModel = viewModel,
                                            onProductClick = { viewModel.selectProduct(it) },
                                            onRateClick = { viewModel.setActiveTab(2) },
                                            onArtisansClick = { showArtisansScreen = true }
                                        )
                                        1 -> CartScreen(
                                            viewModel = viewModel,
                                            onProceedCheckout = { viewModel.navigateTo(AppDestination.CHECKOUT) },
                                            onShopMore = { viewModel.setActiveTab(0) }
                                        )
                                        2 -> MandiRatesScreen(
                                            viewModel = viewModel,
                                            onSellProduceClick = {
                                                viewModel.switchRole(UserRole.SELLER)
                                                viewModel.setActiveTab(1)
                                            }
                                        )
                                        3 -> {
                                            val orders by viewModel.orders.collectAsState()
                                            MyOrdersScreen(
                                                orders = orders,
                                                lang = lang,
                                                onShopNow = { viewModel.setActiveTab(0) }
                                            )
                                        }
                                        4 -> ProfileScreen(
                                            viewModel = viewModel,
                                            onNavigateToLanguage = { viewModel.navigateTo(AppDestination.LANGUAGE_SELECT) }
                                        )
                                    }
                                }
                                UserRole.SELLER -> {
                                    when (activeTab) {
                                        0 -> SellerDashboardScreen(
                                            viewModel = viewModel,
                                            onNavigateToAddProduct = { viewModel.setActiveTab(1) },
                                            onNavigateToTraining = { viewModel.setActiveTab(3) }
                                        )
                                        1 -> AddProductScreen(
                                            viewModel = viewModel,
                                            onBack = { viewModel.setActiveTab(0) }
                                        )
                                        2 -> MandiRatesScreen(
                                            viewModel = viewModel,
                                            onSellProduceClick = { viewModel.setActiveTab(1) }
                                        )
                                        3 -> TrainingAcademyScreen(viewModel = viewModel)
                                        4 -> ProfileScreen(
                                            viewModel = viewModel,
                                            onNavigateToLanguage = { viewModel.navigateTo(AppDestination.LANGUAGE_SELECT) }
                                        )
                                    }
                                }
                                UserRole.DELIVERY -> {
                                    when (activeTab) {
                                        0 -> DeliveryPartnerScreen(viewModel = viewModel)
                                        1 -> DeliveryPartnerScreen(viewModel = viewModel)
                                        2 -> NotificationsScreen(
                                            viewModel = viewModel,
                                            onBack = { viewModel.setActiveTab(0) }
                                        )
                                        3 -> ProfileScreen(
                                            viewModel = viewModel,
                                            onNavigateToLanguage = { viewModel.navigateTo(AppDestination.LANGUAGE_SELECT) }
                                        )
                                    }
                                }
                                UserRole.ADMIN -> {
                                    when (activeTab) {
                                        0 -> AdminOverviewScreen(viewModel = viewModel)
                                        1 -> AdminOverviewScreen(viewModel = viewModel)
                                        2 -> {
                                            val orders by viewModel.orders.collectAsState()
                                            MyOrdersScreen(
                                                orders = orders,
                                                lang = lang,
                                                onShopNow = { viewModel.setActiveTab(0) }
                                            )
                                        }
                                        3 -> MandiRatesScreen(
                                            viewModel = viewModel,
                                            onSellProduceClick = {}
                                        )
                                        4 -> ProfileScreen(
                                            viewModel = viewModel,
                                            onNavigateToLanguage = { viewModel.navigateTo(AppDestination.LANGUAGE_SELECT) }
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
    Text(text = "Welcome to GramVyapar, $name!", modifier = modifier)
}
