package com.grupp.assessment.productexplorer.data.io.db

import com.grupp.assessment.productexplorer.domain.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class LocalDataSource(
    private val database: ExplorerDatabase
) {

    suspend fun insert(product: List<RoomProduct>) {
        withContext(Dispatchers.IO) {
            database.productDao.insert(product)
        }
    }

    suspend fun fetchById(id: String): Product {
        return withContext(Dispatchers.IO) {
            database.productDao.byId(id).toProduct()
        }
    }

    fun allProducts(): Flow<List<Product>> {
        return database.productDao.all().map { list -> list.map { it.toProduct() } }
    }
}