package com.coreclouet.getscan.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.coreclouet.getscan.db.entity.ErrorEntity
import com.coreclouet.getscan.utils.ERROR_TABLE_NAME

@Dao
interface ErrorDao {
    @Query("SELECT * FROM $ERROR_TABLE_NAME")
    fun getAll(): List<ErrorEntity>

    @Insert
    fun insert(error: ErrorEntity)

    @Delete
    fun delete(error: ErrorEntity)

    @Delete
    fun deleteAll(errors: List<ErrorEntity>)
}