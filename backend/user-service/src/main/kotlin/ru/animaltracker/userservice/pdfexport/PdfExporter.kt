package ru.animaltracker.userservice.pdfexport

import com.itextpdf.io.source.ByteArrayOutputStream
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.properties.TextAlignment
import org.springframework.stereotype.Component
import ru.animaltracker.userservice.entity.Animal

@Component
class PdfExporter {
    fun exportAnimal(animal: Animal): ByteArray {
        ByteArrayOutputStream().use { outputStream ->
            PdfDocument(PdfWriter(outputStream)).use { pdfDoc ->
                Document(pdfDoc).use { doc ->

                    doc.add(
                        Paragraph("Данные о животном")
                        .setTextAlignment(TextAlignment.CENTER)
                        .setFontSize(20f))

                    doc.add(Paragraph("Основная информация:").setBold())
                    addKeyValue(doc, "Имя", animal.name)
                    addKeyValue(doc, "Описание", animal.description)
                    addKeyValue(doc, "Дата рождения", animal.birthDate?.toString())
                    addKeyValue(doc, "Вес", animal.mass?.toString())

                    doc.add(Paragraph("Атрибуты:").setBold())
                    animal.attributes.forEach { attr ->
                        attr.name?.let { addKeyValue(doc, it, attr.values.firstOrNull()?.value) }
                    }

                    doc.add(Paragraph("История параметров:").setBold())
                    animal.parameterHistories.sortedBy { it.recordedAt }.forEach { history ->
                        val changes = mutableListOf<String>()

                        if (history.oldMass != null && history.newMass != null) {
                            changes.add("Вес: ${history.oldMass} → ${history.newMass}")
                        }
                        if (history.oldHeight != null && history.newHeight != null) {
                            changes.add("Рост: ${history.oldHeight} → ${history.newHeight}")
                        }
                        if (history.oldTemperature != null && history.newTemperature != null) {
                            changes.add("Температура: ${history.oldTemperature} → ${history.newTemperature}")
                        }
                        if (history.oldActivityLevel != null && history.newActivityLevel != null) {
                            changes.add("Активность: ${history.oldActivityLevel} → ${history.newActivityLevel}")
                        }
                        if (history.oldAppetiteLevel != null && history.newAppetiteLevel != null) {
                            changes.add("Аппетит: ${history.oldAppetiteLevel} → ${history.newAppetiteLevel}")
                        }

                        if (changes.isNotEmpty()) {
                            doc.add(Paragraph("${history.recordedAt}: ${changes.joinToString(", ")}"))
                        }
                }
            }

            return outputStream.toByteArray()
        }
    }
}
    private fun addKeyValue(doc: Document, key: String, value: String?) {
        doc.add(Paragraph("$key: ${value ?: "не указано"}"))
    }
    }

