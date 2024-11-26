package com.grupp.assessment.productexplorer.data.io.db

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocalDataSource(
    private val database: ExplorerDatabase
) {

    suspend fun insert(product: RoomProduct) {
        withContext(Dispatchers.IO) {
            database.productDao.insert(product)
        }
    }
}