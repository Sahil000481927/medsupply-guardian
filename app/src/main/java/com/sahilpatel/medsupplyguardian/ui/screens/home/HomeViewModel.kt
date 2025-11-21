/**
 * File Overview: ViewModel for Home Screen
 * 
 * This ViewModel manages the state and business logic for the home dashboard
 * screen, including loading critical items, expiring items, and displaying
 * summary metrics for quick decision-making.
 * 
 * @author Sahil Patel
 * @version 1.1
 */

package com.sahilpatel.medsupplyguardian.ui.screens.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sahilpatel.medsupplyguardian.data.database.AppDatabase
import com.sahilpatel.medsupplyguardian.data.preferences.UserPreferencesManager
import com.sahilpatel.medsupplyguardian.data.repository.SupplyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
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
 * @property criticalStockCount Number of items below minimum required quantity
 * @property expiringSoonCount Number of items expiring within threshold
 * @property alertThreshold Number of days threshold for expiration warnings
 */
data class HomeUiState(
    val isLoading: Boolean = true,
    val technicianName: String = "Technician",
    val criticalStockCount: Int = 0,
    val expiringSoonCount: Int = 0,
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
        
        viewModelScope.launch {
            combine(
                preferencesManager.staffName,
                repository.getCriticalItems(),
                preferencesManager.alertThreshold.flatMapLatest { threshold ->
                    repository.getExpiringItems(threshold)
                }
            ) { staffName, criticalItems, expiringItems ->
                _uiState.update {
                    it.copy(
                        technicianName = staffName,
                        criticalStockCount = criticalItems.size,
                        expiringSoonCount = expiringItems.size,
                        alertThreshold = expiringItems.firstOrNull()?.daysUntilExpiry()?.toInt() ?: 30,
                        isLoading = false
                    )
                }
            }.collect {}
        }
    }
}