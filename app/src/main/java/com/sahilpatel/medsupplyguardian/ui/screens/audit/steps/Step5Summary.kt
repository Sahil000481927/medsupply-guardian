/**
 * File Overview: Audit Step 5 - Summary and Submission
 * 
 * This file contains the final step of the audit workflow where technicians
 * review all collected data and submit the complete audit report.
 * 
 * @author Sahil Patel
 * @version 1.0
 */

package com.sahilpatel.medsupplyguardian.ui.screens.audit.steps

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sahilpatel.medsupplyguardian.ui.components.AuditStepProgress
import com.sahilpatel.medsupplyguardian.ui.components.UploadProgressOverlay
import com.sahilpatel.medsupplyguardian.ui.screens.audit.AuditViewModel
import com.sahilpatel.medsupplyguardian.utils.Constants

/**
 * Audit Step 5 screen for reviewing and submitting the audit.
 * 
 * Displays a comprehensive summary of all audit findings and allows
 * technicians to submit the completed audit report for upload.
 * 
 * @param onNavigateToHome Callback to navigate back to home screen after submission
 * @param onNavigateBack Callback to navigate back to previous step
 * @param viewModel ViewModel for audit state management
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Step5Summary(
    onNavigateToHome: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: AuditViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(uiState.uploadComplete) {
        if (uiState.uploadComplete) {
            onNavigateToHome()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Step 5: Summary") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                AuditStepProgress(currentStep = Constants.Audit.STEP_SUMMARY)
                
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Review audit findings before submission",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Assessment,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "Audit Complete",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "All steps completed successfully",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                    
                    Text(
                        text = "Audit Summary",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    
                    SummaryCard(
                        title = "Items Reviewed",
                        value = uiState.supplyItems.size.toString(),
                        icon = Icons.Default.Inventory,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    
                    SummaryCard(
                        title = "Quantities Verified",
                        value = uiState.verifiedQuantities.size.toString(),
                        icon = Icons.Default.CheckCircle,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    
                    SummaryCard(
                        title = "Expiry Dates Checked",
                        value = uiState.expiryChecked.size.toString(),
                        icon = Icons.Default.CalendarToday,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    
                    SummaryCard(
                        title = "Storage Conditions",
                        value = if (uiState.storageConditionsPassed) "Passed" else "Failed",
                        icon = if (uiState.storageConditionsPassed) Icons.Default.CheckCircle else Icons.Default.Error,
                        containerColor = if (uiState.storageConditionsPassed)
                            MaterialTheme.colorScheme.secondaryContainer
                        else MaterialTheme.colorScheme.errorContainer,
                        contentColor = if (uiState.storageConditionsPassed)
                            MaterialTheme.colorScheme.onSecondaryContainer
                        else MaterialTheme.colorScheme.onErrorContainer
                    )
                    
                    if (uiState.missingItems.isNotEmpty() || uiState.damagedItems.isNotEmpty()) {
                        SummaryCard(
                            title = "Discrepancies",
                            value = "Missing: ${uiState.missingItems.size}, Damaged: ${uiState.damagedItems.size}",
                            icon = Icons.Default.Warning,
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                    
                    if (uiState.auditNotes.isNotEmpty()) {
                        ElevatedCard(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Notes",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = uiState.auditNotes,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
                
                Surface(
                    shadowElevation = 8.dp,
                    tonalElevation = 0.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onNavigateBack,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Back")
                        }
                        
                        Button(
                            onClick = { viewModel.submitAudit() },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudUpload,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Submit Audit")
                        }
                    }
                }
            }
            
            UploadProgressOverlay(
                isVisible = uiState.isUploading,
                message = "Uploading audit report..."
            )
        }
    }
}

/**
 * Summary card component for displaying audit metrics.
 * 
 * @param title Metric title
 * @param value Metric value
 * @param icon Icon representing the metric
 * @param containerColor Card background color
 * @param contentColor Text and icon color
 */
@Composable
fun SummaryCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    containerColor: androidx.compose.ui.graphics.Color,
    contentColor: androidx.compose.ui.graphics.Color
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = contentColor
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = contentColor
                )
            }
            
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
        }
    }
}