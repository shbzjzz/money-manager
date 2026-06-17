package com.wealthflow.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "emis")
data class EmiEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,        // e.g. "Tabby - iPhone 15"
    val totalAmount: Double,
    val paidAmount: Double,
    val installments: Int,
    val dueDate: Long          // epoch millis, next due
)
