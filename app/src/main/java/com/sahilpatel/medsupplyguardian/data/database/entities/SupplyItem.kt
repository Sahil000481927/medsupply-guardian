/**
 * File Overview: Supply Item entity for Room Database
 * 
 * This entity represents a single medical supply item in the inventory system.
 * Each item tracks quantity, expiration, location, and risk level for compliance
 * monitoring. The entity is mapped to the supply_items table in SQLite.
 * 
 * @author Sahil Patel
 * @version 1.0
 */

package com.sahilpatel.medsupplyguardian.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Supply Item entity representing medical inventory items.
 * 
 * This entity stores all critical information about medical supplies including
 * stock levels, expiration dates, storage locations, and computed risk levels
 * based on quantity thresholds and expiration proximity.
 * 
 * @property itemId Unique identifier for the supply item (auto-generated)
 * @property name Display name of the medical supply item
 * @property category Classification of item (PPE, Medication, Surgical Kit, Device)
 * @property minimumRequired Minimum stock threshold before alert is triggered
 * @property currentQuantity Current stock quantity available
 * @property expiryDate Expiration date stored as Unix timestamp (milliseconds)
 * @property location Physical storage location within the facility
 * @property riskLevel Computed risk classification (Critical, Elevated, Normal)
 */
@Entity(tableName = "supply_items")
data class SupplyItem(
    @PrimaryKey(autoGenerate = true)
    val itemId: Int = 0,
    
    val name: String,
    
    val category: String,
    
    val minimumRequired: Int,
    
    val currentQuantity: Int,
    
    val expiryDate: Long,
    
    val location: String,
    
    val riskLevel: String
) {
    /**
     * Companion object containing constants for supply item categories and risk levels.
     */
    companion object {
        /**
         * Available supply categories for classification.
         */
        object Categories {
            const val PPE = "PPE"
            const val MEDICATION = "Medication"
            const val SURGICAL_KIT = "Surgical Kit"
            const val DEVICE = "Device"
        }
        
        /**
         * Risk level classifications based on stock and expiration status.
         */
        object RiskLevels {
            const val CRITICAL = "Critical"
            const val ELEVATED = "Elevated"
            const val NORMAL = "Normal"
        }
    }
    
    /**
     * Determines if the item is below minimum required quantity threshold.
     * 
     * @return true if current quantity is below minimum required, false otherwise
     */
    fun isBelowMinimum(): Boolean = currentQuantity < minimumRequired
    
    /**
     * Calculates the number of days until the item expires.
     * 
     * @return Number of days until expiration (negative if already expired)
     */
    fun daysUntilExpiry(): Long {
        val currentTime = System.currentTimeMillis()
        val timeDiff = expiryDate - currentTime
        return timeDiff / (1000 * 60 * 60 * 24)
    }
    
    /**
     * Checks if the item is expiring within the specified number of days.
     * 
     * @param days Number of days to check against
     * @return true if item expires within the specified days, false otherwise
     */
    fun isExpiringWithin(days: Int): Boolean = daysUntilExpiry() <= days && daysUntilExpiry() >= 0
    
    /**
     * Determines if the item has already expired.
     * 
     * @return true if expiration date has passed, false otherwise
     */
    fun isExpired(): Boolean = System.currentTimeMillis() > expiryDate
}