package com.example.fetchcodingexercise

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fetchcodingexercise.databinding.ListItemBinding

/**
 * This Adapter manages data relating to an item's name, and sublist headers.
 * A header will have a name associated with its corresponding listId and will be a different
 * color than the items.
 */
class JsonRecyclerViewAdapter (
    private var names: ArrayList<String>,
    private var colors: ArrayList<Int>
) : RecyclerView.Adapter<JsonRecyclerViewAdapter.JsonViewHolder>() {

    /**
     * The ViewHolder manages the different TextViews within the overall RecyclerView
     */
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewtype: Int
    ): JsonViewHolder {
        return JsonViewHolder(
            ListItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    /**
     * Set the data of the ViewHolder by giving it the name and color associated with the list index.
     */
    override fun onBindViewHolder(holder: JsonViewHolder, position: Int) {
        holder.name.text = names[position]
        holder.name.setBackgroundResource(colors[position])
    }

    /**
     * Necessary override so the Adapter knows how many ViewHolders should exist.
     * Either names.size or colors.size would work as they should be equal.
     */
    override fun getItemCount(): Int = names.size

    /**
     * This is called if the user selects to apply a filter or sort the items differently.
     * A new list of data is passed in, which is given to the names and colors lists, then calls
     * the built in function for refreshing display.
     */
    internal fun updateData(lists: Pair<ArrayList<String>, ArrayList<Int>>) {
        names = lists.first
        colors = lists.second
        notifyDataSetChanged()
    }

    /**
     * inner class of ViewHolder describes how to access a TextView via the binding.
     */
    inner class JsonViewHolder(binding: ListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val name: TextView = binding.textView // textView is the id of the TextView layout
    }
}