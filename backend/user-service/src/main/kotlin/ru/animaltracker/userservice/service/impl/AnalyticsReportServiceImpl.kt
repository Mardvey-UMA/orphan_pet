package ru.animaltracker.userservice.service.impl

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.animaltracker.userservice.dto.AnimalAnalyticsResponse
import ru.animaltracker.userservice.dto.ParameterStats
import ru.animaltracker.userservice.pdfexport.PdfExporter
import ru.animaltracker.userservice.service.interfaces.AnalyticsReportService
import ru.animaltracker.userservice.service.interfaces.AnimalValidationService
import ru.animaltracker.userservice.service.interfaces.StatusLogService


@Service
class AnalyticsReportServiceImpl(
    private val statusLogService: StatusLogService,
    private val animalValidationService: AnimalValidationService
) : AnalyticsReportService {

    @Autowired
    private lateinit var pdfExporter: PdfExporter

    @Transactional(readOnly = true)
    override fun getAnimalAnalytics(animalId: Long): List<AnimalAnalyticsResponse> {
        val parameters = listOf("mass", "height", "temperature", "activityLevel", "appetiteLevel")

        return parameters.map { parameterName ->
            val changes = statusLogService.getParameterHistory("system", animalId, parameterName)

            val numericValues = changes
                .mapNotNull { it.newValue.toDoubleOrNull() }
                .takeIf { it.isNotEmpty() }

            val stats = numericValues?.let {
                ParameterStats(
                    minValue = it.min().toString(),
                    maxValue = it.max().toString(),
                    avgValue = it.average(),
                    firstValue = changes.first().newValue,
                    lastValue = changes.last().newValue
                )
            }

            AnimalAnalyticsResponse(
                parameterName = parameterName,
                changes = changes,
                stats = stats
            )
        }
    }

    @Transactional(readOnly = true)
    override fun exportAnimalToPdf(username: String, animalId: Long): ByteArray {
        val (_, animal) = animalValidationService.validateUserAndAnimal(username, animalId)
        return pdfExporter.exportAnimal(animal)
    }
}