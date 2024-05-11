package com.example.mytasks

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mytasks.adapters.TaskAdapter
import com.example.mytasks.database.TaskDatabase
import com.example.mytasks.database.TaskRepository
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: TaskAdapter
    private lateinit var viewModel: MainActivityData
    private lateinit var repository: TaskRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView: RecyclerView = findViewById(R.id.taskRecycler)
        repository = TaskRepository(TaskDatabase.getInstance(this))
        viewModel = ViewModelProvider(this)[MainActivityData::class.java]

        // Fetch initial data from the database
        CoroutineScope(Dispatchers.IO).launch {
            val initialData = repository.getAllTasksList()
            viewModel.setData(initialData)
        }

        viewModel.data.observe(this) {
            adapter = TaskAdapter(it, repository, viewModel)
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(this)
        }

        val fabAddItem: FloatingActionButton = findViewById(R.id.newTask)
        fabAddItem.setOnClickListener {
            openAddTaskDialog()
        }
    }

    private fun openAddTaskDialog() {
        val dialogFragment = AddTaskBottomSheetDialogFragment(viewModel, repository)
        dialogFragment.show(supportFragmentManager, "AddTaskDialogFragment")
    }

    


}


