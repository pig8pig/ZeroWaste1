package com.example.zerowaste.ui.home

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
class HomeViewModel @Inject constructor(
    groceryDao: GroceryDao
) : ViewModel() {

    val expiringSoon: StateFlow<List<Grocery>> = groceryDao.getExpiringSoon()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}
