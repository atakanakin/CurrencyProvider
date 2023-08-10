package com.atakan.mainclient.presentation.currency

import com.atakan.mainclient.domain.model.BPI
import com.atakan.mainclient.domain.model.Currency
import com.atakan.mainclient.domain.model.ExchangeRate
import com.atakan.mainclient.domain.model.TimeInfo

data class CurrencyState (
    val isLoading: Boolean = false,
    val currency: Currency = Currency(
        TimeInfo(""), "",
        BPI(
            ExchangeRate("", "","", "",0.0f),
            ExchangeRate("", "","", "",0.0f),
            ExchangeRate("", "","", "",0.0f))),
    val error: String = ""
)