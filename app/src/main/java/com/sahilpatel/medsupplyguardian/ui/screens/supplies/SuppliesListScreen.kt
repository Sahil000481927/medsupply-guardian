/**
 * File Overview: Supplies List Screen composable
 * 
 * This file contains the supplies inventory list screen with search,
 * filtering, and sorting capabilities. Displays all supply items with
 * visual indicators for risk levels and stock status.
 * 
 * @author Sahil Patel
 * @version 1.2
 */

package com.sahilpatel.medsupplyguardian.ui.screens.supplies

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sahilpatel.medsupplyguardian.data.database.entities.SupplyItem
import com.sahilpatel.medsupplyguardian.data.preferences.UserPreferencesManager
import com.sahilpatel.medsupplyguardian.ui.components.FilterChipGroup
import com.sahilpatel.medsupplyguardian.ui.components.SupplyItemCard

/**
 * Supplies list screen composable with search and filter functionality.
 * 
 * Displays a searchable, filterable, and sortable list of all supply items
 * in the inventory with comprehensive item information and navigation to
 * detailed views.
 * 
 * @param onNavigateToDetails Callback to navigate to supply details screen
 * @param onNavigateBack Callback to navigate back to previous screen
 * @param viewModel ViewModel for supplies list state management
 * @param filterType Type of filter to apply (e.g., "risk", "expiring")
 * @param filterValue Value of the filter to apply
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuppliesListScreen(
    onNavigateToDetails: (Int) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: SuppliesViewModel = viewModel(),
    filterType: String?,
    filterValue: String?
) {
    val uiState by viewModel.listUiState.collectAsState()
    var showSortMenu by remember { mutableStateOf(false) }
    
    LaunchedEffect(filterType, filterValue) {
        if (filterType != null && filterValue != null) {
            viewModel.applyInitialFilter(filterType, filterValue)
        }
    }
    
    val sortingOptions = listOf(
        UserPreferencesManager.Companion.SortingModes.NAME_ASC,
        UserPreferencesManager.Companion.SortingModes.NAME_DESC,
        UserPreferencesManager.Companion.SortingModes.QUANTITY_LOW,
        UserPreferencesManager.Companion.SortingModes.QUANTITY_HIGH,
        UserPreferencesManager.Companion.SortingModes.EXPIRY_SOON,
        UserPreferencesManager.Companion.SortingModes.RISK_CRITICAL
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Supply Inventory") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showSortMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.Sort,
                            contentDescription = "Sort"
                        )
                    }
                    
                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false }
                    ) {
                        sortingOptions.forEach { mode ->
                            DropdownMenuItem(
                                text = { Text(mode) },
                                onClick = {
                                    viewModel.updateSortingMode(mode)
                                    showSortMenu = false
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
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
            ) {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    placeholder = { Text("Search supplies...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear search"
                                )
                            }
                        }
                    },
                    singleLine = true
                )
                
                FilterChipGroup(
                    items = listOf(SupplyItem.Companion.Categories.PPE, SupplyItem.Companion.Categories.MEDICATION, SupplyItem.Companion.Categories.SURGICAL_KIT, SupplyItem.Companion.Categories.DEVICE),
                    selectedItem = uiState.selectedCategory,
                    onItemSelected = { viewModel.updateCategoryFilter(it) }
                )

                Spacer(modifier = Modifier.height(8.dp))

                FilterChipGroup(
                    items = listOf(SupplyItem.Companion.RiskLevels.CRITICAL, SupplyItem.Companion.RiskLevels.ELEVATED, SupplyItem.Companion.RiskLevels.NORMAL),
                    selectedItem = uiState.selectedRiskLevel,
                    onItemSelected = { viewModel.updateRiskFilter(it) }
                )

                Spacer(modifier = Modifier.height(16.dp))
                
                if (uiState.supplyItems.isEmpty()) {
                    EmptyState()
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(
                            items = uiState.supplyItems,
                            key = { it.itemId }
                        ) { item ->
                            SupplyItemCard(
                                item = item,
                                onClick = { onNavigateToDetails(item.itemId) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Inventory2,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "No supplies found",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Try adjusting your search or filters.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}