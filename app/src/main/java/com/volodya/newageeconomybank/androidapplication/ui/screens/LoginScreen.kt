package com.volodya.newageeconomybank.androidapplication.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.volodya.newageeconomybank.androidapplication.ui.screens.extra.CustomTextField
import com.volodya.newageeconomybank.androidapplication.R
import com.volodya.newageeconomybank.androidapplication.logic.api.ServiceApiClient
import com.volodya.newageeconomybank.androidapplication.logic.api.Token
import com.volodya.newageeconomybank.androidapplication.logic.api.extra.RespondType
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController, api: ServiceApiClient) {
    var showDialog by remember {
        mutableStateOf(false)
    }
    var username by remember {
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
    }
    var adminPassword by remember {
        mutableStateOf("")
    }
    var isLoading by remember {
        mutableStateOf(false)
    }
    var showError by remember {
        mutableStateOf(false)
    }
    var showAdminError by remember {
        mutableStateOf(false)
    }
    var showAdminSuccess by remember {
        mutableStateOf(false)
    }
    var adminLoading by remember {
        mutableStateOf(false)
    }
    var showAdminServerDownError by remember {
        mutableStateOf(false)
    }

    val coroutineScope = rememberCoroutineScope()

    if (showDialog) {
        Dialog(onDismissRequest = {showDialog = false}) {
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
                    Text(stringResource(R.string.admin_textbox))
                    CustomTextField(
                        value = adminPassword,
                        onValueChange = {
                            showAdminError = false
                            showAdminSuccess = false
                            showAdminServerDownError = false
                            adminPassword = it
                        }, visualTransformation = PasswordVisualTransformation())
                    Spacer(Modifier.height(15.dp))
                    Button(onClick = { coroutineScope.launch {
                        adminLoading = true
                        try {
                            val result = api.adminApi.getAccess(adminPassword)
                            when (result) {
                                is RespondType.SuccessfulAuthorization -> {
                                    showAdminError = false
                                    showAdminServerDownError = false
                                    showAdminSuccess = true
                                }

                                is RespondType.AdminError -> {
                                    showAdminError = true
                                    showAdminServerDownError = false
                                    showAdminSuccess = false
                                }

                                else -> {
                                    showAdminError = false
                                    showAdminServerDownError = true
                                    showAdminSuccess = false
                                }
                            }
                        }
                        catch (e: Exception) {
                            Log.e("AdminError", e.toString())
                            showAdminError = false
                            showAdminServerDownError = true
                            showAdminSuccess = false
                        }
                        finally {
                            adminLoading = false
                        }
                    }
                    }, enabled = adminPassword.isNotEmpty()) {
                        Text(stringResource(R.string.login_btn_text))
                    }
                    if (showAdminError) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(stringResource(R.string.admin_error_first_line), color = Color.Red, textAlign = TextAlign.Center)
                        Text(stringResource(R.string.admin_error_second_line), color = Color.Red, textAlign = TextAlign.Center)
                    }
                    if (showAdminSuccess) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(stringResource(R.string.admin_success), color = Color.Gray, textAlign = TextAlign.Center)
                    }
                    if (showAdminServerDownError) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(stringResource(R.string.service_offline), color = Color.Red, textAlign = TextAlign.Center)
                    }
                    if (adminLoading) {
                        showAdminError = false
                        showAdminSuccess = false
                        showAdminServerDownError = false
                        Spacer(modifier = Modifier.height(16.dp))
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }

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
            text = stringResource(R.string.welcome_back),
            fontSize = 28.sp)
        Spacer(modifier = Modifier.height(16.dp))

        CustomTextField(
            value = username,
            onValueChange = {
                username = it
                showError = false
            },
            label = {
                Text(stringResource(R.string.username_textbox))
            })

        Spacer(modifier = Modifier.height(16.dp))

        CustomTextField(
            value = password,
            onValueChange = {
                password = it
                showError = false
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
                    val result = api.authApi.login(username, password)
                    Token.value =
                        if (result is RespondType.SuccessfulAuthorization) result.token
                        else {
                            showError = true
                            null
                        }
                    if (Token.value != null) {
                        navController.navigate("accounts")
                    }
                }
                catch (e: Exception) {
                    Log.e("LoginViewError", e.toString())
                    showError = true
                }
                finally {
                    isLoading = false
                }
            }
        }, enabled = username.isNotEmpty() && password.isNotEmpty()) {
            Text(stringResource(R.string.login_btn_text))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = stringResource(R.string.didnt_join))
        TextButton(onClick = {
            navController.navigate("register")
        }) {
            Text(stringResource(R.string.try_register))
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = {
            showDialog = true
        }) {
            Text(stringResource(R.string.become_admin))
        }


        if (isLoading) {
            showError = false
            CircularProgressIndicator()
        }

        if (showError) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(stringResource(R.string.login_error), color = Color.Red, textAlign = TextAlign.Center)
        }
    }
}