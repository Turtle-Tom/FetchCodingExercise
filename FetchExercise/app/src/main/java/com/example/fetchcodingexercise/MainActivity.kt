package com.example.fetchcodingexercise

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.viewModels
import com.example.fetchcodingexercise.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    // ViewModel that retrieves, parses, saves, and loads json data
    private val listViewModel by viewModels<ListViewModel>()
    private lateinit var listAdapter: JsonRecyclerViewAdapter

    private fun getJsonArrayList(): ArrayList<String> {
//        val list = listViewModel.fetchList.value!!.filter {
//            !it.issue
//        }.mapTo(arrayListOf()) {
//            it.name.toString()
//        }

        var list = ArrayList<String>()
        val modelList = listViewModel.fetchList.value!!
        var listId = -1

        for (i in 0 until modelList.size) {
            if (!modelList[i].issue) {
                if (listId != modelList[i].listId) {
                    listId = modelList[i].listId
                    list.add("List ID # $listId")
                }
                list.add(modelList[i].name.toString())
            }
        }

        return list
    }

    private fun createAdapter() {
        listAdapter = JsonRecyclerViewAdapter(getJsonArrayList()).also {
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

        setSupportActionBar(binding.toolbar)

        createAdapter()

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
}