/**
 * File Overview: ViewModel for Home Screen
 * 
 * This ViewModel manages the state and business logic for the home dashboard
 * screen, including loading critical items, expiring items, and displaying
 * summary metrics for quick decision-making.
 * 
 * @author Sahil Patel
 * @version 1.0
 */

package com.sahilpatel.medsupplyguardian.ui.screens.home

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
 * UI state for the Home screen.
 * 
 * Represents the current state of the dashboard including loading status,
 * critical alerts, and expiring items counts.
 * 
 * @property isLoading Whether data is currently being loaded from the database
 * @property technicianName Name of the current user from SharedPreferences
 * @property criticalItemsCount Number of items below minimum required quantity
 * @property expiringItemsCount Number of items expiring within threshold
 * @property alertThreshold Number of days threshold for expiration warnings
 */
data class HomeUiState(
    val isLoading: Boolean = true,
    val technicianName: String = "Technician",
    val criticalItemsCount: Int = 0,
    val expiringItemsCount: Int = 0,
    val alertThreshold: Int = 30
)

/**
 * ViewModel for managing Home screen state and operations.
 * 
 * Observes supply item data from the repository and calculates dashboard
 * metrics including critical stock alerts and expiring items. Retrieves
 * user preferences for personalization.
 * 
 * @property application Application context for database and preferences access
 */
class HomeViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: SupplyRepository
    private val preferencesManager: UserPreferencesManager
    
    private val _uiState = MutableStateFlow(HomeUiState())
    
    /**
     * Publicly exposed UI state as immutable StateFlow.
     */
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    init {
        val database = AppDatabase.getDatabase(application)
        repository = SupplyRepository(database.supplyItemDao())
        preferencesManager = UserPreferencesManager(application)
        
        loadDashboardData()
    }
    
    /**
     * Loads dashboard data from repository and preferences.
     * 
     * Observes critical items and expiring items from the database,
     * retrieves technician name from SharedPreferences, and updates
     * the UI state accordingly.
     */
    private fun loadDashboardData() {
        viewModelScope.launch {
            val techName = preferencesManager.getStaffName()
            val threshold = preferencesManager.getAlertThreshold()
            
            _uiState.update { it.copy(
                technicianName = techName,
                alertThreshold = threshold
            )}
            
            repository.getCriticalItems().collect { criticalItems ->
                _uiState.update { it.copy(
                    criticalItemsCount = criticalItems.size
                )}
            }
        }
        
        viewModelScope.launch {
            val threshold = preferencesManager.getAlertThreshold()
            repository.getExpiringItems(threshold).collect { expiringItems ->
                _uiState.update { it.copy(
                    expiringItemsCount = expiringItems.size,
                    isLoading = false
                )}
            }
        }
    }
    
    /**
     * Refreshes dashboard data manually.
     * 
     * Can be called when returning from other screens to ensure
     * metrics are up-to-date.
     */
    fun refreshDashboard() {
        _uiState.update { it.copy(isLoading = true) }
        loadDashboardData()
    }
}
