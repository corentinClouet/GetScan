package com.coreclouet.getscan.usecase

import com.coreclouet.getscan.db.entity.FolderEntity
import com.coreclouet.getscan.repository.FolderRepository

class AddFolderUseCase(private val folderRepository: FolderRepository) {

    /**
     * Add folder
     */
    suspend fun invoke(name: String, endpoint: String, lastChapter: Int) {
        folderRepository.insert(FolderEntity(name, endpoint, lastChapter))
    }

}