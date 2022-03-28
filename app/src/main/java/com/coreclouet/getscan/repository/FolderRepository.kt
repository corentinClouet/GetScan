package com.coreclouet.getscan.repository

import com.coreclouet.getscan.db.entity.FolderEntity

interface FolderRepository {
    suspend fun getAll(): List<FolderEntity>?
    suspend fun insert(folder: FolderEntity)
    suspend fun deleteAll(folders: List<FolderEntity>)
}