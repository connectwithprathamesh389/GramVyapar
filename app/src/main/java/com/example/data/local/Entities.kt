package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val name: String,
    val nameHi: String,
    val nameMr: String,
    val category: String,
    val price: Double,
    val unit: String,
    val stock: Double,
    val sellerName: String,
    val sellerPhone: String,
    val village: String,
    val district: String,
    val isOrganic: Boolean,
    val rating: Double,
    val reviewCount: Int,
    val marketMandiRate: Double,
    val description: String,
    val isArtisanCraft: Boolean,
    val badge: String?
)

@Entity(tableName = "mandi_rates")
data class MandiRateEntity(
    @PrimaryKey val id: String,
    val commodity: String,
    val commodityHi: String,
    val commodityMr: String,
    val category: String,
    val mandiName: String,
    val district: String,
    val state: String,
    val modalPrice: Double,
    val minPrice: Double,
    val maxPrice: Double,
    val unit: String,
    val changePercentage: Double,
    val trendCsv: String,
    val lastUpdated: String
)

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey val productId: String,
    val quantity: Double
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val id: String,
    val orderDate: String,
    val buyerName: String,
    val buyerPhone: String,
    val totalAmount: Double,
    val paymentMethod: String,
    val paymentStatus: String,
    val orderStatus: String,
    val deliveryAddress: String,
    val deliveryOtp: String
)

@Entity(tableName = "artisans")
data class ArtisanEntity(
    @PrimaryKey val id: String,
    val name: String,
    val craftType: String,
    val village: String,
    val district: String,
    val state: String,
    val experienceYears: Int,
    val story: String,
    val contactPhone: String,
    val specialtiesCsv: String,
    val rating: Double
)
