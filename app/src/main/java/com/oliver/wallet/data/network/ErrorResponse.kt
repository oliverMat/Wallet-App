package com.oliver.wallet.data.network

data class ErrorResponse(
    val errorDescription: String,
    val causes: Map<String, String> = emptyMap()
)
