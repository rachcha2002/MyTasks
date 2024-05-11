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

    fun setData(newData: List<Task>) {
        // Ensure this code runs on the main thread
        if (Looper.myLooper() == Looper.getMainLooper()) {
            _data.value = newData
        } else {
            // Post the update to the main thread
            Handler(Looper.getMainLooper()).post { _data.value = newData }
        }
    }
}
