package com.example.dawaidata.addmedicineschedule.model

data class MedicineSchedule(
    val medicineName: String = "",
    val numberOfDays: Int = 0,
    val medicinesPerDay: String = "",
    val selectedTime: String = ""
)
