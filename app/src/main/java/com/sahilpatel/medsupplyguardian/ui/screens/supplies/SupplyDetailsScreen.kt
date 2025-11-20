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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sahilpatel.medsupplyguardian.ui.components.QuantityUpdateDialog
import com.sahilpatel.medsupplyguardian.ui.components.SupplyDetailItem

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
            Text(text = "Loading item details...")
        } else if (uiState.supplyItem == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text("Item not found.")
            }
        } else {
            val item = uiState.supplyItem!!
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                SupplyDetailItem(label = "Item Name", value = item.name)
                SupplyDetailItem(label = "Category", value = item.category)
                SupplyDetailItem(label = "Location", value = item.location)
                SupplyDetailItem(label = "Risk Level", value = item.riskLevel)
                SupplyDetailItem(label = "Current Quantity", value = item.currentQuantity.toString())
                SupplyDetailItem(label = "Minimum Required", value = item.minimumRequired.toString())
                SupplyDetailItem(label = "Expires On", value = java.text.SimpleDateFormat("MM/dd/yyyy").format(java.util.Date(item.expiryDate)))
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