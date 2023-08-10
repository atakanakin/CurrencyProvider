package com.atakan.mainclient.domain.repository

import com.atakan.mainclient.domain.model.Currency

interface CurrencyRepo {

    suspend fun getCurrency() : Currency
}