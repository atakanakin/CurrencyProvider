package com.atakan.mainclient.presentation.currency.screen.main

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue



@Composable
fun MainScreen(context: Context) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxSize()) {

        var isClicked by remember { mutableStateOf(false) }
        Text(text = "Hello")
        Button(onClick = {
            isClicked = !isClicked
        }) {
            Text(text = if(isClicked) "Back" else "Update")
        }
        Text(text = "Atakan")
    }
}