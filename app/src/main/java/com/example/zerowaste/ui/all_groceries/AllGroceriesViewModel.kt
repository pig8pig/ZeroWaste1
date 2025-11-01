package com.example.zerowaste.ui.all_groceries

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zerowaste.data.db.GroceryDao
import com.example.zerowaste.data.model.Grocery
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

enum class GroceryFilter {
    ALL,
    EXPIRING_TODAY,
    EXPIRING_TOMORROW
}

@HiltViewModel
class AllGroceriesViewModel @Inject constructor(
    private val groceryDao: GroceryDao
) : ViewModel() {

    private val _filter = MutableStateFlow(GroceryFilter.ALL)
    val filter: StateFlow<GroceryFilter> = _filter

    val groceries: StateFlow<List<Grocery>> = _filter.flatMapLatest { filter ->
        when (filter) {
            GroceryFilter.ALL -> groceryDao.getAllGroceries()
            GroceryFilter.EXPIRING_TODAY -> groceryDao.getExpiringToday()
            GroceryFilter.EXPIRING_TOMORROW -> groceryDao.getExpiringTomorrow()
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setFilter(filter: GroceryFilter) {
        _filter.value = filter
    }
}
