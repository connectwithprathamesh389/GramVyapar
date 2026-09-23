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
        private const val KEY_LANGUAGE = "selected_language"
        private const val KEY_IS_FIRST_LAUNCH = "is_first_launch"
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun saveSession(user: UserProfile) {
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
            isKycVerified = true
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
