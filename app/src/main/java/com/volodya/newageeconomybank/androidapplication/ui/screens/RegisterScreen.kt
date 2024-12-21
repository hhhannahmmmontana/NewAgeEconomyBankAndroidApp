package com.volodya.newageeconomybank.androidapplication.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.volodya.newageeconomybank.androidapplication.ui.screens.extra.CustomTextField
import com.volodya.newageeconomybank.androidapplication.R
import com.volodya.newageeconomybank.androidapplication.logic.api.ServiceApiClient
import com.volodya.newageeconomybank.androidapplication.logic.api.Token
import com.volodya.newageeconomybank.androidapplication.logic.api.extra.RespondType
import com.volodya.newageeconomybank.androidapplication.logic.api.extra.StatusCode
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(navController: NavController, api: ServiceApiClient) {
    var username by remember {
        mutableStateOf("")
    }
    var passport by remember {
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
    }
    var isLoading by remember {
        mutableStateOf(false)
    }
    var statusCode by remember {
        mutableStateOf(StatusCode.Success)
    }
    var errorData by remember {
        mutableStateOf("")
    }

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 50.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_black),
            contentDescription = stringResource(R.string.logo_description),
            modifier = Modifier.width(300.dp))

        Text(
            text = stringResource(R.string.welcome),
            fontSize = 28.sp)
        Spacer(modifier = Modifier.height(16.dp))

        CustomTextField(
            value = username,
            onValueChange = {
                username = it
                statusCode = StatusCode.Success
            },
            label = {
                Text(stringResource(R.string.username_textbox))
            })
        Spacer(modifier = Modifier.height(16.dp))

        CustomTextField(
            value = passport,
            onValueChange = { value ->
                if (value.length <= 10) {
                    passport = value.filter { it.isDigit() }
                }
                statusCode = StatusCode.Success
            },
            label = {
                Text(stringResource(R.string.passport_textbox))
            })
        Spacer(modifier = Modifier.height(16.dp))

        CustomTextField(
            value = password,
            onValueChange = {
                password = it
                statusCode = StatusCode.Success
            },
            label = {
                Text(stringResource(R.string.password_textbox))
            },
            visualTransformation = PasswordVisualTransformation())
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            coroutineScope.launch {
                isLoading = true
                try {
                    val result = api.authApi.register(username, passport.toInt(), password)
                    Token.value =
                        if (result is RespondType.SuccessfulAuthorization) {
                            statusCode = StatusCode.Success
                            result.token
                        }
                        else {
                            if (result is RespondType.Collision) {
                                statusCode = result.statusCode
                                errorData = result.username
                            } else {
                                statusCode = StatusCode.UndefinedError
                            }
                            null
                        }
                    if (Token.value != null) {
                        navController.navigate("accounts")
                    }
                }
                catch (e: Exception) {
                    Log.e("RegisterViewError", e.toString())
                    statusCode = StatusCode.UndefinedError
                }
                finally {
                    isLoading = false
                }
            }
        }, enabled = username.isNotEmpty() && passport.isNotEmpty() && password.isNotEmpty()) {
            Text(stringResource(R.string.register_btn_text))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = stringResource(R.string.already_registered))
        TextButton(onClick = {
            navController.navigate("login")
        }) {
            Text(stringResource(R.string.try_login))
        }


        if (isLoading) {
            statusCode = StatusCode.Success
            CircularProgressIndicator()
        }

        if (statusCode != StatusCode.Success) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(when (statusCode) {
               StatusCode.UsernameOccupied -> stringResource(R.string.username_collision)
               StatusCode.PassportOccupied -> stringResource(R.string.passport_collision) + " $errorData"
               StatusCode.PasswordOccupied -> stringResource(R.string.password_collision) + " $errorData"
               else -> stringResource(R.string.undefined_error)
            }, color = Color.Red, textAlign = TextAlign.Center)
        }
    }
}