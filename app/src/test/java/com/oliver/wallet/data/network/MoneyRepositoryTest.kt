package com.oliver.wallet.data.network

import com.oliver.wallet.data.model.MoneyModel
import com.oliver.wallet.data.model.MoneyResponse
import com.oliver.wallet.data.network.money.MoneyRepository
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException


class MoneyRepositoryTest {

    private lateinit var repository: MoneyRepository

    @Before
    fun setup() {
        repository = mock(MoneyRepository::class.java)
    }


    @Test
    fun getCurrentCoinData_ReturnsSuccess() = runBlocking {
        // GIVEN
        val mockApiResponse = mock(MoneyResponse::class.java)
        val mockResponse = safeApiCall { mockApiResponse }

        // WHEN
        `when`(repository.getCurrentCoinData("")).thenReturn(mockResponse)

        // THEN
        val result = repository.getCurrentCoinData("")
        assertEquals(mockResponse, result)
    }

    @Test
    fun getCoinDailyFixedDate_ReturnsSuccess() = runBlocking {
        // GIVEN
        val mockMoneyModel = mock(MoneyModel::class.java)  // Mock de MoneyModel
        val mockList = listOf(mockMoneyModel)
        val mockResponse = safeApiCall { mockList }

        // WHEN
        `when`(repository.getCoinDailyFixedDate("")).thenReturn(mockResponse)

        // THEN
        val result = repository.getCoinDailyFixedDate("")
        assertEquals(mockResponse, result)
    }


    @Test
    fun getCurrentCoinData_negativeResponse_HttpException() = runBlocking {

        val errorBody = "{\"message\": [\"Bad Request\"]}".toResponseBody("application/json".toMediaTypeOrNull())
        val httpException = HttpException(Response.error<Any>(404, errorBody))

        `when`(repository.getCurrentCoinData("")).thenThrow(httpException)

        val result = safeApiCall {
            repository.getCurrentCoinData("")
        }

        assert(result is ResultWrapper.GenericError)
        val genericError = result as ResultWrapper.GenericError
        assertEquals(404, genericError.code)
        assertEquals("Bad Request", genericError.message)
    }

    @Test
    fun getCoinDailyFixedDate_negativeResponse_HttpException() = runBlocking {

        val errorBody = "{\"message\": [\"Bad Request\"]}".toResponseBody("application/json".toMediaTypeOrNull())
        val httpException = HttpException(Response.error<Any>(404, errorBody))

        `when`(repository.getCoinDailyFixedDate("")).thenThrow(httpException)

        val result = safeApiCall {
            repository.getCoinDailyFixedDate("")
        }

        assert(result is ResultWrapper.GenericError)
        val genericError = result as ResultWrapper.GenericError
        assertEquals(404, genericError.code)
        assertEquals("Bad Request", genericError.message)
    }

    @Test
    fun getCurrentCoinData_negativeResponse_GenericError() = runBlocking {

        `when`(repository.getCurrentCoinData("")).thenThrow(IllegalStateException())

        val result = safeApiCall {
            repository.getCurrentCoinData("")
        }

        assertEquals(ResultWrapper.GenericError(), result)
    }

    @Test
    fun getCoinDailyFixedDate_negativeResponse_GenericError() = runBlocking {

        `when`(repository.getCoinDailyFixedDate("")).thenThrow(IllegalStateException())

        val result = safeApiCall {
            repository.getCoinDailyFixedDate("")
        }

        assertEquals(ResultWrapper.GenericError(), result)
    }

    @Test
    fun getCurrentCoinData_IOException() = runBlocking {

        given(repository.getCurrentCoinData("")).willAnswer {
            throw IOException()
        }

        val result = safeApiCall {
            repository.getCurrentCoinData("")
        }

        assertEquals(result, ResultWrapper.NetworkError)
    }

    @Test
    fun getCoinDailyFixedDate_IOException() = runBlocking {

        given(repository.getCoinDailyFixedDate("")).willAnswer {
            throw IOException()
        }

        val result = safeApiCall {
            repository.getCoinDailyFixedDate("")
        }

        assertEquals(result, ResultWrapper.NetworkError)
    }
}