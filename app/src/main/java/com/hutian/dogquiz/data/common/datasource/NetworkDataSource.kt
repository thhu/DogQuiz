package com.hutian.dogquiz.data.common.datasource

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException
import javax.inject.Inject
import kotlin.coroutines.resumeWithException


class NetworkDataSource @Inject constructor(
    private val okHttpClient: OkHttpClient
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun getJsonString(url: String): String {
        val request = Request.Builder()
            .url(url)
            .build()

        return suspendCancellableCoroutine { continuation ->
            val call = okHttpClient.newCall(request)
            call.enqueue(object : Callback {
                // Called if the request fails due to network issues.
                override fun onFailure(call: Call, e: IOException) {
                    // Don't resume if the coroutine was already cancelled.
                    if (continuation.isCancelled) return
                    continuation.resumeWithException(e)
                }

                // Called when the server responds.
                override fun onResponse(call: Call, response: Response) {
                    if (continuation.isCancelled) return

                    if (!response.isSuccessful) {
                        // Resume with a null value for unsuccessful responses
                        continuation.resumeWithException(IllegalStateException("Request was not successful"))
                        return
                    }

                    val body = response.body.string()
                    continuation.resume(body) { cause, _, _ -> }

                }
            })

            // When the coroutine is cancelled, cancel the OkHttp call.
            continuation.invokeOnCancellation {
                call.cancel()
            }
        }
    }
}