package com.coreclouet.getscan.repository

import com.coreclouet.getscan.db.dao.FolderDao
import com.coreclouet.getscan.db.entity.FolderEntity

class FolderRepositoryImpl(private val folderDao: FolderDao) : FolderRepository {

    override suspend fun getAll(): List<FolderEntity> = folderDao.getAll()

    override suspend fun insert(folder: FolderEntity) {
        folderDao.insert(folder)
    }

    override suspend fun deleteAll(folders: List<FolderEntity>) = folderDao.deleteAll(folders)
}