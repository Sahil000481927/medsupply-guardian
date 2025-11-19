/**
 * File Overview: Data Access Object for Supply Items
 * 
 * This DAO provides all database operations for managing supply items including
 * queries, insertions, updates, and deletions. All methods are suspend functions
 * or return Flow for reactive data observation with proper coroutine support.
 * 
 * @author Sahil Patel
 * @version 1.0
 */

package com.sahilpatel.medsupplyguardian.data.database.dao

import androidx.room.*
import com.sahilpatel.medsupplyguardian.data.database.entities.SupplyItem
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for supply items table operations.
 * 
 * Provides comprehensive CRUD operations and specialized queries for inventory
 * management, filtering, searching, and compliance monitoring. All queries return
 * Flow for reactive UI updates or use suspend for one-time operations.
 */
@Dao
interface SupplyItemDao {
    
    /**
     * Retrieves all supply items from the database as a reactive Flow.
     * 
     * Returns a Flow that emits the complete list of supply items whenever
     * the database changes, enabling automatic UI updates.
     * 
     * @return Flow emitting list of all supply items
     */
    @Query("SELECT * FROM supply_items ORDER BY name ASC")
    fun getAllItems(): Flow<List<SupplyItem>>
    
    /**
     * Retrieves a single supply item by its unique identifier.
     * 
     * @param itemId The unique identifier of the supply item
     * @return Flow emitting the matching supply item or null if not found
     */
    @Query("SELECT * FROM supply_items WHERE itemId = :itemId")
    fun getItemById(itemId: Int): Flow<SupplyItem?>
    
    /**
     * Searches supply items by name using case-insensitive partial matching.
     * 
     * Enables real-time search functionality in the UI by matching any part
     * of the item name against the search keyword.
     * 
     * @param keyword Search term to match against item names
     * @return Flow emitting list of matching supply items
     */
    @Query("SELECT * FROM supply_items WHERE name LIKE '%' || :keyword || '%' ORDER BY name ASC")
    fun searchItems(keyword: String): Flow<List<SupplyItem>>
    
    /**
     * Filters supply items by category.
     * 
     * @param category The category to filter by (PPE, Medication, Surgical Kit, Device)
     * @return Flow emitting list of items matching the specified category
     */
    @Query("SELECT * FROM supply_items WHERE category = :category ORDER BY name ASC")
    fun filterByCategory(category: String): Flow<List<SupplyItem>>
    
    /**
     * Filters supply items by risk level.
     * 
     * @param riskLevel The risk level to filter by (Critical, Elevated, Normal)
     * @return Flow emitting list of items matching the specified risk level
     */
    @Query("SELECT * FROM supply_items WHERE riskLevel = :riskLevel ORDER BY name ASC")
    fun filterByRisk(riskLevel: String): Flow<List<SupplyItem>>
    
    /**
     * Retrieves all items that are below their minimum required quantity.
     * 
     * Used for critical stock alerts on the dashboard.
     * 
     * @return Flow emitting list of items with currentQuantity less than minimumRequired
     */
    @Query("SELECT * FROM supply_items WHERE currentQuantity < minimumRequired ORDER BY name ASC")
    fun getCriticalItems(): Flow<List<SupplyItem>>
    
    /**
     * Retrieves items expiring within a specified number of days.
     * 
     * Calculates expiration based on current timestamp and compares against
     * the specified threshold in milliseconds.
     * 
     * @param thresholdMillis Time threshold in milliseconds from current time
     * @return Flow emitting list of items expiring within the threshold
     */
    @Query("SELECT * FROM supply_items WHERE expiryDate <= :thresholdMillis AND expiryDate >= :currentTime ORDER BY expiryDate ASC")
    fun getExpiringItems(currentTime: Long, thresholdMillis: Long): Flow<List<SupplyItem>>
    
    /**
     * Updates the quantity of a specific supply item.
     * 
     * Performs an atomic update operation for inventory adjustments during
     * audits or replenishment activities.
     * 
     * @param itemId The unique identifier of the item to update
     * @param newQuantity The new quantity value to set
     */
    @Query("UPDATE supply_items SET currentQuantity = :newQuantity WHERE itemId = :itemId")
    suspend fun updateQuantity(itemId: Int, newQuantity: Int)
    
    /**
     * Updates the risk level of a specific supply item.
     * 
     * Allows manual or computed risk level adjustments based on
     * inventory status changes.
     * 
     * @param itemId The unique identifier of the item to update
     * @param newRiskLevel The new risk level to set
     */
    @Query("UPDATE supply_items SET riskLevel = :newRiskLevel WHERE itemId = :itemId")
    suspend fun updateRiskLevel(itemId: Int, newRiskLevel: String)
    
    /**
     * Inserts a new supply item into the database.
     * 
     * If a conflict occurs (duplicate primary key), the existing item is replaced.
     * 
     * @param item The supply item to insert
     * @return The row ID of the newly inserted item
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: SupplyItem): Long
    
    /**
     * Inserts multiple supply items into the database in a single transaction.
     * 
     * Optimized for bulk insertions during initial database seeding or
     * batch import operations.
     * 
     * @param items List of supply items to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<SupplyItem>)
    
    /**
     * Updates an existing supply item with all modified fields.
     * 
     * @param item The supply item with updated values
     */
    @Update
    suspend fun updateItem(item: SupplyItem)
    
    /**
     * Deletes a supply item from the database.
     * 
     * @param item The supply item to delete
     */
    @Delete
    suspend fun deleteItem(item: SupplyItem)
    
    /**
     * Deletes all supply items from the database.
     * 
     * Use with caution - primarily for testing or complete data reset.
     */
    @Query("DELETE FROM supply_items")
    suspend fun deleteAll()
    
    /**
     * Gets the total count of supply items in the database.
     * 
     * @return Total number of items
     */
    @Query("SELECT COUNT(*) FROM supply_items")
    suspend fun getItemCount(): Int
}