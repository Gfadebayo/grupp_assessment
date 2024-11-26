package com.grupp.assessment.productexplorer.data.network

import com.grupp.assessment.productexplorer.core.Result
import com.grupp.assessment.productexplorer.domain.Product
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext

class RemoteDataSource(
    private val service: ProductService,
    private val networkManager: NetworkManager
) {

    private val scope = CoroutineScope(Dispatchers.IO)

    private var isOnline = networkManager.isOnline.stateIn(
        scope = scope,
        started = SharingStarted.Eagerly,
        initialValue = true
    )

    suspend fun getProducts(): Result<List<Product>> {
        return send {
            service.productList()
        }
    }

    private suspend fun<T> send(command: suspend () -> Result<T>): Result<T> {
        return if(isOnline.value) withContext(Dispatchers.IO) { command() }
        else Result.NetworkError
    }
}