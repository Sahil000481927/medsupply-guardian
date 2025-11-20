/**
 * File Overview: Supplies List Screen composable
 * 
 * This file contains the supplies inventory list screen with search,
 * filtering, and sorting capabilities. Displays all supply items with
 * visual indicators for risk levels and stock status.
 * 
 * @author Sahil Patel
 * @version 1.0
 */

package com.sahilpatel.medsupplyguardian.ui.screens.supplies

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sahilpatel.medsupplyguardian.data.database.entities.SupplyItem
import com.sahilpatel.medsupplyguardian.data.preferences.UserPreferencesManager

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
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuppliesListScreen(
    onNavigateToDetails: (Int) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: SuppliesViewModel = viewModel()
) {
    val uiState by viewModel.listUiState.collectAsState()
    var showFilterMenu by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }
    
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
                    IconButton(onClick = { showFilterMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filter"
                        )
                    }
                    IconButton(onClick = { showSortMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.Sort,
                            contentDescription = "Sort"
                        )
                    }
                    
                    DropdownMenu(
                        expanded = showFilterMenu,
                        onDismissRequest = { showFilterMenu = false }
                    ) {
                        Text(
                            text = "Filter by Category",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                        DropdownMenuItem(
                            text = { Text("All Categories") },
                            onClick = {
                                viewModel.updateCategoryFilter(null)
                                showFilterMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(SupplyItem.Companion.Categories.PPE) },
                            onClick = {
                                viewModel.updateCategoryFilter(SupplyItem.Companion.Categories.PPE)
                                showFilterMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(SupplyItem.Companion.Categories.MEDICATION) },
                            onClick = {
                                viewModel.updateCategoryFilter(SupplyItem.Companion.Categories.MEDICATION)
                                showFilterMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(SupplyItem.Companion.Categories.SURGICAL_KIT) },
                            onClick = {
                                viewModel.updateCategoryFilter(SupplyItem.Companion.Categories.SURGICAL_KIT)
                                showFilterMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(SupplyItem.Companion.Categories.DEVICE) },
                            onClick = {
                                viewModel.updateCategoryFilter(SupplyItem.Companion.Categories.DEVICE)
                                showFilterMenu = false
                            }
                        )
                        
                        Divider()
                        
                        Text(
                            text = "Filter by Risk",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                        DropdownMenuItem(
                            text = { Text("All Risk Levels") },
                            onClick = {
                                viewModel.updateRiskFilter(null)
                                showFilterMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(SupplyItem.Companion.RiskLevels.CRITICAL) },
                            onClick = {
                                viewModel.updateRiskFilter(SupplyItem.Companion.RiskLevels.CRITICAL)
                                showFilterMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(SupplyItem.Companion.RiskLevels.ELEVATED) },
                            onClick = {
                                viewModel.updateRiskFilter(SupplyItem.Companion.RiskLevels.ELEVATED)
                                showFilterMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(SupplyItem.Companion.RiskLevels.NORMAL) },
                            onClick = {
                                viewModel.updateRiskFilter(SupplyItem.Companion.RiskLevels.NORMAL)
                                showFilterMenu = false
                            }
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
            Text(text = "Loading supplies...")
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
                
                if (uiState.selectedCategory != null || uiState.selectedRiskLevel != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        uiState.selectedCategory?.let { category ->
                            FilterChip(
                                selected = true,
                                onClick = { viewModel.updateCategoryFilter(null) },
                                label = { Text(category) },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove filter",
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            )
                        }
                        uiState.selectedRiskLevel?.let { risk ->
                            FilterChip(
                                selected = true,
                                onClick = { viewModel.updateRiskFilter(null) },
                                label = { Text(risk) },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove filter",
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                if (uiState.supplyItems.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        Text(
                            text = "No supplies found",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(
                            items = uiState.supplyItems,
                            key = { it.itemId }
                        ) { item ->
                            Text(text = "SupplyItemCard")
                        }
                    }
                }
            }
        }
    }
}