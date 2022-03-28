package com.coreclouet.getscan.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.coreclouet.getscan.db.dao.ErrorDao
import com.coreclouet.getscan.db.dao.FolderDao
import com.coreclouet.getscan.db.entity.ErrorEntity
import com.coreclouet.getscan.db.entity.FolderEntity

@Database(entities = [ErrorEntity::class, FolderEntity::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun errorDao(): ErrorDao
    abstract fun folderDao(): FolderDao
}