package com.grupp.assessment.productexplorer.data.io.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [RoomProduct::class], version = 1)
abstract class ExplorerDatabase: RoomDatabase() {
    companion object {
        fun build(context: Context): ExplorerDatabase {
            return Room.databaseBuilder(
                context = context,
                klass = ExplorerDatabase::class.java,
                name = "product-explorer.db"
            ).build()
        }
    }
}