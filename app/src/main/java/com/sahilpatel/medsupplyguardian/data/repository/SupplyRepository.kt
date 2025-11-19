/**
 * File Overview: Repository for Supply Items data operations
 * 
 * This repository provides a clean API for accessing supply item data from the
 * Room database. It abstracts the data layer from the UI layer, following the
 * repository pattern for better separation of concerns and testability.
 * 
 * @author Sahil Patel
 * @version 1.0
 */

package com.sahilpatel.medsupplyguardian.data.repository

import com.sahilpatel.medsupplyguardian.data.database.dao.SupplyItemDao
import com.sahilpatel.medsupplyguardian.data.database.entities.SupplyItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

/**
 * Repository for managing supply item data operations.
 * 
 * Provides a clean abstraction layer between the DAO and ViewModels,
 * handling data transformations and business logic related to supply
 * item management, filtering, and compliance monitoring.
 * 
 * @property supplyItemDao Data access object for supply items
 */
class SupplyRepository(private val supplyItemDao: SupplyItemDao) {
    
    /**
     * Retrieves all supply items as a Flow for reactive updates.
     * 
     * @return Flow emitting the complete list of supply items
     */
    fun getAllItems(): Flow<List<SupplyItem>> = supplyItemDao.getAllItems()
    
    /**
     * Retrieves a single supply item by its ID.
     * 
     * @param itemId The unique identifier of the supply item
     * @return Flow emitting the supply item or null if not found
     */
    fun getItemById(itemId: Int): Flow<SupplyItem?> = supplyItemDao.getItemById(itemId)
    
    /**
     * Searches supply items by name keyword.
     * 
     * Performs case-insensitive partial matching on item names.
     * 
     * @param keyword The search term to match against item names
     * @return Flow emitting list of matching supply items
     */
    fun searchItems(keyword: String): Flow<List<SupplyItem>> = 
        supplyItemDao.searchItems(keyword)
    
    /**
     * Filters supply items by category.
     * 
     * @param category The category to filter by (PPE, Medication, Surgical Kit, Device)
     * @return Flow emitting list of items in the specified category
     */
    fun filterByCategory(category: String): Flow<List<SupplyItem>> = 
        supplyItemDao.filterByCategory(category)
    
    /**
     * Filters supply items by risk level.
     * 
     * @param riskLevel The risk level to filter by (Critical, Elevated, Normal)
     * @return Flow emitting list of items with the specified risk level
     */
    fun filterByRisk(riskLevel: String): Flow<List<SupplyItem>> = 
        supplyItemDao.filterByRisk(riskLevel)
    
    /**
     * Retrieves all items that are below their minimum required quantity.
     * 
     * Used for displaying critical stock alerts on the dashboard.
     * 
     * @return Flow emitting list of critical items
     */
    fun getCriticalItems(): Flow<List<SupplyItem>> = supplyItemDao.getCriticalItems()
    
    /**
     * Retrieves items expiring within the specified number of days.
     * 
     * Calculates the threshold timestamp and queries items expiring
     * before that time.
     * 
     * @param days Number of days from current time to check for expiration
     * @return Flow emitting list of items expiring within the threshold
     */
    fun getExpiringItems(days: Int): Flow<List<SupplyItem>> {
        val currentTime = System.currentTimeMillis()
        val thresholdMillis = currentTime + TimeUnit.DAYS.toMillis(days.toLong())
        return supplyItemDao.getExpiringItems(currentTime, thresholdMillis)
    }
    
    /**
     * Retrieves items expiring within 7 days (critical threshold).
     * 
     * @return Flow emitting list of critically expiring items
     */
    fun getCriticalExpiringItems(): Flow<List<SupplyItem>> = getExpiringItems(7)
    
    /**
     * Retrieves items expiring within 30 days (elevated threshold).
     * 
     * @return Flow emitting list of items expiring soon
     */
    fun getElevatedExpiringItems(): Flow<List<SupplyItem>> = getExpiringItems(30)
    
    /**
     * Updates the quantity of a specific supply item.
     * 
     * After updating quantity, recalculates and updates the risk level
     * based on new quantity status and expiration date.
     * 
     * @param itemId The unique identifier of the item to update
     * @param newQuantity The new quantity value
     */
    suspend fun updateQuantity(itemId: Int, newQuantity: Int) {
        supplyItemDao.updateQuantity(itemId, newQuantity)
        
        val item = supplyItemDao.getItemById(itemId).first()
        item?.let {
            val updatedRiskLevel = calculateRiskLevel(
                currentQuantity = newQuantity,
                minimumRequired = it.minimumRequired,
                expiryDate = it.expiryDate
            )
            supplyItemDao.updateRiskLevel(itemId, updatedRiskLevel)
        }
    }
    
    /**
     * Updates the risk level of a specific supply item.
     * 
     * @param itemId The unique identifier of the item to update
     * @param newRiskLevel The new risk level to set
     */
    suspend fun updateRiskLevel(itemId: Int, newRiskLevel: String) {
        supplyItemDao.updateRiskLevel(itemId, newRiskLevel)
    }
    
    /**
     * Inserts a new supply item into the database.
     * 
     * Calculates the initial risk level before insertion.
     * 
     * @param item The supply item to insert
     * @return The row ID of the newly inserted item
     */
    suspend fun insertItem(item: SupplyItem): Long {
        val riskLevel = calculateRiskLevel(
            currentQuantity = item.currentQuantity,
            minimumRequired = item.minimumRequired,
            expiryDate = item.expiryDate
        )
        val itemWithRisk = item.copy(riskLevel = riskLevel)
        return supplyItemDao.insertItem(itemWithRisk)
    }
    
    /**
     * Inserts multiple supply items into the database.
     * 
     * @param items List of supply items to insert
     */
    suspend fun insertAll(items: List<SupplyItem>) {
        supplyItemDao.insertAll(items)
    }
    
    /**
     * Updates an existing supply item.
     * 
     * @param item The supply item with updated values
     */
    suspend fun updateItem(item: SupplyItem) {
        supplyItemDao.updateItem(item)
    }
    
    /**
     * Deletes a supply item from the database.
     * 
     * @param item The supply item to delete
     */
    suspend fun deleteItem(item: SupplyItem) {
        supplyItemDao.deleteItem(item)
    }
    
    /**
     * Gets the total count of supply items in the database.
     * 
     * @return Total number of items
     */
    suspend fun getItemCount(): Int = supplyItemDao.getItemCount()
    
    /**
     * Calculates the risk level for a supply item based on quantity and expiration.
     * 
     * Risk levels are determined by:
     * - Critical: Below minimum quantity OR expiring within 30 days
     * - Elevated: Within 10% of minimum OR expiring within 60 days
     * - Normal: Above minimum and not expiring soon
     * 
     * @param currentQuantity Current stock quantity
     * @param minimumRequired Minimum required quantity threshold
     * @param expiryDate Expiration date as Unix timestamp
     * @return Risk level string (Critical, Elevated, or Normal)
     */
    private fun calculateRiskLevel(
        currentQuantity: Int,
        minimumRequired: Int,
        expiryDate: Long
    ): String {
        val currentTime = System.currentTimeMillis()
        val daysUntilExpiry = (expiryDate - currentTime) / (1000 * 60 * 60 * 24)
        
        val isBelowMinimum = currentQuantity < minimumRequired
        val isNearMinimum = currentQuantity < (minimumRequired * 1.1)
        val isExpiringSoon = daysUntilExpiry <= 30 && daysUntilExpiry >= 0
        val isExpiringMediumTerm = daysUntilExpiry <= 60 && daysUntilExpiry >= 0
        
        return when {
            isBelowMinimum || isExpiringSoon -> SupplyItem.Companion.RiskLevels.CRITICAL
            isNearMinimum || isExpiringMediumTerm -> SupplyItem.Companion.RiskLevels.ELEVATED
            else -> SupplyItem.Companion.RiskLevels.NORMAL
        }
    }
}