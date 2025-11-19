/**
 * File Overview: Application-wide constants and configuration values
 * 
 * This file contains all constant values used throughout the application
 * including navigation routes, timeouts, thresholds, and configuration
 * parameters for consistent behavior across all modules.
 * 
 * @author Sahil Patel
 * @version 1.0
 */

package com.sahilpatel.medsupplyguardian.utils

/**
 * Object containing all application-wide constant values.
 */
object Constants {
    
    /**
     * Navigation routes for screen navigation.
     */
    object Routes {
        const val HOME = "home"
        const val SUPPLIES = "supplies"
        const val SUPPLY_DETAILS = "supply/{itemId}"
        const val AUDIT_START = "audit/start"
        const val AUDIT_STEP = "audit/step/{stepNumber}"
        const val AUDIT_SUMMARY = "audit/summary"
        const val SETTINGS = "settings"
        
        fun supplyDetails(itemId: Int) = "supply/$itemId"
        fun auditStep(stepNumber: Int) = "audit/step/$stepNumber"
    }
    
    /**
     * Audit workflow configuration.
     */
    object Audit {
        const val TOTAL_STEPS = 5
        const val STEP_VERIFY_QUANTITY = 1
        const val STEP_CHECK_EXPIRY = 2
        const val STEP_VALIDATE_STORAGE = 3
        const val STEP_MISSING_DAMAGED = 4
        const val STEP_SUMMARY = 5
        
        const val SIMULATED_UPLOAD_DELAY_MS = 2500L
    }
    
    /**
     * Expiration threshold values in days.
     */
    object ExpiryThresholds {
        const val CRITICAL_DAYS = 7
        const val ELEVATED_DAYS = 30
        const val WARNING_DAYS = 60
        const val EXTENDED_DAYS = 90
    }
    
    /**
     * Risk level color identifiers.
     */
    object RiskColors {
        const val CRITICAL = "error"
        const val ELEVATED = "warning"
        const val NORMAL = "primary"
    }
    
    /**
     * Loading and timeout configurations.
     */
    object Loading {
        const val STARTUP_DELAY_MS = 1500L
        const val MIN_LOADING_TIME_MS = 500L
        const val MAX_LOADING_TIME_MS = 3000L
    }
    
    /**
     * UI configuration values.
     */
    object UI {
        const val DEFAULT_PADDING_DP = 16
        const val CARD_SPACING_DP = 12
        const val MIN_TOUCH_TARGET_DP = 48
        const val CORNER_RADIUS_DP = 12
    }
    
    /**
     * Database seeding and testing values.
     */
    object Database {
        const val SEED_ITEM_COUNT = 15
        const val BACKUP_INTERVAL_HOURS = 24
    }
}