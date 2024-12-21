package com.volodya.newageeconomybank.androidapplication.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.volodya.newageeconomybank.androidapplication.logic.api.ServiceApiClient
import androidx.navigation.compose.*
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay

@Composable
fun AppNavHost(api: ServiceApiClient) {
    val navController = rememberNavController()
    Scaffold(bottomBar = {
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
        AnimatedVisibility(
            visible = currentRoute != "login" && currentRoute != "register",
            enter = fadeIn(tween(500)),
            exit = fadeOut(tween(500))
        ) {
            Footer(navController)
        }
    }) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("login") {
                LoginScreen(navController, api)
            }
            composable("register") {
                RegisterScreen(navController, api)
            }
            composable("accounts") {
                AccountsScreen(navController, api)
            }
            composable("creditors") {
                CreditorsScreen(navController, api)
            }
            composable("transactions") {
                TransactionsScreen(navController, api)
            }
        }
    }
}