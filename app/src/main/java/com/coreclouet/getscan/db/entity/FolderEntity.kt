package com.coreclouet.getscan.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.coreclouet.getscan.utils.FOLDER_TABLE_NAME

@Entity(tableName = FOLDER_TABLE_NAME)
data class FolderEntity(
    val name: String,
    @PrimaryKey val endpoint: String,
)
