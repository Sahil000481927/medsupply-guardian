/**
 * File Overview: Supply Details Screen composable
 * 
 * This file contains the detailed view screen for a single supply item.
 * It displays comprehensive information and allows users to update stock
 * quantities through a dialog interface.
 * 
 * @author Sahil Patel
 * @version 1.0
 */

package com.sahilpatel.medsupplyguardian.ui.screens.supplies

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sahilpatel.medsupplyguardian.ui.components.QuantityUpdateDialog
import java.text.SimpleDateFormat
import java.util.Date

/**
 * Supply details screen composable.
 * 
 * Displays detailed information about a specific supply item and provides
 * an interface for updating the item's quantity.
 * 
 * @param itemId The unique identifier of the supply item to display
 * @param onNavigateBack Callback to navigate back to the previous screen
 * @param viewModel ViewModel for managing supply details state
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupplyDetailsScreen(
    itemId: Int,
    onNavigateBack: () -> Unit,
    viewModel: SuppliesViewModel = viewModel()
) {
    val uiState by viewModel.detailsUiState.collectAsState()
    
    LaunchedEffect(itemId) {
        viewModel.loadSupplyItemDetails(itemId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.supplyItem?.name ?: "Supply Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.showUpdateDialog() }) {
                Icon(Icons.Default.Edit, contentDescription = "Update Quantity")
            }
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.supplyItem == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Item not found.")
            }
        } else {
            val item = uiState.supplyItem!!
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // General Information Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "General Information", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 8.dp))
                        DetailRow(label = "Item Name", value = item.name)
                        DetailRow(label = "Category", value = item.category)
                        DetailRow(label = "Location", value = item.location)
                        DetailRow(label = "Risk Level", value = item.riskLevel)
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Stock Information Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Stock Information", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 8.dp))
                        DetailRow(label = "Current Quantity", value = item.currentQuantity.toString())
                        DetailRow(label = "Minimum Required", value = item.minimumRequired.toString())
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Expiration Information Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Expiration Information", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 8.dp))
                        DetailRow(label = "Expires On", value = SimpleDateFormat("MM/dd/yyyy").format(Date(item.expiryDate)))
                    }
                }
            }
            
            if (uiState.showUpdateDialog) {
                QuantityUpdateDialog(
                    uiState = uiState,
                    onDismiss = { viewModel.hideUpdateDialog() },
                    onQuantityChange = { viewModel.updateTempQuantity(it) },
                    onSave = { viewModel.saveQuantity() },
                    onIncrement = { viewModel.incrementQuantity() },
                    onDecrement = { viewModel.decrementQuantity() }
                )
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
    }
}