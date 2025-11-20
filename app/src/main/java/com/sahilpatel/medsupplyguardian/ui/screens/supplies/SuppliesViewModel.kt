/**
 * File Overview: ViewModel for Supplies List and Details Screens
 * 
 * This ViewModel manages the state for both the supplies list screen and
 * individual supply details screen, handling search, filtering, sorting,
 * and quantity updates with proper state management.
 * 
 * @author Sahil Patel
 * @version 1.0
 */

package com.sahilpatel.medsupplyguardian.ui.screens.supplies

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sahilpatel.medsupplyguardian.data.database.AppDatabase
import com.sahilpatel.medsupplyguardian.data.database.entities.SupplyItem
import com.sahilpatel.medsupplyguardian.data.preferences.UserPreferencesManager
import com.sahilpatel.medsupplyguardian.data.repository.SupplyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UI state for the Supplies List screen.
 * 
 * Represents the current state including loaded items, filters, search query,
 * and loading status.
 * 
 * @property isLoading Whether data is currently being loaded
 * @property supplyItems List of supply items to display
 * @property searchQuery Current search keyword
 * @property selectedCategory Selected category filter or null for all
 * @property selectedRiskLevel Selected risk level filter or null for all
 * @property sortingMode Current sorting mode from preferences
 */
data class SuppliesListUiState(
    val isLoading: Boolean = true,
    val supplyItems: List<SupplyItem> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: String? = null,
    val selectedRiskLevel: String? = null,
    val sortingMode: String = UserPreferencesManager.DEFAULT_SORTING_MODE
)

/**
 * UI state for the Supply Details screen.
 * 
 * Represents the state of a single supply item being viewed or edited.
 * 
 * @property isLoading Whether item data is being loaded
 * @property supplyItem The supply item being displayed or null if not found
 * @property showUpdateDialog Whether the quantity update dialog is visible
 * @property tempQuantity Temporary quantity value while editing
 * @property quantityError Error message for invalid quantity input
 */
data class SupplyDetailsUiState(
    val isLoading: Boolean = true,
    val supplyItem: SupplyItem? = null,
    val showUpdateDialog: Boolean = false,
    val tempQuantity: String = "",
    val quantityError: String? = null
)

/**
 * ViewModel for managing Supplies screens state and operations.
 * 
 * Handles data loading, filtering, searching, sorting for the list screen,
 * and quantity updates for the details screen. Maintains separation between
 * list and details state for proper screen lifecycle management.
 * 
 * @property application Application context for database and preferences access
 */
class SuppliesViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: SupplyRepository
    private val preferencesManager: UserPreferencesManager
    
    private val _listUiState = MutableStateFlow(SuppliesListUiState())
    private val _detailsUiState = MutableStateFlow(SupplyDetailsUiState())
    
    /**
     * Publicly exposed list UI state.
     */
    val listUiState: StateFlow<SuppliesListUiState> = _listUiState.asStateFlow()
    
    /**
     * Publicly exposed details UI state.
     */
    val detailsUiState: StateFlow<SupplyDetailsUiState> = _detailsUiState.asStateFlow()
    
    init {
        val database = AppDatabase.getDatabase(application)
        repository = SupplyRepository(database.supplyItemDao())
        preferencesManager = UserPreferencesManager(application)
        
        loadSupplyItems()
    }
    
    /**
     * Loads supply items based on current filters and search query.
     * 
     * Applies category filter, risk level filter, and search query,
     * then sorts the results according to user preferences.
     */
    private fun loadSupplyItems() {
        viewModelScope.launch {
            val sortingMode = preferencesManager.getSortingMode()
            _listUiState.update { it.copy(sortingMode = sortingMode) }
            
            val currentState = _listUiState.value
            val flow = when {
                currentState.searchQuery.isNotEmpty() -> {
                    repository.searchItems(currentState.searchQuery)
                }
                currentState.selectedCategory != null -> {
                    repository.filterByCategory(currentState.selectedCategory)
                }
                currentState.selectedRiskLevel != null -> {
                    repository.filterByRisk(currentState.selectedRiskLevel)
                }
                else -> {
                    repository.getAllItems()
                }
            }
            
            flow.collect { items ->
                val sortedItems = sortItems(items, sortingMode)
                _listUiState.update { it.copy(
                    supplyItems = sortedItems,
                    isLoading = false
                )}
            }
        }
    }
    
    /**
     * Updates the search query and reloads items.
     * 
     * @param query Search keyword to filter by item name
     */
    fun updateSearchQuery(query: String) {
        _listUiState.update { it.copy(searchQuery = query, isLoading = true) }
        loadSupplyItems()
    }
    
    /**
     * Updates the selected category filter and reloads items.
     * 
     * @param category Category to filter by or null to clear filter
     */
    fun updateCategoryFilter(category: String?) {
        _listUiState.update { it.copy(
            selectedCategory = category,
            searchQuery = "",
            isLoading = true
        )}
        loadSupplyItems()
    }
    
    /**
     * Updates the selected risk level filter and reloads items.
     * 
     * @param riskLevel Risk level to filter by or null to clear filter
     */
    fun updateRiskFilter(riskLevel: String?) {
        _listUiState.update { it.copy(
            selectedRiskLevel = riskLevel,
            searchQuery = "",
            isLoading = true
        )}
        loadSupplyItems()
    }
    
    /**
     * Updates the sorting mode and persists to SharedPreferences.
     * 
     * @param sortingMode New sorting mode to apply
     */
    fun updateSortingMode(sortingMode: String) {
        preferencesManager.setSortingMode(sortingMode)
        _listUiState.update { it.copy(sortingMode = sortingMode) }
        loadSupplyItems()
    }
    
    /**
     * Clears all active filters and search query.
     */
    fun clearAllFilters() {
        _listUiState.update { it.copy(
            searchQuery = "",
            selectedCategory = null,
            selectedRiskLevel = null,
            isLoading = true
        )}
        loadSupplyItems()
    }
    
    /**
     * Loads a specific supply item for the details screen.
     * 
     * @param itemId Unique identifier of the supply item to load
     */
    fun loadSupplyItemDetails(itemId: Int) {
        viewModelScope.launch {
            repository.getItemById(itemId).collect { item ->
                _detailsUiState.update { it.copy(
                    supplyItem = item,
                    isLoading = false,
                    tempQuantity = item?.currentQuantity?.toString() ?: ""
                )}
            }
        }
    }
    
    /**
     * Shows the quantity update dialog.
     */
    fun showUpdateDialog() {
        val currentQuantity = _detailsUiState.value.supplyItem?.currentQuantity?.toString() ?: ""
        _detailsUiState.update { it.copy(
            showUpdateDialog = true,
            tempQuantity = currentQuantity,
            quantityError = null
        )}
    }
    
    /**
     * Hides the quantity update dialog.
     */
    fun hideUpdateDialog() {
        _detailsUiState.update { it.copy(
            showUpdateDialog = false,
            quantityError = null
        )}
    }
    
    /**
     * Updates the temporary quantity value during editing.
     * 
     * @param quantity New quantity value as string
     */
    fun updateTempQuantity(quantity: String) {
        _detailsUiState.update { it.copy(
            tempQuantity = quantity,
            quantityError = null
        )}
    }
    
    /**
     * Increments the temporary quantity by 1.
     */
    fun incrementQuantity() {
        val current = _detailsUiState.value.tempQuantity.toIntOrNull() ?: 0
        _detailsUiState.update { it.copy(tempQuantity = (current + 1).toString()) }
    }
    
    /**
     * Decrements the temporary quantity by 1 (minimum 0).
     */
    fun decrementQuantity() {
        val current = _detailsUiState.value.tempQuantity.toIntOrNull() ?: 0
        if (current > 0) {
            _detailsUiState.update { it.copy(tempQuantity = (current - 1).toString()) }
        }
    }
    
    /**
     * Saves the updated quantity to the database.
     * 
     * Validates the input and updates the supply item if valid.
     * Shows error message if validation fails.
     */
    fun saveQuantity() {
        val quantity = _detailsUiState.value.tempQuantity.toIntOrNull()
        
        if (quantity == null || quantity < 0) {
            _detailsUiState.update { it.copy(
                quantityError = "Quantity must be a valid number greater than or equal to 0"
            )}
            return
        }
        
        val itemId = _detailsUiState.value.supplyItem?.itemId ?: return
        
        viewModelScope.launch {
            repository.updateQuantity(itemId, quantity)
            _detailsUiState.update { it.copy(showUpdateDialog = false) }
        }
    }
    
    /**
     * Sorts a list of supply items according to the specified sorting mode.
     * 
     * @param items List of items to sort
     * @param sortingMode Sorting mode from preferences
     * @return Sorted list of supply items
     */
    private fun sortItems(items: List<SupplyItem>, sortingMode: String): List<SupplyItem> {
        return when (sortingMode) {
            UserPreferencesManager.Companion.SortingModes.NAME_ASC -> {
                items.sortedBy { it.name }
            }
            UserPreferencesManager.Companion.SortingModes.NAME_DESC -> {
                items.sortedByDescending { it.name }
            }
            UserPreferencesManager.Companion.SortingModes.QUANTITY_LOW -> {
                items.sortedBy { it.currentQuantity }
            }
            UserPreferencesManager.Companion.SortingModes.QUANTITY_HIGH -> {
                items.sortedByDescending { it.currentQuantity }
            }
            UserPreferencesManager.Companion.SortingModes.EXPIRY_SOON -> {
                items.sortedBy { it.expiryDate }
            }
            UserPreferencesManager.Companion.SortingModes.RISK_CRITICAL -> {
                items.sortedBy { item ->
                    when (item.riskLevel) {
                        SupplyItem.Companion.RiskLevels.CRITICAL -> 0
                        SupplyItem.Companion.RiskLevels.ELEVATED -> 1
                        else -> 2
                    }
                }
            }
            else -> items
        }
    }
}
