package com.example.mytasks

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mytasks.database.Task

class MainActivityData : ViewModel() {
    private val _data = MutableLiveData<List<Task>>()
    val data: LiveData<List<Task>> = _data

    private val _filteredData = MutableLiveData<List<Task>>()
    val filteredData: LiveData<List<Task>> = _filteredData

    fun setData(newData: List<Task>) {
        // Ensure this code runs on the main thread
        if (Looper.myLooper() == Looper.getMainLooper()) {
            _data.value = newData
        } else {
            // Post the update to the main thread
            Handler(Looper.getMainLooper()).post { _data.value = newData }
        }
    }

    fun filterTasksByDate(date: String) {
        val tasks = data.value ?: return
        val filteredTasks = tasks.filter { it.date == date }
        _filteredData.value = filteredTasks
    }
}
