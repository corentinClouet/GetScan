package com.coreclouet.getscan.repository

import com.coreclouet.getscan.db.entity.ErrorEntity

interface ErrorRepository {
    suspend fun getAll(): List<ErrorEntity>?
    suspend fun insert(error: ErrorEntity)
    suspend fun deleteAll(errors: List<ErrorEntity>)
    suspend fun deleteAll()
}