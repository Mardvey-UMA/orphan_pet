package ru.animaltracker.userservice.service.interfaces

import ru.animaltracker.userservice.dto.ParameterChangeResponse
import ru.animaltracker.userservice.dto.StatusLogCreateRequest
import ru.animaltracker.userservice.dto.StatusLogResponse
import ru.animaltracker.userservice.dto.StatusLogUpdateRequest

interface StatusLogService {
    fun addStatusLog(
        username: String,
        animalId: Long,
        request: StatusLogCreateRequest
    ): StatusLogResponse
    fun updateStatusLog(
    username: String,
    animalId: Long,
    statusLogId: Long,
    request: StatusLogUpdateRequest
    ): StatusLogResponse
    fun deleteStatusLog(username: String, animalId: Long, statusLogId: Long)
    fun getStatusLogs(username: String, animalId: Long): List<StatusLogResponse>
    fun getStatusLog(id: Long): StatusLogResponse
    fun getParameterHistory(
        username: String,
        animalId: Long,
        parameterName: String
    ): List<ParameterChangeResponse>
}