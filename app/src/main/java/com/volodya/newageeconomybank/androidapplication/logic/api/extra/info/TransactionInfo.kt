package com.volodya.newageeconomybank.androidapplication.logic.api.extra.info

data class TransactionInfo(
    val senderId: Int?,
    val recipientId: Int?,
    val amount: Double,
    val bank: String?)