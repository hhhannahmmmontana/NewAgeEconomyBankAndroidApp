package com.volodya.newageeconomybank.androidapplication.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Icon
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.volodya.newageeconomybank.androidapplication.R
import com.volodya.newageeconomybank.androidapplication.logic.api.Token

@Composable
fun Footer(navController: NavController) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        IconButton(onClick = {
            if (navController.currentDestination?.label != "accounts") {
                navController.navigate("accounts")
            }
        }) {
            Icon(
                Icons.Default.Lock,
                stringResource(R.string.accounts_icon),
                Modifier.size(500.dp))
        }
        IconButton(onClick = {
            if (navController.currentDestination?.label != "creditors") {
                navController.navigate("creditors")
            }
        }) {
            Icon(
                Icons.Default.Warning,
                stringResource(R.string.creditors_icon),
                Modifier.size(500.dp))
        }
        IconButton(onClick = {
            if (navController.currentDestination?.label != "transactions") {
                navController.navigate("transactions")
            }
        }) {
            Icon(
                Icons.AutoMirrored.Filled.Send,
                stringResource(R.string.transactions_icon),
                Modifier.size(500.dp))
        }
        IconButton(onClick = {
            Token.value = null
            navController.navigate("login")
        }) {
            Icon(
                Icons.AutoMirrored.Filled.ExitToApp,
                stringResource(R.string.personal_icon),
                Modifier.size(500.dp))
        }
    }
}