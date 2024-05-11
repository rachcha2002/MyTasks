package com.example.mytasks.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TaskDao {
    @Query("SELECT * FROM Task")
    fun getAllTasksList(): List<Task>

    @Query("DELETE FROM Task")
    fun truncateTheList()

    @Insert
    fun insertDataIntoTaskList(task: Task)

    @Query("DELETE FROM Task WHERE taskId=:taskId")
    fun deleteTaskFromId(taskId: Int)

    @Query("SELECT * FROM Task WHERE taskId=:taskId")
    fun selectDataFromAnId(taskId: Int): Task

    @Query("UPDATE Task SET isComplete = 1 WHERE taskId = :taskId")
    fun markTaskAsComplete(taskId: Int)

    @Query("UPDATE Task SET taskTitle=:taskTitle, taskDescription = :taskDescription, date = :taskDate, " +
            "lastAlarm = :taskTime, priority = :taskPriority WHERE taskId = :taskId")
    fun updateAnExistingRow(taskId: Int, taskTitle: String, taskDescription: String, taskDate: String,
                            taskTime: String, taskPriority: String)
}