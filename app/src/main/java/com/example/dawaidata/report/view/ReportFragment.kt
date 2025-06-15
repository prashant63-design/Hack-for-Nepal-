package com.example.dawaidata.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dawaidata.R
import com.example.dawaidata.addmedicineschedule.repository.AddMedicineRepository
import com.example.dawaidata.report.view.MedicineAdapter

class ReportFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private val repository = AddMedicineRepository()
    private lateinit var adapter: MedicineAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_report, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        searchView = view.findViewById(R.id.searchView)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        repository.getMedicineSchedules(
            onSuccess = { list ->
                adapter = MedicineAdapter(list)
                recyclerView.adapter = adapter
                setupSearchListener()
            },
            onFailure = { e ->
                Toast.makeText(requireContext(), "Failed to load: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun setupSearchListener() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false // no action needed
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter(newText.orEmpty())
                return true
            }
        })
    }
}
