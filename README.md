# CsvTableAndPersistenceWithRoom
Android Kotlin example to import data from csv, persist via Room and Flow, display in a table and export back to csv

Setting up the project
======================
In Android Studio create a new project from template "Basic Activity (Material3)".

Our table in this example needs four columns. Let's choose ```New->Fragement->Fragment (List)``` and select "3" at column count.

To exchange the original "first_fragment" to the just added "item_fragment", change "first" to "item" in ```nav_graph.xml``` id ```FirstFragment```:
```xml
    <fragment
        android:id="@+id/FirstFragment"
        android:name="com.sesko.csvtableandpersistencewithroom.ItemFragment"
        android:label="@string/first_fragment_label"
        tools:layout="@layout/fragment_item">
```

THIS PROJECT IS STILL WORK IN PROGRESS!
