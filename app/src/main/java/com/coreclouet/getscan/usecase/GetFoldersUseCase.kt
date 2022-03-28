package com.coreclouet.getscan.usecase

import com.coreclouet.getscan.db.entity.FolderEntity
import com.coreclouet.getscan.repository.FolderRepository

class GetFoldersUseCase(private val folderRepository: FolderRepository) {

    suspend fun invoke(): List<FolderEntity> {
        return folderRepository.getAll() ?: listOf()
    }

}