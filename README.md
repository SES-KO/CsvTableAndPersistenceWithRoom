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

Create the entity
-----------------
Create a new package `database`.
Inside `database` create a new package called `shapes`.
Add a kotlin file with name `Shape.kt` in `database.shapes` with the following content

```kotlin
data class Shape(
    val id: Int,
    val shape: String,
    val corners: Int,
    val edges: Int
)
```

Create the CSV methods
----------------------
Create a new package `utils` and inside a new kotlin class `CsvUtils` with the following content:

```kotlin
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
```

Update the data content
-----------------------
Change the content of `PlaceholderContent.kt` to

```kotlin
object PlaceholderContent {

    var shapes: List<Shape> = ArrayList()

    fun readCsv(inputStream: InputStream) {
        shapes = CsvUtils.csvReader(inputStream)
    }

}
```

Since we have changed the variable name `ITEMS` to `shapes`, we must correct all occurances. Correct the line in `ItemFragment.kt` as follows:

```kotlin
    override fun onCreateView(
        ...
            adapter = MyItemRecyclerViewAdapter(PlaceholderContent.shapes)
      }
        return view
    }
```

Change the view adapter
-----------------------
Adapt the content of `MyItemRecycleViewAdapter.kt` to

```kotlin
class MyItemRecyclerViewAdapter(
    private val values: List<Shape>
) : RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder>() {
   ...

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

Change the content of `fragment_item.xml` to

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

Add csv reading action
----------------------
Define the csv filename and bind csv reading to the floating button `fab` in `MainActivity.kt`:

```kotlin
...
import android.net.Uri
import com.sesko.csvtableandpersistencewithroom.placeholder.PlaceholderContent
import java.io.File
...
    companion object {
        val shapes: PlaceholderContent = PlaceholderContent
        val csvFileName: File = File(
            Environment.getExternalStorageDirectory(),
            "Download/shapes.csv"
        )
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
        val csvInputStream = activity?.contentResolver?.openInputStream(uri)!!
        shapes.readCsv(csvInputStream)
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

The reason ist, that we have changed the item view to a table row view, but still use the ItemFragment in the original manner.
In `ItemFragment.kt` simply change the `GridLayoutManager` to `LinearLayoutManager`.
In addition, `DividerItemDecoration` is used to separate each row with a thin line.

```kotlin
    override fun onCreateView(
        ...

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                view.addItemDecoration(DividerItemDecoration(context,
                    DividerItemDecoration.VERTICAL))
                layoutManager = LinearLayoutManager(context)
                adapter = MyItemRecyclerViewAdapter(PlaceholderContent.shapes)
            }
        }
        return view
    }
```

It looks more like a table, but the columns are not aligned, now:

<img src="https://github.com/SES-KO/CsvTableAndPersistenceWithRoom/blob/master/images/three_columns_2.png" width="128"/>

This can be achieved by wrapping each `TextView` in `fragment_item.xml` into a `LinearLayout` 
and changing `android:layout_width="match_content"` to `android:layout_width="match_parent"` to spread the table to the width of the fragment.
The `fragment_item.xml` content becomes:

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="horizontal">

    <LinearLayout
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_weight="0.33"
        android:orientation="vertical"
        android:gravity="center_horizontal">
        <TextView
            android:id="@+id/shape"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_margin="@dimen/text_margin"
            android:textAppearance="?attr/textAppearanceListItem" />
    </LinearLayout>

    <LinearLayout
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_weight="0.33"
        android:orientation="vertical"
        android:gravity="center_horizontal">
        <TextView
            android:id="@+id/corners"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_margin="@dimen/text_margin"
            android:textAppearance="?attr/textAppearanceListItem" />
    </LinearLayout>

    <LinearLayout
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_weight="0.33"
        android:orientation="vertical"
        android:gravity="center_horizontal">
        <TextView
            android:id="@+id/edges"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_margin="@dimen/text_margin"
            android:textAppearance="?attr/textAppearanceListItem" />
    </LinearLayout>
</LinearLayout>
```

<img src="https://github.com/SES-KO/CsvTableAndPersistenceWithRoom/blob/master/images/three_columns_3.png" width="128"/>

Adding the table header row
---------------------------

The header of the table columns are missing. This is how to add them to the table view.

Change the `fragment_item_list.xml` to:

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical">

    <LinearLayout
        android:id="@+id/table_header"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal">

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_weight="0.33"
            android:orientation="vertical"
            android:gravity="center_horizontal"
            android:layout_margin="@dimen/text_margin"
            android:text="Shape"
            android:textSize="16sp"
            android:textAppearance="?attr/textAppearanceListItem" />

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_weight="0.33"
            android:orientation="vertical"
            android:gravity="center_horizontal"
            android:layout_margin="@dimen/text_margin"
            android:text="Corners"
            android:textSize="16sp"
            android:textAppearance="?attr/textAppearanceListItem" />

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_weight="0.33"
            android:orientation="vertical"
            android:gravity="center_horizontal"
            android:layout_margin="@dimen/text_margin"
            android:text="Edges"
            android:textSize="16sp"
            android:textAppearance="?attr/textAppearanceListItem" />
    </LinearLayout>

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/recycler_view"
        android:name="com.sesko.csvtableandpersistencewithroom.ItemFragment"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_marginLeft="16dp"
        android:layout_marginRight="16dp"/>
</LinearLayout>
```

and `ItemFragment.kt` to:

```kotlin
class ItemFragment : Fragment() {

    private var _binding: FragmentItemListBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView

    ...

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentItemListBinding.inflate(inflater, container, false)
        val view = binding.root
        recyclerView = binding.recyclerView

        // Set the adapter
        with(recyclerView) {
            recyclerView.addItemDecoration(
                DividerItemDecoration(
                    context,
                    DividerItemDecoration.VERTICAL
                )
            )
            layoutManager = LinearLayoutManager(context)
            adapter = MyItemRecyclerViewAdapter(PlaceholderContent.shapes)
        }
        return view
    }
    ...
```


<img src="https://github.com/SES-KO/CsvTableAndPersistenceWithRoom/blob/master/images/three_columns_4.png" width="128"/>

Adding persistance with Room
============================

So far, the data is lost when the application is closed.
Room is an Android ORM (Object Relational Mapping) database as an abstraction layer over SQLlite.

Add the Room version to the project-level `build.grade` file and enable `ksp` (Kotlin Symbol Processing):

```kotlin
plugins {
    ...
    id 'com.google.devtools.ksp' version '1.8.0-1.0.8' apply false
}
ext {
   room_version = '2.5.2'
}
```

Add the dependencies to the module-level `build.grade` file:
```kotlin
plugins {
    ...
    id 'com.google.devtools.ksp'
}
...
dependencies {
    ...
    implementation "androidx.room:room-runtime:$room_version"
    ksp("androidx.room:room-compiler:$room_version")
    
    // optional - Kotlin Extensions and Coroutines support for Room
    implementation "androidx.room:room-ktx:$room_version"
}
```

Room works with annotations. Add them to the entity `Shape.kt`:

```kotlin
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Shape(
    @PrimaryKey val id: Int,
    @NonNull @ColumnInfo(name = "shape") val shape: String,
    @NonNull @ColumnInfo(name = "corners") val corners: Int,
    @NonNull @ColumnInfo(name = "edges") val edges: Int
)
```

DAO (Data Access Object) `ShapesDao.kt`:
```kotlin
@Dao
interface ShapesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(shapes: List<Shape?>?)
    @Query("SELECT * FROM Shape ORDER BY shape ASC")
    fun getAll(): List<Shape>
}
```

`ShapesViewModel.kt`:

```kotlin
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
```

`AppDatabase.kt`:

```kotlin
@Database(entities = arrayOf(Shape::class), version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun shapesDao(): ShapesDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "shapes_db")
                    .allowMainThreadQueries()
                    .build()
                INSTANCE = instance

                instance
            }
        }
    }
}
```

`ShapesApplication.kt`:

```kotlin
class ShapesApplication : Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
}
```

`AndroidMainifest.xml`:

```xml
        android:name="com.sesko.csvtableandpersistencewithroom.ShapesApplication"
```

`ItemFragment.kt`:

```kotlin
...
    private val viewModel: ShapesViewModel by activityViewModels {
        ShapesViewModelFactory(
            (activity?.application as ShapesApplication).database.shapesDao()
        )
    }
...
   override fun onCreateView(
       ...
           adapter = MyItemRecyclerViewAdapter(viewModel.allShapes())
        }
        return view
    }
...
```

Moving floating action button to ItemFragment.kt:

From `activity_main.xml` move the following code lines to the end of `fragment_item_list.xml`:

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

Because this shall be a floating button, the outer `LinearLayout` must be wrapped by a CoordinatorLayout in `fragment_item_list.xml`:

```xml
<androidx.coordinatorlayout.widget.CoordinatorLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical">

    <LinearLayout
    ...
    </LinearLayout>

    <com.google.android.material.floatingactionbutton.FloatingActionButton
    android:id="@+id/fab"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_gravity="bottom|end"
    android:layout_marginEnd="@dimen/fab_margin"
    android:layout_marginBottom="64dp"
    app:srcCompat="@drawable/baseline_input_24" />

</androidx.coordinatorlayout.widget.CoordinatorLayout>
```

Move the code lines

```kotlin
        binding.fab.setOnClickListener {
            readContentFromCsv()
        }
...
   private fun readContentFromCsv() {
        val uri: Uri = Uri.fromFile(csvFileName)
        val csvInputStream = activity?.contentResolver?.openInputStream(uri)!!
        content.readCsv(csvInputStream)
        refreshCurrentFragment()
    }
```

from `MainActivity.kt` to `ItemFragment.kt`: The button binding goes into the `onCreateView` function and the `readContentFromCsv()` function is modified as follows:

```kotlin
    private fun readContentFromCsv() {
        val uri: Uri = Uri.fromFile(csvFileName)
        val csvInputStream = activity?.contentResolver?.openInputStream(uri)!!
        shapesViewModel.readCsv(csvInputStream)
    }
```

Please note, that we have removed the `refreshCurrentFragment()` function. When using `Room`, there is a more elegant to update the view when the database has changed. It is explained further down how to add `Flow`.

Respond to data changes using Flow
==================================

The app should run without errors, now. But when clicking on the Import button, you will see no effect, because the view is not updated (as we have removed the `refreshCurrentFragment()` function).
But when the app is closed and opened again, then it shows the full table content. So we can see, that the data loading from CSV and the persistence via Room is working well.

`ShapesDao.kt`: Change functions to return a `Flow`:

```kotlin
    fun getAll(): Flow<List<Shape>>
```

Same it is to be done in `ShapesViewModel.kt`:

```kotlin
    fun allShapes(): Flow<List<Shape>> = shapesDao.getAll()
```

Wrap the adapter call in `itemFragment.kt into a lifecycle coroutine as follows:

From

```kotlin
           adapter = MyItemRecyclerViewAdapter(viewModel.allShapes())
```

to

```kotlin
            lifecycle.coroutineScope.launch {
                shapesViewModel.allShapes().collect() {
                    adapter = MyItemRecyclerViewAdapter(it)
                }
            }
```

For testing you can delete the already created database files on the phone emulator by using the device explorer `data/data/<yourAppName>/databases/*` and run the App again.


THIS PROJECT IS STILL WORK IN PROGRESS!
