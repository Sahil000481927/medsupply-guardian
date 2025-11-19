/**
 * File Overview: SharedPreferences Manager for User Settings
 * 
 * This manager handles all persistent user preferences including technician
 * information, UI settings, theme preferences, and alert thresholds. Uses
 * SharedPreferences for simple key-value storage that persists across app restarts.
 * 
 * @author Sahil Patel
 * @version 1.0
 */

package com.sahilpatel.medsupplyguardian.data.preferences

import android.content.Context
import android.content.SharedPreferences

/**
 * Manager class for user preferences and settings.
 * 
 * Provides a clean API for storing and retrieving user preferences such as
 * technician identity, theme settings, sorting preferences, and alert thresholds.
 * All settings persist across application restarts.
 * 
 * @property context Application context for accessing SharedPreferences
 */
class UserPreferencesManager(context: Context) {
    
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    /**
     * Companion object containing preference keys and default values.
     */
    companion object {
        private const val PREFS_NAME = "medsupply_guardian_prefs"
        
        private const val KEY_STAFF_NAME = "staff_name"
        private const val KEY_STAFF_ID = "staff_id"
        private const val KEY_SORTING_MODE = "sorting_mode"
        private const val KEY_ALERT_THRESHOLD = "alert_threshold"
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_AUDIT_REMINDER_HOURS = "audit_reminder_hours"
        private const val KEY_IS_FIRST_LAUNCH = "is_first_launch"
        
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
    
    /**
     * Gets the stored staff name.
     * 
     * @return Staff name or default value if not set
     */
    fun getStaffName(): String {
        return sharedPreferences.getString(KEY_STAFF_NAME, DEFAULT_STAFF_NAME) 
            ?: DEFAULT_STAFF_NAME
    }
    
    /**
     * Sets the staff name.
     * 
     * @param name The staff member's name to store
     */
    fun setStaffName(name: String) {
        sharedPreferences.edit().putString(KEY_STAFF_NAME, name).apply()
    }
    
    /**
     * Gets the stored staff ID.
     * 
     * @return Staff ID or default value if not set
     */
    fun getStaffId(): String {
        return sharedPreferences.getString(KEY_STAFF_ID, DEFAULT_STAFF_ID) 
            ?: DEFAULT_STAFF_ID
    }
    
    /**
     * Sets the staff ID.
     * 
     * @param id The staff member's ID to store
     */
    fun setStaffId(id: String) {
        sharedPreferences.edit().putString(KEY_STAFF_ID, id).apply()
    }
    
    /**
     * Gets the current sorting mode preference.
     * 
     * @return Sorting mode string or default value if not set
     */
    fun getSortingMode(): String {
        return sharedPreferences.getString(KEY_SORTING_MODE, DEFAULT_SORTING_MODE) 
            ?: DEFAULT_SORTING_MODE
    }
    
    /**
     * Sets the sorting mode preference.
     * 
     * @param mode The sorting mode to store
     */
    fun setSortingMode(mode: String) {
        sharedPreferences.edit().putString(KEY_SORTING_MODE, mode).apply()
    }
    
    /**
     * Gets the alert threshold in days for expiration warnings.
     * 
     * @return Alert threshold in days (default: 30)
     */
    fun getAlertThreshold(): Int {
        return sharedPreferences.getInt(KEY_ALERT_THRESHOLD, DEFAULT_ALERT_THRESHOLD)
    }
    
    /**
     * Sets the alert threshold for expiration warnings.
     * 
     * @param days Number of days before expiration to trigger alerts
     */
    fun setAlertThreshold(days: Int) {
        sharedPreferences.edit().putInt(KEY_ALERT_THRESHOLD, days).apply()
    }
    
    /**
     * Gets the theme mode preference.
     * 
     * @return true for dark theme, false for light theme
     */
    fun isDarkTheme(): Boolean {
        return sharedPreferences.getBoolean(KEY_THEME_MODE, DEFAULT_THEME_MODE)
    }
    
    /**
     * Sets the theme mode preference.
     * 
     * @param isDark true to enable dark theme, false for light theme
     */
    fun setDarkTheme(isDark: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_THEME_MODE, isDark).apply()
    }
    
    /**
     * Gets the audit reminder interval in hours.
     * 
     * @return Audit reminder interval in hours (default: 24)
     */
    fun getAuditReminderHours(): Int {
        return sharedPreferences.getInt(KEY_AUDIT_REMINDER_HOURS, DEFAULT_AUDIT_REMINDER)
    }
    
    /**
     * Sets the audit reminder interval.
     * 
     * @param hours Number of hours between audit reminders
     */
    fun setAuditReminderHours(hours: Int) {
        sharedPreferences.edit().putInt(KEY_AUDIT_REMINDER_HOURS, hours).apply()
    }
    
    /**
     * Checks if this is the first launch of the application.
     * 
     * Used to determine whether to show onboarding or initial setup.
     * 
     * @return true if first launch, false otherwise
     */
    fun isFirstLaunch(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_FIRST_LAUNCH, true)
    }
    
    /**
     * Marks the first launch as complete.
     * 
     * Should be called after initial setup is finished.
     */
    fun setFirstLaunchComplete() {
        sharedPreferences.edit().putBoolean(KEY_IS_FIRST_LAUNCH, false).apply()
    }
    
    /**
     * Clears all stored preferences.
     * 
     * Resets the application to default settings. Use with caution.
     */
    fun clearAllPreferences() {
        sharedPreferences.edit().clear().apply()
    }
    
    /**
     * Exports all preferences as a Map for backup or debugging.
     * 
     * @return Map of all preference key-value pairs
     */
    fun exportPreferences(): Map<String, *> {
        return sharedPreferences.all
    }
}