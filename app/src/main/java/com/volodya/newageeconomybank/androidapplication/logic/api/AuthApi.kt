package com.volodya.newageeconomybank.androidapplication.logic.api

import android.content.Context
import android.util.Log
import com.android.volley.Request.Method
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.volodya.newageeconomybank.androidapplication.logic.api.extra.RespondType
import com.volodya.newageeconomybank.androidapplication.logic.api.extra.StatusCode
import org.json.JSONObject
import java.nio.charset.Charset
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AuthApi(url: String, private val context: Context) {
    private val url = "$url/auth"

    suspend fun login(username: String, password: String): RespondType = suspendCoroutine { result ->
        val q = Volley.newRequestQueue(context)
        val loginUrl = "$url/login"
        val postData = JSONObject()
        postData.put("username", username)
        postData.put("password", password)

        val request = JsonObjectRequest(
            Method.POST,
            loginUrl,
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
                result.resume(RespondType.Failure())
            }
        )
        q.add(request)
    }

    suspend fun register(username: String, passportNumber: Int, password: String): RespondType = suspendCoroutine { result ->
        val q = Volley.newRequestQueue(context)
        val registerUrl = "$url/register"
        val postData = JSONObject()
        postData.put("username", username)
        postData.put("passportNumber", passportNumber)
        postData.put("password", password)

        val request = JsonObjectRequest(
            Method.POST,
            registerUrl,
            postData,
            { response: JSONObject? ->
                result.resume(try {
                    val token = response?.getString("token")
                    if (token != null) RespondType.SuccessfulAuthorization(token)
                    else RespondType.Failure()
                } catch(e: Exception) {
                    Log.e("RegisterError", e.toString())
                    RespondType.Failure()
                })
            },
            { error: VolleyError? ->
                Log.e("RegisterError", error.toString())
                if (error?.networkResponse == null || error.networkResponse.statusCode != 409) {
                    result.resume(RespondType.Failure())
                } else {
                    val errorData = String(error.networkResponse.data, Charset.defaultCharset())
                    val jsonData = JSONObject(errorData)
                    result.resume(try {
                        val userOwner = jsonData.get("username").toString()
                        val field = jsonData.get("field").toString()
                        val statusCode = when(field) {
                            "Username" -> StatusCode.UsernameOccupied
                            "Passport" -> StatusCode.PassportOccupied
                            "Password" -> StatusCode.PasswordOccupied
                            else -> StatusCode.UndefinedError
                        }
                        RespondType.Collision(userOwner, statusCode)
                    }
                    catch (_: Exception) {
                        RespondType.Failure()
                    })
                }
            }
        )
        q.add(request)
    }
}