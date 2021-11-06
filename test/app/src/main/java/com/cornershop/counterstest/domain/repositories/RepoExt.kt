package com.cornershop.counterstest.domain.repositories

import android.util.Log
import com.cornershop.counterstest.core.ApiResult
import com.cornershop.counterstest.core.CacheResult
import com.cornershop.counterstest.core.Util.CACHE_ERROR_TIMEOUT
import com.cornershop.counterstest.core.Util.CACHE_TIMEOUT
import com.cornershop.counterstest.core.Util.NETWORK_ERROR_OFFLINE
import com.cornershop.counterstest.core.Util.NETWORK_ERROR_TIMEOUT
import com.cornershop.counterstest.core.Util.NETWORK_TIMEOUT
import com.cornershop.counterstest.core.Util.UNKNOWN_ERROR
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import okio.IOException
import retrofit2.HttpException
import java.net.UnknownHostException

suspend fun <T> safeApiCall(
    dispatcher: CoroutineDispatcher,
    apiCall: suspend () -> T?
): ApiResult<T?> {
    return withContext(dispatcher) {
        try {
            withTimeout(NETWORK_TIMEOUT) {
                ApiResult.Success(apiCall.invoke())
            }
        } catch (t: Throwable) {
            Log.d("RepoExt", "${t.message}")
            if ("${t.message}".contains("No address associated with hostname", ignoreCase = true)) {
                val code = 500
                ApiResult.GenericError(code, NETWORK_ERROR_OFFLINE)
            } else {
                when (t) {
                    is TimeoutCancellationException -> {
                        val code = 408
                        ApiResult.GenericError(code, NETWORK_ERROR_TIMEOUT)
                    }
                    is IOException -> {
                        ApiResult.NetworkError
                    }
                    is HttpException -> {
                        val code = t.code()
                        Log.d("RepoExt", "$code - $t")
                        val errorRes = convertErrorBody(t)
                        ApiResult.GenericError(
                            code,
                            errorRes
                        )
                    }
                    else -> {
                        ApiResult.GenericError(null, UNKNOWN_ERROR)
                    }
                }
            }
        }
    }
}

suspend fun <T> safeCacheCall(
    dispatcher: CoroutineDispatcher,
    cacheCall: suspend () -> T?
): CacheResult<T?> {
    return withContext(dispatcher) {
        try {
            withTimeout(CACHE_TIMEOUT) {
                CacheResult.Success(cacheCall.invoke())
            }
        } catch (throwable: Throwable) {
            when (throwable) {
                is TimeoutCancellationException -> {
                    CacheResult.GenericError(CACHE_ERROR_TIMEOUT)
                }
                else -> {
                    CacheResult.GenericError(UNKNOWN_ERROR)
                }
            }
        }
    }
}

private fun convertErrorBody(throwable: HttpException): String? {
    return try {
        throwable.response()?.errorBody()?.toString()
    } catch (exception: Exception) {
        UNKNOWN_ERROR
    }
}