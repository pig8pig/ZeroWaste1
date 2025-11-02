package com.example.zerowaste.data.remote.model.cart

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateCartResponse(
    @SerialName("data")
    val cartId: String
)
