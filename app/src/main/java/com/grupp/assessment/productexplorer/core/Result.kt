package com.grupp.assessment.productexplorer.core

import android.os.Bundle
import android.os.Parcelable
import androidx.core.os.BundleCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

sealed interface Result<out R> {

    data object None: Result<Nothing>

    data object Loading: Result<Nothing>

    data class Success<out T>(
        val data: T,
        val status: String = "",
        val message: String = ""
    ) : Result<T>

    sealed interface Error : Result<Nothing>

    data object UnauthenticatedError: Error

    data object NetworkError : Error

    data object UnknownError : Error

    data object TimeoutError : Error

    data class EmptyError(val message: String): Error

    /**
     * Error gotten from the response body
     * It sends the entire result unlike other errors so the receiver will know exactly
     * how to handle it
     * It is very similar to Success, but the fact that it is an error completely changes its usage
     * */
    data class ApiError(
        val title: String,
        val message: String,
        val errorBody: String = "",
        val throwable: Throwable? = null
    ) : Error
}

val <T>Result<T>.Unit: Result<Unit>
    get() = this.transform { kotlin.Unit }

fun <T>Result<T>.unwrap(): T? {
    return if(this is Result.Success) data
    else null
}

fun <From, To>Result<From>.transform(transform: Result.Success<From>.() -> To): Result<To> {
    return when(this) {
        is Result.Success -> Result.Success(transform())

        is Result.Loading -> Result.Loading

        is Result.None -> Result.None

        is Result.Error -> this
    }
}

/**
 * This value will be used to identify the class of a Result, using a predefined int value.
 * This same value will be used to map from a bundle back to it's original class.
 *
 * 1 -> [Result.None]
 *
 * 2 -> [Result.Loading]
 *
 * 3 -> [Result.Success]
 *
 * 4 -> [Result.ApiError]
 *
 * 5 -> [Result.UnauthenticatedError]
 *
 * 6 -> [Result.NetworkError]
 *
 * 7 -> [Result.UnknownError]
 *
 * 8 -> [Result.EmptyError]
 *
 * 9 -> [Result.TimeoutError]
 */
const val TYPE = "bundle_name"
const val DATA = "bundle_data"
const val TITLE = "bundle_title"
const val MESSAGE = "bundle_message"
const val THROWABLE = "bundle_throwable"
const val ERROR_BODY = "bundle_errorbody"

inline fun <reified T: Any>Result<T>.asBundle(
    prefix: String = "",
    transform: (T) -> Any = {
        if(isSuperClassOf<Parcelable, T>()) it
        else {
            Gson().toJson(it)
        }
    }
): Bundle {
    val data = if(this is Result.Success) transform(this.data)
    else ""

    return putIntoBundle(prefix, this, data)
}


/**
 * Represents the result as a bundle
 */
inline fun <reified T: Any>putIntoBundle(prefix: String, result: Result<T>, data: Any): Bundle {
    val type = when(result) {
        is Result.None -> 1
        is Result.Loading -> 2
        is Result.Success -> 3
        is Result.ApiError -> 4
        is Result.UnauthenticatedError -> 5
        is Result.NetworkError -> 6
        is Result.UnknownError -> 7
        is Result.EmptyError -> 8
        is Result.TimeoutError -> 9
    }

    return Bundle().apply {
        putInt("$prefix$TYPE", type)

        if(result is Result.Success) {
            if(isSuperClassOf<Parcelable, T>()) putParcelable("$prefix$DATA", data as Parcelable)
            else putString("$prefix$DATA", data as String)
        }

        val title = if(result is Result.Success) result.status
        else if(result is Result.ApiError) result.title
        else ""

        putString("$prefix$TITLE", title)

        val message = if(result is Result.Success) result.message
        else if(result is Result.ApiError) result.message
        else if(result is Result.EmptyError) result.message
        else ""

        putString("$prefix$MESSAGE", message)

        if(result is Result.ApiError) {
            putSerializable("$prefix$THROWABLE", result.throwable)
            putString("$prefix$THROWABLE", result.errorBody)
        }
    }
}

inline fun <reified T :Any>Bundle.toResult(
    prefix: String = "",
    transform: (String) -> T = {
        val type = object: TypeToken<T>(){}.type
        Gson().fromJson(it, type)
    }
): Result<T> {
    val data: T? = if(containsKey("$prefix$DATA")) {
        if(isSuperClassOf<Parcelable, T>()) {
            BundleCompat.getParcelable(this, "$prefix$DATA", T::class.java)
        }
        else transform(getString("$prefix$DATA")!!)
    }
    else null

    return createFromBundle(prefix, this, data)
}

fun <T>createFromBundle(prefix: String, bundle: Bundle, data: T?): Result<T> {
    return when(bundle.getInt("$prefix$TYPE")) {
        1 -> Result.None
        2 -> Result.Loading
        3 -> {
            val status = bundle.getString("$prefix$TITLE", "")
            val message = bundle.getString("$prefix$MESSAGE", "")

            Result.Success(data = data!!, status = status, message = message)
        }
        4 -> {
            val status = bundle.getString("$prefix$TITLE", "")
            val message = bundle.getString("$prefix$MESSAGE", "")
            val errorBody = bundle.getString("$prefix$ERROR_BODY", "")
            val throwable = BundleCompat.getSerializable(bundle, "$prefix$THROWABLE", Throwable::class.java)

            Result.ApiError(
                title = status,
                message = message,
                throwable = throwable,
                errorBody = errorBody
            )
        }
        5 -> Result.UnauthenticatedError
        6 -> Result.NetworkError
        7 -> Result.UnknownError
        8 -> {
            val message = bundle.getString("$prefix$MESSAGE", "")

            Result.EmptyError(message)
        }
        9 -> Result.TimeoutError

        else -> Result.None
    }
}

fun <T1,T2, R>Result<T1>.merge(other: Result<T2>, transform: (T1?, T2?) -> R): Result<R> {
    return if(this is Result.Success && other is Result.Success) {
        Result.Success(transform(this.data, other.data))
    }
    else if(this is Result.Success && other is Result.None) {
        Result.Success(transform(data, null))
    }
    else if(this is Result.None && other is Result.Success) {
        Result.Success(transform(null, other.data))
    }
    else if(this is Result.Error) this
    else if(other is Result.Error) other
    else if(this is Result.Loading || other is Result.Loading) Result.Loading
    else Result.None
}

operator fun Result<Unit>.plus(other: Result<Unit>): Result<Unit> {
    return this.merge(other) { _, _, -> Unit }
}

inline fun <reified L :Any, reified R :Any> isSubClassOf() = R::class.java.isAssignableFrom(L::class.java)

inline fun <reified L : Any, reified R : Any> isSuperClassOf() = L::class.java.isAssignableFrom(R::class.java)