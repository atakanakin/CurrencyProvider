package com.atakan.mainclient.presentation

sealed class Screen (val route: String){
    object MainScreen: Screen("main_screen")
    object AIDLScreen: Screen("aidl_screen")
    object MessengerScreen: Screen("messenger_screen")
    object BroadcastScreen: Screen("broadcast_screen")
}