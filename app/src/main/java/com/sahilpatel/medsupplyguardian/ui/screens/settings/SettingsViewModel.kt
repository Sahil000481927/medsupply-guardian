/**
 * File Overview: ViewModel for Settings Screen
 * 
 * This ViewModel manages user preferences and settings, handling all
 * DataStore operations including staff identity, UI preferences,
 * theme mode, and alert thresholds.
 * 
 * @author Sahil Patel
 * @version 1.6
 */

package com.sahilpatel.medsupplyguardian.ui.screens.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sahilpatel.medsupplyguardian.data.preferences.UserPreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
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
 * Handles loading and saving all user preferences using DataStore.
 * Provides methods for updating individual settings and persisting changes.
 * 
 * @property application Application context for preferences access
 */
class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    
    private val preferencesManager: UserPreferencesManager = UserPreferencesManager(application)

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadInitialSettings()
    }

    private fun loadInitialSettings() {
        viewModelScope.launch {
            val staffName = preferencesManager.staffName.first()
            val staffId = preferencesManager.staffId.first()
            val sortingMode = preferencesManager.sortingMode.first()
            val alertThreshold = preferencesManager.alertThreshold.first()
            val isDarkTheme = preferencesManager.isDarkTheme.first()
            val auditReminderHours = preferencesManager.auditReminderHours.first()
            _uiState.value = SettingsUiState(
                staffName = staffName,
                staffId = staffId,
                sortingMode = sortingMode,
                alertThreshold = alertThreshold,
                isDarkTheme = isDarkTheme,
                auditReminderHours = auditReminderHours
            )
        }
    }

    fun updateStaffName(name: String) {
        _uiState.update { it.copy(staffName = name) }
    }

    fun updateStaffId(id: String) {
        _uiState.update { it.copy(staffId = id) }
    }

    fun updateSortingMode(mode: String) {
        _uiState.update { it.copy(sortingMode = mode) }
    }

    fun updateAlertThreshold(threshold: Int) {
        _uiState.update { it.copy(alertThreshold = threshold) }
    }

    fun updateThemeMode(isDark: Boolean) {
        _uiState.update { it.copy(isDarkTheme = isDark) }
    }

    fun updateAuditReminderHours(hours: Int) {
        _uiState.update { it.copy(auditReminderHours = hours) }
    }

    fun saveSettings() {
        viewModelScope.launch {
            preferencesManager.setStaffName(_uiState.value.staffName)
            preferencesManager.setStaffId(_uiState.value.staffId)
            preferencesManager.setSortingMode(_uiState.value.sortingMode)
            preferencesManager.setAlertThreshold(_uiState.value.alertThreshold)
            preferencesManager.setDarkTheme(_uiState.value.isDarkTheme)
            preferencesManager.setAuditReminderHours(_uiState.value.auditReminderHours)
            _uiState.update { it.copy(saveSuccess = true) }
        }
    }

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