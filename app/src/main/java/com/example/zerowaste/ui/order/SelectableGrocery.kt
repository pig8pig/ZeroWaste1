package com.example.zerowaste.ui.order

import com.example.zerowaste.data.model.Grocery

data class SelectableGrocery(
    val grocery: Grocery,
    val isChecked: Boolean = false
)
