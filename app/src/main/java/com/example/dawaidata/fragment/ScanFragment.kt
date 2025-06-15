package com.example.dawaidata.fragment

import android.Manifest
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.dawaidata.R
import com.example.dawaidata.addmedicineschedule.model.MedicineSchedule
import com.example.dawaidata.addmedicineschedule.repository.AddMedicineRepository
import com.example.dawaidata.notification.view.MedicineNotificationReceiver
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.*

class ScanFragment : Fragment() {

    private lateinit var medicineEditText: EditText
    private lateinit var daysEditText: EditText
    private lateinit var numberOfMedicine: EditText
    private lateinit var timeEditText: EditText
    private lateinit var submitButton: Button

    private val repository = AddMedicineRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_scan, container, false)
        initializeViews(view)
        setListeners()
        return view
    }

    private fun initializeViews(view: View) {
        medicineEditText = view.findViewById(R.id.medicineEditText)
        daysEditText = view.findViewById(R.id.daysEditText)
        numberOfMedicine = view.findViewById(R.id.numberOfMedicine)
        timeEditText = view.findViewById(R.id.dateEditText)
        submitButton = view.findViewById(R.id.submitButton)
    }

    private fun setListeners() {
        numberOfMedicine.setOnClickListener { showNumberPickerDialog() }
        timeEditText.setOnClickListener { showTimePickerDialog() }
        submitButton.setOnClickListener { submitForm() }
    }

    private fun showNumberPickerDialog() {
        val options = arrayOf("1", "2", "3", "4")
        AlertDialog.Builder(requireContext())
            .setTitle("Select Number per Day")
            .setItems(options) { _, which ->
                numberOfMedicine.setText(options[which])
            }
            .show()
    }

    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(
            requireContext(),
            { _, selectedHour, selectedMinute ->
                val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                timeEditText.setText(formattedTime)
            },
            hour,
            minute,
            true
        ).show()
    }

    private fun submitForm() {
        val name = medicineEditText.text.toString().trim()
        val days = daysEditText.text.toString().trim()
        val perDay = numberOfMedicine.text.toString().trim()
        val time = timeEditText.text.toString().trim()

        if (!isFormValid(name, days, perDay, time)) {
            Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val schedule = MedicineSchedule(
            medicineName = name,
            numberOfDays = days.toInt(),
            medicinesPerDay = perDay,
            selectedTime = time
        )

        repository.addMedicineSchedule(schedule,
            onSuccess = {
                Toast.makeText(context, "Schedule added successfully", Toast.LENGTH_SHORT).show()
                requestExactAlarmPermissionIfNeeded()
                scheduleNotification(name, time, days.toInt())
                clearFields()
                redirectToHome()
            },
            onFailure = { e ->
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        )
    }

    private fun isFormValid(name: String, days: String, perDay: String, time: String): Boolean {
        return name.isNotEmpty() && days.isNotEmpty() && perDay.isNotEmpty() && time.isNotEmpty()
    }

    private fun clearFields() {
        medicineEditText.text.clear()
        daysEditText.text.clear()
        numberOfMedicine.text.clear()
        timeEditText.text.clear()
    }

    private fun redirectToHome() {
        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.selectedItemId = R.id.bottom_nav_home
    }

    private fun requestExactAlarmPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    data = Uri.parse("package:${requireContext().packageName}")
                }
                startActivity(intent)
            }
        }
    }

    private fun scheduleNotification(medicineName: String, time: String, days: Int) {
        val (hour, minute) = time.split(":").map { it.toInt() }

        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        for (i in 0 until days) {
            val calendar = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, i)
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            val intent = Intent(requireContext(), MedicineNotificationReceiver::class.java).apply {
                putExtra("medicineName", medicineName)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                requireContext(),
                (System.currentTimeMillis() + i).toInt(), // Unique ID
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }
}
