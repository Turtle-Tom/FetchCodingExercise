package com.example.fetchcodingexercise

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.fetchcodingexercise.databinding.ListItemBinding

class JsonRecyclerViewAdapter (
    private var names: ArrayList<String>,
    private var colors: ArrayList<Int>
) : RecyclerView.Adapter<JsonRecyclerViewAdapter.JsonViewHolder>() {

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

    override fun onBindViewHolder(holder: JsonViewHolder, position: Int) {
        holder.name.text = names[position]
        holder.name.setBackgroundResource(colors[position])
    }

    override fun getItemCount(): Int = names.size

    internal fun updateData(lists: Pair<ArrayList<String>, ArrayList<Int>>) {
        names = lists.first
        colors = lists.second
        notifyDataSetChanged()
    }

    inner class JsonViewHolder(binding: ListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val name: TextView = binding.textView

        init {
            itemView.setOnClickListener {
                // Display a Toast message indicting the selected item
                Toast.makeText(
                    binding.root.context,
                    name.text,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}