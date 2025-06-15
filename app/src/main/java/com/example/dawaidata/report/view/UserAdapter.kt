package com.example.dawaidata.report.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dawaidata.R
import com.example.dawaidata.addmedicineschedule.model.MedicineSchedule

class MedicineAdapter(
    private val fullList: List<MedicineSchedule>
) : RecyclerView.Adapter<MedicineAdapter.MedicineViewHolder>() {

    private val filteredList = fullList.toMutableList()

    class MedicineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameText: TextView = itemView.findViewById(R.id.nameText)
        val medicineCountText: TextView = itemView.findViewById(R.id.medicineCountText)
        val timeText: TextView = itemView.findViewById(R.id.timeText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicineViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_item, parent, false)
        return MedicineViewHolder(view)
    }

    override fun onBindViewHolder(holder: MedicineViewHolder, position: Int) {
        val medicine = filteredList[position]
        holder.nameText.text = medicine.medicineName
        holder.medicineCountText.text = "Medicines per day: ${medicine.medicinesPerDay}"
        holder.timeText.text = "Time: ${medicine.selectedTime}"
    }

    override fun getItemCount(): Int = filteredList.size

    // Filter function for searching by medicineName
    fun filter(query: String) {
        filteredList.clear()
        if (query.isEmpty()) {
            filteredList.addAll(fullList)
        } else {
            val lowerCaseQuery = query.lowercase()
            filteredList.addAll(
                fullList.filter {
                    it.medicineName.lowercase().contains(lowerCaseQuery)
                }
            )
        }
        notifyDataSetChanged()
    }
}
