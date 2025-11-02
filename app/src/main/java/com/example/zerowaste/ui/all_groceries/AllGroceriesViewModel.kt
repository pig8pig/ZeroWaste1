package com.example.zerowaste.ui.all_groceries

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zerowaste.data.db.GroceryDao
import com.example.zerowaste.data.model.Grocery
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

sealed class GroceryFilter {
    object All : GroceryFilter()
    object ExpiringToday : GroceryFilter()
    object ExpiringTomorrow : GroceryFilter()
    data class Type(val type: String) : GroceryFilter()
}

@HiltViewModel
class AllGroceriesViewModel @Inject constructor(
    private val groceryDao: GroceryDao
) : ViewModel() {

    private val _filter = MutableStateFlow<GroceryFilter>(GroceryFilter.All)
    val filter: StateFlow<GroceryFilter> = _filter

    // Search text
    private val _search = MutableStateFlow("")
    val search: StateFlow<String> = _search

    // Combined flow: respond to both filter and search
    val groceries: StateFlow<List<Grocery>> = combine(_filter, _search) { f, s -> Pair(f, s) }
        .flatMapLatest { (filter, searchText) ->
            // base list from DAO based on the selected filter
            val baseFlow = when (filter) {
                GroceryFilter.All -> groceryDao.getAllGroceries()
                GroceryFilter.ExpiringToday -> groceryDao.getExpiringToday()
                GroceryFilter.ExpiringTomorrow -> groceryDao.getExpiringTomorrow()
                is GroceryFilter.Type -> groceryDao.getGroceriesByType(filter.type)
            }

            // if search text is blank, return baseFlow directly;
            // otherwise map results and filter client-side by name (case-insensitive)
            if (searchText.isBlank()) {
                baseFlow
            } else {
                baseFlow.map { list ->
                    val q = searchText.trim()
                    list.filter { it.name.contains(q, ignoreCase = true) }
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun setFilter(filter: GroceryFilter) {
        _filter.value = filter
    }

    fun setSearch(text: String) {
        _search.value = text
    }
}