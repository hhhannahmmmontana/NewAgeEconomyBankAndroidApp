package com.volodya.newageeconomybank.androidapplication.logic.api

import android.content.Context
import android.util.Log
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.volodya.newageeconomybank.androidapplication.logic.api.extra.RespondType
import com.volodya.newageeconomybank.androidapplication.logic.api.extra.info.AccountInfo
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class CreditApi(url: String, private val context: Context) {
    private val url = "$url/credit"

    suspend fun getCreditors(): RespondType = suspendCoroutine { result ->
        val q = Volley.newRequestQueue(context)
        val getCreditorsUrl = "$url/getcreditors"

        val request = JsonObjectRequest(
            getCreditorsUrl,
            { response: JSONObject? ->
                if (response == null) RespondType.NotFound()
                result.resume(
                    try {
                        val rawData = response!!.get("data").toString()
                        val typeToken = object : TypeToken<List<String>>() {}.type
                        val data = Gson().fromJson<List<String>>(rawData, typeToken)
                        RespondType.OkWithData<List<String>>(data)
                    }
                    catch (e: Exception) {
                        Log.e("GetCreditorsError", e.toString())
                        RespondType.Failure()
                    })
            },
            { error: VolleyError? ->
                Log.e("GetCreditorsError", error.toString())
                result.resume(RespondType.Failure())
            }
        )

        q.add(request)
    }

    suspend fun take(accountId: Int, amount: Double): RespondType = suspendCoroutine { result ->
        val q = Volley.newRequestQueue(context)
        val takeUrl = "$url/take"
        val postData = JSONObject()
        postData.put("accountId", accountId)
        postData.put("amount", amount)

        val request = object : JsonObjectRequest(
            Method.POST,
            takeUrl,
            postData,
            { response: JSONObject? ->
                result.resume(
                    if (response == null) RespondType.NotFound()
                    else RespondType.Ok())
            },
            { error: VolleyError? ->
                Log.e("TakeCreditError", error.toString())
                if (error == null) {
                    result.resume(RespondType.Failure())
                } else if (error.networkResponse == null) {
                    result.resume(RespondType.Ok())
                } else if (error.networkResponse.statusCode == 401) {
                    result.resume(RespondType.Unauthorized())
                } else {
                    result.resume(RespondType.Failure())
                }

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