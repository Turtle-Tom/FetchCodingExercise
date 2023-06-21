package com.example.fetchcodingexercise

import android.R
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.fetchcodingexercise.databinding.FragmentListBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class ListFragment : Fragment() {

    private var _binding: FragmentListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    // ViewModel that retrieves, parses, saves, and loads json data
    private val listViewModel by viewModels<ListViewModel>()

    private lateinit var listAdapter: JsonRecyclerViewAdapter

    private fun observeJsonData() {
        listViewModel.fetchList.observe(viewLifecycleOwner) {
            listAdapter.setData(getJsonArrayList())
        }
    }

    private fun getJsonArrayList(): ArrayList<String> {
        val list = listViewModel.fetchList.value!!.filter {
            !it.name.isNullOrBlank()
        }.mapTo(arrayListOf()) {
            it.name.toString()
        }

        return list
    }
    private fun createAdapter() {
        listAdapter = JsonRecyclerViewAdapter(getJsonArrayList()).also {
            binding.jsonList.adapter = it
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentListBinding.inflate(inflater, container, false)

        createAdapter()
        observeJsonData()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}