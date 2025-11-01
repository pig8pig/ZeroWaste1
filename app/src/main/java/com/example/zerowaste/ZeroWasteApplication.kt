package com.example.zerowaste

import android.app.Application
import com.example.zerowaste.data.db.GroceryDao
import com.example.zerowaste.data.model.Grocery
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class ZeroWasteApplication : Application() {

    @Inject
    lateinit var groceryDao: GroceryDao

    @Inject
    lateinit var applicationScope: CoroutineScope

    override fun onCreate() {
        super.onCreate()
        applicationScope.launch {
            if (groceryDao.count() == 0) {
                groceryDao.insertAll(
                    listOf(
                        Grocery(name = "Apples", quantity = 5, units = "pcs", daysToExpiry = 7, type = "Fruit"),
                        Grocery(name = "Bananas", quantity = 6, units = "pcs", daysToExpiry = 3, type = "Fruit"),
                        Grocery(name = "Milk", quantity = 1, units = "L", daysToExpiry = 1, type = "Dairy"),
                        Grocery(name = "Bread", quantity = 1, units = "loaf", daysToExpiry = 5, type = "Bakery"),
                        Grocery(name = "Eggs", quantity = 12, units = "pcs", daysToExpiry = 14, type = "Dairy"),
                        Grocery(name = "Chicken Breast", quantity = 500, units = "g", daysToExpiry = 2, type = "Meat"),
                        Grocery(name = "Salmon Fillet", quantity = 250, units = "g", daysToExpiry = 1, type = "Meat"),
                        Grocery(name = "Ground Beef", quantity = 500, units = "g", daysToExpiry = 3, type = "Meat"),
                        Grocery(name = "Lettuce", quantity = 1, units = "head", daysToExpiry = 5, type = "Vegetable"),
                        Grocery(name = "Tomatoes", quantity = 4, units = "pcs", daysToExpiry = 7, type = "Vegetable"),
                        Grocery(name = "Cucumbers", quantity = 2, units = "pcs", daysToExpiry = 10, type = "Vegetable"),
                        Grocery(name = "Onions", quantity = 3, units = "pcs", daysToExpiry = 30, type = "Vegetable"),
                        Grocery(name = "Potatoes", quantity = 1, units = "kg", daysToExpiry = 60, type = "Vegetable"),
                        Grocery(name = "Carrots", quantity = 500, units = "g", daysToExpiry = 14, type = "Vegetable"),
                        Grocery(name = "Broccoli", quantity = 1, units = "head", daysToExpiry = 7, type = "Vegetable"),
                        Grocery(name = "Cheese", quantity = 200, units = "g", daysToExpiry = 21, type = "Dairy"),
                        Grocery(name = "Yogurt", quantity = 500, units = "g", daysToExpiry = 10, type = "Dairy"),
                        Grocery(name = "Butter", quantity = 250, units = "g", daysToExpiry = 90, type = "Dairy"),
                        Grocery(name = "Orange Juice", quantity = 1, units = "L", daysToExpiry = 14, type = "Pantry"),
                        Grocery(name = "Pasta", quantity = 500, units = "g", daysToExpiry = 365, type = "Pantry"),
                        Grocery(name = "Pasta Sauce", quantity = 500, units = "g", daysToExpiry = 180, type = "Pantry"),
                        Grocery(name = "Rice", quantity = 1, units = "kg", daysToExpiry = 365, type = "Pantry"),
                        Grocery(name = "Cereal", quantity = 1, units = "box", daysToExpiry = 180, type = "Pantry"),
                        Grocery(name = "Oatmeal", quantity = 1, units = "box", daysToExpiry = 365, type = "Pantry"),
                        Grocery(name = "Coffee", quantity = 250, units = "g", daysToExpiry = 365, type = "Pantry")
                    )
                )
            }
        }
    }
}
