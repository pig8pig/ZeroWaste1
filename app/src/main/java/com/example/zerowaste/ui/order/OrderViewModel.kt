package com.example.zerowaste.ui.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zerowaste.data.db.GroceryDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val groceryDao: GroceryDao
) : ViewModel() {

    private val _uiState = MutableStateFlow<OrderUiState>(OrderUiState.Initial)
    val uiState: StateFlow<OrderUiState> = _uiState.asStateFlow()

    init {
        fetchExpiringGroceries()
    }

    fun fetchExpiringGroceries() {
        viewModelScope.launch {
            _uiState.value = OrderUiState.Loading
            try {
                val expiringGroceries = groceryDao.getExpiringSoon().first()
                val selectableGroceries = expiringGroceries.map { SelectableGrocery(it) }
                _uiState.value = OrderUiState.Success(selectableGroceries)
            } catch (e: Exception) {
                _uiState.value = OrderUiState.Error(e.message ?: "Failed to fetch expiring groceries.")
            }
        }
    }

    fun checkout() {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState is OrderUiState.Success) {
                currentState.groceries.forEachIndexed { index, item ->
                    delay(1000) // Simulate network request
                    _uiState.update { a ->
                        (a as OrderUiState.Success).copy(
                            groceries = a.groceries.toMutableList().apply {
                                this[index] = item.copy(isChecked = true)
                            }
                        )
                    }
                }
                delay(500)
                _uiState.update {
                    (it as OrderUiState.Success).copy(showSuccessDialog = true)
                }
            }
        }
    }

    fun dismissSuccessDialog() {
        _uiState.update {
            (it as OrderUiState.Success).copy(showSuccessDialog = false)
        }
    }
}

sealed class OrderUiState {
    object Initial : OrderUiState()
    object Loading : OrderUiState()
    data class Success(
        val groceries: List<SelectableGrocery>,
        val showSuccessDialog: Boolean = false
    ) : OrderUiState()
    data class Error(val message: String) : OrderUiState()
}
