package com.example.ui.i18n

import com.example.data.model.AppLanguage

object AppStrings {
    fun get(key: String, lang: AppLanguage): String {
        val entry = strings[key] ?: return key
        return when (lang) {
            AppLanguage.ENGLISH -> entry.en
            AppLanguage.HINDI -> entry.hi
            AppLanguage.MARATHI -> entry.mr
        }
    }

    private data class Trans(val en: String, val hi: String, val mr: String)

    private val strings = mapOf(
        // App Identity
        "app_name" to Trans("GramVyapar", "ग्रामव्यापार", "ग्रामव्यापार"),
        "app_tagline" to Trans("Rural E-Commerce & Entrepreneurship", "ग्रामीण ई-कॉमर्स और उद्यमिता", "ग्रामीण ई-कॉमर्स आणि उद्योजकता"),

        // Navigation Tabs - Buyer
        "nav_home" to Trans("Home", "होम", "मुख्यपृष्ठ"),
        "nav_cart" to Trans("Cart", "कार्ट", "खरेदी सूची"),
        "nav_notifications" to Trans("Alerts", "सूचनाएं", "सूचना"),
        "nav_orders" to Trans("Orders", "ऑर्डर", "ऑर्डर"),
        "nav_profile" to Trans("Profile", "प्रोफाइल", "प्रोफाइल"),

        // Navigation Tabs - Seller
        "nav_seller_dashboard" to Trans("Dashboard", "डैशबोर्ड", "डॅशबोर्ड"),
        "nav_add_product" to Trans("Add Product", "उत्पाद जोड़ें", "उत्पादन जोडा"),
        "nav_mandi_rates" to Trans("Live Rates", "मंडी भाव", "थेट बाजारभाव"),

        // Navigation Tabs - Delivery
        "nav_deliveries" to Trans("Deliveries", "डिलीवरी", "वितरण"),
        "nav_status" to Trans("Status", "स्थिति", "स्थिती"),

        // Navigation Tabs - Admin
        "nav_users" to Trans("Users", "उपयोगकर्ता", "वापरकर्ते"),
        "nav_reports" to Trans("Reports", "रिपोर्ट्स", "अहवाल"),

        // Splash & Language
        "choose_language" to Trans("Select Your Language", "अपनी भाषा चुनें", "आपली भाषा निवडा"),
        "language_subtitle" to Trans("You can change this anytime from Profile", "आप इसे प्रोफाइल से कभी भी बदल सकते हैं", "हे आपण प्रोफाईल मधून कधीही बदलू शकता"),
        "continue_btn" to Trans("Continue", "आगे बढ़ें", "पुढे जा"),

        // Auth
        "login_title" to Trans("Welcome to GramVyapar", "ग्रामव्यापार में स्वागत है", "ग्रामव्यापार मध्ये आपले स्वागत आहे"),
        "login_subtitle" to Trans("Connecting rural farmers, artisans & buyers directly", "किसानों, कारीगरों और खरीदारों को सीधा जोड़ना", "शेतकरी, कारागीर आणि ग्राहकांना थेट जोडणारे"),
        "email_or_phone" to Trans("Mobile Number or Email", "मोबाइल नंबर या ईमेल", "मोबाईल नंबर किंवा ईमेल"),
        "password" to Trans("Password", "पासवर्ड", "पासवर्ड"),
        "remember_me" to Trans("Remember Me", "मुझे याद रखें", "माझी आठवण ठेवा"),
        "forgot_password" to Trans("Forgot Password?", "पासवर्ड भूल गए?", "पासवर्ड विसरलात?"),
        "login_button" to Trans("Log In", "लॉग इन करें", "लॉग इन करा"),
        "register_prompt" to Trans("New user? Register now", "नया उपयोगकर्ता? पंजीकरण करें", "नवीन वापरकर्ता? नोंदणी करा"),
        "register_title" to Trans("Create GramVyapar Account", "ग्रामव्यापार खाता बनाएं", "ग्रामव्यापार खाते तयार करा"),
        "full_name" to Trans("Full Name", "पूरा नाम", "पूर्ण नाव"),
        "select_role" to Trans("Select Your Role", "अपनी भूमिका चुनें", "आपली भूमिका निवडा"),
        "village" to Trans("Village / Town", "गाँव / कस्बा", "गाव / शहर"),
        "taluka" to Trans("Taluka / Block", "तहसील / ब्लॉक", "तालुका / विभाग"),
        "district" to Trans("District", "जिला", "जिल्हा"),
        "state" to Trans("State", "राज्य", "राज्य"),
        "pincode" to Trans("Pincode", "पिनकोड", "पिनकोड"),
        "kyc_upload_note" to Trans("Upload Aadhaar / Land record for verification", "सत्यापन के लिए आधार / 7/12 अपलोड करें", "पडताळणीसाठी आधार / 7/12 उतारा जोडा"),
        "terms_accept" to Trans("I agree to Farmer & Artisan Terms & Conditions", "मैं किसान एवं कारीगर नियमों से सहमत हूँ", "मी शेतकरी आणि कारागीर अटींशी सहमत आहे"),
        "verify_otp" to Trans("Verify OTP", "ओटीपी सत्यापित करें", "ओटीपी पडताळा"),

        // Home Screen
        "search_placeholder" to Trans("Search fresh veggies, grains, artisans...", "सब्जियां, अनाज, हस्तशिल्प खोजें...", "भाजीपाला, धान्य, हस्तकला शोधा..."),
        "mandi_ticker_title" to Trans("LIVE MANDI RATES (Agmarknet/eNAM)", "लाइव मंडी भाव (Agmarknet/eNAM)", "थेट कृषी बाजारभाव (Agmarknet/eNAM)"),
        "categories" to Trans("Categories", "श्रेणियां", "वर्गवारी"),
        "featured_farm_fresh" to Trans("Direct From Farms", "सीधे खेतों से ताजा", "थेट शेतातून ताजे"),
        "support_artisans" to Trans("Rural Artisans Guild", "ग्रामीण हस्तशिल्प कारीगर", "ग्रामीण हस्तकला कारागीर"),
        "filter" to Trans("Filter", "फ़िल्टर", "फिल्टर"),
        "all" to Trans("All", "सभी", "सर्व"),
        "add_to_cart" to Trans("Add to Cart", "कार्ट में जोड़ें", "कार्टमध्ये जोडा"),
        "buy_now" to Trans("Buy Now", "अभी खरीदें", "आत्ताच खरेदी करा"),
        "market_rate_badge" to Trans("Mandi Rate", "मंडी दर", "बाजारभाव"),

        // Seller
        "seller_dashboard_title" to Trans("Farmer & Seller Dashboard", "किसान एवं विक्रेता डैशबोर्ड", "शेतकरी व विक्रेता डॅशबोर्ड"),
        "total_sales" to Trans("Total Sales", "कुल बिक्री", "एकूण विक्री"),
        "orders_received" to Trans("Orders Received", "प्राप्त ऑर्डर", "मिळालेल्या ऑर्डर"),
        "pending_payout" to Trans("Pending Payout", "लंबित भुगतान", "प्रलंबित देयक"),
        "add_new_produce" to Trans("List New Farm Produce / Craft", "नया उत्पाद सूचीबद्ध करें", "नवीन उत्पादन जोडा"),
        "product_name" to Trans("Produce / Item Name", "उत्पाद का नाम", "उत्पादनाचे नाव"),
        "price_per_unit" to Trans("Selling Price (₹)", "विक्रय मूल्य (₹)", "विक्री किंमत (₹)"),
        "unit_type" to Trans("Unit", "इकाई", "एकक"),
        "auto_mandi_compare" to Trans("Live Mandi Benchmark", "लाइव मंडी बेंचमार्क", "थेट बाजारभाव संदर्भ"),
        "publish_product" to Trans("Publish to Marketplace", "बाजार में प्रकाशित करें", "बाजारात प्रकाशित करा"),

        // Mandi Rates Screen
        "mandi_title" to Trans("Mandi Wholesale Rates", "मंडी थोक भाव", "घाऊक बाजारभाव"),
        "select_mandi" to Trans("Select APMC Mandi", "कृषि उपज मंडी चुनें", "कृषी उत्पन्न बाजार समिती निवडा"),
        "trend_7day" to Trans("7-Day Trend", "7 दिवसीय रुझान", "7 दिवसांचा कल"),
        "trend_30day" to Trans("30-Day Trend", "30 दिवसीय रुझान", "30 दिवसांचा कल"),
        "rate_alert_sub" to Trans("Get alert if rate changes > 5%", "दर 5% से अधिक बदलने पर अलर्ट पाएं", "दर 5% पेक्षा जास्त बदलल्यास सूचना मिळवा"),

        // Cart & Checkout
        "my_cart" to Trans("My Shopping Cart", "मेरी खरीदारी कार्ट", "माझी खरेदी सूची"),
        "cart_empty" to Trans("Your cart is empty", "आपकी कार्ट खाली है", "तुमची कार्ट रिकामी आहे"),
        "subtotal" to Trans("Subtotal", "उप-योग", "एकूण रक्कम"),
        "delivery_fee" to Trans("Delivery Fee", "वितरण शुल्क", "वितरण शुल्क"),
        "free_delivery" to Trans("FREE (Govt Rural Subsidy)", "मुफ़्त (सरकारी ग्रामीण सब्सिडी)", "मोफत (शासकीय ग्रामीण अनुदान)"),
        "proceed_checkout" to Trans("Proceed to Checkout", "भुगतान के लिए आगे बढ़ें", "खरेदी पूर्ण करा"),
        "payment_method" to Trans("Payment Method", "भुगतान विधि", "पेमेंट पद्धत"),
        "order_success_title" to Trans("Order Placed Successfully!", "ऑर्डर सफलतापूर्वक दर्ज की गई!", "ऑर्डर यशस्वीरीत्या नोंदवली गेली!"),
        "track_order" to Trans("Track Order", "ऑर्डर ट्रैक करें", "ऑर्डर ट्रॅक करा"),

        // Delivery
        "assigned_deliveries" to Trans("Assigned Deliveries", "सौंपी गई डिलीवरी", "नियुक्त वितरण"),
        "pickup_from" to Trans("Pickup from Farmer", "किसान से संकलन", "शेतकऱ्याकडून संकलन"),
        "deliver_to" to Trans("Deliver to Buyer", "खरीदार को वितरण", "ग्राहकाला वितरण"),
        "mark_delivered" to Trans("Enter OTP & Complete", "ओटीपी दर्ज करें और पूरा करें", "ओटीपी नोंदवून पूर्ण करा"),

        // Training
        "training_title" to Trans("Rural Entrepreneur Academy", "ग्रामीण उद्यमिता अकादमी", "ग्रामीण उद्योजकता अकादमी"),
        "training_subtitle" to Trans("Master online selling on Amazon Kisan, Flipkart & eNAM", "अमेज़न किसान, फ्लिपकार्ट और ई-नाम पर ऑनलाइन बिक्री सीखें", "ॲमेझॉन किसान, फ्लिपकार्ट व ई-नामवर ऑनलाइन विक्री शिका"),
        "take_course" to Trans("Start Lesson", "पाठ शुरू करें", "धडा सुरू करा"),
        "certificate_btn" to Trans("View Certificate", "प्रमाणपत्र देखें", "प्रमाणपत्र पहा")
    )
}
