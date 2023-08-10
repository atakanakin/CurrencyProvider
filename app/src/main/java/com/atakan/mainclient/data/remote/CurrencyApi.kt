package com.atakan.mainclient.data.remote

import com.atakan.mainclient.domain.model.Currency
import retrofit2.http.GET

interface CurrencyApi {
    @GET("v1/bpi/currentprice.json")
    suspend fun getCurrency() : Currency
}