package com.kutluhangul.liftgenius.ui.components

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.rememberCoroutineScope
import com.kutluhangul.liftgenius.R
import com.kutluhangul.liftgenius.pdf.PdfExporter
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import java.io.File

/**
 * Returns a launcher that runs a suspending PDF producer, then opens the share sheet.
 * Errors surface as a Toast — PDF export never blocks or crashes the calling screen.
 */
@Composable
fun rememberPdfShareLauncher(): (suspend (Context) -> File) -> Unit {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    return remember(context) {
        { producer ->
            scope.launch {
                try {
                    val file = producer(context)
                    context.startActivity(PdfExporter.shareIntent(context, file))
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.pdf_export_failed, e.message ?: ""),
                        Toast.LENGTH_LONG,
                    ).show()
                }
            }
        }
    }
}
