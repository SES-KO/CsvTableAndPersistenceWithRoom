package com.sesko.csvtableandpersistencewithroom.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sesko.csvtableandpersistencewithroom.database.shapes.Shape
import com.sesko.csvtableandpersistencewithroom.database.shapes.ShapesDao
import com.sesko.csvtableandpersistencewithroom.utils.CsvUtils
import java.io.InputStream

class ShapesViewModel(private val shapesDao: ShapesDao): ViewModel() {

    fun allShapes(): List<Shape> = shapesDao.getAll()

    fun readCsv(inputStream: InputStream) {
        shapesDao.insertAll(CsvUtils.csvReader(inputStream))
    }
}

class ShapesViewModelFactory(
    private val shapesDao: ShapesDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShapesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ShapesViewModel(shapesDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}