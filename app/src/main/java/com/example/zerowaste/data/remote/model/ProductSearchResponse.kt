package com.example.zerowaste.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductSearchResponse(
    @SerialName("products")
    val products: List<Product>? = null,
    @SerialName("stores")
    val stores: List<Store>? = null
)
