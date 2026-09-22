package com.example.data.model

enum class AppLanguage(val code: String, val nativeName: String, val englishName: String) {
    ENGLISH("en", "English", "English"),
    HINDI("hi", "हिंदी", "Hindi"),
    MARATHI("mr", "मराठी", "Marathi")
}

enum class UserRole(val displayName: String) {
    BUYER("Buyer / खरेदीदार"),
    SELLER("Farmer & Seller / शेतकरी"),
    DELIVERY("Delivery Partner / वितरण"),
    ADMIN("Admin / प्रशासक")
}

data class UserProfile(
    val id: String = "user_1",
    val name: String = "Ramesh Patil",
    val phone: String = "+91 98223 45678",
    val email: String = "ramesh.patil@gramvyapar.in",
    val role: UserRole = UserRole.BUYER,
    val village: String = "Dindori",
    val taluka: String = "Dindori",
    val district: String = "Nashik",
    val state: String = "Maharashtra",
    val pincode: String = "422202",
    val isKycVerified: Boolean = true,
    val aadhaarMasked: String = "XXXX-XXXX-4829",
    val panNumber: String = "ABCDE1234F",
    val walletBalance: Double = 1250.0
)

enum class ProductCategory(val titleEn: String, val titleHi: String, val titleMr: String, val iconEmoji: String) {
    ALL("All", "सभी", "सर्व", "🌾"),
    VEGETABLES("Vegetables", "सब्जियां", "भाजीपाला", "🥦"),
    FRUITS("Fruits", "फल", "फळे", "🍎"),
    GRAINS("Grains", "अनाज", "धान्य", "🌾"),
    PULSES("Pulses", "दालें", "कडधान्ये", "🫘"),
    SPICES("Spices", "मसाले", "मसाले", "🌶️"),
    DAIRY("Dairy", "डेयरी", "दुग्ध उत्पादने", "🥛"),
    HANDICRAFTS("Handicrafts", "हस्तशिल्प", "हस्तकला", "🏺")
}

data class Product(
    val id: String,
    val name: String,
    val nameHi: String,
    val nameMr: String,
    val category: ProductCategory,
    val price: Double,
    val unit: String, // "kg", "quintal", "piece", "bundle"
    val stock: Double,
    val sellerName: String,
    val sellerPhone: String,
    val village: String,
    val district: String,
    val isOrganic: Boolean = true,
    val rating: Double = 4.8,
    val reviewCount: Int = 34,
    val marketMandiRate: Double, // wholesale mandi rate comparison
    val description: String,
    val isArtisanCraft: Boolean = false,
    val badge: String? = null
)

data class CartItem(
    val product: Product,
    var quantity: Double
) {
    val totalPrice: Double get() = product.price * quantity
}

data class MandiRate(
    val id: String,
    val commodity: String,
    val commodityHi: String,
    val commodityMr: String,
    val category: ProductCategory,
    val mandiName: String,
    val district: String,
    val state: String,
    val modalPrice: Double, // in ₹/quintal or ₹/kg
    val minPrice: Double,
    val maxPrice: Double,
    val unit: String = "Quintal",
    val changePercentage: Double, // e.g. +3.5 or -2.1
    val trend: List<Double>, // 7-day trend values
    val lastUpdated: String = "Today, 08:30 AM"
)

enum class OrderStatus(val title: String) {
    PLACED("Order Placed / ऑर्डर दिली"),
    CONFIRMED("Confirmed by Farmer / शेतकऱ्याने पुष्टी केली"),
    PICKED_UP("Picked Up / संकलित केले"),
    OUT_FOR_DELIVERY("Out for Delivery / वितरणासाठी बाहेर"),
    DELIVERED("Delivered / वितरित झाले"),
    CANCELLED("Cancelled / रद्द केले")
}

data class Order(
    val id: String,
    val orderDate: String,
    val buyerName: String,
    val buyerPhone: String,
    val items: List<CartItem>,
    val subtotal: Double,
    val deliveryFee: Double,
    val discount: Double,
    val totalAmount: Double,
    val paymentMethod: String,
    val paymentStatus: String,
    val orderStatus: OrderStatus,
    val deliveryAddress: String,
    val deliveryOtp: String = "4826",
    val assignedDeliveryPartner: String = "Suresh Shinde (+91 94220 11223)"
)

data class Artisan(
    val id: String,
    val name: String,
    val craftType: String,
    val village: String,
    val district: String,
    val state: String,
    val experienceYears: Int,
    val story: String,
    val contactPhone: String,
    val specialties: List<String>,
    val rating: Double = 4.9,
    val supportBadge: String = "Vocal for Local • Certified Artisan"
)

data class TrainingModule(
    val id: String,
    val title: String,
    val titleHi: String,
    val titleMr: String,
    val platformTag: String,
    val duration: String,
    val description: String,
    val keyTakeaways: List<String>,
    var isCompleted: Boolean = false,
    val certificateTitle: String
)

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: String,
    val type: String, // "RATE", "ORDER", "WEATHER", "SCHEME"
    val isRead: Boolean = false
)
