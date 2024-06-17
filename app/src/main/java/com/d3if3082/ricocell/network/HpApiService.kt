package com.d3if3082.ricocell.network

import com.d3if3082.ricocell.model.HandPhone
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

private const val BASE_URL = "https://wjhxmplq-8080.asse.devtunnels.ms/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface HandphoneApiService {
    @GET("products")
    suspend fun getHandphone(): List<HandPhone>

    @POST("products")
    suspend fun addHandphone(@Body handPhone: HandPhone)

    @DELETE("products/{id}")
    suspend fun deleteHanphone(@Path("id") id: String)
}

object HandphoneApi {
    val service: HandphoneApiService by lazy {
        retrofit.create(HandphoneApiService::class.java)
    }

    fun getHandphoneUrl(gambar: String): String {
        return  return "$gambar"
    }
}

enum class ApiStatus { LOADING, SUCCESS, FAILED}