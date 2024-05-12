package com.example.mytasks.adapters

import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mytasks.R

class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val day: TextView = itemView.findViewById(R.id.day)
    val date: TextView = itemView.findViewById(R.id.date)
    val month: TextView = itemView.findViewById(R.id.month)
    val title: TextView = itemView.findViewById(R.id.title)
    val description: TextView = itemView.findViewById(R.id.description)
    val status: TextView = itemView.findViewById(R.id.status)
    val options: ImageButton = itemView.findViewById(R.id.options)
    val time: TextView = itemView.findViewById(R.id.time)
    val priority:TextView = itemView.findViewById(R.id.priorities)
}