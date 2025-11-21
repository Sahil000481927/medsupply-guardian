/**
 * File Overview: Home Screen composable
 * 
 * This file contains the main dashboard screen displaying critical metrics,
 * alerts, and quick access navigation to key features. Shows real-time
 * inventory status and compliance monitoring overview.
 * 
 * @author Sahil Patel
 * @version 1.2
 */

package com.sahilpatel.medsupplyguardian.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sahilpatel.medsupplyguardian.data.database.entities.SupplyItem
import com.sahilpatel.medsupplyguardian.ui.components.CriticalAlertCard
import com.sahilpatel.medsupplyguardian.ui.components.ExpiringItemsCard

/**
 * Home screen composable displaying the main dashboard.
 * 
 * Shows a personalized greeting, critical stock alerts, expiring items,
 * and quick access buttons for inventory management and audit workflows.
 * 
 * @param onNavigateToSupplies Callback to navigate to supplies list screen
 * @param onNavigateToAudit Callback to navigate to audit start screen
 * @param onNavigateToSettings Callback to navigate to settings screen
 * @param viewModel ViewModel for home screen state management
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToSupplies: (String?, String?) -> Unit,
    onNavigateToAudit: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("MedSupply Guardian") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
            ) {
                Text(
                    text = "Welcome, ${uiState.technicianName}",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                CriticalAlertCard(
                    count = uiState.criticalStockCount,
                    onClick = { onNavigateToSupplies("risk", SupplyItem.Companion.RiskLevels.CRITICAL) }
                )
                
                Spacer(modifier = Modifier.height(8.dp))

                ExpiringItemsCard(
                    count = uiState.expiringSoonCount,
                    daysThreshold = uiState.alertThreshold,
                    onClick = { onNavigateToSupplies("expiring", uiState.alertThreshold.toString()) }
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { onNavigateToSupplies(null, null) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("View Supplies")
                    }
                    FilledTonalButton(
                        onClick = onNavigateToAudit,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Start Audit")
                    }
                    OutlinedButton(
                        onClick = onNavigateToSettings,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Settings")
                    }
                }
            }
        }
    }
}