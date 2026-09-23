package com.example.data.model

enum class AppLanguage(val code: String, val nativeName: String, val englishName: String) {
    MARATHI("mr", "मराठी", "Marathi"),
    HINDI("hi", "हिंदी", "Hindi"),
    ENGLISH("en", "English", "English")
}

enum class UserRole(val displayName: String) {
    BUYER("Buyer / खरेदीदार"),
    SELLER("Farmer & Seller / शेतकरी"),
    DELIVERY("Delivery Partner / डिलिव्हरी"),
    ADMIN("Admin / व्यवस्थापक")
}

data class UserProfile(
    val id: String = "user_bld_01",
    val name: String = "Gajanan Patil",
    val phone: String = "+91 98229 45678",
    val email: String = "gajanan.patil@gramvyapar.in",
    val role: UserRole = UserRole.BUYER,
    val village: String = "Chikhli",
    val taluka: String = "Chikhli",
    val district: String = "Buldhana",
    val state: String = "Maharashtra",
    val pincode: String = "443201",
    val serviceArea: String = "Chikhli",
    val isActive: Boolean = true,
    val isKycVerified: Boolean = true,
    val aadhaarMasked: String = "XXXX-XXXX-7821",
    val panNumber: String = "ABCDP1234F",
    val walletBalance: Double = 1450.0
)

enum class ProductCategory(val titleEn: String, val titleHi: String, val titleMr: String, val iconEmoji: String) {
    ALL("All", "सभी", "सर्व", "🌾"),
    VEGETABLES("Vegetables", "सब्जियां", "भाजीपाला", "🥦"),
    FRUITS("Fruits", "फल", "फळे", "🍎"),
    GRAINS("Grains", "अनाज", "धान्य", "🌾"),
    PULSES("Pulses", "दालें", "कडधान्ये", "🫘"),
    OILSEEDS("Oilseeds", "तिलहन", "गळीत धान्य (सोयाबीन)", "🌻"),
    COTTON("Cotton", "कपास", "कापूस (रुई)", "☁️"),
    OTHER_AGRI("Other Agriculture", "अन्य कृषि", "इतर शेतीमाल / मसाले", "🌶️"),
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
    val district: String = "Buldhana",
    val isOrganic: Boolean = false,
    val rating: Double = 4.8,
    val reviewCount: Int = 28,
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
    val mandiName: String, // e.g. "Khamgaon", "Buldhana", "Malkapur", "Shegaon", "Chikhli", "Mehkar", "Deulgaon Raja"
    val district: String = "Buldhana",
    val state: String = "Maharashtra",
    val modalPrice: Double, // in ₹/quintal or ₹/kg
    val minPrice: Double,
    val maxPrice: Double,
    val unit: String = "Quintal",
    val changePercentage: Double = 0.0,
    val lastUpdated: String = "Today, 10:30 AM",
    val isAvailable: Boolean = true,
    val isCached: Boolean = false
)

enum class OrderStatus(val titleEn: String, val titleMr: String) {
    PLACED("Order Placed", "ऑर्डर दिली"),
    CONFIRMED("Confirmed", "स्वीकारली"),
    PREPARING("Preparing", "तयार करत आहे"),
    OUT_FOR_DELIVERY("Out for Delivery", "वितरणासाठी निघाली"),
    DELIVERED("Delivered", "वितरित झाली"),
    CANCELLED("Cancelled", "रद्द केली")
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
    val assignedDeliveryPartner: String = "Santosh Wankhede (Buldhana Express)",
    val assignedDeliveryBoyId: String? = null,
    val isOtpVerified: Boolean = false
) {
    val isCod: Boolean get() = paymentMethod.contains("COD", ignoreCase = true) || paymentMethod.contains("CASH", ignoreCase = true)
    val deliveryBoyName: String? get() = if (assignedDeliveryPartner.isNotBlank()) assignedDeliveryPartner else null
}

data class Artisan(
    val id: String,
    val name: String,
    val craftType: String,
    val village: String,
    val district: String = "Buldhana",
    val state: String = "Maharashtra",
    val experienceYears: Int,
    val story: String,
    val contactPhone: String,
    val specialties: List<String>,
    val rating: Double = 4.9
)

data class TrainingModule(
    val id: String,
    val title: String,
    val titleHi: String,
    val titleMr: String,
    val category: String, // Branding, Packaging, Selling Online, Digital Marketing, Direct Farm Sales
    val duration: String,
    val description: String,
    val keyTakeaways: List<String>,
    var isCompleted: Boolean = false
)

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: String,
    val type: String = "ALERT", // "ORDER", "RATE", "ALERT"
    val isRead: Boolean = false
)
