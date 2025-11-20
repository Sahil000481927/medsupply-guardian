/**
 * File Overview: Audit Step 4 - Report Missing or Damaged Items
 * 
 * This file contains the fourth step of the audit workflow where technicians
 * identify and report any missing or damaged supply items with notes.
 * 
 * @author Sahil Patel
 * @version 1.0
 */

package com.sahilpatel.medsupplyguardian.ui.screens.audit.steps

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.sahilpatel.medsupplyguardian.ui.screens.audit.AuditViewModel
import com.sahilpatel.medsupplyguardian.utils.Constants

/**
 * Audit Step 4 screen for reporting missing or damaged items.
 * 
 * Allows technicians to mark items as missing or damaged and add
 * detailed notes about discrepancies found during the audit.
 * 
 * @param onNavigateNext Callback to navigate to next audit step
 * @param onNavigateBack Callback to navigate back to previous step
 * @param viewModel ViewModel for audit state management
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Step4MissingDamaged(
    onNavigateNext: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: AuditViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Step 4: Missing/Damaged") },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AuditStepProgress(currentStep = Constants.Audit.STEP_MISSING_DAMAGED)
            
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "Mark any items that are missing or damaged",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                
                item {
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                modifier = Modifier.size(32.dp),
                                tint = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                            Column {
                                Text(
                                    text = "Discrepancies Found",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                                Text(
                                    text = "Missing: ${uiState.missingItems.size} | Damaged: ${uiState.damagedItems.size}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                            }
                        }
                    }
                }
                
                items(
                    items = uiState.supplyItems,
                    key = { it.itemId }
                ) { item ->
                    val isMissing = uiState.missingItems.contains(item.itemId)
                    val isDamaged = uiState.damagedItems.contains(item.itemId)
                    
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = when {
                                isMissing || isDamaged -> MaterialTheme.colorScheme.errorContainer
                                else -> MaterialTheme.colorScheme.surface
                            }
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = item.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            
                            Text(
                                text = "${item.category} - ${item.location}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilterChip(
                                    selected = isMissing,
                                    onClick = { viewModel.toggleMissingItem(item.itemId) },
                                    label = { Text("Missing") },
                                    leadingIcon = if (isMissing) {
                                        {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    } else null,
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.error,
                                        selectedLabelColor = MaterialTheme.colorScheme.onError
                                    )
                                )
                                
                                FilterChip(
                                    selected = isDamaged,
                                    onClick = { viewModel.toggleDamagedItem(item.itemId) },
                                    label = { Text("Damaged") },
                                    leadingIcon = if (isDamaged) {
                                        {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    } else null,
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.error,
                                        selectedLabelColor = MaterialTheme.colorScheme.onError
                                    )
                                )
                            }
                        }
                    }
                }
                
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Audit Notes",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        
                        OutlinedTextField(
                            value = uiState.auditNotes,
                            onValueChange = { viewModel.updateAuditNotes(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp),
                            placeholder = { Text("Add any additional observations or notes...") },
                            maxLines = 6
                        )
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
                        onClick = onNavigateNext,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Next Step")
                    }
                }
            }
        }
    }
}