package com.example.syncmate.ui.screens.uploadcontract

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.syncmate.ui.components.AppHeader
import com.example.syncmate.ui.components.PrimaryButton
import com.example.syncmate.ui.components.StatBadge
import com.example.syncmate.ui.theme.EmeraldSuccess
import com.example.syncmate.ui.theme.IndigoPrimary
import com.example.syncmate.ui.theme.SlateBackground
import com.example.syncmate.ui.theme.SlateCard
import com.example.syncmate.ui.theme.SlateCardBorder
import com.example.syncmate.ui.theme.TextPrimary
import com.example.syncmate.ui.theme.TextSecondary
import com.example.syncmate.viewmodel.ContractViewModel

fun getFileNameFromUri(context: Context, uri: Uri): String {
    var fileName = ""
    try {
        if (uri.scheme == "content") {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1) {
                        fileName = cursor.getString(nameIndex)
                    }
                }
            }
        }
        if (fileName.isEmpty() && !uri.path.isNullOrEmpty()) {
            val path = uri.path!!
            val cut = path.lastIndexOf('/')
            if (cut != -1) {
                fileName = path.substring(cut + 1)
            } else {
                fileName = path
            }
        }
    } catch (_: Exception) {}

    return if (fileName.isNotBlank()) fileName else "Sublease_Agreement_2025.pdf"
}

@Composable
fun UploadContractScreen(
    contractViewModel: ContractViewModel,
    onContractAnalyzed: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val uiState by contractViewModel.uiState.collectAsStateWithLifecycle()
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var selectedFileName by remember { mutableStateOf("Sublease_Agreement_2025.pdf") }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedFileUri = uri
            selectedFileName = getFileNameFromUri(context, uri)
        }
    }

    // Automatically navigate to Contract Summary Screen once AI analysis completes
    LaunchedEffect(uiState.summary) {
        if (uiState.summary != null && !uiState.isLoading) {
            onContractAnalyzed()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateBackground)
            .safeDrawingPadding()
    ) {
        AppHeader(
            title = "Select Contract Document",
            onBackClick = onNavigateBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Text(
                text = "Select Document for Analysis",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Text(
                text = "Pick your sublease PDF, DOCX, or text agreement from Android storage to analyze INR terms and clauses with AI Contract Guard.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.padding(top = 6.dp, bottom = 20.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SlateCard)
                    .border(1.dp, SlateCardBorder, RoundedCornerShape(16.dp))
                    .clickable { filePickerLauncher.launch("*/*") }
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(IndigoPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (selectedFileName.endsWith(".pdf", ignoreCase = true)) Icons.Default.PictureAsPdf else Icons.Default.Description,
                            contentDescription = "Document Icon",
                            tint = IndigoPrimary,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = selectedFileName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (selectedFileUri != null) "✓ Document Selected • Tap to Change" else "Tap to choose PDF or Document",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (selectedFileUri != null) EmeraldSuccess else TextSecondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatBadge(text = "PDF", backgroundColor = IndigoPrimary)
                        StatBadge(text = "DOCX", backgroundColor = IndigoPrimary)
                        StatBadge(text = "TXT", backgroundColor = IndigoPrimary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            PrimaryButton(
                text = "Analyze Contract with AI Guard",
                isLoading = uiState.isLoading,
                onClick = {
                    val fileBytes = try {
                        selectedFileUri?.let { uri ->
                            context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                        }
                    } catch (_: Exception) { null }

                    contractViewModel.analyzeContract(selectedFileName, fileBytes)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
