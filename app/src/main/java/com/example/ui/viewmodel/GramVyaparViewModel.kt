package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.GramVyaparRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppDestination {
    SPLASH,
    LANGUAGE_SELECT,
    LOGIN,
    REGISTER,
    FORGOT_PASSWORD,
    MAIN_APP,
    PRODUCT_DETAIL,
    CHECKOUT,
    ORDER_SUCCESS,
    TRAINING_DETAIL,
    ARTISAN_DETAIL,
    SELLER_DASHBOARD,
    ADD_PRODUCT,
    DELIVERY_DASHBOARD,
    ADMIN_DASHBOARD,
    MANDI_RATES
}

enum class SortBy {
    POPULARITY,
    PRICE_LOW_HIGH,
    PRICE_HIGH_LOW,
    RATING
}

class GramVyaparViewModel(private val repository: GramVyaparRepository) : ViewModel() {

    // App Navigation State
    private val _currentDestination = MutableStateFlow(AppDestination.SPLASH)
    val currentDestination: StateFlow<AppDestination> = _currentDestination.asStateFlow()

    // Clean 5-Tab Navigation Bar: 0: Home | 1: Rates | 2: Cart | 3: Notifications | 4: Profile
    private val _activeTab = MutableStateFlow(0)
    val activeTab: StateFlow<Int> = _activeTab.asStateFlow()

    // Selected product & artisan & training for detail view
    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct: StateFlow<Product?> = _selectedProduct.asStateFlow()

    private val _selectedArtisan = MutableStateFlow<Artisan?>(null)
    val selectedArtisan: StateFlow<Artisan?> = _selectedArtisan.asStateFlow()

    private val _selectedTraining = MutableStateFlow<TrainingModule?>(null)
    val selectedTraining: StateFlow<TrainingModule?> = _selectedTraining.asStateFlow()

    private val _lastPlacedOrder = MutableStateFlow<Order?>(null)
    val lastPlacedOrder: StateFlow<Order?> = _lastPlacedOrder.asStateFlow()

    // Search and Filters
    val searchQuery = MutableStateFlow("")
    val selectedCategory = MutableStateFlow(ProductCategory.ALL)
    val maxPriceFilter = MutableStateFlow(1500.0)
    val isOrganicOnly = MutableStateFlow(false)
    val selectedSort = MutableStateFlow(SortBy.POPULARITY)
    val showFilterSheet = MutableStateFlow(false)

    // Repository Flows
    val language = repository.currentLanguage
    val user = repository.currentUser
    val allUsers = repository.allUsers
    val allProducts = repository.products
    val cart = repository.cart
    val mandiRates = repository.mandiRates
    val orders = repository.orders
    val artisans = repository.artisans
    val trainingModules = repository.trainingModules
    val notifications = repository.notifications
    val isMandiServiceOnline = repository.isMandiServiceOnline

    // User-facing feedback/toast message
    val actionMessage = MutableStateFlow<String?>(null)
    fun clearActionMessage() { actionMessage.value = null }

    fun isLoggedIn(): Boolean = repository.sessionManager?.isLoggedIn() ?: false
    fun isFirstLaunch(): Boolean = repository.sessionManager?.isFirstLaunch() ?: false

    fun login(identifier: String, pass: String): Boolean {
        return repository.login(identifier, pass)
    }

    fun logout() {
        repository.logout()
        _currentDestination.value = AppDestination.LOGIN
    }

    fun registerUser(
        name: String,
        phone: String,
        email: String,
        role: UserRole,
        village: String,
        taluka: String,
        pincode: String,
        serviceArea: String = ""
    ): UserProfile {
        return repository.registerUser(name, phone, email, role, village, taluka, pincode, serviceArea)
    }

    fun verifyDeliveryOtp(orderId: String, otpInput: String): Boolean {
        return repository.verifyDeliveryOtp(orderId, otpInput)
    }

    fun assignDeliveryBoy(orderId: String, deliveryBoyId: String, deliveryBoyName: String) {
        repository.assignDeliveryBoy(orderId, deliveryBoyId, deliveryBoyName)
    }

    fun toggleUserStatus(userId: String) {
        repository.toggleUserStatus(userId)
    }

    fun updateUserPermissions(userId: String, newPermissions: UserPermissions) {
        try {
            repository.updateUserPermissions(userId, newPermissions)
            actionMessage.value = "Permissions updated successfully"
        } catch (e: Exception) {
            actionMessage.value = e.message ?: "Failed to update permissions"
        }
    }

    data class FilterCriteria(
        val query: String = "",
        val category: ProductCategory = ProductCategory.ALL,
        val maxPrice: Double = 1500.0,
        val organicOnly: Boolean = false,
        val sort: SortBy = SortBy.POPULARITY
    )

    private val filterCriteria = combine(
        combine(searchQuery, selectedCategory, maxPriceFilter) { q, cat, price -> Triple(q, cat, price) },
        combine(isOrganicOnly, selectedSort) { organic, sort -> Pair(organic, sort) }
    ) { (q, cat, price), (organic, sort) ->
        FilterCriteria(q, cat, price, organic, sort)
    }

    // Filtered Products Flow - strictly Buldhana District
    val filteredProducts: StateFlow<List<Product>> = combine(
        allProducts,
        filterCriteria
    ) { products, criteria ->
        var list = products

        if (criteria.query.isNotBlank()) {
            val q = criteria.query.trim().lowercase()
            list = list.filter {
                it.name.lowercase().contains(q) ||
                it.nameHi.lowercase().contains(q) ||
                it.nameMr.lowercase().contains(q) ||
                it.sellerName.lowercase().contains(q) ||
                it.village.lowercase().contains(q) ||
                it.district.lowercase().contains(q)
            }
        }

        if (criteria.category != ProductCategory.ALL) {
            list = list.filter { it.category == criteria.category }
        }

        list = list.filter { it.price <= criteria.maxPrice }

        if (criteria.organicOnly) {
            list = list.filter { it.isOrganic }
        }

        when (criteria.sort) {
            SortBy.POPULARITY -> list.sortedByDescending { it.reviewCount }
            SortBy.PRICE_LOW_HIGH -> list.sortedBy { it.price }
            SortBy.PRICE_HIGH_LOW -> list.sortedByDescending { it.price }
            SortBy.RATING -> list.sortedByDescending { it.rating }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Navigation Actions
    fun navigateTo(dest: AppDestination) {
        _currentDestination.value = dest
    }

    fun setActiveTab(index: Int) {
        _activeTab.value = index
    }

    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun setCategory(category: ProductCategory) {
        selectedCategory.value = category
    }

    fun setLanguage(lang: AppLanguage) {
        repository.setLanguage(lang)
    }

    fun selectProduct(product: Product) {
        _selectedProduct.value = product
        _currentDestination.value = AppDestination.PRODUCT_DETAIL
    }

    fun selectArtisan(artisan: Artisan) {
        _selectedArtisan.value = artisan
        _currentDestination.value = AppDestination.ARTISAN_DETAIL
    }

    fun selectTraining(module: TrainingModule) {
        _selectedTraining.value = module
        _currentDestination.value = AppDestination.TRAINING_DETAIL
    }

    // Cart Operations with Permission Verification
    fun addToCart(product: Product, quantity: Double = 1.0) {
        try {
            repository.addToCart(product, quantity)
            actionMessage.value = "Added to cart"
        } catch (e: Exception) {
            actionMessage.value = e.message ?: "Permission denied"
        }
    }

    fun updateCartQuantity(productId: String, delta: Double) {
        try {
            repository.updateCartQuantity(productId, delta)
        } catch (e: Exception) {
            actionMessage.value = e.message ?: "Permission denied"
        }
    }

    fun removeFromCart(productId: String) {
        repository.removeFromCart(productId)
    }

    fun placeOrder(paymentMethod: String, address: String) {
        try {
            val order = repository.placeOrder(paymentMethod, address)
            _lastPlacedOrder.value = order
            _currentDestination.value = AppDestination.ORDER_SUCCESS
        } catch (e: Exception) {
            actionMessage.value = e.message ?: "Order placement failed"
        }
    }

    fun addProduct(
        name: String,
        category: ProductCategory,
        price: Double,
        unit: String,
        stock: Double,
        description: String,
        isOrganic: Boolean = false
    ) {
        try {
            repository.addProduct(name, category, price, unit, stock, description)
            actionMessage.value = "Product published successfully"
        } catch (e: Exception) {
            actionMessage.value = e.message ?: "Permission denied to add product"
        }
    }

    fun markTrainingComplete(id: String) {
        repository.markModuleComplete(id)
    }

    fun updateOrderStatus(orderId: String, status: OrderStatus) {
        repository.updateOrderStatus(orderId, status)
    }
}

class GramVyaparViewModelFactory(private val repository: GramVyaparRepository) :
    ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GramVyaparViewModel::class.java)) {
            return GramVyaparViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
