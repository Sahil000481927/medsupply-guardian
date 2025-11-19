/**
 * File Overview: Data Access Object for Audit Records
 * 
 * This DAO manages all database operations for compliance audit records including
 * storage, retrieval, and status updates. Supports historical audit tracking and
 * compliance reporting requirements.
 * 
 * @author Sahil Patel
 * @version 1.0
 */

package com.sahilpatel.medsupplyguardian.data.database.dao

import androidx.room.*
import com.sahilpatel.medsupplyguardian.data.database.entities.AuditRecord
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for audit records table operations.
 * 
 * Provides operations for creating, retrieving, and managing compliance audit
 * records with support for filtering by date, technician, and upload status.
 */
@Dao
interface AuditRecordDao {
    
    /**
     * Retrieves all audit records ordered by date (most recent first).
     * 
     * @return Flow emitting list of all audit records
     */
    @Query("SELECT * FROM audit_records ORDER BY auditDate DESC")
    fun getAllAudits(): Flow<List<AuditRecord>>
    
    /**
     * Retrieves a single audit record by its unique identifier.
     * 
     * @param auditId The unique identifier of the audit record
     * @return Flow emitting the matching audit record or null if not found
     */
    @Query("SELECT * FROM audit_records WHERE auditId = :auditId")
    fun getAuditById(auditId: Int): Flow<AuditRecord?>
    
    /**
     * Retrieves all audits performed by a specific technician.
     * 
     * @param technicianId The staff ID of the technician
     * @return Flow emitting list of audits by the specified technician
     */
    @Query("SELECT * FROM audit_records WHERE technicianId = :technicianId ORDER BY auditDate DESC")
    fun getAuditsByTechnician(technicianId: String): Flow<List<AuditRecord>>
    
    /**
     * Retrieves audits within a specific date range.
     * 
     * @param startDate Start of date range (Unix timestamp)
     * @param endDate End of date range (Unix timestamp)
     * @return Flow emitting list of audits within the date range
     */
    @Query("SELECT * FROM audit_records WHERE auditDate BETWEEN :startDate AND :endDate ORDER BY auditDate DESC")
    fun getAuditsByDateRange(startDate: Long, endDate: Long): Flow<List<AuditRecord>>
    
    /**
     * Retrieves audits with a specific upload status.
     * 
     * @param status Upload status to filter by (Pending, Uploaded, Failed)
     * @return Flow emitting list of audits matching the upload status
     */
    @Query("SELECT * FROM audit_records WHERE uploadStatus = :status ORDER BY auditDate DESC")
    fun getAuditsByStatus(status: String): Flow<List<AuditRecord>>
    
    /**
     * Retrieves the most recent audit record.
     * 
     * @return Flow emitting the most recent audit or null if no audits exist
     */
    @Query("SELECT * FROM audit_records ORDER BY auditDate DESC LIMIT 1")
    fun getLatestAudit(): Flow<AuditRecord?>
    
    /**
     * Updates the upload status of a specific audit record.
     * 
     * @param auditId The unique identifier of the audit to update
     * @param newStatus The new upload status value
     */
    @Query("UPDATE audit_records SET uploadStatus = :newStatus WHERE auditId = :auditId")
    suspend fun updateUploadStatus(auditId: Int, newStatus: String)
    
    /**
     * Inserts a new audit record into the database.
     * 
     * @param audit The audit record to insert
     * @return The row ID of the newly inserted audit
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAudit(audit: AuditRecord): Long
    
    /**
     * Updates an existing audit record.
     * 
     * @param audit The audit record with updated values
     */
    @Update
    suspend fun updateAudit(audit: AuditRecord)
    
    /**
     * Deletes an audit record from the database.
     * 
     * @param audit The audit record to delete
     */
    @Delete
    suspend fun deleteAudit(audit: AuditRecord)
    
    /**
     * Deletes all audit records from the database.
     * 
     * Use with caution - primarily for testing purposes.
     */
    @Query("DELETE FROM audit_records")
    suspend fun deleteAll()
    
    /**
     * Gets the total count of audit records in the database.
     * 
     * @return Total number of audit records
     */
    @Query("SELECT COUNT(*) FROM audit_records")
    suspend fun getAuditCount(): Int
}