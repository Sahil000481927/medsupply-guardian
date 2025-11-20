/**
 * File Overview: Home Screen composable
 * 
 * This file contains the main dashboard screen displaying critical metrics,
 * alerts, and quick access navigation to key features. Shows real-time
 * inventory status and compliance monitoring overview.
 * 
 * @author Sahil Patel
 * @version 1.0
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
    onNavigateToSupplies: () -> Unit,
    onNavigateToAudit: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "MedSupply Guardian",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "Welcome, ${uiState.technicianName}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Text(text = "Loading dashboard...")
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Dashboard Overview",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                Text(text = "CriticalAlertCard")
                
                Text(text = "ExpiringItemsCard")
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Quick Actions",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ElevatedCard(
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToSupplies
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Inventory,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "View Supplies",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    
                    ElevatedCard(
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToAudit
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Assessment,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp),
                                tint = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = "Start Audit",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
                
                Text(text = "DashboardCard")
            }
        }
    }
}