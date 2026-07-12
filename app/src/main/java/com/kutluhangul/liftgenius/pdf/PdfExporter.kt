@file:OptIn(ExperimentalTime::class)

package com.kutluhangul.liftgenius.pdf

import android.content.Context
import android.content.Intent
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.kutluhangul.liftgenius.domain.model.ClientPackage
import com.kutluhangul.liftgenius.domain.model.Exercise
import com.kutluhangul.liftgenius.domain.model.NutritionPlan
import com.kutluhangul.liftgenius.domain.model.WorkoutDay
import com.kutluhangul.liftgenius.domain.model.WorkoutPlan
import com.kutluhangul.liftgenius.ui.common.Formatters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDate
import kotlin.time.ExperimentalTime

/**
 * Renders app data to PDF using the platform [PdfDocument] (no external dependency) and
 * shares it via [FileProvider]. Files land in cacheDir/pdfs (see res/xml/file_paths.xml).
 */
object PdfExporter {

    data class DayContent(val day: WorkoutDay, val exercises: List<Exercise>)

    suspend fun exportWorkout(
        context: Context,
        plan: WorkoutPlan,
        days: List<DayContent>,
        clientName: String?,
    ): File = render(context, fileName("Antrenman", clientName ?: plan.title)) { pdf ->
        pdf.header(plan.title, generatedLine(plan.createdAt.let { Formatters.fullDate(it) }))
        clientName?.let { pdf.body(it) }
        plan.description?.takeIf { it.isNotBlank() }?.let {
            pdf.spacer(4f)
            pdf.paragraph(it)
        }
        days.forEach { (day, exercises) ->
            pdf.heading(day.dayName)
            if (exercises.isEmpty()) {
                pdf.body("—", indent = 10f)
            } else {
                exercises.forEach { exercise ->
                    val weight = exercise.weight?.takeIf { it.isNotBlank() }?.let { " ($it)" }.orEmpty()
                    pdf.body("• ${exercise.name} — ${exercise.sets} × ${exercise.reps}$weight", indent = 8f)
                    exercise.notes?.takeIf { it.isNotBlank() }?.let {
                        pdf.body("   $it", indent = 8f)
                    }
                }
            }
        }
    }

    suspend fun exportNutrition(
        context: Context,
        plan: NutritionPlan,
        clientName: String?,
    ): File = render(context, fileName("Beslenme", clientName ?: plan.id)) { pdf ->
        pdf.header("Beslenme Planı", generatedLine(Formatters.fullDate(plan.createdAt)))
        clientName?.let { pdf.body(it) }
        pdf.spacer(4f)
        pdf.row("Günlük Kalori", "${plan.dailyCalories} kcal")
        pdf.row("Protein", "${plan.proteinGrams} g")
        pdf.row("Karbonhidrat", "${plan.carbGrams} g")
        pdf.row("Yağ", "${plan.fatGrams} g")
        pdf.heading("Öğün Planı")
        pdf.paragraph(plan.mealPlanText)
        plan.notes?.takeIf { it.isNotBlank() }?.let {
            pdf.heading("Notlar")
            pdf.paragraph(it)
        }
    }

    suspend fun exportFinance(
        context: Context,
        packages: List<ClientPackage>,
        clientNames: Map<String, String>,
    ): File = render(context, fileName("Finans", LocalDate.now().toString())) { pdf ->
        pdf.header("Finans Özeti", generatedLine(Formatters.fullDate(LocalDate.now())))
        val paid = packages.filter { it.isPaid }.sumOf { it.price }
        val unpaid = packages.filterNot { it.isPaid }.sumOf { it.price }
        pdf.row("Tahsil Edilen", Formatters.currency(paid))
        pdf.row("Bekleyen", Formatters.currency(unpaid))
        pdf.row("Toplam", Formatters.currency(paid + unpaid))
        pdf.heading("Paketler")
        pdf.divider()
        packages.forEach { pkg ->
            val name = clientNames[pkg.clientId] ?: "—"
            val status = if (pkg.isPaid) "Ödendi" else "Bekliyor"
            pdf.row("$name · ${pkg.name}", "${Formatters.currency(pkg.price)}  ($status)")
        }
    }

    /** Builds a share chooser Intent for a previously exported file. */
    fun shareIntent(context: Context, file: File): Intent {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val send = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        return Intent.createChooser(send, null)
    }

    private suspend fun render(
        context: Context,
        fileName: String,
        block: (PdfCanvas) -> Unit,
    ): File = withContext(Dispatchers.IO) {
        val document = PdfDocument()
        val pdf = PdfCanvas(document)
        try {
            block(pdf)
            pdf.finish()
            val dir = File(context.cacheDir, "pdfs").apply { mkdirs() }
            val file = File(dir, fileName)
            file.outputStream().use { document.writeTo(it) }
            file
        } finally {
            document.close()
        }
    }

    private fun generatedLine(date: String): String = "Oluşturulma: $date"

    private fun fileName(kind: String, subject: String): String {
        val safe = subject.trim()
            .replace(Regex("[^\\p{L}\\p{Nd}]+"), "_")
            .trim('_')
            .take(40)
            .ifBlank { "LiftGenius" }
        return "LiftGenius_${kind}_$safe.pdf"
    }
}
