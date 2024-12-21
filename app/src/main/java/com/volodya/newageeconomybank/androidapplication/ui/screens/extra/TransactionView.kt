package com.volodya.newageeconomybank.androidapplication.ui.screens.extra

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.volodya.newageeconomybank.androidapplication.R

@Composable
fun TransactionView(from: String, to: String, amount: Double) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(70.dp)
            .padding(end = 50.dp)
            .background(
                color = Color.LightGray,
                shape = RoundedCornerShape(15.dp)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "№$from -> №$to : $amount " + stringResource(R.string.grb),
                fontSize = 25.sp,
                textAlign = TextAlign.Center)
        }
    }
    Spacer(Modifier.height(16.dp))
}