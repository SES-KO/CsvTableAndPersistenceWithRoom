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

Code the data object
----------------------
Change the content of `PlaceholderContent.kt` to

```kotlin
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import io.blackmo18.kotlin.grass.dsl.grass
import java.io.InputStream
import java.util.ArrayList

object PlaceholderContent {

    var ITEMS: MutableList<Entry> = ArrayList()

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

```kotlin
    private fun readStrictCsv(inputStream: InputStream) {
        val csvContents = csvReader().readAllWithHeader(inputStream)
        db = grass<Entry>().harvest(csvContents) as MutableList<Entry>
    }
```

to fill the data object with the content from a csv file.
We will see later how this is used.

Adapt the content of `MyItemRecycleViewAdapter.kt` to

```kotlin
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.shapeView.text = item.shape
        holder.cornersView.text = item.corners.toString()
        holder.edgesView.text = item.edges.toString()
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val shapeView: TextView = binding.shape
        val cornersView: TextView = binding.corners
        val edgesView: TextView = binding.edges

        override fun toString(): String {
            return super.toString() + " '" + shapeView.text + "," +
                    cornersView.text + "," + edgesView.text + "'"
        }
    }
```

Change the content of `ItemFragment.kt` to

```kotlin
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:orientation="horizontal">

    <TextView
        android:id="@+id/shape"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_margin="@dimen/text_margin"
        android:textAppearance="?attr/textAppearanceListItem" />

    <TextView
        android:id="@+id/corners"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_margin="@dimen/text_margin"
        android:textAppearance="?attr/textAppearanceListItem" />

    <TextView
        android:id="@+id/edges"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_margin="@dimen/text_margin"
        android:textAppearance="?attr/textAppearanceListItem" />
</LinearLayout>
```

`MainActivity.kt`:

```kotlin
...
import android.net.Uri
import com.sesko.csvtableandpersistencewithroom.placeholder.PlaceholderContent
import java.io.File
...
        private var csvFileName: File = File(Environment.getExternalStorageDirectory(), 
        "Download/shapes.csv")
...
    companion object {
        val content: PlaceholderContent = PlaceholderContent
    }
...
    private fun readContentFromCsv() {
        val uri: Uri = Uri.fromFile(csvFileName)
        val csvInputStream = getApplicationContext().getContentResolver().openInputStream(uri)!!
        content.readFromCsv(csvInputStream)
    }
...
```

TODO: bind readContentFromCsv() with action

THIS PROJECT IS STILL WORK IN PROGRESS!
