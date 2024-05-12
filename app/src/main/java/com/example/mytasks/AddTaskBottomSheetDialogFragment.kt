package com.example.mytasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.example.mytasks.database.Task
import com.example.mytasks.database.TaskRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.ArrayAdapter
import android.widget.Spinner
import java.text.SimpleDateFormat
import java.util.Locale

class AddTaskBottomSheetDialogFragment(private val viewModel: MainActivityData,private val repository: TaskRepository) : DialogFragment() {

    private lateinit var taskDateEditText: EditText
    private lateinit var taskTimeEditText: EditText

    private val calendar = Calendar.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_create_task, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cancelButton = view.findViewById<Button>(R.id.cancelTask)

        // Handle cancel button click
        cancelButton.setOnClickListener {
            dismiss() // Dismiss the dialog
        }

        taskDateEditText = view.findViewById(R.id.taskDate)
        taskTimeEditText = view.findViewById(R.id.taskTime)

        taskDateEditText.setOnClickListener { showDatePicker() }
        taskTimeEditText.setOnClickListener { showTimePicker() }

        val addTaskTitle = view.findViewById<EditText>(R.id.addTaskTitle)
        val addTaskDescription = view.findViewById<EditText>(R.id.addTaskDescription)
        //val taskPriority = view.findViewById<EditText>(R.id.taskPriority)
        val saveButton = view.findViewById<Button>(R.id.addTask)

        val taskPriority = view.findViewById<Spinner>(R.id.taskPriority)
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.priority_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            taskPriority.adapter = adapter
        }

        // Handle save button click
        saveButton.setOnClickListener {
            val title = addTaskTitle.text.toString()
            val description = addTaskDescription.text.toString()
            val date = taskDateEditText.text.toString()
            val lastAlarm = taskTimeEditText.text.toString()
            val priority = taskPriority.selectedItem.toString()

            // Create a Task object
            val task = Task(title, date, description, priority = priority, lastAlarm = lastAlarm)

            // Perform save operation using coroutines
            CoroutineScope(Dispatchers.IO).launch {
                // Simulate a delay for demonstration purposes
                // Replace this with your actual save operation
                repository.insertDataIntoTaskList(task)
                delay(1000)

                // Retrieve all tasks again from the repository
                val newData = repository.getAllTasksList()

                // Switch to the main thread to update the UI
                withContext(Dispatchers.Main) {
                    // Update the ViewModel with the new data
                    viewModel.setData(newData)

                    // Dismiss the dialog after saving
                    dismiss()
                }
            }
        }
    }

    private fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, monthOfYear, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, monthOfYear)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDate()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun showTimePicker() {
        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                updateTime()
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        )
        timePickerDialog.show()
    }

    private fun updateDate() {
        val dateFormat = "dd-MM-yyyy"
        val simpleDateFormat = SimpleDateFormat(dateFormat, Locale.getDefault())
        taskDateEditText.setText(simpleDateFormat.format(calendar.time))
    }

    private fun updateTime() {
        val timeFormat = "HH:mm"
        val simpleTimeFormat = SimpleDateFormat(timeFormat, Locale.getDefault())
        taskTimeEditText.setText(simpleTimeFormat.format(calendar.time))
    }
}

