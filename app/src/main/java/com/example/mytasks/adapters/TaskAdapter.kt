package com.example.mytasks.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.mytasks.MainActivityData
import com.example.mytasks.R
import com.example.mytasks.database.Task
import com.example.mytasks.database.TaskRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale


class TaskAdapter (items:List<Task>,repository:TaskRepository,viewModel: MainActivityData):
    RecyclerView.Adapter<TaskViewHolder>(){
    var context: Context? = null
    val tasks = items
    val repository = repository
    val viewModel = viewModel
    private val dateFormat = SimpleDateFormat("EE dd MMM yyyy", Locale.US)
    private val inputDateFormat = SimpleDateFormat("dd-M-yyyy", Locale.US)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        context = parent.context
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val currentItem = tasks[position]

        holder.title.text = currentItem.taskTitle
        holder.description.text = currentItem.taskDescription
        holder.status.text = if (currentItem.isComplete) "COMPLETED" else "UPCOMING"

        // Set click listener for the options ImageButton
        holder.options.setOnClickListener { view ->
            showPopupMenu(view, position)
        }

        try {
            val date = inputDateFormat.parse(currentItem.date)
            val outputDateString = dateFormat.format(date)

            val items1 = outputDateString.split(" ").toTypedArray()
            val day = items1[0]
            val dd = items1[1]
            val month = items1[2]

            holder.day.text = day
            holder.date.text = dd
            holder.month.text = month
        } catch (e: Exception) {
            e.printStackTrace()
        }
        //holder.title.text = "Sample Task"
        //holder.description.text="Lorem ipusum van dukes"


    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    private fun showPopupMenu(view: View, position: Int) {
        val task = tasks[position]
        val popupMenu = PopupMenu(context, view)
        popupMenu.inflate(R.menu.menu)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menuDelete -> {
                    // Handle delete action
                    task.taskId?.let { deleteTask(it) }
                    true
                }
                R.id.menuUpdate -> {
                    // Handle update action
                    //Open a dialog or activity for updating the task
                    true
                }
                R.id.menuComplete -> {
                    // Handle complete action
                    // Update the task status as complete
                    task.isComplete = true
                    //repository.updateTask(task)
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    private fun deleteTask(taskId: Int) {
        Log.d("DeleteTask", "Deleting task with ID: $taskId")
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                repository.deleteTaskFromId(taskId)
            }
            // Optionally, update UI or notify adapter after deletion
            // Fetch the updated data after deletion
           // val newData = repository.getAllTasksList()
            //Log.d("New List", "After Deleting new list: $newData")
            // Update the ViewModel with the new data
            //withContext(Dispatchers.Main){
                //viewModel.setData(newData)
           // }

            val newData = withContext(Dispatchers.IO) {
                repository.getAllTasksList()
            }
            // Update the ViewModel with the new data
            viewModel.setData(newData)

        }
    }


}