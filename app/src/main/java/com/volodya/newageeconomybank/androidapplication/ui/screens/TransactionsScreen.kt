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
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.style.TextAlign
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
import com.volodya.newageeconomybank.androidapplication.logic.api.extra.info.TransactionInfo
import com.volodya.newageeconomybank.androidapplication.ui.screens.extra.CustomTextField
import com.volodya.newageeconomybank.androidapplication.ui.screens.extra.TransactionView
import kotlinx.coroutines.launch

@Composable
fun TransactionsScreen(navController: NavController, api: ServiceApiClient) {

    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    var transactionsState by remember {
        mutableStateOf<List<TransactionInfo>?>(null)
    }
    var state by remember {
        mutableStateOf(StatusCode.Success)
    }
    var isLoading by remember {
        mutableStateOf(true)
    }
    var showDialog by remember {
        mutableStateOf(false)
    }
    var transactionTo by remember {
        mutableStateOf("")
    }
    var transactionFrom by remember {
        mutableStateOf("")
    }
    var transactionAmount by remember {
        mutableStateOf("")
    }
    var showTransactionError by remember {
        mutableStateOf(false)
    }
    var newTransactionLoading by remember {
        mutableStateOf(false)
    }

    if (state == StatusCode.Unauthorized) {
        navController.navigate("login")
    }

    fun refresh() = coroutineScope.launch {
        try {
            isLoading = true
            when (val transactions = api.transactionsApi.get()) {
                is RespondType.Unauthorized -> {
                    state = StatusCode.Unauthorized
                    transactionsState = null
                }
                is RespondType.OkWithData<*> -> {
                    val data = transactions.data as List<TransactionInfo>
                    transactionsState = data
                }
                else -> {
                    state = StatusCode.UndefinedError
                    transactionsState = null
                }
            }
        }
        catch (e: Exception) {
            Log.e("TransactionsScreenError", e.toString())
            transactionsState = null
        }
        finally {
            isLoading = false
        }
    }
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isLoading)

    LaunchedEffect(Unit) {
        refresh()
    }

    if (showDialog) {
        Dialog(onDismissRequest = {
            showDialog = false
            transactionFrom = ""
            transactionTo = ""
            transactionAmount = ""
            showTransactionError = false
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
                        value = transactionTo,
                        onValueChange = { value ->
                            transactionTo = value.filter { it.isDigit() }
                        },
                        label = {
                            Text(stringResource(R.string.transaction_from))
                        }
                    )
                    Spacer(Modifier.height(16.dp))
                    CustomTextField(
                        value = transactionFrom,
                        onValueChange = { value ->
                            transactionFrom = value.filter { it.isDigit() }
                        },
                        label = {
                            Text(stringResource(R.string.transaction_to))
                        }
                    )
                    Spacer(Modifier.height(16.dp))
                    CustomTextField(
                        value = transactionAmount,
                        onValueChange = { value ->
                            if (value.count { it == '.' } <= 1) {
                                transactionAmount = value.filter { it.isDigit() || it == '.' }
                            }
                        },
                        label = {
                            Text(stringResource(R.string.transaction_amount))
                        }
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = { coroutineScope.launch {
                        try {
                            newTransactionLoading = true
                            val respond = api.transactionsApi.create(
                                transactionTo.toInt(),
                                transactionFrom.toInt(),
                                transactionAmount.toDouble())
                            when (respond) {
                                is RespondType.Ok -> {
                                    showDialog = false
                                    transactionTo = ""
                                    transactionFrom = ""
                                    transactionAmount = ""
                                    showTransactionError = false
                                    refresh()
                                }
                                is RespondType.Unauthorized -> {
                                    navController.navigate("login")
                                }
                                else -> {
                                    showTransactionError = true
                                }
                            }
                        }
                        catch (e: Exception) {
                            Log.e("TransactionScreenError", e.toString())
                            showTransactionError = true
                        }
                        finally {
                            newTransactionLoading = false
                        }
                    } },
                    enabled =
                        transactionTo.isNotEmpty() &&
                        transactionFrom.isNotEmpty() &&
                        transactionAmount.isNotEmpty()
                    ) {
                        Text(stringResource(R.string.create_transaction))
                    }
                    if (newTransactionLoading) {
                        showTransactionError = false
                        Spacer(Modifier.height(16.dp))
                        CircularProgressIndicator()
                    }
                    if (showTransactionError) {
                        Text(stringResource(
                            R.string.transaction_error),
                            color = Color.Red,
                            textAlign = TextAlign.Center)
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
                    showDialog = true
                },
            ) {
                Icon(Icons.Filled.Add, stringResource(R.string.add_transaction))
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
                    stringResource(R.string.transactions_screen_header),
                    fontSize = 50.sp)
                Spacer(Modifier.height(16.dp))
                if (transactionsState != null) {
                    if (transactionsState!!.isNotEmpty()) {
                        for (i in transactionsState!!) {
                            TransactionView(
                                i.senderId?.toString() ?: i.bank!!,
                                i.recipientId?.toString() ?: i.bank!!,
                                i.amount
                            )
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