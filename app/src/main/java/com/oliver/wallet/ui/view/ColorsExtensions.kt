package com.oliver.wallet.ui.view

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun negativeValueColor(value: String?): Color {
    return if (value?.contains("-") == true) {
        MaterialTheme.colorScheme.onError
    } else {
        MaterialTheme.colorScheme.primary
    }
}