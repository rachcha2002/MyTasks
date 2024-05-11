package com.example.mytasks.database

import androidx.room.Entity
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

@Entity
class Task(
    @ColumnInfo(name = "taskTitle")
    var taskTitle: String? = null,

    @ColumnInfo(name = "date")
    var date: String? = null,

    @ColumnInfo(name = "taskDescription")
    var taskDescription: String? = null,

    @ColumnInfo(name = "isComplete")
    var isComplete: Boolean = false,

    @ColumnInfo(name = "priority")
    var priority: String? = null,

    @ColumnInfo(name = "firstAlarmTime")
    var firstAlarmTime: String? = null,

    @ColumnInfo(name = "secondAlarmTime")
    var secondAlarmTime: String? = null,

    @ColumnInfo(name = "lastAlarm")
    var lastAlarm: String? = null,

    ) {
    @PrimaryKey(autoGenerate = true)
    var taskId: Int? = null
}