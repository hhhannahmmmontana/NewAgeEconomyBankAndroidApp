package com.volodya.newageeconomybank.androidapplication.logic.api

import android.content.Context
import android.util.Log
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.volodya.newageeconomybank.androidapplication.logic.api.extra.RespondType
import com.volodya.newageeconomybank.androidapplication.logic.api.extra.info.TransactionInfo
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class TransactionsApi(url: String, private val context: Context) {
    private val url = "$url/transactions"

    suspend fun get(): RespondType = suspendCoroutine { result ->
        val q = Volley.newRequestQueue(context)
        val getUrl = "$url/get"

        val request = object : JsonObjectRequest(
            getUrl,
            { response: JSONObject? ->
                if (response == null) RespondType.NotFound()
                result.resume(
                    try {
                        val rawData = response!!.get("data").toString()
                        val typeToken = object : TypeToken<List<TransactionInfo>>() {}.type
                        val data = Gson().fromJson<List<TransactionInfo>>(rawData, typeToken)
                        RespondType.OkWithData<List<TransactionInfo>>(data)
                    }
                    catch (e: Exception) {
                        Log.e("GetAccountsError", e.toString())
                        RespondType.Failure()
                    })
            },
            { error: VolleyError? ->
                Log.e("GetAccountsError", error.toString())
                if (error?.networkResponse == null || error.networkResponse.statusCode != 401)
                    result.resume(RespondType.Failure())
                else
                    result.resume(RespondType.Unauthorized())
            }
        ) {
            @Override
            override fun getHeaders(): MutableMap<String, String> {
                val headers = mutableMapOf<String, String>()
                headers["Authorization"] = "Bearer ${Token.value}"
                headers["Content-Type"] = "application/json"
                return headers
            }
        }

        q.add(request)
    }

    suspend fun create(from: Int, to: Int, amount: Double): RespondType = suspendCoroutine { result ->
        val q = Volley.newRequestQueue(context)
        val createUrl = "$url/create"
        val postData = JSONObject()
        postData.put("from", from)
        postData.put("to", to)
        postData.put("amount", amount)

        val request = object : JsonObjectRequest(
            Method.POST,
            createUrl,
            postData,
            { response: JSONObject? ->
                result.resume(
                    if (response == null) RespondType.NotFound()
                    else RespondType.Ok())
            },
            { error: VolleyError? ->
                Log.e("CreateAccountError", error.toString())
                if (error?.networkResponse == null || error.networkResponse.statusCode != 401)
                    result.resume(RespondType.Failure())
                else
                    result.resume(RespondType.Unauthorized())
            }
        ) {
            @Override
            override fun getHeaders(): MutableMap<String, String> {
                val headers = mutableMapOf<String, String>()
                headers["Authorization"] = "Bearer ${Token.value}"
                headers["Content-Type"] = "application/json"
                return headers
            }
        }

        q.add(request)
    }
}