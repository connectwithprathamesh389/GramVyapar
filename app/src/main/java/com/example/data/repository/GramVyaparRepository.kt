package com.example.data.repository

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.local.SessionManager
import com.example.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GramVyaparRepository(
    private val database: AppDatabase,
    private val context: Context? = null
) {
    val sessionManager: SessionManager? = context?.let { SessionManager(it) }

    // Pre-seeded Single Admin Account (ADMIN-001) + default test users
    private val _allUsers = MutableStateFlow<List<UserProfile>>(
        listOf(
            UserProfile(
                id = "ADMIN-001",
                name = "Buldhana District Collectorate Admin",
                phone = "+91 94221 00001",
                email = "admin@buldhana.gov.in",
                role = UserRole.ADMIN,
                village = "Buldhana City",
                taluka = "Buldhana",
                district = "Buldhana",
                state = "Maharashtra",
                pincode = "443001",
                serviceArea = "Buldhana District"
            ),
            UserProfile(
                id = "user_buyer_01",
                name = "Gajanan Patil",
                phone = "+91 98229 45678",
                email = "gajanan.patil@gramvyapar.in",
                role = UserRole.BUYER,
                village = "Chikhli",
                taluka = "Chikhli",
                district = "Buldhana",
                state = "Maharashtra",
                pincode = "443201"
            ),
            UserProfile(
                id = "user_seller_01",
                name = "Santosh Deshmukh",
                phone = "+91 98220 54321",
                email = "santosh.farmer@gramvyapar.in",
                role = UserRole.SELLER,
                village = "Khamgaon",
                taluka = "Khamgaon",
                district = "Buldhana",
                state = "Maharashtra",
                pincode = "444303"
            ),
            UserProfile(
                id = "user_delivery_01",
                name = "Rahul Gaikwad",
                phone = "+91 98221 12233",
                email = "rahul.delivery@gramvyapar.in",
                role = UserRole.DELIVERY,
                village = "Khamgaon",
                taluka = "Khamgaon",
                district = "Buldhana",
                state = "Maharashtra",
                pincode = "444303",
                serviceArea = "Khamgaon"
            )
        )
    )
    val allUsers: StateFlow<List<UserProfile>> = _allUsers.asStateFlow()

    // Default language is Marathi (preferred for Buldhana local community)
    private val _currentLanguage = MutableStateFlow(AppLanguage.MARATHI)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    // Default user is fixed to Buldhana District, Maharashtra
    private val _currentUser = MutableStateFlow(
        UserProfile(
            id = "user_bld_01",
            name = "Gajanan Patil",
            phone = "+91 98229 45678",
            email = "gajanan.patil@gramvyapar.in",
            role = UserRole.BUYER,
            village = "Chikhli",
            taluka = "Chikhli",
            district = "Buldhana",
            state = "Maharashtra",
            pincode = "443201",
            isKycVerified = true,
            walletBalance = 1450.0
        )
    )
    val currentUser: StateFlow<UserProfile> = _currentUser.asStateFlow()

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _cart = MutableStateFlow<List<CartItem>>(emptyList())
    val cart: StateFlow<List<CartItem>> = _cart.asStateFlow()

    // Buldhana District Market Rate Cache & Service
    private val _mandiRates = MutableStateFlow<List<MandiRate>>(emptyList())
    val mandiRates: StateFlow<List<MandiRate>> = _mandiRates.asStateFlow()

    private val _isMandiServiceOnline = MutableStateFlow(true)
    val isMandiServiceOnline: StateFlow<Boolean> = _isMandiServiceOnline.asStateFlow()

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    private val _artisans = MutableStateFlow<List<Artisan>>(emptyList())
    val artisans: StateFlow<List<Artisan>> = _artisans.asStateFlow()

    private val _trainingModules = MutableStateFlow<List<TrainingModule>>(emptyList())
    val trainingModules: StateFlow<List<TrainingModule>> = _trainingModules.asStateFlow()

    private val _notifications = MutableStateFlow<List<NotificationItem>>(emptyList())
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    init {
        loadBuldhanaMarketData()
        sessionManager?.let { sm ->
            if (sm.isLoggedIn()) {
                _currentUser.value = sm.getSavedUser()
            }
            _currentLanguage.value = sm.getSavedLanguage()
        }
        updateRoleNotifications(_currentUser.value.role)
    }

    fun setLanguage(language: AppLanguage) {
        _currentLanguage.value = language
        sessionManager?.saveLanguage(language)
    }

    fun switchRole(role: UserRole) {
        val targetUser = _allUsers.value.find { it.role == role }
        if (targetUser != null) {
            _currentUser.value = targetUser
            sessionManager?.saveSession(targetUser)
        } else {
            val updated = _currentUser.value.copy(role = role)
            _currentUser.value = updated
            sessionManager?.saveSession(updated)
        }
        updateRoleNotifications(role)
    }

    fun login(identifier: String, pass: String, requestedRole: UserRole? = null): Boolean {
        val trimmed = identifier.trim()
        val user = _allUsers.value.find {
            (it.phone.contains(trimmed) || it.email.equals(trimmed, ignoreCase = true) || it.id.equals(trimmed, ignoreCase = true)) &&
            (requestedRole == null || it.role == requestedRole)
        } ?: _allUsers.value.find {
            if (requestedRole != null) it.role == requestedRole else true
        } ?: UserProfile(
            id = "user_bld_" + System.currentTimeMillis(),
            name = if (trimmed.isNotBlank()) trimmed else "Local Buldhana User",
            phone = if (trimmed.all { it.isDigit() }) trimmed else "+91 98229 45678",
            email = "$trimmed@gramvyapar.in",
            role = requestedRole ?: UserRole.BUYER,
            village = "Chikhli",
            taluka = "Chikhli",
            district = "Buldhana",
            state = "Maharashtra",
            pincode = "443201"
        )

        _currentUser.value = user
        sessionManager?.saveSession(user)
        updateRoleNotifications(user.role)
        return true
    }

    fun logout() {
        sessionManager?.clearSession()
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
        // ENFORCE SINGLE ADMIN RULE AT BACKEND LEVEL
        if (role == UserRole.ADMIN) {
            throw SecurityException("Admin registration is strictly prohibited. Only single Admin ADMIN-001 is authorized.")
        }

        val newUser = UserProfile(
            id = "user_bld_" + System.currentTimeMillis(),
            name = name.trim(),
            phone = phone.trim(),
            email = email.trim().ifEmpty { "${phone.trim()}@gramvyapar.in" },
            role = role,
            village = village.trim().ifEmpty { "Chikhli" },
            taluka = taluka.trim().ifEmpty { "Chikhli" },
            district = "Buldhana",
            state = "Maharashtra",
            pincode = pincode.trim().ifEmpty { "443201" },
            serviceArea = if (role == UserRole.DELIVERY) (if (serviceArea.isNotBlank()) serviceArea else village).trim() else village.trim(),
            isKycVerified = true
        )

        _allUsers.value = _allUsers.value + newUser
        _currentUser.value = newUser
        sessionManager?.saveSession(newUser)
        updateRoleNotifications(newUser.role)
        return newUser
    }

    fun toggleUserStatus(userId: String) {
        _allUsers.value = _allUsers.value.map {
            if (it.id == userId) it.copy(isActive = !it.isActive) else it
        }
    }

    fun assignDeliveryBoy(orderId: String, deliveryBoyId: String, deliveryBoyName: String) {
        _orders.value = _orders.value.map {
            if (it.id == orderId) {
                it.copy(
                    assignedDeliveryBoyId = deliveryBoyId,
                    assignedDeliveryPartner = deliveryBoyName,
                    orderStatus = if (it.orderStatus == OrderStatus.PLACED) OrderStatus.CONFIRMED else it.orderStatus
                )
            } else it
        }
    }

    fun verifyDeliveryOtp(orderId: String, enteredOtp: String): Boolean {
        val order = _orders.value.find { it.id == orderId } ?: return false
        if (order.deliveryOtp.trim() == enteredOtp.trim()) {
            _orders.value = _orders.value.map {
                if (it.id == orderId) {
                    it.copy(
                        orderStatus = OrderStatus.DELIVERED,
                        isOtpVerified = true
                    )
                } else it
            }
            _notifications.value = listOf(
                NotificationItem(
                    id = "notif_" + System.currentTimeMillis(),
                    title = "Order $orderId Delivered Successfully",
                    message = "Customer delivery OTP verified. Delivery completed in Buldhana.",
                    timestamp = "Just Now",
                    type = "ORDER"
                )
            ) + _notifications.value
            return true
        }
        return false
    }

    fun updateRoleNotifications(role: UserRole) {
        val notifs = when (role) {
            UserRole.BUYER -> listOf(
                NotificationItem(
                    id = "b_notif_1",
                    title = "Your order #1025 has been confirmed.",
                    message = "Farmer in Chikhli is preparing your fresh harvest. Delivery OTP: 4826",
                    timestamp = "10 mins ago",
                    type = "ORDER"
                ),
                NotificationItem(
                    id = "b_notif_2",
                    title = "Tomato market rate updated in Buldhana.",
                    message = "Today's rate is ₹25/kg in Buldhana APMC Mandi.",
                    timestamp = "Today, 10:30 AM",
                    type = "RATE"
                ),
                NotificationItem(
                    id = "b_notif_3",
                    title = "Fresh Organic Soybean Harvest in Khamgaon",
                    message = "Direct farm price ₹48/kg with doorstep delivery.",
                    timestamp = "Today, 08:30 AM",
                    type = "ALERT"
                )
            )
            UserRole.SELLER -> listOf(
                NotificationItem(
                    id = "s_notif_1",
                    title = "New Purchase Order Received! #1025",
                    message = "Rahul Joshi ordered 5kg Fresh Tomato & 2kg Soybean.",
                    timestamp = "Just Now",
                    type = "ORDER"
                ),
                NotificationItem(
                    id = "s_notif_2",
                    title = "Buldhana Cotton Rate Up ▲ +₹140/quintal",
                    message = "Khamgaon APMC modal price reached ₹7,450/quintal today.",
                    timestamp = "Today, 11:00 AM",
                    type = "RATE"
                ),
                NotificationItem(
                    id = "s_notif_3",
                    title = "Inventory Alert",
                    message = "Your Organic Wheat stock is below 100 kg. Consider adding more harvest.",
                    timestamp = "Yesterday",
                    type = "ALERT"
                )
            )
            UserRole.DELIVERY -> listOf(
                NotificationItem(
                    id = "d_notif_1",
                    title = "New Delivery Assigned! #1025",
                    message = "Pickup from Chikhli Farm to Near Bus Stand, Chikhli. Customer: Rahul Joshi.",
                    timestamp = "5 mins ago",
                    type = "DELIVERY"
                ),
                NotificationItem(
                    id = "d_notif_2",
                    title = "Delivery OTP Reminder",
                    message = "Remember to verify buyer's 4-digit OTP before handing over produce.",
                    timestamp = "Today, 10:00 AM",
                    type = "ALERT"
                ),
                NotificationItem(
                    id = "d_notif_3",
                    title = "Service Route: Khamgaon & Chikhli",
                    message = "Roads clear on Chikhli-Khamgaon bypass highway.",
                    timestamp = "Today, 08:00 AM",
                    type = "INFO"
                )
            )
            UserRole.ADMIN -> listOf(
                NotificationItem(
                    id = "a_notif_1",
                    title = "Buldhana APMC Mandi Rates Synced",
                    message = "Agmarknet rates updated for all 7 Buldhana mandis at 10:30 AM.",
                    timestamp = "15 mins ago",
                    type = "SYSTEM"
                ),
                NotificationItem(
                    id = "a_notif_2",
                    title = "New Seller Onboarded: Mehkar Farmers Group",
                    message = "KYC documents automatically verified under Buldhana District portal.",
                    timestamp = "1 hour ago",
                    type = "USER"
                ),
                NotificationItem(
                    id = "a_notif_3",
                    title = "Delivery Network Status: 100% Operational",
                    message = "12 active orders in transit across Khamgaon, Chikhli, Malkapur, Shegaon.",
                    timestamp = "2 hours ago",
                    type = "DELIVERY"
                )
            )
        }
        _notifications.value = notifs
    }

    fun updateUser(updated: UserProfile) {
        // Enforce Buldhana District consistency
        _currentUser.value = updated.copy(district = "Buldhana", state = "Maharashtra")
        sessionManager?.saveSession(_currentUser.value)
    }

    /**
     * Backend Market Rate Service:
     * Strictly filters for Maharashtra State and Buldhana District markets only.
     * Rejects any external, out-of-district market data.
     */
    fun getBuldhanaMarketRateForProduct(productName: String, category: ProductCategory): MandiRate? {
        val q = productName.lowercase()
        return _mandiRates.value.firstOrNull { rate ->
            rate.district.equals("Buldhana", ignoreCase = true) &&
            (rate.commodity.lowercase().contains(q) ||
             rate.commodityMr.lowercase().contains(q) ||
             rate.commodityHi.lowercase().contains(q) ||
             rate.category == category)
        }
    }

    private fun loadBuldhanaMarketData() {
        // Authentic Buldhana District Market Mandi Rates
        val rawBuldhanaRates = listOf(
            MandiRate(
                id = "bld_mr_1",
                commodity = "Soybean (Yellow)",
                commodityHi = "सोयाबीन (पीला)",
                commodityMr = "सोयाबीन (पिवळा)",
                category = ProductCategory.OILSEEDS,
                mandiName = "Khamgaon",
                district = "Buldhana",
                state = "Maharashtra",
                modalPrice = 4800.0,
                minPrice = 4550.0,
                maxPrice = 4920.0,
                unit = "Quintal",
                changePercentage = 2.4,
                lastUpdated = "Today, 10:30 AM",
                isAvailable = true
            ),
            MandiRate(
                id = "bld_mr_2",
                commodity = "Tomato",
                commodityHi = "टमाटर",
                commodityMr = "टोमॅटो",
                category = ProductCategory.VEGETABLES,
                mandiName = "Buldhana",
                district = "Buldhana",
                state = "Maharashtra",
                modalPrice = 25.0,
                minPrice = 20.0,
                maxPrice = 30.0,
                unit = "kg",
                changePercentage = -1.2,
                lastUpdated = "Today, 10:30 AM",
                isAvailable = true
            ),
            MandiRate(
                id = "bld_mr_3",
                commodity = "Cotton (Medium Staple)",
                commodityHi = "कपास (रुई)",
                commodityMr = "कापूस (मध्यम धागा)",
                category = ProductCategory.COTTON,
                mandiName = "Malkapur",
                district = "Buldhana",
                state = "Maharashtra",
                modalPrice = 7200.0,
                minPrice = 6900.0,
                maxPrice = 7450.0,
                unit = "Quintal",
                changePercentage = 1.8,
                lastUpdated = "Today, 09:45 AM",
                isAvailable = true
            ),
            MandiRate(
                id = "bld_mr_4",
                commodity = "Tur / Pigeon Pea",
                commodityHi = "तूर / अरहर दाल",
                commodityMr = "तूर (गावरान)",
                category = ProductCategory.PULSES,
                mandiName = "Deulgaon Raja",
                district = "Buldhana",
                state = "Maharashtra",
                modalPrice = 9600.0,
                minPrice = 9100.0,
                maxPrice = 9950.0,
                unit = "Quintal",
                changePercentage = 3.1,
                lastUpdated = "Today, 10:15 AM",
                isAvailable = true
            ),
            MandiRate(
                id = "bld_mr_5",
                commodity = "Wheat (Lokwan)",
                commodityHi = "गेहूं (लोकवान)",
                commodityMr = "गहू (लोकवन)",
                category = ProductCategory.GRAINS,
                mandiName = "Mehkar",
                district = "Buldhana",
                state = "Maharashtra",
                modalPrice = 2650.0,
                minPrice = 2400.0,
                maxPrice = 2800.0,
                unit = "Quintal",
                changePercentage = 0.5,
                lastUpdated = "Today, 09:30 AM",
                isAvailable = true
            ),
            MandiRate(
                id = "bld_mr_6",
                commodity = "Green Chillies",
                commodityHi = "हरी मिर्च",
                commodityMr = "हिरवी तिखट मिरची",
                category = ProductCategory.VEGETABLES,
                mandiName = "Chikhli",
                district = "Buldhana",
                state = "Maharashtra",
                modalPrice = 38.0,
                minPrice = 32.0,
                maxPrice = 45.0,
                unit = "kg",
                changePercentage = 4.2,
                lastUpdated = "Today, 08:30 AM",
                isAvailable = true
            ),
            MandiRate(
                id = "bld_mr_7",
                commodity = "Sweet Oranges (Mosambi)",
                commodityHi = "मौसंबी",
                commodityMr = "गोड मोसंबी",
                category = ProductCategory.FRUITS,
                mandiName = "Shegaon",
                district = "Buldhana",
                state = "Maharashtra",
                modalPrice = 42.0,
                minPrice = 35.0,
                maxPrice = 50.0,
                unit = "kg",
                changePercentage = 1.0,
                lastUpdated = "Today, 09:00 AM",
                isAvailable = true
            )
        )

        // Validate that data belongs strictly to Buldhana District, Maharashtra
        _mandiRates.value = rawBuldhanaRates.filter {
            it.district.equals("Buldhana", ignoreCase = true) &&
            it.state.equals("Maharashtra", ignoreCase = true)
        }

        // Local Buldhana Products
        val buldhanaProducts = listOf(
            Product(
                id = "p_bld_1",
                name = "Fresh Red Tomatoes",
                nameHi = "ताजा लाल टमाटर",
                nameMr = "ताजे लाल टोमॅटो",
                category = ProductCategory.VEGETABLES,
                price = 22.0,
                unit = "kg",
                stock = 300.0,
                sellerName = "Suresh Gaikwad",
                sellerPhone = "+91 98221 11223",
                village = "Chikhli Rural",
                district = "Buldhana",
                isOrganic = false,
                rating = 4.8,
                reviewCount = 24,
                marketMandiRate = 25.0,
                description = "Naturally sun-ripened, farm-harvested tomatoes from Chikhli, Buldhana.",
                badge = "₹3 Lower than Mandi"
            ),
            Product(
                id = "p_bld_2",
                name = "Khamgaon Yellow Soybean",
                nameHi = "खामगांव पीला सोयाबीन",
                nameMr = "खामगाव पिवळा दर्जेदार सोयाबीन",
                category = ProductCategory.OILSEEDS,
                price = 47.0,
                unit = "kg",
                stock = 1500.0,
                sellerName = "Baliram Deshmukh",
                sellerPhone = "+91 94228 33445",
                village = "Khamgaon Rural",
                district = "Buldhana",
                isOrganic = true,
                rating = 4.9,
                reviewCount = 42,
                marketMandiRate = 48.0,
                description = "High oil content, clean sorted yellow soybean directly from Khamgaon farm.",
                badge = "Direct Farm Harvest"
            ),
            Product(
                id = "p_bld_3",
                name = "Malkapur Quality White Cotton",
                nameHi = "मलकापुर सफेद कपास",
                nameMr = "मलकापूर दर्जेदार पांढरा कापूस",
                category = ProductCategory.COTTON,
                price = 71.0,
                unit = "kg",
                stock = 2000.0,
                sellerName = "Malkapur Kisan Samiti",
                sellerPhone = "+91 98901 44556",
                village = "Malkapur",
                district = "Buldhana",
                isOrganic = false,
                rating = 4.7,
                reviewCount = 18,
                marketMandiRate = 72.0,
                description = "Clean medium staple raw cotton picked by local farmers in Malkapur.",
                badge = "Malkapur Cotton"
            ),
            Product(
                id = "p_bld_4",
                name = "Deulgaon Raja Desi Tur Dal",
                nameHi = "देऊळगांव राजा शुद्ध तूर दाल",
                nameMr = "देऊळगाव राजा गावरान तूर डाळ",
                category = ProductCategory.PULSES,
                price = 140.0,
                unit = "kg",
                stock = 450.0,
                sellerName = "Ananda Patil",
                sellerPhone = "+91 97654 88990",
                village = "Deulgaon Raja",
                district = "Buldhana",
                isOrganic = true,
                rating = 4.9,
                reviewCount = 35,
                marketMandiRate = 145.0,
                description = "Unpolished, naturally rich desi tur dal grown in black cotton soil of Deulgaon Raja.",
                badge = "Farm Direct"
            ),
            Product(
                id = "p_bld_5",
                name = "Mehkar Sharbati Wheat",
                nameHi = "मेहकर शरबती गेहूं",
                nameMr = "मेहकर शरबती सुवर्ण गहू",
                category = ProductCategory.GRAINS,
                price = 38.0,
                unit = "kg",
                stock = 800.0,
                sellerName = "Kisan Tukaram",
                sellerPhone = "+91 94033 66778",
                village = "Mehkar",
                district = "Buldhana",
                isOrganic = true,
                rating = 4.8,
                reviewCount = 29,
                marketMandiRate = 40.0,
                description = "Golden grain Sharbati wheat, stone ground ready, high protein.",
                badge = "Buldhana Wheat"
            ),
            Product(
                id = "p_bld_6",
                name = "Shegaon Fresh Sweet Oranges (Mosambi)",
                nameHi = "शेगाव ताजा मौसंबी",
                nameMr = "शेगाव ताजी गोड मोसंबी",
                category = ProductCategory.FRUITS,
                price = 40.0,
                unit = "kg",
                stock = 600.0,
                sellerName = "Gajanan Orchard Farm",
                sellerPhone = "+91 98223 99001",
                village = "Shegaon",
                district = "Buldhana",
                isOrganic = true,
                rating = 5.0,
                reviewCount = 47,
                marketMandiRate = 42.0,
                description = "Juicy fresh table Mosambi harvested directly from Shegaon orchards.",
                badge = "Fresh Pick"
            ),
            Product(
                id = "p_bld_7",
                name = "Chikhli Fresh Green Chillies",
                nameHi = "चिखली हरी मिर्च",
                nameMr = "चिखली ताजी हिरवी मिरची",
                category = ProductCategory.VEGETABLES,
                price = 35.0,
                unit = "kg",
                stock = 150.0,
                sellerName = "Rambhau Shinde",
                sellerPhone = "+91 97633 12345",
                village = "Chikhli",
                district = "Buldhana",
                isOrganic = false,
                rating = 4.6,
                reviewCount = 19,
                marketMandiRate = 38.0,
                description = "Pungent, fresh green chillies direct from Chikhli morning harvest.",
                badge = "Spicy Fresh"
            ),
            Product(
                id = "p_bld_8",
                name = "Handmade Buldhana Clay Water Pot (Matka)",
                nameHi = "हस्तनिर्मित मिट्टी का घड़ा",
                nameMr = "बुलढाणा अस्सल मातीचा माठ",
                category = ProductCategory.HANDICRAFTS,
                price = 180.0,
                unit = "piece",
                stock = 30.0,
                sellerName = "Vitthal Kumbhar (Artisan)",
                sellerPhone = "+91 98902 55667",
                village = "Sindkhed Raja",
                district = "Buldhana",
                isOrganic = true,
                rating = 4.9,
                reviewCount = 22,
                marketMandiRate = 220.0,
                description = "Naturally cooling river-silt clay pitcher hand-thrown by rural potter in Sindkhed Raja.",
                isArtisanCraft = true,
                badge = "Rural Artisan"
            )
        )
        _products.value = buldhanaProducts

        // Buldhana Artisans
        val buldhanaArtisans = listOf(
            Artisan(
                id = "art_bld_1",
                name = "Vitthal Kumbhar",
                craftType = "Terracotta Pottery & Water Pots",
                village = "Sindkhed Raja",
                district = "Buldhana",
                experienceYears = 26,
                story = "Traditional potter from Sindkhed Raja creating naturally cooling clay matkas and cooking pots.",
                contactPhone = "+91 98902 55667",
                specialties = listOf("Water Matkas", "Clay Tawas", "Handmade Diyas")
            ),
            Artisan(
                id = "art_bld_2",
                name = "Radhabai Jadhav",
                craftType = "Bamboo Baskets & Rural Storage",
                village = "Mehkar",
                district = "Buldhana",
                experienceYears = 20,
                story = "Rural artisan weaving sturdy natural bamboo harvest grain containers and market baskets.",
                contactPhone = "+91 94211 44332",
                specialties = listOf("Grain Baskets", "Fruit Trays", "Bamboo Winnowing Fans")
            )
        )
        _artisans.value = buldhanaArtisans

        // Simple Learn & Grow Training Modules
        val simpleTraining = listOf(
            TrainingModule(
                id = "trn_1",
                title = "Direct Farm Sales & Pricing",
                titleHi = "सीधा खेत से बिक्री और सही मूल्य",
                titleMr = "थेट शेतातून विक्री आणि योग्य बाजारभाव",
                category = "Direct Farm Sales",
                duration = "10 mins",
                description = "Learn how to price your Buldhana produce competitively against local mandi rates to maximize farm profit.",
                keyTakeaways = listOf(
                    "Check today's Buldhana mandi rate before listing",
                    "Keep prices 5-10% below retail to attract buyers fast",
                    "Offer direct farm pick-up discounts"
                )
            ),
            TrainingModule(
                id = "trn_2",
                title = "Packaging Vegetables to Prevent Damage",
                titleHi = "सब्जियों की सही सुरक्षित पैकेजिंग",
                titleMr = "भाजीपाला सुरक्षित पॅकिंगच्या सोप्या पद्धती",
                category = "Packaging",
                duration = "8 mins",
                description = "Simple, low-cost village packaging tips for tomatoes, chillies, and grains during local delivery.",
                keyTakeaways = listOf(
                    "Ventilated crates for tomatoes to prevent bruising",
                    "Moisture-proof bags for chillies and coriander",
                    "Sealed cloth sacks for soybean and pulses"
                )
            ),
            TrainingModule(
                id = "trn_3",
                title = "Taking Clean Smartphone Photos of Produce",
                titleHi = "मोबाइल से साफ फोटो कैसे लें",
                titleMr = "मोबाईलवर शेतमालाचे स्पष्ट फोटो कसे काढावे",
                category = "Branding",
                duration = "6 mins",
                description = "Use natural morning daylight to click clear pictures of your harvest without special cameras.",
                keyTakeaways = listOf(
                    "Click photos in daylight near farm shed",
                    "Keep background clean and uncluttered",
                    "Show actual harvested condition"
                )
            )
        )
        _trainingModules.value = simpleTraining

        // Simple Orders in Buldhana
        val sampleOrders = listOf(
            Order(
                id = "#1025",
                orderDate = "Today, 11:20 AM",
                buyerName = "Rahul Joshi",
                buyerPhone = "+91 98220 77889",
                items = listOf(
                    CartItem(buldhanaProducts[0], 5.0),
                    CartItem(buldhanaProducts[5], 2.0)
                ),
                subtotal = 190.0,
                deliveryFee = 0.0,
                discount = 0.0,
                totalAmount = 190.0,
                paymentMethod = "UPI",
                paymentStatus = "Paid",
                orderStatus = OrderStatus.CONFIRMED,
                deliveryAddress = "Near Bus Stand, Chikhli, Buldhana - 443201",
                deliveryOtp = "4826",
                assignedDeliveryPartner = "Santosh Wankhede"
            )
        )
        _orders.value = sampleOrders

        // Simple Notifications
        val sampleNotifications = listOf(
            NotificationItem(
                id = "notif_1",
                title = "Your order #1025 has been confirmed.",
                message = "The farmer in Chikhli is preparing your fresh harvest.",
                timestamp = "10 mins ago",
                type = "ORDER"
            ),
            NotificationItem(
                id = "notif_2",
                title = "Tomato market rate updated in Buldhana.",
                message = "Today's rate is ₹25/kg in Buldhana Mandi.",
                timestamp = "Today, 10:30 AM",
                type = "RATE"
            ),
            NotificationItem(
                id = "notif_3",
                title = "New soybean harvest available in Khamgaon.",
                message = "Fresh clean yellow soybean listed at ₹47/kg.",
                timestamp = "Today, 09:00 AM",
                type = "ALERT"
            )
        )
        _notifications.value = sampleNotifications
    }

    // Cart Operations
    fun addToCart(product: Product, quantity: Double = 1.0) {
        val current = _cart.value.toMutableList()
        val index = current.indexOfFirst { it.product.id == product.id }
        if (index >= 0) {
            val existing = current[index]
            current[index] = existing.copy(quantity = existing.quantity + quantity)
        } else {
            current.add(CartItem(product, quantity))
        }
        _cart.value = current
    }

    fun updateCartQuantity(productId: String, delta: Double) {
        val current = _cart.value.toMutableList()
        val index = current.indexOfFirst { it.product.id == productId }
        if (index >= 0) {
            val newQty = current[index].quantity + delta
            if (newQty <= 0) {
                current.removeAt(index)
            } else {
                current[index] = current[index].copy(quantity = newQty)
            }
            _cart.value = current
        }
    }

    fun removeFromCart(productId: String) {
        _cart.value = _cart.value.filter { it.product.id != productId }
    }

    fun clearCart() {
        _cart.value = emptyList()
    }

    fun placeOrder(paymentMethod: String, address: String): Order {
        val items = _cart.value.toList()
        val subtotal = items.sumOf { it.totalPrice }
        val newOrder = Order(
            id = "#" + (1000..9999).random(),
            orderDate = "Just Now",
            buyerName = _currentUser.value.name,
            buyerPhone = _currentUser.value.phone,
            items = items,
            subtotal = subtotal,
            deliveryFee = 0.0,
            discount = 0.0,
            totalAmount = subtotal,
            paymentMethod = paymentMethod,
            paymentStatus = if (paymentMethod.contains("Cash")) "Pending on Delivery" else "Paid",
            orderStatus = OrderStatus.PLACED,
            deliveryAddress = address,
            deliveryOtp = (1000..9999).random().toString()
        )
        _orders.value = listOf(newOrder) + _orders.value
        clearCart()
        return newOrder
    }

    fun addProduct(
        name: String,
        category: ProductCategory,
        price: Double,
        unit: String,
        stock: Double,
        description: String
    ) {
        val benchmarkRate = getBuldhanaMarketRateForProduct(name, category)?.modalPrice ?: (price * 1.05)
        val newProduct = Product(
            id = "p_bld_" + System.currentTimeMillis(),
            name = name,
            nameHi = name,
            nameMr = name,
            category = category,
            price = price,
            unit = unit,
            stock = stock,
            sellerName = _currentUser.value.name,
            sellerPhone = _currentUser.value.phone,
            village = _currentUser.value.village,
            district = "Buldhana",
            isOrganic = false,
            rating = 5.0,
            reviewCount = 1,
            marketMandiRate = benchmarkRate,
            description = description.ifEmpty { "Fresh local farm produce from Buldhana." }
        )
        _products.value = listOf(newProduct) + _products.value
    }

    fun updateOrderStatus(orderId: String, newStatus: OrderStatus) {
        val list = _orders.value.map {
            if (it.id == orderId) it.copy(orderStatus = newStatus) else it
        }
        _orders.value = list
    }

    fun markModuleComplete(id: String) {
        val list = _trainingModules.value.map {
            if (it.id == id) it.copy(isCompleted = true) else it
        }
        _trainingModules.value = list
    }
}
