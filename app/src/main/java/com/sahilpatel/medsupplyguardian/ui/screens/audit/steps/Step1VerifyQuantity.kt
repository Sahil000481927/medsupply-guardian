/**
 * File Overview: Audit Step 1 - Verify Quantities
 * 
 * This file contains the first step of the audit workflow where technicians
 * verify the current stock quantities for all supply items.
 * 
 * @author Sahil Patel
 * @version 1.0
 */

package com.sahilpatel.medsupplyguardian.ui.screens.audit.steps

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
 * Audit Step 1 screen for verifying stock quantities.
 * 
 * Displays all supply items and allows technicians to confirm or update
 * the current quantity for each item during the audit process.
 * 
 * @param onNavigateNext Callback to navigate to next audit step
 * @param onNavigateBack Callback to navigate back to previous screen
 * @param viewModel ViewModel for audit state management
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Step1VerifyQuantity(
    onNavigateNext: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: AuditViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Step 1: Verify Quantities") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
            AuditStepProgress(currentStep = Constants.Audit.STEP_VERIFY_QUANTITY)
            
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "Verify the current quantity for each item",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                
                items(
                    items = uiState.supplyItems,
                    key = { it.itemId }
                ) { item ->
                    var localQuantity by remember {
                        mutableStateOf(
                            uiState.verifiedQuantities[item.itemId]?.toString()
                                ?: item.currentQuantity.toString()
                        )
                    }
                    
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = item.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = localQuantity,
                                    onValueChange = { newValue ->
                                        localQuantity = newValue
                                        newValue.toIntOrNull()?.let { qty ->
                                            viewModel.updateVerifiedQuantity(item.itemId, qty)
                                        }
                                    },
                                    label = { Text("Verified Quantity") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                                
                                Column(
                                    horizontalAlignment = Alignment.End
                                ) {
                                    Text(
                                        text = "System:",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = item.currentQuantity.toString(),
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
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