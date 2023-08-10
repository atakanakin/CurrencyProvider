package com.atakan.mainclient.domain.model

data class Currency (
    val time: TimeInfo,
    val chartName: String,
    val bpi: BPI
)

data class BPI(
    val USD : ExchangeRate,
    val GBP: ExchangeRate,
    val EUR: ExchangeRate
)

data class ExchangeRate(
    val code: String,
    val symbol: String,
    val rate: String,
    val description: String,
    val rate_float: Float
)

data class TimeInfo(
    val updated : String
)