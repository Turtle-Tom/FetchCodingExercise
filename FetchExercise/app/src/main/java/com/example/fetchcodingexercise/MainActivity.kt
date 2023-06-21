package com.example.fetchcodingexercise

import android.os.Bundle
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import com.example.fetchcodingexercise.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    // ViewModel that retrieves, parses, sorts, and holds item data
    private val listViewModel by viewModels<ListViewModel>()
    // adapter used for managing the RecyclerView of items
    private lateinit var listAdapter: JsonRecyclerViewAdapter
    // This class variable keeps track of what filters, if any, are currently being applied
    private lateinit var filtersChecked: ArrayList<Int>

    /**
     * Dynamically creates the list of filter options based on what listIds there are. While this
     * easily could have been statically implemented, this implementation allows for changing data.
     * CheckBox views are created for each listId and are added to the LinearLayout named filters.
     * onClickListeners are set so list can be updated with each interaction.
     */
    private fun addFilterBar() {
        // Iterate through listIds stored in ViewModel
        for (i in listViewModel.fetchListIds.value!!) { // Note i is Int stored, not index
            val box = CheckBox(this)
            box.text = "List #$i" // Text to be displayed on screen
            box.setTextColor(getColor(R.color.filter_text))
            box.setOnClickListener { boxChecked(i) }
            binding.filters.addView(box)
        }
    }

    /**
     * Creates and returns a pair of lists, names and colors, used by the ViewHolder in the adapter
     * to populate TextViews detailed in layout/list_item.
     * This is also the function where filters are applied.
     * Note that names and colors are of equal length, and nameList[i] is related to colorList[i].
     */
    private fun getJsonArrayList(): Pair<ArrayList<String>, ArrayList<Int>> {
        val nameList = ArrayList<String>() // Hold text to display
        val colorList = ArrayList<Int>() // Hold colors which indicate header v item
        val modelList = listViewModel.fetchList.value!! // Store sorted list of items
        // While listIds are known, we want this to be scalable, so get num not in listIds
        var listId = listViewModel.fetchListIds.value!!.min().toInt() - 1

        // Iterate from 0 to last index of list
        for (i in 0 until modelList.size) {
            /*
                Do not display and item if
                    It has an issue (bad data, empty name, etc)
                    AND
                    Filters are being applied AND it is not in a selected listId sublist
             */
            if (!modelList[i].issue &&
                (filtersChecked.isEmpty() || filtersChecked.contains(modelList[i].listId))) {
                if (listId != modelList[i].listId) { // New group of listIds encountered
                    listId = modelList[i].listId // Set new current group of listIds
                    nameList.add("List ID # $listId") // Add header to list
                    colorList.add(R.color.grey)
                }
                nameList.add(modelList[i].name.toString())
                colorList.add(R.color.white)
            }
        }

        return Pair(nameList, colorList)
    }

    /**
     * This creates an adapter for our RecyclerView by calling getJsonArrayList() to get the data
     * needed for initialization. Then sets adapter to our RecyclerView named list.
     */
    private fun createAdapter() {
        val lists = getJsonArrayList() // Get pair of lists, names and colors
        listAdapter = JsonRecyclerViewAdapter(lists.first, lists.second).also {
            binding.list.adapter = it
        }
    }

    /**
     * Called upon creation of the main activity. Set binding, set ContentView, connect ViewModel,
     * and generate UI elements/components.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // listViewModel holds data from json file provided for this exercise
        listViewModel.bindToActivityLifecycle(this)
        // listViewModel needs to get the data that will be displayed
        listViewModel.loadData()

        filtersChecked = ArrayList() // Initialize list that stores active filter information

        setSupportActionBar(binding.toolbar)

        addFilterBar()
        createAdapter()

        binding.radioSort.setOnCheckedChangeListener { _, _ ->
            binding.radioChecked()
        }
    }

    /**
     * Checking a filter box causes this function to be called, which updates the filters applied.
     */
    private fun boxChecked(listId: Int) {
        if (filtersChecked.contains(listId)) { // If was a filter, remove as filter
            filtersChecked.remove(listId)
            listAdapter.updateData(getJsonArrayList()) // Update to apply filter changes
        } else { // Was not a filter, so add to filter list
            filtersChecked.add(listId)
            listAdapter.updateData(getJsonArrayList()) // Update to apply filter changes
        }
    }

    /**
     * Selecting a Sort By radio button causes this function to be called, which updates the order
     * in which item names are displayed in the lists.
     */
    private fun ActivityMainBinding.radioChecked() {
        when (radioSort.checkedRadioButtonId) {
            lexBut.id -> {
                listViewModel.resortData(true) // Tell the ViewModel to re-sort lexicographically
            }
            else -> listViewModel.resortData(false) // Tell the ViewModel to re-sort numerically
        }

        listAdapter.updateData(getJsonArrayList()) // Update to display new list order
    }
}