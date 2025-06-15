package com.example.dawaidata.addmedicineschedule.repository

import com.example.dawaidata.addmedicineschedule.model.MedicineSchedule
import com.google.firebase.firestore.FirebaseFirestore

class AddMedicineRepository {
    private val db = FirebaseFirestore.getInstance()

    fun addMedicineSchedule(
        schedule: MedicineSchedule,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("medicineSchedules")
            .add(schedule)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    fun getMedicineSchedules(
        onSuccess: (List<MedicineSchedule>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("medicineSchedules")
            .get()
            .addOnSuccessListener { result ->
                val list = result.mapNotNull { it.toObject(MedicineSchedule::class.java) }
                onSuccess(list)
            }
            .addOnFailureListener { e -> onFailure(e) }
    }
}
