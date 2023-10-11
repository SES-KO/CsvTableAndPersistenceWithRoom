package com.sesko.csvtableandpersistencewithroom

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.DividerItemDecoration
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.sesko.csvtableandpersistencewithroom.databinding.FragmentItemListBinding
import com.sesko.csvtableandpersistencewithroom.viewmodels.ShapesViewModel
import com.sesko.csvtableandpersistencewithroom.viewmodels.ShapesViewModelFactory
import com.sesko.csvtableandpersistencewithroom.MainActivity.Companion.csvFileName

/**
 * A fragment representing a list of Items.
 */
class ItemFragment : Fragment() {

    private var _binding: FragmentItemListBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView

    private var columnCount = 3

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentItemListBinding.inflate(inflater, container, false)
        val view = binding.root
        recyclerView = binding.recyclerView

        binding.fab.setOnClickListener {
            readContentFromCsv()
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
            adapter = MyItemRecyclerViewAdapter(shapesViewModel.allShapes())
        }
        return view
    }

    private fun readContentFromCsv() {
        val uri: Uri = Uri.fromFile(csvFileName)
        val csvInputStream = activity?.contentResolver?.openInputStream(uri)!!
        shapesViewModel.readCsv(csvInputStream)
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