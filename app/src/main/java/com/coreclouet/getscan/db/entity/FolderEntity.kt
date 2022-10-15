package com.coreclouet.getscan.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.coreclouet.getscan.utils.FOLDER_TABLE_NAME

@Entity(tableName = FOLDER_TABLE_NAME)
data class FolderEntity(
    @PrimaryKey(autoGenerate = false)
    val name: String,
    val endpoint: String,
    val lastChapter: Int
) {
    override fun toString(): String {
        return name
    }
}
