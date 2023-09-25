package com.sesko.csvtableandpersistencewithroom

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.sesko.csvtableandpersistencewithroom.placeholder.PlaceholderContent.PlaceholderItem
import com.sesko.csvtableandpersistencewithroom.databinding.FragmentItemBinding

/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class MyItemRecyclerViewAdapter(
    private val values: List<PlaceholderItem>
) : RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

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

}