package com.volodya.newageeconomybank.androidapplication.logic.api

import android.content.Context
import android.util.Log
import com.android.volley.Request.Method
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.volodya.newageeconomybank.androidapplication.logic.api.extra.RespondType
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AdminApi(url: String, private val context: Context) {
    private val url = "$url/admin"

    suspend fun getAccess(password: String): RespondType = suspendCoroutine { result ->
        val q = Volley.newRequestQueue(context)
        val getAccessUrl = "$url/getaccess"
        val postData = JSONObject()
        postData.put("password", password)

        val request = JsonObjectRequest(
            Method.POST,
            getAccessUrl,
            postData,
            { response: JSONObject? ->
                result.resume(try {
                    val token = response?.getString("token")
                    if (token != null) RespondType.SuccessfulAuthorization(token)
                    else RespondType.Failure()
                } catch(e: Exception) {
                    Log.e("LoginError", e.toString())
                    RespondType.Failure()
                })
            },
            { error: VolleyError? ->
                Log.e("LoginError", error.toString())
                if (error?.networkResponse == null || error.networkResponse.statusCode != 401) {
                    result.resume(RespondType.Failure())
                } else {
                    result.resume(RespondType.AdminError())
                }
            }
        )
        q.add(request)
    }
}