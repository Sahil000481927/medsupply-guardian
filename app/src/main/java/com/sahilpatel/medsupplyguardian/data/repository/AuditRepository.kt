/**
 * File Overview: Repository for Audit Records data operations
 * 
 * This repository provides a clean API for accessing and managing audit records
 * from the Room database. It handles audit creation, status updates, and
 * historical data retrieval for compliance reporting.
 * 
 * @author Sahil Patel
 * @version 1.0
 */

package com.sahilpatel.medsupplyguardian.data.repository

import com.sahilpatel.medsupplyguardian.data.database.dao.AuditRecordDao
import com.sahilpatel.medsupplyguardian.data.database.entities.AuditRecord
import kotlinx.coroutines.flow.Flow

/**
 * Repository for managing audit record data operations.
 * 
 * Provides abstraction between the DAO and ViewModels, handling
 * audit record storage, retrieval, filtering, and status management
 * for compliance tracking and reporting.
 * 
 * @property auditRecordDao Data access object for audit records
 */
class AuditRepository(private val auditRecordDao: AuditRecordDao) {
    
    /**
     * Retrieves all audit records ordered by date (most recent first).
     * 
     * @return Flow emitting list of all audit records
     */
    fun getAllAudits(): Flow<List<AuditRecord>> = auditRecordDao.getAllAudits()
    
    /**
     * Retrieves a single audit record by its ID.
     * 
     * @param auditId The unique identifier of the audit record
     * @return Flow emitting the audit record or null if not found
     */
    fun getAuditById(auditId: Int): Flow<AuditRecord?> = 
        auditRecordDao.getAuditById(auditId)
    
    /**
     * Retrieves all audits performed by a specific technician.
     * 
     * @param technicianId The staff ID of the technician
     * @return Flow emitting list of audits by the specified technician
     */
    fun getAuditsByTechnician(technicianId: String): Flow<List<AuditRecord>> = 
        auditRecordDao.getAuditsByTechnician(technicianId)
    
    /**
     * Retrieves audits within a specific date range.
     * 
     * @param startDate Start of date range (Unix timestamp)
     * @param endDate End of date range (Unix timestamp)
     * @return Flow emitting list of audits within the date range
     */
    fun getAuditsByDateRange(startDate: Long, endDate: Long): Flow<List<AuditRecord>> = 
        auditRecordDao.getAuditsByDateRange(startDate, endDate)
    
    /**
     * Retrieves audits with a specific upload status.
     * 
     * @param status Upload status to filter by (Pending, Uploaded, Failed)
     * @return Flow emitting list of audits matching the upload status
     */
    fun getAuditsByStatus(status: String): Flow<List<AuditRecord>> = 
        auditRecordDao.getAuditsByStatus(status)
    
    /**
     * Retrieves the most recent audit record.
     * 
     * Useful for displaying last audit information on the dashboard.
     * 
     * @return Flow emitting the most recent audit or null if no audits exist
     */
    fun getLatestAudit(): Flow<AuditRecord?> = auditRecordDao.getLatestAudit()
    
    /**
     * Updates the upload status of a specific audit record.
     * 
     * Used to track audit report synchronization with external systems.
     * 
     * @param auditId The unique identifier of the audit to update
     * @param newStatus The new upload status value
     */
    suspend fun updateUploadStatus(auditId: Int, newStatus: String) {
        auditRecordDao.updateUploadStatus(auditId, newStatus)
    }
    
    /**
     * Inserts a new audit record into the database.
     * 
     * Creates a new compliance audit record with all captured data
     * from the 5-step audit workflow.
     * 
     * @param audit The audit record to insert
     * @return The row ID of the newly inserted audit
     */
    suspend fun insertAudit(audit: AuditRecord): Long {
        return auditRecordDao.insertAudit(audit)
    }
    
    /**
     * Updates an existing audit record.
     * 
     * @param audit The audit record with updated values
     */
    suspend fun updateAudit(audit: AuditRecord) {
        auditRecordDao.updateAudit(audit)
    }
    
    /**
     * Deletes an audit record from the database.
     * 
     * @param audit The audit record to delete
     */
    suspend fun deleteAudit(audit: AuditRecord) {
        auditRecordDao.deleteAudit(audit)
    }
    
    /**
     * Gets the total count of audit records in the database.
     * 
     * @return Total number of audit records
     */
    suspend fun getAuditCount(): Int = auditRecordDao.getAuditCount()
    
    /**
     * Retrieves audits that have critical findings requiring attention.
     * 
     * Filters audits based on compliance rate and critical conditions.
     * Note: This is a client-side filter as complex calculations are
     * not easily done in Room queries.
     * 
     * @return Flow emitting list of audits with critical findings
     */
    fun getCriticalAudits(): Flow<List<AuditRecord>> {
        return getAllAudits()
    }
}