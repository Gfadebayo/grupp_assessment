package com.grupp.assessment.productexplorer.data.network

import okhttp3.Request
import org.json.JSONObject
import org.json.JSONTokener
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import com.grupp.assessment.productexplorer.core.Result
import okio.Timeout
import retrofit2.Response
import retrofit2.Retrofit
import java.lang.Exception
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException
import timber.log.Timber

class ResultAdapterFactory : CallAdapter.Factory() {

    override fun get(returnType: Type, annotations: Array<Annotation>, retrofit: Retrofit): CallAdapter<Type, Call<Result<Type>>>? {
        return if (getRawType(returnType) == Call::class.java) {
            val callType = getParameterUpperBound(0, returnType as ParameterizedType)

            if (getRawType(callType) == Result::class.java) {
                val resultType = getParameterUpperBound(0, callType as ParameterizedType)

                object: CallAdapter<Type, Call<Result<Type>>> {
                    override fun responseType() = resultType

                    override fun adapt(call: Call<Type>) = ResultCall(call)
                }
            }
            else null
        }
        else null
    }
}

private class ResultCall<T>(proxy: Call<T>) : CallDelegate<T, Result<T>>(proxy) {

    override fun enqueueImpl(callback: Callback<Result<T>>) = proxy.enqueue(object: Callback<T> {
        override fun onResponse(call: Call<T>, response: Response<T>) {
            val result = if (response.isSuccessful) {
                val body = response.body()

                if(body == null) Result.UnknownError
                else Result.Success(body)
            }
            else if(response.code() == 401) Result.UnauthenticatedError
            else {
                val errorString = response.errorBody()?.string()

                if(!errorString.isNullOrEmpty()) {
                    try {
                        val errorJson = JSONTokener(errorString).nextValue() as JSONObject

                        Result.ApiError(
                            title = errorJson.getString("requestStatus"),
                                message = errorJson.getString("responseMessage"),
                                )
                    }
                    catch(e: Exception) {
                        Timber.d(e)
                        Result.UnknownError
                    }
                }
                else Result.UnknownError
            }

            callback.onResponse(this@ResultCall, Response.success(result))
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
            Timber.d(t)
            callback.onResponse(this@ResultCall, Response.success(t.toError))
        }
    })

    override fun cloneImpl() = ResultCall(proxy.clone())

    override fun timeout(): Timeout = proxy.timeout()
}

abstract class CallDelegate<TIn, TOut>(
        protected val proxy: Call<TIn>
) : Call<TOut> {

    override fun execute(): Response<TOut> = throw NotImplementedError()

    final override fun enqueue(callback: Callback<TOut>) = enqueueImpl(callback)

    final override fun clone(): Call<TOut> = cloneImpl()

    override fun cancel() = proxy.cancel()

    override fun request(): Request = proxy.request()

    override fun isExecuted() = proxy.isExecuted

    override fun isCanceled() = proxy.isCanceled

    abstract fun enqueueImpl(callback: Callback<TOut>)

    abstract fun cloneImpl(): Call<TOut>
}

private val Throwable.toError: Result.Error
    get() {
        return when(this) {
            is UnknownHostException, is SocketTimeoutException -> Result.TimeoutError

            is ConnectException, is SSLHandshakeException -> Result.NetworkError

            is SocketException -> Result.TimeoutError

            else -> Result.UnknownError
        }
    }