package com.grupp.assessment.productexplorer.data.io.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RoomProduct(
    @PrimaryKey val id: String
)
