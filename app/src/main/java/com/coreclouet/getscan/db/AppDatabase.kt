package com.coreclouet.getscan.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.coreclouet.getscan.db.dao.ErrorDao
import com.coreclouet.getscan.db.entity.ErrorEntity

@Database(entities = [ErrorEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun errorDao(): ErrorDao
}