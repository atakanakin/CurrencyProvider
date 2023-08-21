package com.atakan.mainclient.presentation

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.atakan.mainclient.presentation.currency.screen.AIDL.AIDLScreen
import com.atakan.mainclient.presentation.currency.screen.broadcast.BroadcastScreen
import com.atakan.mainclient.presentation.currency.screen.main.MainScreen
import com.atakan.mainclient.presentation.currency.screen.messenger.MessengerScreen
import com.atakan.mainclient.presentation.theme.MainClientTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainClientTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Hello()
                }
            }
        }
    }
}

@Composable
fun Hello() {
    
}