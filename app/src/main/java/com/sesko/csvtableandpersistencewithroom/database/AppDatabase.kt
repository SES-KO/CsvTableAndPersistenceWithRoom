package com.sesko.csvtableandpersistencewithroom.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sesko.csvtableandpersistencewithroom.database.shapes.Shape
import com.sesko.csvtableandpersistencewithroom.database.shapes.ShapesDao
import com.sesko.csvtableandpersistencewithroom.placeholder.PlaceholderContent
import com.sesko.csvtableandpersistencewithroom.utils.CsvUtils
import java.io.InputStream

/**
 * Defines a database and specifies data tables that will be used.
 * Version is incremented as new tables/columns are added/removed/changed.
 * You can optionally use this class for one-time setup, such as pre-populating a database.
 */
@Database(entities = arrayOf(Shape::class), version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun shapesDao(): ShapesDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "shapes_db")
                    .allowMainThreadQueries()
                    .build()
                INSTANCE = instance

                instance
            }
        }
    }
}