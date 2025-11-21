/**
 * File Overview: Settings Screen composable
 * 
 * This file contains the settings screen, allowing users to configure
 * personal information, app behavior, and notification preferences.
 * 
 * @author Sahil Patel
 * @version 1.3
 */

package com.sahilpatel.medsupplyguardian.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * Settings screen composable for user preferences.
 * 
 * Provides UI for updating staff information, sorting preferences,
 * theme, and alert settings. Changes are saved when the user presses the save button.
 * 
 * @param onNavigateBack Callback to navigate back to the previous screen
 * @param viewModel ViewModel for settings state management
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            // Add a Save button at the bottom
            FilledTonalButton(
                onClick = { 
                    viewModel.saveSettings()
                    onNavigateBack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Save Settings")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Staff Information",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            OutlinedTextField(
                value = uiState.staffName,
                onValueChange = { viewModel.updateStaffName(it) },
                label = { Text("Staff Name") },
                modifier = Modifier.fillMaxWidth()
            )
            
            OutlinedTextField(
                value = uiState.staffId,
                onValueChange = { viewModel.updateStaffId(it) },
                label = { Text("Staff ID") },
                modifier = Modifier.fillMaxWidth()
            )
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            
            Text(
                text = "App Preferences",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            SortingPreference(viewModel = viewModel)
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Dark Theme")
                Switch(
                    checked = uiState.isDarkTheme,
                    onCheckedChange = { viewModel.updateThemeMode(it) }
                )
            }
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            
            Text(
                text = "Notifications & Alerts",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            AlertThresholdPreference(viewModel = viewModel)
            
            AuditReminderPreference(viewModel = viewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SortingPreference(viewModel: SettingsViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = uiState.sortingMode,
            onValueChange = {}, // Read-only
            label = { Text("Default Sorting Mode") },
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            viewModel.getSortingModeOptions().forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        viewModel.updateSortingMode(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun AlertThresholdPreference(viewModel: SettingsViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    
    Column {
        Text("Expiration Alert Threshold: ${uiState.alertThreshold} days")
        Slider(
            value = uiState.alertThreshold.toFloat(),
            onValueChange = { viewModel.updateAlertThreshold(it.toInt()) },
            valueRange = 1f..90f,
            steps = 88
        )
    }
}

@Composable
private fun AuditReminderPreference(viewModel: SettingsViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    
    Column {
        Text("Audit Reminder: ${uiState.auditReminderHours} hours")
        Slider(
            value = uiState.auditReminderHours.toFloat(),
            onValueChange = { viewModel.updateAuditReminderHours(it.toInt()) },
            valueRange = 1f..48f,
            steps = 46
        )
    }
}