package com.sesko.csvtableandpersistencewithroom.placeholder

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import io.blackmo18.kotlin.grass.dsl.grass
import java.io.InputStream
import java.util.ArrayList

object PlaceholderContent {

    var ITEMS: MutableList<PlaceholderItem> = ArrayList()

    @OptIn(ExperimentalStdlibApi::class)
    private fun readStrictCsv(inputStream: InputStream) {
        val csvContents = csvReader().readAllWithHeader(inputStream)
        ITEMS = grass<PlaceholderItem>().harvest(csvContents) as MutableList<PlaceholderItem>
    }

    fun isEmpty(): Boolean {
        return ITEMS.isEmpty()
    }

    fun getEntries(): List<PlaceholderItem> {
        return ITEMS
    }

    fun readFromCsv(inputStream: InputStream) {
        readStrictCsv(inputStream)
    }

    data class PlaceholderItem(val shape: String,
                               var corners: Int,
                               var edges: Int) {
        override fun toString(): String = shape + "," +
                corners.toString() + "," +
                edges.toString()
    }
}