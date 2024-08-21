package com.oliver.wallet.ui.view.money

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oliver.wallet.R
import com.oliver.wallet.ui.view.ShimmerEffect

@Composable
fun PriceShimmerEffect() {
    Box(
        contentAlignment = Alignment.Center, modifier = Modifier
            .fillMaxHeight()
            .padding(vertical = 5.dp, horizontal = 10.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.tertiary)
            .padding(10.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier
                    .padding(bottom = 15.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    contentAlignment = Alignment.Center, modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .size(35.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.money_icon),
                        contentDescription = "Custom Money Icon",
                    )
                }
                ShimmerEffect(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp)
                        .padding(start = 10.dp, end = 50.dp)
                        .background(
                            MaterialTheme.colorScheme.tertiary,
                            RoundedCornerShape(10.dp)
                        )
                )
            }
            ShimmerEffect(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(135.dp)
                    .padding(horizontal = 10.dp)
                    .background(
                        MaterialTheme.colorScheme.tertiary,
                        RoundedCornerShape(10.dp)
                    )
            )
        }
    }
}

@Composable
fun GraphicShimmerEffect() {
    Box(
        contentAlignment = Alignment.Center, modifier = Modifier
            .fillMaxHeight()
            .padding(vertical = 5.dp, horizontal = 10.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.tertiary)
            .padding(10.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    contentAlignment = Alignment.Center, modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .size(35.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.show_chart_icon),
                        contentDescription = "Custom Money Icon",
                    )
                }
                Text(
                    text = "Ultimos 7 dias",
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier.padding(start = 10.dp)
                )
            }
            ShimmerEffect(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .padding(
                        start = 10.dp,
                        end = 10.dp,
                        top = 20.dp,
                        bottom = 10.dp
                    )
                    .background(
                        MaterialTheme.colorScheme.tertiary,
                        RoundedCornerShape(10.dp)
                    )
            )
        }
    }
}