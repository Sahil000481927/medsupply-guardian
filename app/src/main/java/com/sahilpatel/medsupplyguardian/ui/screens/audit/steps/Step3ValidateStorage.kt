/**
 * File Overview: Audit Step 3 - Validate Storage Conditions
 * 
 * This file contains the third step of the audit workflow where technicians
 * validate that storage conditions meet regulatory requirements.
 * 
 * @author Sahil Patel
 * @version 1.0
 */

package com.sahilpatel.medsupplyguardian.ui.screens.audit.steps

import androidx.compose.foundation.layout.*
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
 * Audit Step 3 screen for validating storage conditions.
 * 
 * Provides checklist for verifying storage temperature, humidity,
 * lighting, and security conditions meet compliance standards.
 * 
 * @param onNavigateNext Callback to navigate to next audit step
 * @param onNavigateBack Callback to navigate back to previous step
 * @param viewModel ViewModel for audit state management
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Step3ValidateStorage(
    onNavigateNext: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: AuditViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var temperatureOk by remember { mutableStateOf(true) }
    var humidityOk by remember { mutableStateOf(true) }
    var lightingOk by remember { mutableStateOf(true) }
    var securityOk by remember { mutableStateOf(true) }
    
    val allConditionsPassed = temperatureOk && humidityOk && lightingOk && securityOk
    
    LaunchedEffect(allConditionsPassed) {
        viewModel.updateStorageConditions(allConditionsPassed)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Step 3: Validate Storage") },
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
            AuditStepProgress(currentStep = Constants.Audit.STEP_VALIDATE_STORAGE)
            
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Verify all storage conditions meet requirements",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = if (allConditionsPassed)
                            MaterialTheme.colorScheme.secondaryContainer
                        else MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (allConditionsPassed) Icons.Default.CheckCircle else Icons.Default.Error,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp)
                        )
                        Column {
                            Text(
                                text = "Storage Status",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (allConditionsPassed) "All conditions passed" else "Some conditions failed",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
                
                StorageCheckItem(
                    title = "Temperature Control",
                    description = "Storage areas maintain 2-8°C for refrigerated items",
                    isChecked = temperatureOk,
                    onCheckedChange = { temperatureOk = it },
                    icon = Icons.Default.Thermostat
                )
                
                StorageCheckItem(
                    title = "Humidity Levels",
                    description = "Relative humidity maintained below 60%",
                    isChecked = humidityOk,
                    onCheckedChange = { humidityOk = it },
                    icon = Icons.Default.WaterDrop
                )
                
                StorageCheckItem(
                    title = "Lighting Conditions",
                    description = "Protected from direct sunlight and UV exposure",
                    isChecked = lightingOk,
                    onCheckedChange = { lightingOk = it },
                    icon = Icons.Default.LightMode
                )
                
                StorageCheckItem(
                    title = "Security & Access",
                    description = "Controlled access and proper locking mechanisms",
                    isChecked = securityOk,
                    onCheckedChange = { securityOk = it },
                    icon = Icons.Default.Lock
                )
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

/**
 * Individual storage condition check item.
 * 
 * @param title Condition title
 * @param description Condition description
 * @param isChecked Whether the condition is met
 * @param onCheckedChange Callback when check state changes
 * @param icon Icon for the condition
 */
@Composable
fun StorageCheckItem(
    title: String,
    description: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Checkbox(
                checked = isChecked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}