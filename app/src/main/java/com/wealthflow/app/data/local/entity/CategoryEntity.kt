package com.wealthflow.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class CategoryKind { INCOME, EXPENSE }

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val kind: CategoryKind,
    val icon: String,   // material symbol name e.g. "restaurant"
    val color: String   // hex e.g. "#064e3b"
)
