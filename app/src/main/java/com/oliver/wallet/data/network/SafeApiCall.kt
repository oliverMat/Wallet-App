package com.oliver.wallet.data.network

import com.google.gson.Gson
import com.google.gson.JsonObject
import retrofit2.HttpException
import java.io.IOException

sealed class ResultWrapper<out T> {
    data class Success<out T>(val value: T) : ResultWrapper<T>()
    data class GenericError(val code: Int? = null, val message: String? = null) : ResultWrapper<Nothing>()
    data object NetworkError : ResultWrapper<Nothing>()
}

suspend fun <T> safeApiCall(apiCall: suspend () -> T): ResultWrapper<T> {
    return try {
        ResultWrapper.Success(apiCall.invoke())
    } catch (throwable: Throwable) {
        throwable.printStackTrace()
        when (throwable) {
            is IOException -> ResultWrapper.NetworkError
            is HttpException -> {
                val code = throwable.code()
                val message = messageErrorBody(throwable)
                ResultWrapper.GenericError(code, message)
            }
            else -> {
                ResultWrapper.GenericError(null, null)
            }
        }
    }
}

private fun messageErrorBody(throwable: HttpException): String? {
    return try {
        throwable.response()?.errorBody()?.string()?.let {
            val gson = Gson()
            val jsonObject = gson.fromJson(it, JsonObject::class.java)

            when {
                jsonObject.has("message") -> jsonObject.get("message").asString
                else -> "Mensagem de erro desconhecida"
            }
        }
    } catch (exception: Exception) {
        null
    }
}
