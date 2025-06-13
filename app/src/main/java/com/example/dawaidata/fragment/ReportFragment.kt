package com.example.dawaidata.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dawaidata.R
import com.example.dawaidata.User
import com.example.dawaidata.UserAdapter

class ReportFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var userAdapter: UserAdapter
    private lateinit var userList: List<User>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_report, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Initialize RecyclerView
        recyclerView = view.findViewById(R.id.RecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // 2. Create dummy or real user data
        userList = listOf(
            User("Paracetamol", "2025-06-10"),
            User("Cinex", "2025-06-12"),
            User("Amoxicillin", "2025-06-15")
        )

        // 3. Set up Adapter
        userAdapter = UserAdapter(userList)
        recyclerView.adapter = userAdapter
    }
}
