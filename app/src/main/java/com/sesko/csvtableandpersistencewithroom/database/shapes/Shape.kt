package com.sesko.csvtableandpersistencewithroom.database.shapes

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Shape(
    @PrimaryKey val id: Int,
    @NonNull @ColumnInfo(name = "shape") val shape: String,
    @NonNull @ColumnInfo(name = "corners") val corners: Int,
    @NonNull @ColumnInfo(name = "edges") val edges: Int
)
