/**
 * File Overview: Audit Step 2 - Check Expiration Dates
 * 
 * This file contains the second step of the audit workflow where technicians
 * review and validate expiration dates for all supply items.
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
import com.sahilpatel.medsupplyguardian.utils.DateUtils

/**
 * Audit Step 2 screen for checking expiration dates.
 * 
 * Displays all supply items with their expiration dates and allows
 * technicians to confirm that expiration dates have been checked.
 * 
 * @param onNavigateNext Callback to navigate to next audit step
 * @param onNavigateBack Callback to navigate back to previous step
 * @param viewModel ViewModel for audit state management
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Step2CheckExpiry(
    onNavigateNext: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: AuditViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Step 2: Check Expiration") },
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
            AuditStepProgress(currentStep = Constants.Audit.STEP_CHECK_EXPIRY)
            
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "Verify expiration dates and check each item",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                
                items(
                    items = uiState.supplyItems,
                    key = { it.itemId }
                ) { item ->
                    val isChecked = uiState.expiryChecked.contains(item.itemId)
                    val daysUntilExpiry = item.daysUntilExpiry()
                    
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = when {
                                item.isExpiringWithin(7) -> MaterialTheme.colorScheme.errorContainer
                                item.isExpiringWithin(30) -> MaterialTheme.colorScheme.tertiaryContainer
                                else -> MaterialTheme.colorScheme.surface
                            }
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = item.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarToday,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = DateUtils.formatDate(item.expiryDate),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                
                                Text(
                                    text = DateUtils.formatExpiryStatus(daysUntilExpiry),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = when {
                                        daysUntilExpiry < 0 -> MaterialTheme.colorScheme.error
                                        daysUntilExpiry <= 7 -> MaterialTheme.colorScheme.error
                                        daysUntilExpiry <= 30 -> MaterialTheme.colorScheme.tertiary
                                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                )
                            }
                            
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = {
                                    viewModel.toggleExpiryChecked(item.itemId)
                                }
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