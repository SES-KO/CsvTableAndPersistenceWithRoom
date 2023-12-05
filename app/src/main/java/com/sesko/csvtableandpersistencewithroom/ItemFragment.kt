package com.sesko.csvtableandpersistencewithroom

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.DividerItemDecoration
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.coroutineScope
import com.sesko.csvtableandpersistencewithroom.databinding.FragmentItemListBinding
import com.sesko.csvtableandpersistencewithroom.viewmodels.ShapesViewModel
import com.sesko.csvtableandpersistencewithroom.viewmodels.ShapesViewModelFactory
import com.sesko.csvtableandpersistencewithroom.MainActivity.Companion.csvFileNameDir
import kotlinx.coroutines.launch

/**
 * A fragment representing a list of Items.
 */
class ItemFragment : Fragment() {

    private var _binding: FragmentItemListBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView

    private var columnCount = 3

    // Request code for selecting a CSV file.
    val PICK_CSV_FILE = 2

    private val shapesViewModel: ShapesViewModel by activityViewModels {
        ShapesViewModelFactory(
            (activity?.application as ShapesApplication).database.shapesDao()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentItemListBinding.inflate(inflater, container, false)
        val view = binding.root
        recyclerView = binding.recyclerView

        binding.fab.setOnClickListener {
            openFile(Uri.fromFile(csvFileNameDir))
        }

        // Set the adapter
        with(recyclerView) {
            recyclerView.addItemDecoration(
                DividerItemDecoration(
                    context,
                    DividerItemDecoration.VERTICAL
                )
            )
            layoutManager = LinearLayoutManager(context)
            lifecycle.coroutineScope.launch {
                shapesViewModel.allShapes().collect() {
                    adapter = MyItemRecyclerViewAdapter(it)
                }
            }
        }
        return view
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun openFile(pickerInitialUri: Uri) {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
        }

        startActivityForResult(Intent.createChooser(intent, "Import CSV"), 2)
    }

    override fun onActivityResult(
        requestCode: Int, resultCode: Int, resultData: Intent?) {
        if (requestCode == PICK_CSV_FILE
            && resultCode == Activity.RESULT_OK) {
            resultData?.data?.also { uri ->
                val csvInputStream = activity?.contentResolver?.openInputStream(uri)!!
                shapesViewModel.readCsv(csvInputStream)
            }
        }
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            ItemFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}