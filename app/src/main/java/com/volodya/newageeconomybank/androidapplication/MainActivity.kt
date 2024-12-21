package com.volodya.newageeconomybank.androidapplication

import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.security.NetworkSecurityPolicy
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.android.volley.toolbox.Volley
import com.volodya.newageeconomybank.androidapplication.logic.api.ServiceApiClient
import com.volodya.newageeconomybank.androidapplication.ui.screens.AccountsScreen
import com.volodya.newageeconomybank.androidapplication.ui.screens.AppNavHost
import com.volodya.newageeconomybank.androidapplication.ui.screens.LoginScreen
import com.volodya.newageeconomybank.androidapplication.ui.screens.RegisterScreen
import com.volodya.newageeconomybank.androidapplication.ui.theme.NewAgeEconomyBankAndroidApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        val api = ServiceApiClient("http://100.64.0.7:5239/api", this)
        setContent {
            NewAgeEconomyBankAndroidApplicationTheme {
                AppNavHost(api)
            }
        }
    }
}