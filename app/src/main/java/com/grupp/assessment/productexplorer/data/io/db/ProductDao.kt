package com.grupp.assessment.productexplorer.data.io.db

import androidx.room.Dao
import androidx.room.Insert

@Dao
interface ProductDao {

    @Insert
    fun insert(product: RoomProduct)
}