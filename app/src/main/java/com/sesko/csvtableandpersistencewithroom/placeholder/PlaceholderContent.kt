package com.sesko.csvtableandpersistencewithroom.placeholder

import java.io.InputStream
import java.util.ArrayList
import com.sesko.csvtableandpersistencewithroom.database.shapes.Shape
import com.sesko.csvtableandpersistencewithroom.utils.CsvUtils

object PlaceholderContent {

    var shapes: List<Shape> = ArrayList()

    fun readCsv(inputStream: InputStream) {
        shapes = CsvUtils.csvReader(inputStream)
    }

}