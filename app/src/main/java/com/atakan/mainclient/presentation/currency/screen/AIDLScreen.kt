package com.atakan.mainclient.presentation.currency.screen

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.atakan.mainclient.common.Resource
import com.atakan.mainclient.presentation.currency.CurrencyViewModel
import com.atakan.mainclient.service.AIDLService
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.atakan.mainclient.domain.model.BPI
import com.atakan.mainclient.domain.model.Currency
import com.atakan.mainclient.domain.model.ExchangeRate
import com.atakan.mainclient.domain.model.TimeInfo
import javax.inject.Inject


@Composable
fun AIDLService(context: Context) {
    DisposableEffect(Unit) {
        // Start the foreground service when the Composable is first activated
        val intent = Intent(context, AIDLService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(context, intent)
        } else {
            context.startService(intent)
        }
        onDispose {
            // You can stop the service if you do not need to execute background
        }
    }
}

@Composable
fun AIDLScreen(context: Context, viewModel: CurrencyViewModel = hiltViewModel()) {
    val currencyState by viewModel.currencyLiveData.observeAsState()

    Column {
        val newCurrency = Currency(
            time = TimeInfo(updated = "2023-08-10T12:34:56Z"),
            chartName = "New Chart",
            bpi = BPI(
                USD = ExchangeRate(
                    code = "USD",
                    symbol = "$",
                    rate = "10000",
                    description = "United States Dollar",
                    rate_float = 10000f
                ),
                GBP = ExchangeRate(
                    code = "GBP",
                    symbol = "£",
                    rate = "8000",
                    description = "British Pound Sterling",
                    rate_float = 8000f
                ),
                EUR = ExchangeRate(
                    code = "EUR",
                    symbol = "€",
                    rate = "9000",
                    description = "Euro",
                    rate_float = 9000f
                )
            )
        )
        Button(onClick = { viewModel.refreshData(Resource.Success(newCurrency)) }) {
            Text(text = "Update")

        }
        AIDLService(context = context)

        when (currencyState) {
            is Resource.Success -> {
                val data = (currencyState as Resource.Success).data
                Text(text = data?.chartName ?: "null")
            }
            is Resource.Loading -> {
                CircularProgressIndicator()
            }
            is Resource.Error -> {
                Log.e("AIDLScreen", "Something bad happened")
                Text(
                    text = "Something bad happened",
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxSize()
                )
            }
            else -> {
                Log.e("AIDLScreen", "Something bad happened (null)")
                Text(
                    text = "Something bad happened (null)",
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
