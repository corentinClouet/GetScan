package com.coreclouet.getscan.db.dao

import androidx.room.*
import com.coreclouet.getscan.db.entity.FolderEntity
import com.coreclouet.getscan.utils.FOLDER_TABLE_NAME

@Dao
interface FolderDao {
    @Query("SELECT * FROM $FOLDER_TABLE_NAME")
    fun getAll(): List<FolderEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(folder: FolderEntity)

    @Delete
    fun delete(folder: FolderEntity)

    @Delete
    fun deleteAll(folder: List<FolderEntity>)
}