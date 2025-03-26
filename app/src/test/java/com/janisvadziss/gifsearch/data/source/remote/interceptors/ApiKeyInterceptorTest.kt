package com.janisvadziss.gifsearch.data.source.remote.interceptors

import com.janisvadziss.gifsearch.BuildConfig
import junit.framework.TestCase.assertEquals
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.verify
import java.net.HttpURLConnection

class ApiKeyInterceptorTest {

    @Mock
    private lateinit var mockChain: Interceptor.Chain
    private lateinit var interceptor: ApiKeyInterceptor

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        interceptor = ApiKeyInterceptor()
    }

    @Test
    fun addApiKeyParameter() {
        val originalUrl = HttpUrl.Builder()
            .scheme("https")
            .host("api.giphy.com")
            .addPathSegment("v1")
            .addPathSegment("gifs")
            .addPathSegment("trending")
            .build()
        val expectedUrl = originalUrl.newBuilder()
            .addQueryParameter("api_key", BuildConfig.API_KEY)
            .build()
        val originalRequest = Request.Builder()
            .url(originalUrl)
            .build()
        val fakeResponse = Response.Builder()
            .request(originalRequest)
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .body("{}".toResponseBody("application/json".toMediaTypeOrNull()))
            .build()

        `when`(mockChain.request()).thenReturn(originalRequest)
        `when`(mockChain.proceed(any())).thenReturn(fakeResponse)

        val result: Response = interceptor.intercept(mockChain)

        verify(mockChain).request()
        val requestCaptor = argumentCaptor<Request>()
        verify(mockChain).proceed(requestCaptor.capture())
        Assert.assertEquals(expectedUrl, requestCaptor.firstValue.url)
        Assert.assertEquals(fakeResponse, result)
    }
}