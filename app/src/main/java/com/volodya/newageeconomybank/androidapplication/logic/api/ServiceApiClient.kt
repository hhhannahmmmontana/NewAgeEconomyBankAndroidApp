package com.volodya.newageeconomybank.androidapplication.logic.api

import android.content.Context

class ServiceApiClient(url: String, context: Context) {
    val authApi = AuthApi(url, context)

    val adminApi = AdminApi(url, context)

    val accountApi = AccountApi(url, context)

    val creditApi = CreditApi(url, context)

    val transactionsApi = TransactionsApi(url, context)
}