/**
 * File Overview: DataStore Manager for User Settings
 * 
 * This manager handles all persistent user preferences including technician
 * information, UI settings, theme preferences, and alert thresholds. Uses
 * DataStore for modern, asynchronous, and reactive key-value storage.
 * 
 * @author Sahil Patel
 * @version 1.1
 */

package com.sahilpatel.medsupplyguardian.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

/**
 * Manager class for user preferences and settings.
 * 
 * Provides a clean API for storing and retrieving user preferences such as
 * technician identity, theme settings, sorting preferences, and alert thresholds.
 * All settings persist across application restarts and are exposed as Flows.
 * 
 * @property context Application context for accessing DataStore
 */
class UserPreferencesManager(private val context: Context) {

    /**
     * Companion object containing preference keys and default values.
     */
    companion object {
        val KEY_STAFF_NAME = stringPreferencesKey("staff_name")
        val KEY_STAFF_ID = stringPreferencesKey("staff_id")
        val KEY_SORTING_MODE = stringPreferencesKey("sorting_mode")
        val KEY_ALERT_THRESHOLD = intPreferencesKey("alert_threshold")
        val KEY_THEME_MODE = booleanPreferencesKey("theme_mode")
        val KEY_AUDIT_REMINDER_HOURS = intPreferencesKey("audit_reminder_hours")
        val KEY_IS_FIRST_LAUNCH = booleanPreferencesKey("is_first_launch")
        
        const val DEFAULT_STAFF_NAME = "Technician"
        const val DEFAULT_STAFF_ID = "TECH001"
        const val DEFAULT_SORTING_MODE = "Name (A-Z)"
        const val DEFAULT_ALERT_THRESHOLD = 30
        const val DEFAULT_THEME_MODE = false
        const val DEFAULT_AUDIT_REMINDER = 24
        
        object SortingModes {
            const val NAME_ASC = "Name (A-Z)"
            const val NAME_DESC = "Name (Z-A)"
            const val QUANTITY_LOW = "Quantity (Low to High)"
            const val QUANTITY_HIGH = "Quantity (High to Low)"
            const val EXPIRY_SOON = "Expiry (Soonest First)"
            const val RISK_CRITICAL = "Risk (Critical First)"
        }
    }

    val staffName: Flow<String> = context.dataStore.data.map {
        it[KEY_STAFF_NAME] ?: DEFAULT_STAFF_NAME
    }

    val staffId: Flow<String> = context.dataStore.data.map {
        it[KEY_STAFF_ID] ?: DEFAULT_STAFF_ID
    }

    val sortingMode: Flow<String> = context.dataStore.data.map {
        it[KEY_SORTING_MODE] ?: DEFAULT_SORTING_MODE
    }

    val alertThreshold: Flow<Int> = context.dataStore.data.map {
        it[KEY_ALERT_THRESHOLD] ?: DEFAULT_ALERT_THRESHOLD
    }

    val isDarkTheme: Flow<Boolean> = context.dataStore.data.map {
        it[KEY_THEME_MODE] ?: DEFAULT_THEME_MODE
    }

    val auditReminderHours: Flow<Int> = context.dataStore.data.map {
        it[KEY_AUDIT_REMINDER_HOURS] ?: DEFAULT_AUDIT_REMINDER
    }

    val isFirstLaunch: Flow<Boolean> = context.dataStore.data.map {
        it[KEY_IS_FIRST_LAUNCH] ?: true
    }

    suspend fun setStaffName(name: String) {
        context.dataStore.edit { it[KEY_STAFF_NAME] = name }
    }

    suspend fun setStaffId(id: String) {
        context.dataStore.edit { it[KEY_STAFF_ID] = id }
    }

    suspend fun setSortingMode(mode: String) {
        context.dataStore.edit { it[KEY_SORTING_MODE] = mode }
    }

    suspend fun setAlertThreshold(days: Int) {
        context.dataStore.edit { it[KEY_ALERT_THRESHOLD] = days }
    }

    suspend fun setDarkTheme(isDark: Boolean) {
        context.dataStore.edit { it[KEY_THEME_MODE] = isDark }
    }

    suspend fun setAuditReminderHours(hours: Int) {
        context.dataStore.edit { it[KEY_AUDIT_REMINDER_HOURS] = hours }
    }

    suspend fun setFirstLaunchComplete() {
        context.dataStore.edit { it[KEY_IS_FIRST_LAUNCH] = false }
    }
}