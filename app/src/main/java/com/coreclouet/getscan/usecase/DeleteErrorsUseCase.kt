package com.coreclouet.getscan.usecase

import com.coreclouet.getscan.repository.ErrorRepository

class DeleteErrorsUseCase(private val errorRepository: ErrorRepository) {

    suspend fun invoke() {
       errorRepository.deleteAll()
    }

}