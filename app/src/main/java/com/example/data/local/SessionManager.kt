package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.AppLanguage
import com.example.data.model.UserProfile
import com.example.data.model.UserRole

/**
 * Persistent Session Manager:
 * Stores authenticated user session, role, language, and service area.
 * Ensures user stays logged in across app restarts, language updates, and navigation.
 */
class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "gramvyapar_session_prefs"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_PHONE = "user_phone"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_ROLE = "user_role"
        private const val KEY_VILLAGE = "village"
        private const val KEY_TALUKA = "taluka"
        private const val KEY_DISTRICT = "district"
        private const val KEY_STATE = "state"
        private const val KEY_PINCODE = "pincode"
        private const val KEY_SERVICE_AREA = "service_area"
        private const val KEY_IS_ACTIVE = "is_active"
        private const val KEY_LANGUAGE = "selected_language"
        private const val KEY_IS_FIRST_LAUNCH = "is_first_launch"
        private const val KEY_PERM_PREFIX = "perm_"
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun saveSession(user: com.example.data.model.UserProfile) {
        prefs.edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, true)
            putString(KEY_USER_ID, user.id)
            putString(KEY_USER_NAME, user.name)
            putString(KEY_USER_PHONE, user.phone)
            putString(KEY_USER_EMAIL, user.email)
            putString(KEY_USER_ROLE, user.role.name)
            putString(KEY_VILLAGE, user.village)
            putString(KEY_TALUKA, user.taluka)
            putString(KEY_DISTRICT, "Buldhana")
            putString(KEY_STATE, "Maharashtra")
            putString(KEY_PINCODE, user.pincode)
            putString(KEY_SERVICE_AREA, user.village)
            putBoolean(KEY_IS_ACTIVE, user.isActive)

            // Save individual permission flags
            putBoolean(KEY_PERM_PREFIX + "profile", user.permissions.profile)
            putBoolean(KEY_PERM_PREFIX + "notifications", user.permissions.notifications)
            putBoolean(KEY_PERM_PREFIX + "marketRates", user.permissions.marketRates)
            putBoolean(KEY_PERM_PREFIX + "shopping", user.permissions.shopping)
            putBoolean(KEY_PERM_PREFIX + "cart", user.permissions.cart)
            putBoolean(KEY_PERM_PREFIX + "checkout", user.permissions.checkout)
            putBoolean(KEY_PERM_PREFIX + "orders", user.permissions.orders)
            putBoolean(KEY_PERM_PREFIX + "payments", user.permissions.payments)
            putBoolean(KEY_PERM_PREFIX + "sellerDashboard", user.permissions.sellerDashboard)
            putBoolean(KEY_PERM_PREFIX + "manageProducts", user.permissions.manageProducts)
            putBoolean(KEY_PERM_PREFIX + "addProduct", user.permissions.addProduct)
            putBoolean(KEY_PERM_PREFIX + "sellerOrders", user.permissions.sellerOrders)
            putBoolean(KEY_PERM_PREFIX + "deliveryManagement", user.permissions.deliveryManagement)
            putBoolean(KEY_PERM_PREFIX + "deliveryOtp", user.permissions.deliveryOtp)
            putBoolean(KEY_PERM_PREFIX + "adminGovernance", user.permissions.adminGovernance)
            apply()
        }
    }

    fun getSavedUser(): UserProfile {
        val roleStr = prefs.getString(KEY_USER_ROLE, UserRole.BUYER.name) ?: UserRole.BUYER.name
        val role = try {
            UserRole.valueOf(roleStr)
        } catch (e: Exception) {
            UserRole.BUYER
        }

        val defaultPerms = com.example.data.model.UserPermissions.defaultForRole(role)
        val permissions = com.example.data.model.UserPermissions(
            profile = prefs.getBoolean(KEY_PERM_PREFIX + "profile", defaultPerms.profile),
            notifications = prefs.getBoolean(KEY_PERM_PREFIX + "notifications", defaultPerms.notifications),
            marketRates = prefs.getBoolean(KEY_PERM_PREFIX + "marketRates", defaultPerms.marketRates),
            shopping = prefs.getBoolean(KEY_PERM_PREFIX + "shopping", defaultPerms.shopping),
            cart = prefs.getBoolean(KEY_PERM_PREFIX + "cart", defaultPerms.cart),
            checkout = prefs.getBoolean(KEY_PERM_PREFIX + "checkout", defaultPerms.checkout),
            orders = prefs.getBoolean(KEY_PERM_PREFIX + "orders", defaultPerms.orders),
            payments = prefs.getBoolean(KEY_PERM_PREFIX + "payments", defaultPerms.payments),
            sellerDashboard = prefs.getBoolean(KEY_PERM_PREFIX + "sellerDashboard", defaultPerms.sellerDashboard),
            manageProducts = prefs.getBoolean(KEY_PERM_PREFIX + "manageProducts", defaultPerms.manageProducts),
            addProduct = prefs.getBoolean(KEY_PERM_PREFIX + "addProduct", defaultPerms.addProduct),
            sellerOrders = prefs.getBoolean(KEY_PERM_PREFIX + "sellerOrders", defaultPerms.sellerOrders),
            deliveryManagement = prefs.getBoolean(KEY_PERM_PREFIX + "deliveryManagement", defaultPerms.deliveryManagement),
            deliveryOtp = prefs.getBoolean(KEY_PERM_PREFIX + "deliveryOtp", defaultPerms.deliveryOtp),
            adminGovernance = prefs.getBoolean(KEY_PERM_PREFIX + "adminGovernance", defaultPerms.adminGovernance)
        )

        return UserProfile(
            id = prefs.getString(KEY_USER_ID, "user_bld_01") ?: "user_bld_01",
            name = prefs.getString(KEY_USER_NAME, "Gajanan Patil") ?: "Gajanan Patil",
            phone = prefs.getString(KEY_USER_PHONE, "+91 98229 45678") ?: "+91 98229 45678",
            email = prefs.getString(KEY_USER_EMAIL, "gajanan.patil@gramvyapar.in") ?: "gajanan.patil@gramvyapar.in",
            role = role,
            village = prefs.getString(KEY_VILLAGE, "Chikhli") ?: "Chikhli",
            taluka = prefs.getString(KEY_TALUKA, "Chikhli") ?: "Chikhli",
            district = "Buldhana",
            state = "Maharashtra",
            pincode = prefs.getString(KEY_PINCODE, "443201") ?: "443201",
            isActive = prefs.getBoolean(KEY_IS_ACTIVE, true),
            isKycVerified = true,
            permissions = permissions
        )
    }

    fun saveLanguage(language: AppLanguage) {
        prefs.edit().putString(KEY_LANGUAGE, language.code).apply()
    }

    fun getSavedLanguage(): AppLanguage {
        val code = prefs.getString(KEY_LANGUAGE, "mr") ?: "mr"
        return when (code) {
            "hi" -> AppLanguage.HINDI
            "en" -> AppLanguage.ENGLISH
            else -> AppLanguage.MARATHI
        }
    }

    fun isFirstLaunch(): Boolean {
        val first = prefs.getBoolean(KEY_IS_FIRST_LAUNCH, true)
        if (first) {
            prefs.edit().putBoolean(KEY_IS_FIRST_LAUNCH, false).apply()
        }
        return first
    }

    fun clearSession() {
        // Keep saved language and non-sensitive configurations
        val savedLang = getSavedLanguage()
        prefs.edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, false)
            remove(KEY_USER_ID)
            remove(KEY_USER_NAME)
            remove(KEY_USER_PHONE)
            remove(KEY_USER_EMAIL)
            remove(KEY_USER_ROLE)
            putString(KEY_LANGUAGE, savedLang.code)
            apply()
        }
    }
}
