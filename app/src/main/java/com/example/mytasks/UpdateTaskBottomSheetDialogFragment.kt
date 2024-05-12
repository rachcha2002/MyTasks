package com.example.mytasks

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.DialogFragment
import com.example.mytasks.database.TaskRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class UpdateTaskBottomSheetDialogFragment(private val taskId: Int, private val repository: TaskRepository, private val viewModel: MainActivityData) : DialogFragment() {
    private lateinit var updateTaskTitle: EditText
    private lateinit var updateTaskDescription: EditText
    private lateinit var updateTaskDate: EditText
    private lateinit var updateTaskTime: EditText
    //private lateinit var updateTaskPriority: EditText
    private lateinit var updateTaskPrioritySpinner:Spinner
    private lateinit var updateTaskButton: Button
    private lateinit var cancelUpdateTaskButton: Button
    private val calendar = Calendar.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_update_task_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateTaskTitle = view.findViewById(R.id.upaddTaskTitle)
        updateTaskDescription = view.findViewById(R.id.upaddTaskDescription)
        updateTaskDate = view.findViewById(R.id.uptaskDate)
        updateTaskTime = view.findViewById(R.id.uptaskTime)
        //updateTaskPriority = view.findViewById<Spinner>(R.id.uptaskPriority)
        updateTaskButton = view.findViewById(R.id.updateTask)
        cancelUpdateTaskButton = view.findViewById(R.id.upcancelTask)
        updateTaskPrioritySpinner = view.findViewById(R.id.uptaskPriority)
        val priorityOptions = resources.getStringArray(R.array.priority_options)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, priorityOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        updateTaskPrioritySpinner.adapter = adapter

        updateTaskDate.setOnClickListener { showDatePicker() }
        updateTaskTime.setOnClickListener { showTimePicker() }

        loadTaskDetails()

        updateTaskButton.setOnClickListener { updateTask() }
        cancelUpdateTaskButton.setOnClickListener { dismiss() }
    }

    private fun loadTaskDetails() {
        CoroutineScope(Dispatchers.Main).launch {
            val task = withContext(Dispatchers.IO) {
                repository.selectDataFromAnId(taskId)
            }
            task.let {
                updateTaskTitle.setText(it.taskTitle)
                updateTaskDescription.setText(it.taskDescription)
                updateTaskDate.setText(it.date)
                updateTaskTime.setText(it.lastAlarm)
                // Find the index of the priority in the priority options array
                val priorityOptions = resources.getStringArray(R.array.priority_options)
                val priorityIndex = priorityOptions.indexOf(it.priority.toString())
                // Set the selection in the Spinner
                updateTaskPrioritySpinner.setSelection(priorityIndex)
            }
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun updateTask() {
        val updatedTitle = updateTaskTitle.text.toString()
        val updatedDescription = updateTaskDescription.text.toString()
        val updatedDate = updateTaskDate.text.toString()
        val updatedTime = updateTaskTime.text.toString()
        val updatedPriority = updateTaskPrioritySpinner.selectedItem.toString()

        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                repository.updateAnExistingRow(taskId, updatedTitle, updatedDescription, updatedDate, updatedTime, updatedPriority)
            }
            val newData = withContext(Dispatchers.IO) {
                repository.getAllTasksList()
            }
            // Update the ViewModel with the new data
            viewModel.setData(newData)
            dismiss()

            // Show success AlertDialog
            showSuccessDialog()
        }
    }

    private fun showSuccessDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Task Updated Successfully")
        builder.setMessage("Your task has been updated successfully.")
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
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
        updateTaskDate.setText(simpleDateFormat.format(calendar.time))
    }

    private fun updateTime() {
        val timeFormat = "HH:mm"
        val simpleTimeFormat = SimpleDateFormat(timeFormat, Locale.getDefault())
        updateTaskTime.setText(simpleTimeFormat.format(calendar.time))
    }

    companion object {
        private const val ARG_TASK_ID = "taskId"

        fun newInstance(taskId: Int, repository: TaskRepository,viewModel: MainActivityData): UpdateTaskBottomSheetDialogFragment {
            val fragment = UpdateTaskBottomSheetDialogFragment(taskId, repository,viewModel)
            val args = Bundle()
            args.putInt(ARG_TASK_ID, taskId)
            fragment.arguments = args
            return fragment
        }
    }

}
