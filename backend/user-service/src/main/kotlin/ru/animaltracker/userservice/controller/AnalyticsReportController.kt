package ru.animaltracker.userservice.controller
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.animaltracker.userservice.dto.*
import ru.animaltracker.userservice.service.interfaces.AnalyticsReportService

@RestController
@RequestMapping("/api/animals/{animalId}/analytics")
class AnalyticsReportController(
    private val analyticsReportService: AnalyticsReportService
) {

    @GetMapping
    fun getAnimalAnalytics(
        @RequestHeader("X-User-Name") username: String,
        @PathVariable animalId: Long
    ): ResponseEntity<List<AnimalAnalyticsResponse>> {
        return ResponseEntity.ok(analyticsReportService.getAnimalAnalytics(animalId))
    }

    @GetMapping("/export/pdf")
    fun exportAnimalToPdf(
        @RequestHeader("X-User-Name") username: String,
        @PathVariable animalId: Long
    ): ResponseEntity<ByteArray> {
        val pdfBytes = analyticsReportService.exportAnimalToPdf(username, animalId)

        return ResponseEntity.ok()
            .header("Content-Type", "application/pdf")
            .header("Content-Disposition", "attachment; filename=animal_${animalId}.pdf")
            .body(pdfBytes)
    }
}