package com.coreclouet.getscan.repository

import android.util.Log
import com.coreclouet.getscan.db.dao.ErrorDao
import com.coreclouet.getscan.db.entity.ErrorEntity

class ErrorRepositoryImpl(private val errorDao: ErrorDao) : ErrorRepository {

    override suspend fun getAll(): List<ErrorEntity> = errorDao.getAll()

    override suspend fun insert(error: ErrorEntity) {
        Log.e("CCL", "$error")
        errorDao.insert(error)
    }

    override suspend fun deleteAll(errors: List<ErrorEntity>) = errorDao.deleteAll(errors)

    override suspend fun deleteAll() = errorDao.deleteAll()
}