package com.atakan.mainclient.presentation.currency.screen.main

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun MainScreen(context: Context, navController: NavController) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxSize()){
        Box(
            modifier = Modifier.padding(10.dp)
        ) {

            Button(onClick = {
                navController.navigate(Screen.AIDLScreen.route)
            }) {
                Text("AIDL")
            }
        }
        Box(
            modifier = Modifier.padding(10.dp)
        ) {

            Button(onClick = { navController.navigate(Screen.MessengerScreen.route) }) {
                Text("Messenger")
            }
        }
        Box(
            modifier = Modifier.padding(10.dp)
        ) {

            Button(onClick = { navController.navigate(Screen.BroadcastScreen.route) }) {
                Text("Broadcast")
            }
        }
    }
}