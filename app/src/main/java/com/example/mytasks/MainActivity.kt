package com.example.mytasks

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.DatePicker
import android.widget.EditText
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mytasks.adapters.TaskAdapter
import com.example.mytasks.adapters.TaskItemClickListener
import com.example.mytasks.database.Task
import com.example.mytasks.database.TaskDatabase
import com.example.mytasks.database.TaskRepository
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener{

    private lateinit var adapter: TaskAdapter
    private lateinit var viewModel: MainActivityData
    private lateinit var repository: TaskRepository
    private lateinit var searchView: SearchView  // Declare searchView as a class-level variable

    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView: RecyclerView = findViewById(R.id.taskRecycler)
        repository = TaskRepository(TaskDatabase.getInstance(this))
        viewModel = ViewModelProvider(this)[MainActivityData::class.java]

        searchView = findViewById(R.id.searchView)  // Initialize searchView

        // Set a click listener for the SearchView
        //searchView.setOnClickListener { showDatePicker() }
        searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                showDatePicker()
            }else {
                CoroutineScope(Dispatchers.IO).launch {
                    val allTasks = repository.getAllTasksList()

                    // Update the filtered tasks in the ViewModel
                    viewModel.setData(allTasks)
                }
            }
        }


        // Set a listener for the SearchView
        /*searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })*/

        // Fetch initial data from the database
        CoroutineScope(Dispatchers.IO).launch {
            val initialData = repository.getAllTasksList()
            viewModel.setData(initialData)
        }

        viewModel.data.observe(this) {
            adapter = TaskAdapter(it, repository, viewModel,supportFragmentManager)
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(this)
        }

        val fabAddItem: FloatingActionButton = findViewById(R.id.newTask)
        fabAddItem.setOnClickListener {
            openAddTaskDialog()
        }

        /*viewModel.filteredData.observe(this) { filteredTasks ->
            adapter.submitList(filteredTasks)
        }*/
    }

    private fun openAddTaskDialog() {
        val dialogFragment = AddTaskBottomSheetDialogFragment(viewModel, repository)
        dialogFragment.show(supportFragmentManager, "AddTaskDialogFragment")
    }

    private fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(
            this,
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

    private fun updateDate() {
        val dateFormat = "dd-MM-yyyy"
        val simpleDateFormat = SimpleDateFormat(dateFormat, Locale.getDefault())
        val formattedDate = simpleDateFormat.format(calendar.time)
        searchView.setQuery(formattedDate, false)
        filterTasks(formattedDate)
    }

    private fun filterTasks(date: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val allTasks = repository.getAllTasksList()
            val filteredTasks = allTasks.filter { it.date == date }
            // Update the filtered tasks in the ViewModel
            viewModel.setData(filteredTasks)
        }
    }


    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val formattedDate = String.format(Locale.getDefault(), "%02d-%02d-%04d", dayOfMonth, month + 1, year)
        searchView.setQuery(formattedDate, false)
        filterTasks(formattedDate)
    }
}
