package com.example.mytasks.database

class TaskRepository(private val taskDB: TaskDatabase) {

    suspend fun getAllTasksList(): List<Task> = taskDB.getTaskDao().getAllTasksList()

    suspend fun truncateTheList() = taskDB.getTaskDao().truncateTheList()

    suspend fun insertDataIntoTaskList(task: Task) = taskDB.getTaskDao().insertDataIntoTaskList(task)

    suspend fun deleteTaskFromId(taskId: Int) = taskDB.getTaskDao().deleteTaskFromId(taskId)

    suspend fun selectDataFromAnId(taskId: Int): Task = taskDB.getTaskDao().selectDataFromAnId(taskId)

    suspend fun updateAnExistingRow(taskId: Int, taskTitle: String, taskDescription: String, taskDate: String,
                                    taskTime: String, taskPriority: String) {
        taskDB.getTaskDao().updateAnExistingRow(taskId, taskTitle, taskDescription, taskDate, taskTime, taskPriority)
    }
}