package com.example.mytasks.adapters

import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mytasks.R

class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val day: TextView = itemView.findViewById(R.id.day)
    private val date: TextView = itemView.findViewById(R.id.date)
    private val month: TextView = itemView.findViewById(R.id.month)
    val title: TextView = itemView.findViewById(R.id.title)
    val description: TextView = itemView.findViewById(R.id.description)
    private val status: TextView = itemView.findViewById(R.id.status)
    private val options: ImageButton = itemView.findViewById(R.id.options)
    private val time: TextView = itemView.findViewById(R.id.time)
}