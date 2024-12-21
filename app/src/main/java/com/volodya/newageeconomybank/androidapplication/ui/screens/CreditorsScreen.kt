package com.volodya.newageeconomybank.androidapplication.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.volodya.newageeconomybank.androidapplication.R
import com.volodya.newageeconomybank.androidapplication.logic.api.ServiceApiClient
import com.volodya.newageeconomybank.androidapplication.logic.api.extra.RespondType
import com.volodya.newageeconomybank.androidapplication.logic.api.extra.StatusCode
import kotlinx.coroutines.launch

@Composable
fun CreditorsScreen(navController: NavController, api: ServiceApiClient) {
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    var creditorsState by remember {
        mutableStateOf<List<String>?>(null)
    }
    var state by remember {
        mutableStateOf(StatusCode.Success)
    }
    var isLoading by remember {
        mutableStateOf(false)
    }
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isLoading)

    fun refresh() = coroutineScope.launch {
        try {
            isLoading = true
            when (val accounts = api.creditApi.getCreditors()) {
                is RespondType.Unauthorized -> {
                    state = StatusCode.Unauthorized
                    creditorsState = null
                }
                is RespondType.OkWithData<*> -> {
                    val data = accounts.data as List<String>
                    creditorsState = data
                }
                else -> {
                    state = StatusCode.UndefinedError
                    creditorsState = null
                }
            }
        }
        catch (e: Exception) {
            Log.e("CreditorsScreenError", e.toString())
            creditorsState = null
        }
        finally {
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        refresh()
    }

    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = { refresh() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 50.dp, start = 20.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                stringResource(R.string.creditors),
                fontSize = 50.sp)
            Spacer(Modifier.height(16.dp))
            if (creditorsState != null) {
                if (creditorsState!!.isNotEmpty()) {
                    for (i in creditorsState!!) {
                        Text(i, fontSize = 30.sp)
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