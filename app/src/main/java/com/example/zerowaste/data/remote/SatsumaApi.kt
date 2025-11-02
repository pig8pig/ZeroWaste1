package com.example.zerowaste.data.remote

import com.example.zerowaste.data.remote.model.ProductSearchResponse
import com.example.zerowaste.data.remote.model.cart.AddItemRequest
import com.example.zerowaste.data.remote.model.cart.CreateCartRequest
import com.example.zerowaste.data.remote.model.cart.CreateCartResponse
import com.example.zerowaste.data.remote.model.order.CreateOrderRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface SatsumaApi {

    @GET("product")
    suspend fun searchProducts(
        @Header("Authorization") apiKey: String,
        @Query("query") query: String,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("distance") distance: String = "10mi"
    ): ProductSearchResponse

    @POST("cart")
    suspend fun createCart(
        @Header("Authorization") apiKey: String,
        @Body request: CreateCartRequest
    ): CreateCartResponse

    @POST("cart/{cartId}/item")
    suspend fun addItemToCart(
        @Header("Authorization") apiKey: String,
        @Path("cartId") cartId: String,
        @Body request: AddItemRequest
    ): Response<Unit>

    @POST("order")
    suspend fun createOrder(
        @Header("Authorization") apiKey: String,
        @Body request: CreateOrderRequest
    ): Response<Unit>
}
