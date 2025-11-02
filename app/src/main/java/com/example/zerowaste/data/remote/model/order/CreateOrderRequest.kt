package com.example.zerowaste.data.remote.model.order

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateOrderRequest(
    @SerialName("location_id")
    val locationId: String,
    @SerialName("fulfillment_method")
    val fulfillmentMethod: String,
    @SerialName("customer")
    val customer: Customer,
    @SerialName("items")
    val items: List<OrderItem>
)

@Serializable
data class Customer(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String,
    @SerialName("address")
    val address: Address
)

@Serializable
data class Address(
    @SerialName("street_address")
    val streetAddress: String,
    @SerialName("city")
    val city: String,
    @SerialName("region")
    val region: String,
    @SerialName("postal_code")
    val postalCode: String,
    @SerialName("country")
    val country: String
)

@Serializable
data class OrderItem(
    @SerialName("product_id")
    val productId: String,
    @SerialName("quantity")
    val quantity: Int
)
