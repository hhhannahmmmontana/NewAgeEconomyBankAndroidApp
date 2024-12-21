package com.volodya.newageeconomybank.androidapplication.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.volodya.newageeconomybank.androidapplication.R
import com.volodya.newageeconomybank.androidapplication.logic.api.ServiceApiClient
import com.volodya.newageeconomybank.androidapplication.logic.api.extra.RespondType
import com.volodya.newageeconomybank.androidapplication.logic.api.extra.StatusCode
import com.volodya.newageeconomybank.androidapplication.logic.api.extra.info.AccountInfo
import com.volodya.newageeconomybank.androidapplication.ui.screens.extra.AccountView
import com.volodya.newageeconomybank.androidapplication.ui.screens.extra.CustomTextField
import kotlinx.coroutines.launch

@Composable
fun AccountsScreen(navController: NavController, api: ServiceApiClient) {

    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    var accountsState by remember {
        mutableStateOf<List<AccountInfo>?>(null)
    }
    var state by remember {
        mutableStateOf(StatusCode.Success)
    }
    var isLoading by remember {
        mutableStateOf(true)
    }
    var creditAccountId by remember {
        mutableIntStateOf(-1)
    }
    var creditAmount by remember {
        mutableStateOf("")
    }
    var showCreditError by remember {
        mutableStateOf(false)
    }

    if (state == StatusCode.Unauthorized) {
        navController.navigate("login")
    }

    fun refresh() = coroutineScope.launch {
        try {
            isLoading = true
            when (val accounts = api.accountApi.get()) {
                is RespondType.Unauthorized -> {
                    state = StatusCode.Unauthorized
                    accountsState = null
                }
                is RespondType.OkWithData<*> -> {
                    val data = accounts.data as List<AccountInfo>
                    accountsState = data
                }
                else -> {
                    state = StatusCode.UndefinedError
                    accountsState = null
                }
            }
        }
        catch (e: Exception) {
            Log.e("AccountScreenError", e.toString())
            accountsState = null
        }
        finally {
            isLoading = false
        }
    }
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isLoading)

    LaunchedEffect(Unit) {
        refresh()
    }

    if (creditAccountId > 0) {
        Dialog(onDismissRequest = {
            creditAccountId = -1
            creditAmount = ""
            showCreditError = false
        }) {
            Column (
                Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color.White,
                        RoundedCornerShape(5.dp))
            ) {
                Column(
                    Modifier
                        .padding(top = 15.dp, bottom = 20.dp, start = 10.dp, end = 10.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CustomTextField(
                        value = creditAmount,
                        onValueChange = { value ->
                            if (value.count { it == '.' } <= 1) {
                                creditAmount = value.filter { it.isDigit() || it == '.' }
                            }
                        },
                        label = {
                            Text(stringResource(R.string.credit_sum))
                        }
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = { coroutineScope.launch {
                        val respond = api.creditApi.take(creditAccountId, creditAmount.toDouble())
                        if (respond is RespondType.Ok) {
                            creditAccountId = -1
                            creditAmount = ""
                            refresh()
                        } else if (respond is RespondType.Unauthorized) {
                            navController.navigate("login")
                        } else {
                            showCreditError = true
                        }
                    } }) {
                        Text(stringResource(R.string.become_slave))
                    }
                    if (showCreditError) {
                        Text(stringResource(R.string.credit_error))
                    }
                }
            }
        }
    }

    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = { refresh() }
    ) {
        Scaffold(floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        val result = api.accountApi.open()
                        if (result is RespondType.Unauthorized) {
                            state = StatusCode.Unauthorized
                        } else if (result is RespondType.Ok) {
                            state = StatusCode.Success
                        } else {
                            state = StatusCode.UndefinedError
                        }
                        refresh()
                    }
                },
            ) {
                Icon(Icons.Filled.Add, stringResource(R.string.add_account))
            }
        }, floatingActionButtonPosition = FabPosition.End) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(top = 50.dp, start = 20.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    stringResource(R.string.accounts_screen_header),
                    fontSize = 50.sp)
                Spacer(Modifier.height(16.dp))
                if (accountsState != null) {
                    if (accountsState!!.isNotEmpty()) {
                        for (i in accountsState!!) {
                            AccountView(i.id, i.savings) {
                                creditAccountId = i.id
                            }
                        }
                    } else {
                        Text(stringResource(R.string.error_404), color = Color.Gray, fontSize = 30.sp)
                    }
                } else {
                    if (!isLoading) {
                        Text(stringResource(R.string.error_404), color = Color.Gray, fontSize = 30.sp)
                    }
                }
            }
        }
    }



}