package com.sesko.csvtableandpersistencewithroom.placeholder

import java.io.InputStream
import java.util.ArrayList

object PlaceholderContent {

    var ITEMS: MutableList<PlaceholderItem> = ArrayList()

    private fun csvReader(inputStream: InputStream): MutableList<PlaceholderItem> {
        val reader = inputStream.bufferedReader()
        val header = reader.readLine()
        return reader.lineSequence()
            .filter { it.isNotBlank() }
            .map {
                val (shape, corners, edges)
                        = it.split(',', ignoreCase = false, limit = 3)
                PlaceholderItem(shape, corners.toInt(), edges.toInt())
            }.toList().toMutableList()
    }

    fun isEmpty(): Boolean {
        return ITEMS.isEmpty()
    }

    fun getEntries(): List<PlaceholderItem> {
        return ITEMS
    }

    fun readCsv(inputStream: InputStream) {
        ITEMS = csvReader(inputStream)
    }

    data class PlaceholderItem(val shape: String,
                               var corners: Int,
                               var edges: Int) {
        override fun toString(): String = shape + "," +
                corners.toString() + "," +
                edges.toString()
    }
}