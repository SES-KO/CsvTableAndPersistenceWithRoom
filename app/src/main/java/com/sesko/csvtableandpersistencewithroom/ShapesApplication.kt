package com.sesko.csvtableandpersistencewithroom

import android.app.Application
import com.sesko.csvtableandpersistencewithroom.database.AppDatabase

class ShapesApplication : Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
}