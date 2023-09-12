package com.sesko.csvtableandpersistencewithroom.placeholder

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import io.blackmo18.kotlin.grass.dsl.grass
import java.io.InputStream
import java.util.ArrayList

object PlaceholderContent {

    var db: MutableList<Entry> = ArrayList()

    @OptIn(ExperimentalStdlibApi::class)
    private fun readStrictCsv(inputStream: InputStream) {
        val csvContents = csvReader().readAllWithHeader(inputStream)
        db = grass<Entry>().harvest(csvContents) as MutableList<Entry>
    }

    fun isEmpty(): Boolean {
        return db.isEmpty()
    }

    fun getEntries(): List<Entry> {
        return db
    }

    fun readFromCsv(inputStream: InputStream) {
        readStrictCsv(inputStream)
    }

    data class Entry(val shape: String,
                     var corners: Int,
                     var edges: Int) {
        override fun toString(): String = shape + "," +
                corners.toString() + "," +
                edges.toString()
    }
}