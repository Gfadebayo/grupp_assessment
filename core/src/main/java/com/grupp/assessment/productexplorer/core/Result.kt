package com.grupp.assessment.productexplorer.core

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
