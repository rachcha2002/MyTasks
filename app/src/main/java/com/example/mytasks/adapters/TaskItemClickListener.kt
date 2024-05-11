package com.example.mytasks.adapters

import com.example.mytasks.database.Task

interface TaskItemClickListener {
    fun onUpdateTaskClicked(task: Task)
}