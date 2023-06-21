package com.example.fetchcodingexercise

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.net.HttpCookie
import java.net.URL
import kotlin.concurrent.thread

class ListViewModel : ViewModel(), DefaultLifecycleObserver {
    /**
     * fetchList will hold the list of items retrieved from https://fetch-hiring.s3.amazonaws.com/hiring.json
     */
    private val _fetchList: MutableLiveData<ArrayList<JsonElement>> =
            MutableLiveData<ArrayList<JsonElement>>()
    internal val fetchList: LiveData<ArrayList<JsonElement>>
        get() = _fetchList

    /**
     * This set will hold all ids currently in use, to ensure no ids are repeated.
     * Note: This is not referring to listIds
     */
    private val _idSet: MutableLiveData<HashSet<Int>> =
        MutableLiveData<HashSet<Int>>()
    internal val idSet: LiveData<HashSet<Int>>
        get() = _idSet

    /*
        TODO: This needs to be checked for loading in or retrieving from storage
     */
    private var jsonData = JSONArray()

    /**
     * This data class is the expected format of the json objects retrieved from the list.
     * Additionally holds data on potential issues with "id" or "name". For sake of brevity in this
     * exercise, only one issue will be held, either with id OR name.
     */
    data class JsonElement(val id: Int, val listId: Int, val name: String?, val issue: Boolean, val msg: String?)

    init {
        _fetchList.value = ArrayList<JsonElement>()
        _idSet.value = HashSet<Int>()
    }

    internal fun sendNetworkRequest() {
        // Network requests must be done on a separate thread from main
        runBlocking {
            // Using Dispatchers.IO due to large read
            val job = launch(Dispatchers.IO) {
                // Get the raw string data to be converted to JSON
                val rawJsonData: String = URL(URL_STR).readText()
                jsonData = JSONArray(rawJsonData)
            }
        }
    }

    internal fun loadData(sharedPrefs: SharedPreferences) {
        if (sharedPrefs.getBoolean(R.bool.data_loaded.toString(), false)) { // Data already paresed
            loadFromFirebase()
        } else { // Data has not been parsed in yet
            sendNetworkRequest()
            parseData()
            Log.d("Giraffe", _fetchList.value!!.toString())
        }
    }

    /**
     * This function takes the data stored in jsonData and validates values, adds the data to the
     * LiveData list, and sorts that list.
     */
    internal fun parseData() {
        // iterate through json array, appending elements into fetchList
        for (i in 0 until jsonData.length()) {
            // unlikely an object here will be null, but check in case
            var currObj: JSONObject?
            try {
                currObj = jsonData.getJSONObject(i)
            } catch (e: JSONException) {
                continue
            }

            // Make sure json object data is valid
            if (!currObj.has("id") || !currObj.has("listId") || !currObj.has("name"))
                continue

            // These three values will create the JsonElement data class object
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

            val name: String? = try {
                currObj.getString("name")
            } catch(e: JSONException) {
                null
            }

            /**
             * names that are null will be brought in for edit by admin, but not display.
             * Need to check that id and listId are valid ints, otherwise, don't add
             */
            if (id == null || listId == null)
                continue

            if (id == 444)
                print("444")

            // Data is valid, look for potential issues
            var issue = false
            var msg: String? = null

            // Check for valid name
            issue = name == null || name.isBlank()
            if (issue) {
                msg = "Empty name field"
            } else {
                // add() returns true if id doesn't exist, so not an issue
                issue = !_idSet.value!!.add(id)
                msg = if (!issue) null else "This id is already in use."
            }

            // Data is valid, add to the list, indicating any possible issues
            val currElem = JsonElement(id, listId, name as String?, issue, msg)
            _fetchList.value!!.add(currElem)
        }

        // All elements are added. Sort now so we aren't sorting every activity call
        _fetchList.value!!.sortWith(compareBy<JsonElement> {it.listId}.thenBy {it.name})
    }

    internal fun updateData(pref: SharedPreferences) {
        val prefEditor = pref.edit()

        prefEditor.putBoolean(R.bool.data_loaded.toString(), true)
        prefEditor.commit()

        saveToFirebase()

        prefEditor.apply()
    }

    internal fun saveToFirebase() {
        // TODO: Save to database
    }

    internal fun loadFromFirebase() {
        // TODO: Load from database
    }

    internal fun bindToActivityLifecycle(mainActivity: MainActivity) {
        mainActivity.lifecycle.addObserver(this)
    }

    // Holds constants such as URL of data location
    companion object {
        const val URL_STR = "https://fetch-hiring.s3.amazonaws.com/hiring.json"
    }
}