package ru.animaltracker.userservice.service.impl

import ru.animaltracker.userservice.dto.StatusLogCreateRequest
import ru.animaltracker.userservice.dto.StatusLogResponse
import ru.animaltracker.userservice.dto.StatusLogUpdateRequest
import ru.animaltracker.userservice.service.interfaces.StatusLogService

class StatusLogServiceImpl : StatusLogService {
    override fun addStatusLog(username: String, animalId: Long, request: StatusLogCreateRequest): StatusLogResponse {
        TODO("Not yet implemented")
    }

    override fun updateStatusLog(
        username: String,
        animalId: Long,
        statusLogId: Long,
        request: StatusLogUpdateRequest
    ): StatusLogResponse {
        TODO("Not yet implemented")
    }

    override fun deleteStatusLog(username: String, animalId: Long, statusLogId: Long) {
        TODO("Not yet implemented")
    }

    override fun getStatusLogs(username: String, animalId: Long): List<StatusLogResponse> {
        TODO("Not yet implemented")
    }

    override fun getStatusLog(id: Long): StatusLogResponse {
        TODO("Not yet implemented")
    }
}