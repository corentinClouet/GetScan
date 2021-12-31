package com.coreclouet.getscan.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.coreclouet.getscan.utils.ERROR_TABLE_NAME

@Entity(tableName = ERROR_TABLE_NAME)
data class ErrorEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val manga: String?,
    val error: String,
)
