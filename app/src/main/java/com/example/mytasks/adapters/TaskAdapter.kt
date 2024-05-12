package com.example.mytasks.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mytasks.CompletionDialogFragment
import com.example.mytasks.MainActivityData
import com.example.mytasks.R
import com.example.mytasks.UpdateTaskBottomSheetDialogFragment
import com.example.mytasks.database.Task
import com.example.mytasks.database.TaskRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale


class TaskAdapter (items:List<Task>,repository:TaskRepository,viewModel: MainActivityData,private val fragmentManager: FragmentManager):
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

        holder.title.text = currentItem.taskTitle.toString()
        holder.description.text = currentItem.taskDescription
        holder.status.text = if (currentItem.isComplete) "COMPLETED" else "UPCOMING"
        holder.time.text = "Time:${currentItem.lastAlarm}"
        holder.priority.text = "Priority:${currentItem.priority}"
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
                    context?.let { context1 ->
                        AlertDialog.Builder(context1)
                            .setTitle(R.string.delete_confirmation)
                            .setMessage(R.string.sureToDelete)
                            .setPositiveButton(android.R.string.ok) { dialog, which ->
                                task.taskId?.let { deleteTask(it) }
                            }
                            .setNegativeButton(android.R.string.cancel) { dialog, which -> dialog.dismiss() }
                            .show()
                    }
                    true
                }
                R.id.menuUpdate -> {
                    // Open the update fragment directly
                    val taskId = tasks[position].taskId ?: return@setOnMenuItemClickListener true
                    val updateFragment = UpdateTaskBottomSheetDialogFragment.newInstance(taskId,repository,viewModel)
                    updateFragment.show(fragmentManager, "UpdateFragment")
                    true
                }
                R.id.menuComplete -> {
                    // Handle complete action
                    // Show confirmation dialog before marking as complete
                    context?.let {
                        AlertDialog.Builder(it)
                            .setTitle(R.string.confirmation)
                            .setMessage(R.string.sureToMarkAsComplete)
                            .setPositiveButton(android.R.string.ok) { dialog, which ->
                                updateTaskCompletion(task.taskId ?: -1)
                                // Show the completion dialog
                                val completionDialog = CompletionDialogFragment.newInstance()
                                completionDialog.show(fragmentManager, "CompletionDialogFragment")
                            }
                            .setNegativeButton(android.R.string.cancel) { dialog, which -> dialog.dismiss() }
                            .show()
                    }

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

            val newData = withContext(Dispatchers.IO) {
                repository.getAllTasksList()
            }
            // Update the ViewModel with the new data
            viewModel.setData(newData)

        }
    }

    private fun updateTaskCompletion(taskId: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            Log.d("UpdateTaskCompletion", "Updating task completion with ID: $taskId")
            withContext(Dispatchers.IO) {
                repository.markTaskAsComplete(taskId)
            }

            val newData = withContext(Dispatchers.IO) {
                repository.getAllTasksList()
            }
            // Update the ViewModel with the new data
            viewModel.setData(newData)
        }
    }


}

