package com.sesko.csvtableandpersistencewithroom.utils

import com.sesko.csvtableandpersistencewithroom.database.shapes.Shape
import java.io.InputStream

class CsvUtils {

    companion object {
        fun csvReader(inputStream: InputStream): List<Shape> {
            val reader = inputStream.bufferedReader()
            val header = reader.readLine()
            var id = -1
            return reader.lineSequence()
                .filter { it.isNotBlank() }
                .map {
                    val (shape, corners, edges)
                            = it.split(',', ignoreCase = false, limit = 3)
                    id += 1
                    Shape(id, shape, corners.toInt(), edges.toInt())
                }.toList()
        }
    }
}