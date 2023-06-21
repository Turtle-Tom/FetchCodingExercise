package com.example.fetchcodingexercise

import android.os.Bundle
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import com.example.fetchcodingexercise.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    // ViewModel that retrieves, parses, saves, and loads json data
    private val listViewModel by viewModels<ListViewModel>()
    private lateinit var listAdapter: JsonRecyclerViewAdapter
    private lateinit var filtersChecked: ArrayList<Int>

    private fun addFilterBar() {
        for (i in listViewModel.fetchListIds.value!!) {
            val box = CheckBox(this)
            box.text = "List #$i"
            box.id = i
            box.setTextColor(getColor(R.color.filter_text))
            box.setOnClickListener { boxChecked(i) }
            binding.filters.addView(box)
        }
    }

    private fun getJsonArrayList(): Pair<ArrayList<String>, ArrayList<Int>> {
        val nameList = ArrayList<String>()
        val colorList = ArrayList<Int>()
        val modelList = listViewModel.fetchList.value!!
        var listId = -1

        for (i in 0 until modelList.size) {
            if (!modelList[i].issue &&
                (filtersChecked.isEmpty() || filtersChecked.contains(modelList[i].listId))) {
                if (listId != modelList[i].listId) {
                    listId = modelList[i].listId
                    nameList.add("List ID # $listId")
                    colorList.add(R.color.grey)
                }
                nameList.add(modelList[i].name.toString())
                colorList.add(R.color.white)
            }
        }

        return Pair(nameList, colorList)
    }

    private fun createAdapter() {
        val lists = getJsonArrayList()
        listAdapter = JsonRecyclerViewAdapter(lists.first, lists.second).also {
            binding.list.adapter = it
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listViewModel.bindToActivityLifecycle(this)
        // ListViewModel needs to get the data that will be displayed
        listViewModel.loadData()

        filtersChecked = ArrayList()

        setSupportActionBar(binding.toolbar)

        addFilterBar()
        createAdapter()

        binding.radioSort.setOnCheckedChangeListener { _, _ ->
            binding.radioChecked()
        }
    }

    /**
     * A button click toggles the checkbox visibility.
     */
    private fun boxChecked(listId: Int) {
        if (filtersChecked.contains(listId)) {
            filtersChecked.remove(listId)
            listAdapter.updateData(getJsonArrayList())
        } else {
            filtersChecked.add(listId)
            listAdapter.updateData(getJsonArrayList())
        }
    }

    private fun ActivityMainBinding.radioChecked() {
        when (radioSort.checkedRadioButtonId) {
            lexBut.id -> {
                listViewModel.resortData(true)
            }
            else -> listViewModel.resortData(false)
        }

        listAdapter.updateData(getJsonArrayList())
    }
}