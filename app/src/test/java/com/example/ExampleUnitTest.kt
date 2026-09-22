package com.example

import com.example.data.model.AppLanguage
import com.example.data.model.CartItem
import com.example.data.model.Product
import com.example.data.model.ProductCategory
import com.example.ui.i18n.AppStrings
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun testLocalizationStrings() {
    assertEquals("GramVyapar", AppStrings.get("app_name", AppLanguage.ENGLISH))
    assertEquals("ग्रामव्यापार", AppStrings.get("app_name", AppLanguage.HINDI))
    assertEquals("ग्रामव्यापार", AppStrings.get("app_name", AppLanguage.MARATHI))

    assertEquals("Home", AppStrings.get("nav_home", AppLanguage.ENGLISH))
    assertEquals("होम", AppStrings.get("nav_home", AppLanguage.HINDI))
    assertEquals("मुख्यपृष्ठ", AppStrings.get("nav_home", AppLanguage.MARATHI))
  }

  @Test
  fun testCartCalculation() {
    val sampleProduct = Product(
      id = "test_p1",
      name = "Nashik Onions",
      nameHi = "प्याज",
      nameMr = "कांदे",
      category = ProductCategory.VEGETABLES,
      price = 25.0,
      unit = "kg",
      stock = 100.0,
      sellerName = "Kisan Patil",
      sellerPhone = "9876543210",
      village = "Dindori",
      district = "Nashik",
      marketMandiRate = 28.0,
      description = "Fresh onions"
    )

    val cartItem = CartItem(sampleProduct, 4.0)
    assertEquals(100.0, cartItem.totalPrice, 0.001)
  }
}
