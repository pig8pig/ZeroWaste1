package com.example.zerowaste.ui.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zerowaste.data.db.GroceryDao
import com.example.zerowaste.data.model.Grocery
import com.example.zerowaste.util.NotificationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val groceryDao: GroceryDao,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val expiringSoon: StateFlow<List<Grocery>> = groceryDao.getExpiringSoon()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        checkAndNotifyOfExpiringFood()
    }

    fun advanceDay() {
        viewModelScope.launch {
            val allGroceries = groceryDao.getAll()
            val updatedGroceries = allGroceries.map {
                it.copy(daysToExpiry = it.daysToExpiry - 1)
            }
            groceryDao.insertAll(updatedGroceries)
            checkAndNotifyOfExpiringFood()
        }
    }

    fun rewindDay() {
        viewModelScope.launch {
            val allGroceries = groceryDao.getAll()
            val updatedGroceries = allGroceries.map {
                it.copy(daysToExpiry = it.daysToExpiry + 1)
            }
            groceryDao.insertAll(updatedGroceries)
        }
    }

    fun checkAndNotifyOfExpiringFood() {
        viewModelScope.launch {
            val expiringFood = groceryDao.getExpiringAndExpired()
            if (expiringFood.isNotEmpty()) {
                val notificationHelper = NotificationHelper(context)
                val foodNames = expiringFood.joinToString { it.name }
                notificationHelper.showNotification(
                    "Food is expiring!",
                    "Don't forget to use your: $foodNames"
                )
            }
        }
    }
}
