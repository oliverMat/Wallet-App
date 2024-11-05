package com.oliver.wallet.data.network

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


class OnlineMoneyRepositoryTest {

    private lateinit var repository: MoneyRepo

    @Before
    fun setup() {
        repository = mock(MoneyRepo::class.java)
    }


    @Test
    fun getCurrentCoinData_ReturnsSuccess() = runBlocking {
        // GIVEN
        val mockApiResponse = mock(MoneyResponse::class.java)
        val mockResponse = safeApiCall { mockApiResponse }

        // WHEN
        `when`(repository.getPriceOfDay("")).thenReturn(mockResponse)

        // THEN
        val result = repository.getPriceOfDay("")
        assertEquals(mockResponse, result)
    }

    @Test
    fun getCoinDaily_ReturnsSuccess() = runBlocking {
        // GIVEN
        val mockMoneyModel = mock(MoneyModel::class.java)  // Mock de MoneyModel
        val mockList = listOf(mockMoneyModel)
        val mockResponse = safeApiCall { mockList }

        // WHEN
        `when`(repository.getChartForPeriod("", "")).thenReturn(mockResponse)

        // THEN
        val result = repository.getChartForPeriod("", "")
        assertEquals(mockResponse, result)
    }


    @Test
    fun getCurrentCoinData_negativeResponse_HttpException() = runBlocking {

        val errorBody = "{\"message\": [\"Bad Request\"]}".toResponseBody("application/json".toMediaTypeOrNull())
        val httpException = HttpException(Response.error<Any>(404, errorBody))

        `when`(repository.getPriceOfDay("")).thenThrow(httpException)

        val result = safeApiCall {
            repository.getPriceOfDay("")
        }

        assert(result is ResultWrapper.GenericError)
        val genericError = result as ResultWrapper.GenericError
        assertEquals(404, genericError.code)
        assertEquals("Bad Request", genericError.message)
    }

    @Test
    fun getCoinDaily_negativeResponse_HttpException() = runBlocking {

        val errorBody = "{\"message\": [\"Bad Request\"]}".toResponseBody("application/json".toMediaTypeOrNull())
        val httpException = HttpException(Response.error<Any>(404, errorBody))

        `when`(repository.getChartForPeriod("", "")).thenThrow(httpException)

        val result = safeApiCall {
            repository.getChartForPeriod("", "")
        }

        assert(result is ResultWrapper.GenericError)
        val genericError = result as ResultWrapper.GenericError
        assertEquals(404, genericError.code)
        assertEquals("Bad Request", genericError.message)
    }

    @Test
    fun getCurrentCoinData_negativeResponse_GenericError() = runBlocking {

        `when`(repository.getPriceOfDay("")).thenThrow(IllegalStateException())

        val result = safeApiCall {
            repository.getPriceOfDay("")
        }

        assertEquals(ResultWrapper.GenericError(), result)
    }

    @Test
    fun getCoinDaily_negativeResponse_GenericError() = runBlocking {

        `when`(repository.getChartForPeriod("", "")).thenThrow(IllegalStateException())

        val result = safeApiCall {
            repository.getChartForPeriod("", "")
        }

        assertEquals(ResultWrapper.GenericError(), result)
    }

    @Test
    fun getCurrentCoinData_IOException() = runBlocking {

        given(repository.getPriceOfDay("")).willAnswer {
            throw IOException()
        }

        val result = safeApiCall {
            repository.getPriceOfDay("")
        }

        assertEquals(result, ResultWrapper.NetworkError)
    }

    @Test
    fun getCoinDaily_IOException() = runBlocking {

        given(repository.getChartForPeriod("", "")).willAnswer {
            throw IOException()
        }

        val result = safeApiCall {
            repository.getChartForPeriod("", "")
        }

        assertEquals(result, ResultWrapper.NetworkError)
    }
}