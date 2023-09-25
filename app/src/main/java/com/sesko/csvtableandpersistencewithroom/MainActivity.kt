package com.sesko.csvtableandpersistencewithroom

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import com.sesko.csvtableandpersistencewithroom.placeholder.PlaceholderContent
import com.sesko.csvtableandpersistencewithroom.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private var csvFileName: File = File(Environment.getExternalStorageDirectory(),
        "Download/shapes.csv")

    private val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: Int = 1

    companion object {
        val content: PlaceholderContent = PlaceholderContent
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener {
            readContentFromCsv()
        }

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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    private fun readContentFromCsv() {
        println("Reading from csv file...")
        val uri: Uri = Uri.fromFile(csvFileName)
        val csvInputStream = getApplicationContext().getContentResolver().openInputStream(uri)!!
        content.readFromCsv(csvInputStream)
        println("...reading completed.")
    }
}