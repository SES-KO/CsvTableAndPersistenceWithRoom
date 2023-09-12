# CsvTableAndPersistenceWithRoom
Android Kotlin example to import data from csv, persist via Room and Flow, display in a table and export back to csv

Setting up the project
======================
In Android Studio create a new project from template "Basic Activity (Material3)".

Our table in this example needs three columns. Let's choose ```New->Fragement->Fragment (List)``` and select "3" at column count.

To exchange the original "first_fragment" to the just added "item_fragment", change "first" to "item" in ```nav_graph.xml``` id ```FirstFragment```:
```xml
    <fragment
        android:id="@+id/FirstFragment"
        android:name="com.sesko.csvtableandpersistencewithroom.ItemFragment"
        android:label="@string/first_fragment_label"
        tools:layout="@layout/fragment_item">
```

If you build and run this code you will see this screen

<img src="https://github.com/SES-KO/CsvTableAndPersistenceWithRoom/blob/master/images/three_columns_0.png" width="128"/>

Load content from csv
=====================
Now, let's load the table content from a csv file.

Prepare the csv example file
----------------------------
This is our example table data:

|shape|corners|edges|
|-----|-------|-----|
|rectangle|4|4|
|circle|0|0|
|triangle|3|3|
|pentagon|5|5|
|hexagon|6|6|
|octagon|8|8|
|cube|8|12|
|cylinder|0|2|
|pyramid|5|8|
|cone|1|1|
|sphere|0|0|

Save the table content in csv format to a file `shapes.csv`:
```
shape,corners,edges
rectangle,4,4
circle,0,0
triangle,3,3
pentagon,5,5
hexagon,6,6
octagon,8,8
cube,8,12
cylinder,0,2
pyramid,5,8
cone,1,1
sphere,0,0
```

and upload this file to your Android device. Easiest way is to use drag&drop via the Device File Explorer in Android Studio.

Create the data object
----------------------
Change the content of `PlaceholderContent.kt` to

```kotlin
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
```

This object uses a csv reader class from `doyaaaaaken` combined with `grass`.
In `build.gradle` (Module :app) add the following dependencies:

```kotlin
    // doyaaaaaken's kotlin-csv
    implementation("com.github.doyaaaaaken:kotlin-csv-jvm:1.7.0")
    // kotlin-grass
    implementation("io.github.blackmo18:kotlin-grass-core-jvm:1.0.0")
    implementation("io.github.blackmo18:kotlin-grass-parser-jvm:0.8.0")
```

Please note the function
```kotlin
    fun readFromCsv(inputStream: InputStream) {
        readStrictCsv(inputStream)
    }
```

which calls

´´´kotlin
    private fun readStrictCsv(inputStream: InputStream) {
        val csvContents = csvReader().readAllWithHeader(inputStream)
        db = grass<Entry>().harvest(csvContents) as MutableList<Entry>
    }
´´´

to fill the data object with the content from a csv file.
We will see later how this is used.


THIS PROJECT IS STILL WORK IN PROGRESS!
