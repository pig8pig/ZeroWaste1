package com.example.zerowaste.data.remote.model.cart

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateCartRequest(
    @SerialName("location_id")
    val locationId: String,
    @SerialName("user_id")
    val userId: String,
    @SerialName("status")
    val status: String = "Active"
)
