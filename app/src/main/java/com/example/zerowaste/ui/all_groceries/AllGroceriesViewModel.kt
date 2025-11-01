
package com.example.zerowaste.ui.all_groceries

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zerowaste.data.db.GroceryDao
import com.example.zerowaste.data.model.Grocery
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AllGroceriesViewModel @Inject constructor(
    groceryDao: GroceryDao
) : ViewModel() {

    val groceries: StateFlow<List<Grocery>> = groceryDao.getAllGroceries()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}
