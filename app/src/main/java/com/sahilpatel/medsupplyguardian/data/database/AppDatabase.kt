/**
 * File Overview: Room Database singleton instance for MedSupply Guardian
 * 
 * This file defines the main database configuration for the application including
 * all entities, version management, and DAO access. Implements singleton pattern
 * to ensure only one database instance exists throughout the app lifecycle.
 * 
 * @author Sahil Patel
 * @version 1.0
 */

package com.sahilpatel.medsupplyguardian.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.sahilpatel.medsupplyguardian.data.database.dao.AuditRecordDao
import com.sahilpatel.medsupplyguardian.data.database.dao.SupplyItemDao
import com.sahilpatel.medsupplyguardian.data.database.entities.AuditRecord
import com.sahilpatel.medsupplyguardian.data.database.entities.SupplyItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

/**
 * Main Room database for MedSupply Guardian application.
 * 
 * Manages the SQLite database with entities for supply items and audit records.
 * Provides DAO interfaces for data access and implements database initialization
 * with sample data for demonstration purposes.
 * 
 * @property supplyItemDao Data access object for supply items operations
 * @property auditRecordDao Data access object for audit records operations
 */
@Database(
    entities = [SupplyItem::class, AuditRecord::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    
    /**
     * Provides access to supply items data operations.
     * 
     * @return SupplyItemDao instance for database operations
     */
    abstract fun supplyItemDao(): SupplyItemDao
    
    /**
     * Provides access to audit records data operations.
     * 
     * @return AuditRecordDao instance for database operations
     */
    abstract fun auditRecordDao(): AuditRecordDao
    
    /**
     * Companion object implementing singleton pattern for database instance.
     */
    companion object {
        /**
         * Volatile singleton instance of the database.
         * Volatile ensures visibility of changes across threads.
         */
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        /**
         * Database name constant.
         */
        private const val DATABASE_NAME = "medsupply_guardian_db"
        
        /**
         * Gets or creates the singleton database instance.
         * 
         * Uses double-checked locking pattern to ensure thread-safe initialization
         * of the database instance. If instance doesn't exist, creates new database
         * with initialization callback for seeding sample data.
         * 
         * @param context Application context for database creation
         * @return Singleton AppDatabase instance
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
        
        /**
         * Database callback for initialization operations.
         * 
         * Populates the database with sample supply items when first created.
         * This ensures the app has demonstration data for testing and evaluation.
         */
        private class DatabaseCallback : Callback() {
            /**
             * Called when the database is created for the first time.
             * 
             * Populates the database with sample supply items covering all
             * categories, risk levels, and expiration scenarios.
             * 
             * @param db The database instance
             */
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateDatabase(database.supplyItemDao())
                    }
                }
            }
        }
        
        /**
         * Populates the database with sample supply items.
         * 
         * Creates a comprehensive set of sample data representing various
         * medical supplies with different risk levels, categories, and
         * expiration dates for demonstration and testing purposes.
         * 
         * @param supplyItemDao DAO for inserting supply items
         */
        private suspend fun populateDatabase(supplyItemDao: SupplyItemDao) {
            val currentTime = System.currentTimeMillis()
            val oneDayMillis = TimeUnit.DAYS.toMillis(1)
            
            val sampleItems = listOf(
                SupplyItem(
                    name = "N95 Respirator Masks",
                    category = SupplyItem.Companion.Categories.PPE,
                    minimumRequired = 500,
                    currentQuantity = 150,
                    expiryDate = currentTime + (oneDayMillis * 45),
                    location = "Storage Room A",
                    riskLevel = SupplyItem.Companion.RiskLevels.CRITICAL
                ),
                SupplyItem(
                    name = "Nitrile Gloves (Size L)",
                    category = SupplyItem.Companion.Categories.PPE,
                    minimumRequired = 1000,
                    currentQuantity = 1200,
                    expiryDate = currentTime + (oneDayMillis * 90),
                    location = "Storage Room A",
                    riskLevel = SupplyItem.Companion.RiskLevels.NORMAL
                ),
                SupplyItem(
                    name = "Surgical Gowns",
                    category = SupplyItem.Companion.Categories.PPE,
                    minimumRequired = 300,
                    currentQuantity = 280,
                    expiryDate = currentTime + (oneDayMillis * 120),
                    location = "Storage Room B",
                    riskLevel = SupplyItem.Companion.RiskLevels.ELEVATED
                ),
                SupplyItem(
                    name = "Face Shields",
                    category = SupplyItem.Companion.Categories.PPE,
                    minimumRequired = 200,
                    currentQuantity = 450,
                    expiryDate = currentTime + (oneDayMillis * 180),
                    location = "Storage Room A",
                    riskLevel = SupplyItem.Companion.RiskLevels.NORMAL
                ),
                SupplyItem(
                    name = "Epinephrine Auto-Injector",
                    category = SupplyItem.Companion.Categories.MEDICATION,
                    minimumRequired = 50,
                    currentQuantity = 35,
                    expiryDate = currentTime + (oneDayMillis * 15),
                    location = "Pharmacy Cabinet 1",
                    riskLevel = SupplyItem.Companion.RiskLevels.CRITICAL
                ),
                SupplyItem(
                    name = "IV Saline Solution 0.9%",
                    category = SupplyItem.Companion.Categories.MEDICATION,
                    minimumRequired = 200,
                    currentQuantity = 225,
                    expiryDate = currentTime + (oneDayMillis * 60),
                    location = "Pharmacy Refrigerator",
                    riskLevel = SupplyItem.Companion.RiskLevels.NORMAL
                ),
                SupplyItem(
                    name = "Morphine Sulfate 10mg",
                    category = SupplyItem.Companion.Categories.MEDICATION,
                    minimumRequired = 100,
                    currentQuantity = 85,
                    expiryDate = currentTime + (oneDayMillis * 30),
                    location = "Secure Pharmacy Vault",
                    riskLevel = SupplyItem.Companion.RiskLevels.ELEVATED
                ),
                SupplyItem(
                    name = "Antibiotic Ointment",
                    category = SupplyItem.Companion.Categories.MEDICATION,
                    minimumRequired = 150,
                    currentQuantity = 200,
                    expiryDate = currentTime + (oneDayMillis * 150),
                    location = "Pharmacy Cabinet 2",
                    riskLevel = SupplyItem.Companion.RiskLevels.NORMAL
                ),
                SupplyItem(
                    name = "Sterile Suture Kit",
                    category = SupplyItem.Companion.Categories.SURGICAL_KIT,
                    minimumRequired = 75,
                    currentQuantity = 45,
                    expiryDate = currentTime + (oneDayMillis * 90),
                    location = "Operating Room Supply",
                    riskLevel = SupplyItem.Companion.RiskLevels.CRITICAL
                ),
                SupplyItem(
                    name = "Laparoscopy Instrument Set",
                    category = SupplyItem.Companion.Categories.SURGICAL_KIT,
                    minimumRequired = 10,
                    currentQuantity = 12,
                    expiryDate = currentTime + (oneDayMillis * 365),
                    location = "Operating Room Supply",
                    riskLevel = SupplyItem.Companion.RiskLevels.NORMAL
                ),
                SupplyItem(
                    name = "Catheterization Kit",
                    category = SupplyItem.Companion.Categories.SURGICAL_KIT,
                    minimumRequired = 60,
                    currentQuantity = 55,
                    expiryDate = currentTime + (oneDayMillis * 75),
                    location = "Emergency Department",
                    riskLevel = SupplyItem.Companion.RiskLevels.ELEVATED
                ),
                SupplyItem(
                    name = "Pulse Oximeter",
                    category = SupplyItem.Companion.Categories.DEVICE,
                    minimumRequired = 25,
                    currentQuantity = 30,
                    expiryDate = currentTime + (oneDayMillis * 730),
                    location = "Equipment Room",
                    riskLevel = SupplyItem.Companion.RiskLevels.NORMAL
                ),
                SupplyItem(
                    name = "Digital Thermometer",
                    category = SupplyItem.Companion.Categories.DEVICE,
                    minimumRequired = 40,
                    currentQuantity = 15,
                    expiryDate = currentTime + (oneDayMillis * 365),
                    location = "Equipment Room",
                    riskLevel = SupplyItem.Companion.RiskLevels.CRITICAL
                ),
                SupplyItem(
                    name = "Blood Pressure Monitor",
                    category = SupplyItem.Companion.Categories.DEVICE,
                    minimumRequired = 20,
                    currentQuantity = 22,
                    expiryDate = currentTime + (oneDayMillis * 500),
                    location = "Equipment Room",
                    riskLevel = SupplyItem.Companion.RiskLevels.NORMAL
                ),
                SupplyItem(
                    name = "Defibrillator Pads",
                    category = SupplyItem.Companion.Categories.DEVICE,
                    minimumRequired = 30,
                    currentQuantity = 25,
                    expiryDate = currentTime + (oneDayMillis * 20),
                    location = "Emergency Department",
                    riskLevel = SupplyItem.Companion.RiskLevels.CRITICAL
                )
            )
            
            supplyItemDao.insertAll(sampleItems)
        }
    }
}