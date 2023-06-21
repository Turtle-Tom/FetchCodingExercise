package com.example.fetchcodingexercise

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.net.URL

class ListViewModel : ViewModel(), DefaultLifecycleObserver {
    /**
     * fetchList will hold the list of items retrieved from
     * https://fetch-hiring.s3.amazonaws.com/hiring.json
     * as well as additional issue info
     */
    private val _fetchList: MutableLiveData<ArrayList<JsonElement>> =
            MutableLiveData<ArrayList<JsonElement>>()
    internal val fetchList: LiveData<ArrayList<JsonElement>>
        get() = _fetchList

    /**
     * This will be used for filtering by list id group. Note lists is used instead of set so we
     * can more easily store the data in sorted order.
     */
    private val _fetchListIds: MutableLiveData<MutableList<Int>> =
        MutableLiveData<MutableList<Int>>()
    internal val fetchListIds: LiveData<MutableList<Int>>
        get() = _fetchListIds

    /**
     * This set will hold all ids currently in use, to ensure no ids are repeated.
     * Note: This is not referring to listIds
     * Duplicate ids are a potential issue for expansions of this project, and it is unknown if
     * any currently exist in the supplied dataset.
     */
    private val idSet = HashSet<Int>()

    /**
     * This will hold the previously raw json string as an array of json elements.
     */
    private var jsonData = JSONArray()

    /**
     * This data class is the expected format of the json objects retrieved from the list.
     * Additionally holds data on potential issues with "id" or "name". For sake of brevity in this
     * exercise, only one issue will be held, either involving id OR name.
     */
    data class JsonElement(val id: Int, val listId: Int, val name: String?, val issue: Boolean, val msg: String?)

    init {
        _fetchList.value = ArrayList()
        _fetchListIds.value = mutableListOf()
    }

    /**
     * Creates a coroutine to request the data from the online JSON file. Data is read in and
     * converted to a JSONArray, stored as a class variable.
     */
    private fun sendNetworkRequest() {
        // Network requests must be done on a separate thread from main
        runBlocking {
            // Using Dispatchers.IO due to potentially large read
            launch(Dispatchers.IO) {
                // Get the raw string data to be converted to JSON
                val rawJsonData: String = URL(URL_STR).readText()
                jsonData = JSONArray(rawJsonData)
            }
        }
    }

    /**
     * Primarily called by the MainActivity, within onCreate(), in order to retrieve and parse the
     * data needed for display.
     */
    internal fun loadData() {
        sendNetworkRequest()
        parseData()
    }

    /**
     * This function takes the data stored in jsonData and validates values, adds the data to the
     * LiveData list, and sorts that list.
     */
    private fun parseData() {
        // iterate through json array, appending elements into fetchList
        for (i in 0 until jsonData.length()) {
            // unlikely an object here will be null, but check in case
            var currObj: JSONObject?
            try {
                currObj = jsonData.getJSONObject(i)
            } catch (e: JSONException) {
                continue // Null object, continue to next item
            }

            // Make sure json object data is valid
            if (!currObj.has("id") || !currObj.has("listId") || !currObj.has("name"))
                continue // Not valid, continue to next item

            // These three values will be used to create the JsonElement data class object
            val id: Int? = try {
                currObj.getInt("id")
            } catch (e: JSONException) {
                null
            }

            val listId: Int? = try {
                currObj.getInt("listId")
            } catch (e: JSONException) {
                null
            }

            var name: String? = try {
                currObj.getString("name")
            } catch(e: JSONException) {
                null
            }

            /**
             * names that are null will be brought in to store, but not display.
             * Need to check that id and listId are valid ints, otherwise, don't add
             */
            if (id == null || listId == null)
                continue

            // Data is valid, look for potential issues
            var issue: Boolean
            var msg: String?

            // Check for valid name
            issue = name == null || name.isBlank() || name == "null"
            if (issue) {
                msg = "Empty name field"
                name = null // Ensure it isn't "null"
            } else {
                // add() returns true if id doesn't exist, so not an issue
                issue = !idSet.add(id)
                msg = if (!issue) null else "This id is already in use."
            }

            // Data is valid, add to the list, indicating any possible issues
            val currElem = JsonElement(id, listId, name, issue, msg)
            _fetchList.value!!.add(currElem)
            // Need to store all listIds for grouping into sublists. Could be done statically as
            // they are known for the exercise, but dynamic is preferred. If listId not known, add
            // to known list (not Set because we want sorted order).
            if (!_fetchListIds.value!!.contains(listId)) {
                _fetchListIds.value!!.add(listId)
            }
        }

        // All elements are added. Sort now so we aren't sorting every activity call
        _fetchList.value!!.sortWith(compareBy<JsonElement> {it.listId}.thenBy {it.name})
        _fetchListIds.value!!.sort()
    }

    /**
     * Read re-sort, this is called if the user decided to change the sort order to/from
     * lexicographical/numerical. The boolean passed in referred to true/false in regard to lex.
     */
    internal fun resortData(lex: Boolean) {
        if (lex) { // User wants lexicographical
            _fetchList.value!!.sortWith(compareBy<JsonElement> {it.listId}.thenBy {it.name})
        } else { // User wants numerical
            _fetchList.value!!.sortWith(compareBy<JsonElement> {it.listId}.thenBy
            {it.name?.substring(5, it.name.length)?.toInt()})
        }
    }

    /**
     * Bind ViewModel's lifecycle with main activity
     */
    internal fun bindToActivityLifecycle(mainActivity: MainActivity) {
        mainActivity.lifecycle.addObserver(this)
    }

    /**
     * Holds constants such as URL of data location
     */
    companion object {
        const val URL_STR = "https://fetch-hiring.s3.amazonaws.com/hiring.json"
    }
}