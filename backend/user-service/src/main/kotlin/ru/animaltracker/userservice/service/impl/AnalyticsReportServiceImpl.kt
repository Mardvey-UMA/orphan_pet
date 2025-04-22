package ru.animaltracker.userservice.service.impl

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import ru.animaltracker.userservice.dto.AnimalAnalyticsResponse
import ru.animaltracker.userservice.dto.AttributeChange
import ru.animaltracker.userservice.dto.AttributeStats
import ru.animaltracker.userservice.pdfexport.PdfExporter
import ru.animaltracker.userservice.repository.AttributeRepository
import ru.animaltracker.userservice.service.interfaces.AnalyticsReportService
import java.time.LocalDate

class AnalyticsReportServiceImpl (
    private val attributeRepository: AttributeRepository,
    
): AnalyticsReportService {

    @Autowired
    private lateinit var pdfExporter: PdfExporter


    @Transactional(readOnly = true)
    override fun getAnimalAnalytics(animalId: Long): List<AnimalAnalyticsResponse> {
        val attributes = attributeRepository.findByAnimalId(animalId)

        return attributes.map { attribute ->
            val histories = attribute.valueHistories
                .sortedBy { it.recordedAt }
                .map { history ->
                    AttributeChange(
                        date = history.recordedAt ?: LocalDate.now(),
                        value = history.value ?: "",
                        changedBy = history.user.username ?: ""
                    )
                }

            val numericValues = histories
                .mapNotNull { it.value.toDoubleOrNull() }

            val stats = if (numericValues.isNotEmpty()) {
                AttributeStats(
                    minValue = numericValues.min().toString(),
                    maxValue = numericValues.max().toString(),
                    avgValue = numericValues.average()
                )
            } else {
                null
            }

            AnimalAnalyticsResponse(
                attributeName = attribute.name ?: "Unknown",
                changes = histories,
                stats = stats
            )
        }
    }


    @Transactional(readOnly = true)
    override  fun exportAnimalToPdf(username: String, animalId: Long): ByteArray {
        val (_, animal) = validateUserAndAnimal(username, animalId)

        return pdfExporter.exportAnimal(animal)
    }
}