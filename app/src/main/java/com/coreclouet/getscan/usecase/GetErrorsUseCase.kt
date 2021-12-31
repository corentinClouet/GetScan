package com.coreclouet.getscan.usecase

import com.coreclouet.getscan.repository.ErrorRepository

class GetErrorsUseCase(private val errorRepository: ErrorRepository) {

    suspend fun invoke(): String {
        val errors = errorRepository.getAll()
        if (errors.isNullOrEmpty()) return "\nNo error"
        var result = ""
        errors.forEach { result += "\n${it.manga} : ${it.error}" }
        return result
    }

}