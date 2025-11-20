/**
 * File Overview: ViewModel for Settings Screen
 * 
 * This ViewModel manages user preferences and settings, handling all
 * SharedPreferences operations including staff identity, UI preferences,
 * theme mode, and alert thresholds.
 * 
 * @author Sahil Patel
 * @version 1.0
 */

package com.sahilpatel.medsupplyguardian.ui.screens.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sahilpatel.medsupplyguardian.data.preferences.UserPreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UI state for the Settings screen.
 * 
 * Represents all user-configurable settings with their current values.
 * 
 * @property staffName Name of the staff member
 * @property staffId ID number of the staff member
 * @property sortingMode Current sorting preference for supplies list
 * @property alertThreshold Days threshold for expiration warnings
 * @property isDarkTheme Whether dark theme is enabled
 * @property auditReminderHours Hours between audit reminders
 * @property saveSuccess Whether settings were successfully saved
 */
data class SettingsUiState(
    val staffName: String = "",
    val staffId: String = "",
    val sortingMode: String = UserPreferencesManager.DEFAULT_SORTING_MODE,
    val alertThreshold: Int = UserPreferencesManager.DEFAULT_ALERT_THRESHOLD,
    val isDarkTheme: Boolean = UserPreferencesManager.DEFAULT_THEME_MODE,
    val auditReminderHours: Int = UserPreferencesManager.DEFAULT_AUDIT_REMINDER,
    val saveSuccess: Boolean = false
)

/**
 * ViewModel for managing Settings screen state and operations.
 * 
 * Handles loading and saving all user preferences using SharedPreferences.
 * Provides methods for updating individual settings and persisting changes.
 * 
 * @property application Application context for preferences access
 */
class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    
    private val preferencesManager: UserPreferencesManager = UserPreferencesManager(application)
    
    private val _uiState = MutableStateFlow(SettingsUiState())
    
    /**
     * Publicly exposed UI state.
     */
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    
    init {
        loadSettings()
    }
    
    /**
     * Loads all settings from SharedPreferences.
     * 
     * Retrieves current values for all user preferences and updates
     * the UI state accordingly.
     */
    private fun loadSettings() {
        viewModelScope.launch {
            _uiState.update {
                SettingsUiState(
                    staffName = preferencesManager.getStaffName(),
                    staffId = preferencesManager.getStaffId(),
                    sortingMode = preferencesManager.getSortingMode(),
                    alertThreshold = preferencesManager.getAlertThreshold(),
                    isDarkTheme = preferencesManager.isDarkTheme(),
                    auditReminderHours = preferencesManager.getAuditReminderHours()
                )
            }
        }
    }
    
    /**
     * Updates the staff name in UI state.
     * 
     * @param name New staff name value
     */
    fun updateStaffName(name: String) {
        _uiState.update { it.copy(staffName = name, saveSuccess = false) }
    }
    
    /**
     * Updates the staff ID in UI state.
     * 
     * @param id New staff ID value
     */
    fun updateStaffId(id: String) {
        _uiState.update { it.copy(staffId = id, saveSuccess = false) }
    }
    
    /**
     * Updates the sorting mode in UI state.
     * 
     * @param mode New sorting mode value
     */
    fun updateSortingMode(mode: String) {
        _uiState.update { it.copy(sortingMode = mode, saveSuccess = false) }
    }
    
    /**
     * Updates the alert threshold in UI state.
     * 
     * @param threshold New threshold value in days
     */
    fun updateAlertThreshold(threshold: Int) {
        _uiState.update { it.copy(alertThreshold = threshold, saveSuccess = false) }
    }
    
    /**
     * Toggles the dark theme setting.
     * 
     * @param isDark New dark theme state
     */
    fun updateThemeMode(isDark: Boolean) {
        _uiState.update { it.copy(isDarkTheme = isDark, saveSuccess = false) }
        preferencesManager.setDarkTheme(isDark)
    }
    
    /**
     * Updates the audit reminder hours in UI state.
     * 
     * @param hours New reminder interval in hours
     */
    fun updateAuditReminderHours(hours: Int) {
        _uiState.update { it.copy(auditReminderHours = hours, saveSuccess = false) }
    }
    
    /**
     * Saves all settings to SharedPreferences.
     * 
     * Persists all current values from the UI state to storage and
     * sets the save success flag for user feedback.
     */
    fun saveSettings() {
        viewModelScope.launch {
            val currentState = _uiState.value
            
            preferencesManager.setStaffName(currentState.staffName)
            preferencesManager.setStaffId(currentState.staffId)
            preferencesManager.setSortingMode(currentState.sortingMode)
            preferencesManager.setAlertThreshold(currentState.alertThreshold)
            preferencesManager.setDarkTheme(currentState.isDarkTheme)
            preferencesManager.setAuditReminderHours(currentState.auditReminderHours)
            
            _uiState.update { it.copy(saveSuccess = true) }
        }
    }
    
    /**
     * Resets the save success flag.
     * 
     * Called after displaying success feedback to clear the state.
     */
    fun clearSaveSuccess() {
        _uiState.update { it.copy(saveSuccess = false) }
    }
    
    /**
     * Available sorting mode options.
     * 
     * @return List of sorting mode strings
     */
    fun getSortingModeOptions(): List<String> {
        return listOf(
            UserPreferencesManager.Companion.SortingModes.NAME_ASC,
            UserPreferencesManager.Companion.SortingModes.NAME_DESC,
            UserPreferencesManager.Companion.SortingModes.QUANTITY_LOW,
            UserPreferencesManager.Companion.SortingModes.QUANTITY_HIGH,
            UserPreferencesManager.Companion.SortingModes.EXPIRY_SOON,
            UserPreferencesManager.Companion.SortingModes.RISK_CRITICAL
        )
    }
}
