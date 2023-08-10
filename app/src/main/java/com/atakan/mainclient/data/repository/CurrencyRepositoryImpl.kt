package com.atakan.mainclient.data.repository

import com.atakan.mainclient.data.remote.CurrencyApi
import com.atakan.mainclient.domain.model.Currency
import com.atakan.mainclient.domain.repository.CurrencyRepo
import javax.inject.Inject

class CurrencyRepositoryImpl @Inject constructor(
    private val api: CurrencyApi
) : CurrencyRepo{

    override suspend fun getCurrency(): Currency {
        return api.getCurrency()
    }
}