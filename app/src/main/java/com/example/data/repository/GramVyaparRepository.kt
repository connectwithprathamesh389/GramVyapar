package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.CartItemEntity
import com.example.data.local.OrderEntity
import com.example.data.local.ProductEntity
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GramVyaparRepository(private val database: AppDatabase) {
    private val scope = CoroutineScope(Dispatchers.IO)

    // Current State
    private val _currentLanguage = MutableStateFlow(AppLanguage.ENGLISH)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    private val _currentUser = MutableStateFlow(
        UserProfile(
            id = "user_01",
            name = "Ramesh Tukaram Patil",
            phone = "+91 98220 54321",
            email = "ramesh.patil@gramvyapar.in",
            role = UserRole.BUYER,
            village = "Dindori",
            taluka = "Dindori",
            district = "Nashik",
            state = "Maharashtra",
            pincode = "422202",
            isKycVerified = true,
            walletBalance = 1450.0
        )
    )
    val currentUser: StateFlow<UserProfile> = _currentUser.asStateFlow()

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _cart = MutableStateFlow<List<CartItem>>(emptyList())
    val cart: StateFlow<List<CartItem>> = _cart.asStateFlow()

    private val _mandiRates = MutableStateFlow<List<MandiRate>>(emptyList())
    val mandiRates: StateFlow<List<MandiRate>> = _mandiRates.asStateFlow()

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    private val _artisans = MutableStateFlow<List<Artisan>>(emptyList())
    val artisans: StateFlow<List<Artisan>> = _artisans.asStateFlow()

    private val _trainingModules = MutableStateFlow<List<TrainingModule>>(emptyList())
    val trainingModules: StateFlow<List<TrainingModule>> = _trainingModules.asStateFlow()

    private val _notifications = MutableStateFlow<List<NotificationItem>>(emptyList())
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    init {
        initInitialData()
    }

    fun setLanguage(language: AppLanguage) {
        _currentLanguage.value = language
    }

    fun switchRole(role: UserRole) {
        _currentUser.value = _currentUser.value.copy(role = role)
    }

    fun updateUser(updated: UserProfile) {
        _currentUser.value = updated
    }

    private fun initInitialData() {
        val initialProducts = listOf(
            Product(
                id = "p1",
                name = "Nashik Fresh Red Onions (Direct Harvest)",
                nameHi = "नासिक ताजा लाल प्याज (सीधा खेत से)",
                nameMr = "नाशिकचे ताजे लाल कांदे (थेट शेतातून)",
                category = ProductCategory.VEGETABLES,
                price = 24.0,
                unit = "kg",
                stock = 500.0,
                sellerName = "Kisan Baburao Borse",
                sellerPhone = "+91 94231 87654",
                village = "Pimpalgaon Baswant",
                district = "Nashik",
                isOrganic = false,
                rating = 4.8,
                reviewCount = 89,
                marketMandiRate = 26.5,
                description = "Grade-A sun-cured Nashik red onions with long shelf-life. Harvested 2 days ago and directly sorted at the farm.",
                badge = "Best Deal • 10% Below Mandi"
            ),
            Product(
                id = "p2",
                name = "Organic Sharbati Wheat (M.P. Sehore)",
                nameHi = "जैविक शरबती गेहूं (सीहोर)",
                nameMr = "सेंद्रिय शरबती गहू (सीहोर)",
                category = ProductCategory.GRAINS,
                price = 48.0,
                unit = "kg",
                stock = 1200.0,
                sellerName = "Balram Farmer Producer Co.",
                sellerPhone = "+91 98260 11442",
                village = "Sehore Rural",
                district = "Sehore",
                isOrganic = true,
                rating = 4.9,
                reviewCount = 142,
                marketMandiRate = 52.0,
                description = "Golden heavy grains, naturally rain-fed and stone grounded aroma. 100% pesticide-free lab certified.",
                badge = "Organic Certified"
            ),
            Product(
                id = "p3",
                name = "Farm Fresh Polyhouse Vine Tomatoes",
                nameHi = "पॉलीहाउस ताजा बेल वाले टमाटर",
                nameMr = "पॉलीहाऊस ताजे वेल टोमॅटो",
                category = ProductCategory.VEGETABLES,
                price = 22.0,
                unit = "kg",
                stock = 350.0,
                sellerName = "Sunita Ganesh Gaikwad",
                sellerPhone = "+91 97654 32109",
                village = "Narayangaon",
                district = "Pune",
                isOrganic = true,
                rating = 4.7,
                reviewCount = 56,
                marketMandiRate = 28.0,
                description = "Firm, juicy, naturally ripened polyhouse tomatoes rich in lycopene. Delivered within 24 hours of harvest.",
                badge = "Direct Harvest"
            ),
            Product(
                id = "p4",
                name = "Traditional Kolhapuri Jaggery (Gul)",
                nameHi = "पारंपरिक कोल्हापुरी जैविक गुड़",
                nameMr = "पारंपरिक सेंद्रिय कोल्हापुरी गूळ",
                category = ProductCategory.SPICES,
                price = 65.0,
                unit = "kg",
                stock = 400.0,
                sellerName = "Shree Mahalaxmi Agro FPO",
                sellerPhone = "+91 98224 88776",
                village = "Karveer",
                district = "Kolhapur",
                isOrganic = true,
                rating = 4.9,
                reviewCount = 210,
                marketMandiRate = 70.0,
                description = "Unrefined chemical-free golden brown block jaggery made from fresh sugarcane juice in traditional iron pans.",
                badge = "GI Tagged"
            ),
            Product(
                id = "p5",
                name = "Desi Cow A2 Bilona Ghee (Grass-Fed)",
                nameHi = "देसी गाय का A2 बिलोना घी",
                nameMr = "देशी गाईचे A2 बिलोना तूप",
                category = ProductCategory.DAIRY,
                price = 1250.0,
                unit = "litre",
                stock = 45.0,
                sellerName = "Gokul Gaushala Trust",
                sellerPhone = "+91 94033 22110",
                village = "Sangamner",
                district = "Ahmednagar",
                isOrganic = true,
                rating = 5.0,
                reviewCount = 94,
                marketMandiRate = 1400.0,
                description = "Hand-churned curd bilona ghee prepared on firewood in earthen pots. Rich in aroma and Ayurvedic vitality.",
                badge = "100% Pure Bilona"
            ),
            Product(
                id = "p6",
                name = "Polished Farm Split Tur Dal (Pigeon Pea)",
                nameHi = "खेत की शुद्ध तूर दाल",
                nameMr = "घरगुती पॉलिश नसलेली तूर डाळ",
                category = ProductCategory.PULSES,
                price = 145.0,
                unit = "kg",
                stock = 600.0,
                sellerName = "Vidarbha Farmers Federation",
                sellerPhone = "+91 97633 44556",
                village = "Akola Rural",
                district = "Akola",
                isOrganic = true,
                rating = 4.8,
                reviewCount = 78,
                marketMandiRate = 158.0,
                description = "Unpolished protein-rich dal grown in the fertile black soils of Vidarbha. Easy to digest and cooks quickly.",
                badge = "High Protein"
            ),
            Product(
                id = "p7",
                name = "Handcrafted Terracotta Clay Water Pot (Matka)",
                nameHi = "हस्तनिर्मित मिट्टी का घड़ा (मटका)",
                nameMr = "हस्तकला मातीचे माठ (सुगंधी गार पाणी)",
                category = ProductCategory.HANDICRAFTS,
                price = 320.0,
                unit = "piece",
                stock = 25.0,
                sellerName = "Dattatray Kumbhar (Master Potter)",
                sellerPhone = "+91 98901 23456",
                village = "Kumbharwada, Bhigwan",
                district = "Pune",
                isOrganic = true,
                rating = 4.9,
                reviewCount = 48,
                marketMandiRate = 380.0,
                description = "Naturally cooling earthen pitcher fired in wood kilns. Adds natural alkaline minerals to drinking water.",
                isArtisanCraft = true,
                badge = "Support Artisan"
            ),
            Product(
                id = "p8",
                name = "Warli Tribal Canvas Painting Frame",
                nameHi = "वारली आदिवासी हस्तकला फ्रेम",
                nameMr = "वारली आदिवासी हस्तनिर्मित चित्रकला",
                category = ProductCategory.HANDICRAFTS,
                price = 850.0,
                unit = "piece",
                stock = 15.0,
                sellerName = "Janu Barku Vartha (Warli Artist)",
                sellerPhone = "+91 94211 99887",
                village = "Ganjad",
                district = "Palghar",
                isOrganic = true,
                rating = 5.0,
                reviewCount = 33,
                marketMandiRate = 1200.0,
                description = "Authentic rice paste painting on natural cow dung treated canvas depicting Tarpa celebration dance of harvest.",
                isArtisanCraft = true,
                badge = "Tribal Heritage"
            )
        )
        _products.value = initialProducts

        val initialRates = listOf(
            MandiRate(
                id = "mr1",
                commodity = "Wheat (Sharbati / Lokwan)",
                commodityHi = "गेहूं (शरबती / लोकवान)",
                commodityMr = "गहू (शरबती / लोकवन)",
                category = ProductCategory.GRAINS,
                mandiName = "Pune APMC (Gultekdi)",
                district = "Pune",
                state = "Maharashtra",
                modalPrice = 2750.0,
                minPrice = 2400.0,
                maxPrice = 3100.0,
                changePercentage = 3.2,
                trend = listOf(2600.0, 2620.0, 2650.0, 2690.0, 2710.0, 2730.0, 2750.0)
            ),
            MandiRate(
                id = "mr2",
                commodity = "Red Onion (Garva / Summer)",
                commodityHi = "लाल प्याज (ग्रीष्मकालीन)",
                commodityMr = "लाल कांदा (उन्हाळी गरवा)",
                category = ProductCategory.VEGETABLES,
                mandiName = "Lasalgaon Mandi (Asia's Largest)",
                district = "Nashik",
                state = "Maharashtra",
                modalPrice = 1850.0,
                minPrice = 1200.0,
                maxPrice = 2300.0,
                changePercentage = -1.8,
                trend = listOf(1980.0, 1950.0, 1920.0, 1900.0, 1880.0, 1860.0, 1850.0)
            ),
            MandiRate(
                id = "mr3",
                commodity = "Hybrid Red Tomato",
                commodityHi = "टमाटर (हाइब्रिड लाल)",
                commodityMr = "टोमॅटो (हायब्रिड लाल)",
                category = ProductCategory.VEGETABLES,
                mandiName = "Narayangaon Tomato Market",
                district = "Pune",
                state = "Maharashtra",
                modalPrice = 1600.0,
                minPrice = 1100.0,
                maxPrice = 2050.0,
                changePercentage = 6.4,
                trend = listOf(1350.0, 1400.0, 1450.0, 1500.0, 1540.0, 1580.0, 1600.0)
            ),
            MandiRate(
                id = "mr4",
                commodity = "Soybean (Yellow)",
                commodityHi = "सोयाबीन (पीला)",
                commodityMr = "सोयाबीन (पिवळा)",
                category = ProductCategory.PULSES,
                mandiName = "Indore Krishi Upaj Mandi",
                district = "Indore",
                state = "Madhya Pradesh",
                modalPrice = 4650.0,
                minPrice = 4300.0,
                maxPrice = 4820.0,
                changePercentage = 2.1,
                trend = listOf(4450.0, 4490.0, 4520.0, 4560.0, 4600.0, 4620.0, 4650.0)
            ),
            MandiRate(
                id = "mr5",
                commodity = "Tur / Arhar Dal (Raw Pod)",
                commodityHi = "अरहर / तूर दाल",
                commodityMr = "तूर / अरहर डाळ",
                category = ProductCategory.PULSES,
                mandiName = "Akola APMC",
                district = "Akola",
                state = "Maharashtra",
                modalPrice = 9850.0,
                minPrice = 8900.0,
                maxPrice = 10400.0,
                changePercentage = 4.8,
                trend = listOf(9200.0, 9350.0, 9480.0, 9600.0, 9720.0, 9800.0, 9850.0)
            ),
            MandiRate(
                id = "mr6",
                commodity = "Kolhapuri Organic Jaggery Block",
                commodityHi = "कोल्हापुरी गुड़",
                commodityMr = "कोल्हापुरी गूळ ढेप",
                category = ProductCategory.SPICES,
                mandiName = "Kolhapur Shahu Market Yard",
                district = "Kolhapur",
                state = "Maharashtra",
                modalPrice = 4200.0,
                minPrice = 3800.0,
                maxPrice = 4500.0,
                changePercentage = 1.2,
                trend = listOf(4100.0, 4120.0, 4140.0, 4160.0, 4180.0, 4190.0, 4200.0)
            )
        )
        _mandiRates.value = initialRates

        val initialArtisans = listOf(
            Artisan(
                id = "art1",
                name = "Dattatray Vitthal Kumbhar",
                craftType = "Terracotta & Black Clay Pottery",
                village = "Bhigwan",
                district = "Pune",
                state = "Maharashtra",
                experienceYears = 28,
                story = "4th generation master potter creating therapeutic natural clay vessels, tawas, and storage jars using local river silt.",
                contactPhone = "+91 98901 23456",
                specialties = listOf("Water Matkas", "Clay Biryani Pots", "Earthen Tawas", "Decorative Diyas")
            ),
            Artisan(
                id = "art2",
                name = "Janu Barku Vartha",
                craftType = "Authentic Warli Folk Art",
                village = "Ganjad",
                district = "Palghar",
                state = "Maharashtra",
                experienceYears = 22,
                story = "National award winner keeping the 10th-century Warli geometric tribal storytelling tradition alive through eco-friendly canvas works.",
                contactPhone = "+91 94211 99887",
                specialties = listOf("Harvest Dance Canvas", "Bamboo Scroll Painting", "Wall Murals", "Lampshades")
            ),
            Artisan(
                id = "art3",
                name = "Arundhati & Mohan Shinde",
                craftType = "Traditional Paithani Silk Weaving",
                village = "Yeola",
                district = "Nashik",
                state = "Maharashtra",
                experienceYears = 34,
                story = "Handloom weavers weaving pure zari peacock borders with natural dyes, empowering 18 rural women weavers in their cluster.",
                contactPhone = "+91 98229 33211",
                specialties = listOf("Pure Zari Sarees", "Silk Stoles", "Handloom Dupattas")
            )
        )
        _artisans.value = initialArtisans

        val initialModules = listOf(
            TrainingModule(
                id = "tm1",
                title = "Selling on Amazon KisanStore & Flipkart Krishi",
                titleHi = "अमेज़न किसान और फ्लिपकार्ट कृषि पर ऑनलाइन बिक्री",
                titleMr = "ॲमेझॉन किसान व फ्लिपकार्ट कृषीवर शेतमाल विक्री",
                platformTag = "Amazon Kisan / Flipkart",
                duration = "18 mins • 4 Lessons",
                description = "Learn step-by-step onboarding for Farmer Producer Organizations (FPOs) and individual farmers to reach 100M+ buyers across India.",
                keyTakeaways = listOf(
                    "FPO GST & PAN onboarding document checklist",
                    "Creating bulk listings with standardized weight units",
                    "Packaging perishable items to prevent in-transit spoilage",
                    "Receiving direct bank settlement within 48 hours"
                ),
                certificateTitle = "Certified Rural Digital Seller"
            ),
            TrainingModule(
                id = "tm2",
                title = "eNAM National Agriculture Market Onboarding",
                titleHi = "ई-नाम राष्ट्रीय कृषि बाजार में ऑनलाइन बोली",
                titleMr = "ई-नाम (eNAM) राष्ट्रीय बाजारात ऑनलाईन लिलाव",
                platformTag = "eNAM Govt Portal",
                duration = "22 mins • 5 Lessons",
                description = "Master digital e-bidding, quality testing reports, and interstate trade directly from your village APMC mandi yard.",
                keyTakeaways = listOf(
                    "Registering with farmer Aadhaar and land passbook",
                    "Requesting computerized lab assaying & grading",
                    "Participating in online e-auctions across 1,000+ mandis",
                    "Direct MSP protection and online payment receipts"
                ),
                certificateTitle = "eNAM Digital Trader Practitioner"
            ),
            TrainingModule(
                id = "tm3",
                title = "Smartphone Product Photography & Branding",
                titleHi = "मोबाइल से उत्पाद फोटोग्राफी और पैकेजिंग",
                titleMr = "मोबाईलने आकर्षक फोटो आणि ब्रँडिंग कसे करावे",
                platformTag = "Smart Packaging",
                duration = "14 mins • 3 Lessons",
                description = "Take studio-quality photos of your fresh farm harvest or pottery crafts using natural daylight and a budget smartphone.",
                keyTakeaways = listOf(
                    "Using morning sun angles for crisp appetizing colors",
                    "Clean background tricks using white craft sheets",
                    "Designing your own village brand label with QR code",
                    "Writing clear origin and freshness guarantees"
                ),
                certificateTitle = "Rural Brand Visualizer"
            )
        )
        _trainingModules.value = initialModules

        val initialOrders = listOf(
            Order(
                id = "GV-89241",
                orderDate = "21 Sep 2026, 04:15 PM",
                buyerName = "Sunil Deshmukh",
                buyerPhone = "+91 98221 00998",
                items = listOf(
                    CartItem(initialProducts[0], 10.0),
                    CartItem(initialProducts[1], 5.0)
                ),
                subtotal = 480.0,
                deliveryFee = 0.0,
                discount = 40.0,
                totalAmount = 440.0,
                paymentMethod = "UPI (Google Pay)",
                paymentStatus = "Paid Successfully",
                orderStatus = OrderStatus.CONFIRMED,
                deliveryAddress = "Flat 402, Shiv Shrushti Apts, College Road, Nashik - 422005",
                deliveryOtp = "5192"
            ),
            Order(
                id = "GV-89190",
                orderDate = "19 Sep 2026, 11:30 AM",
                buyerName = "Priya Sharma",
                buyerPhone = "+91 97664 12345",
                items = listOf(
                    CartItem(initialProducts[4], 1.0)
                ),
                subtotal = 1250.0,
                deliveryFee = 0.0,
                discount = 100.0,
                totalAmount = 1150.0,
                paymentMethod = "RuPay Debit Card",
                paymentStatus = "Paid Successfully",
                orderStatus = OrderStatus.DELIVERED,
                deliveryAddress = "Plot 18, Sahakar Nagar, Pune - 411009",
                deliveryOtp = "7731"
            )
        )
        _orders.value = initialOrders

        val initialNotifications = listOf(
            NotificationItem(
                id = "n1",
                title = "Mandi Price Alert: Tomatoes ▲ +6.4%",
                message = "Narayangaon APMC tomato modal rate surged to ₹1,600/quintal due to high interstate demand.",
                timestamp = "10 mins ago",
                type = "RATE"
            ),
            NotificationItem(
                id = "n2",
                title = "Weather Alert: Light Showers Forecast",
                message = "IMD Advisory: Scattered rains expected in Nashik & Pune districts over next 36 hours. Shield open drying grain beds.",
                timestamp = "2 hours ago",
                type = "WEATHER"
            ),
            NotificationItem(
                id = "n3",
                title = "PM-Kisan 19th Installment Credited",
                message = "Government DBT transfer of ₹2,000 processed to registered bank accounts. Check your passbook or PM-Kisan portal.",
                timestamp = "Yesterday",
                type = "SCHEME"
            )
        )
        _notifications.value = initialNotifications
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

    // Place Order
    fun placeOrder(
        paymentMethod: String,
        address: String,
        deliveryFee: Double = 0.0,
        discount: Double = 30.0
    ): Order {
        val items = _cart.value.toList()
        val subtotal = items.sumOf { it.totalPrice }
        val total = (subtotal + deliveryFee - discount).coerceAtLeast(0.0)
        val newOrder = Order(
            id = "GV-" + (10000..99999).random(),
            orderDate = "Just Now",
            buyerName = _currentUser.value.name,
            buyerPhone = _currentUser.value.phone,
            items = items,
            subtotal = subtotal,
            deliveryFee = deliveryFee,
            discount = discount,
            totalAmount = total,
            paymentMethod = paymentMethod,
            paymentStatus = if (paymentMethod.contains("COD")) "Pending on Delivery" else "Paid Successfully",
            orderStatus = OrderStatus.PLACED,
            deliveryAddress = address,
            deliveryOtp = (1000..9999).random().toString()
        )
        _orders.value = listOf(newOrder) + _orders.value
        clearCart()
        return newOrder
    }

    // Seller add product
    fun addProduct(
        name: String,
        category: ProductCategory,
        price: Double,
        unit: String,
        stock: Double,
        description: String,
        isOrganic: Boolean
    ) {
        val benchmark = _mandiRates.value.firstOrNull { it.category == category }?.modalPrice?.div(100.0) ?: (price * 1.05)
        val newProduct = Product(
            id = "p_" + System.currentTimeMillis(),
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
            district = _currentUser.value.district,
            isOrganic = isOrganic,
            rating = 5.0,
            reviewCount = 1,
            marketMandiRate = benchmark,
            description = description,
            badge = if (price < benchmark) "Best Deal" else null
        )
        _products.value = listOf(newProduct) + _products.value
    }

    // Complete Training Module
    fun markModuleComplete(moduleId: String) {
        val list = _trainingModules.value.map {
            if (it.id == moduleId) it.copy(isCompleted = true) else it
        }
        _trainingModules.value = list
    }

    // Delivery Status update
    fun updateOrderStatus(orderId: String, newStatus: OrderStatus) {
        val list = _orders.value.map {
            if (it.id == orderId) it.copy(orderStatus = newStatus) else it
        }
        _orders.value = list
    }
}
