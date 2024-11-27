package com.grupp.assessment.productexplorer.data.io.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(product: List<RoomProduct>)

    @Query("SELECT * FROM product WHERE id = :id")
    fun byId(id: String): RoomProduct

    @Query("SELECT * FROM product")
    fun all(): Flow<List<RoomProduct>>
}