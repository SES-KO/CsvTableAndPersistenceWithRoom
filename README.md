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

```xml
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

Define the csv filename and bind csv reading to the floating button `fab` in `MainActivity.kt`:

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
    override fun onCreate(savedInstanceState: Bundle?) {
        ...

        binding.fab.setOnClickListener { 
            readContentFromCsv()
        }
    }
...
    private fun readContentFromCsv() {
        val uri: Uri = Uri.fromFile(csvFileName)
        val csvInputStream = getApplicationContext().getContentResolver().openInputStream(uri)!!
        content.readFromCsv(csvInputStream)
    }
...
```

Changing the icon of the floating button
----------------------------------------
The floating button still shows a "mail" icon.
Let's change it to an "input" icon from material design icons db.

In the "Project" browser, right-click and choose "New->Vector Asset".
Click on "Clip art", enter "input" in the search field and select the "input" icon and click "Next" and "Finish".

Change the icon name as follows in `res/layout/activity_main.xml`:
```xml
    <com.google.android.material.floatingactionbutton.FloatingActionButton
        android:id="@+id/fab"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="bottom|end"
        android:layout_marginEnd="@dimen/fab_margin"
        android:layout_marginBottom="16dp"
        app:srcCompat="@drawable/baseline_input_24" />
```

Setting the Android permissions to read from the file system
============================================================

When trying to run the App and clicking on the floating input button, the following error will occur:

```
java.io.FileNotFoundException: /storage/emulated/0/Download/shapes.csv (Permission denied)
```

To solve this, Android permissions to read from the file system must be given

Up to SDC Version 32, the following line to the `AndroidManifest.xml` after `<manifest .../>` will solve this:
```xml
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
```

and the user is requested to allow the access to the file system in `MainActivity.kt`:

```kotlin
    private val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        ...

        appRequestPermissions()
    }

    private fun appRequestPermissions() {
        if (checkSelfPermission(READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {

            if (shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE)) {
                // TODO: show explanation
            }

            requestPermissions(
                arrayOf(READ_EXTERNAL_STORAGE.toString()),
                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

            return;
        }
    }
```

Refreshing the current fragment
-------------------------------

After loading the CSV content, the fragment with the table view must be rebuilt.
This can be done with the following code in `MainActivity.kt`:

```kotlin
   private fun readContentFromCsv() {
        ...
        refreshCurrentFragment()
    }

    private fun refreshCurrentFragment() {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        val id = navController.currentDestination?.id
        navController.popBackStack(id!!,true)
        navController.navigate(id)
    }
```

When running this code, the table view is not satisfying:

<img src="https://github.com/SES-KO/CsvTableAndPersistenceWithRoom/blob/master/images/three_columns_1.png" width="128"/>

The reason ist, that we have changed the item view to a table row view, but still use the RecyclerView in the original manner.
Simply change the `GridLayoutManager` to `LinearLayoutManager`. In addition, `DividerItemDecoration` is used to separate each row with a thin line.

```kotlin
    override fun onCreateView(
        ...

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                view.addItemDecoration(DividerItemDecoration(context,
                    DividerItemDecoration.VERTICAL))
                layoutManager = LinearLayoutManager(context)
                adapter = MyItemRecyclerViewAdapter(PlaceholderContent.ITEMS)
            }
        }
        return view
    }
```

It looks more like a table, but the columns are not aligned, now:

<img src="https://github.com/SES-KO/CsvTableAndPersistenceWithRoom/blob/master/images/three_columns_2.png" width="128"/>


THIS PROJECT IS STILL WORK IN PROGRESS!
