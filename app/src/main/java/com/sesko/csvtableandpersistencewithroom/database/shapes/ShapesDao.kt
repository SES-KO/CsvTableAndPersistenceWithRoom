package com.sesko.csvtableandpersistencewithroom.database.shapes

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface ShapesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(shapes: List<Shape?>?)
    @Query("SELECT * FROM Shape ORDER BY shape ASC")
    fun getAll(): List<Shape>
}
