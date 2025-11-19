/**
 * File Overview: Audit Record entity for Room Database
 * 
 * This entity stores compliance audit records completed by healthcare staff.
 * Each audit captures inventory verification, expiration checks, storage validation,
 * and discrepancy reporting for regulatory compliance tracking.
 * 
 * @author Sahil Patel
 * @version 1.0
 */

package com.sahilpatel.medsupplyguardian.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Audit Record entity representing completed compliance audits.
 * 
 * Stores comprehensive audit data including technician information, timestamps,
 * compliance metrics, and detailed findings from the 5-step audit workflow.
 * 
 * @property auditId Unique identifier for the audit record (auto-generated)
 * @property technicianName Name of the staff member who performed the audit
 * @property technicianId Staff ID number for accountability tracking
 * @property auditDate Date and time when audit was completed (Unix timestamp)
 * @property totalItemsReviewed Total number of supply items included in audit
 * @property itemsFailing Number of items that failed compliance checks
 * @property itemsExpiringSoon Number of items expiring within threshold period
 * @property storageConditionsPassed Boolean indicating storage validation success
 * @property missingItemsCount Count of items identified as missing
 * @property damagedItemsCount Count of items identified as damaged
 * @property notes Additional comments or observations from the technician
 * @property uploadStatus Status of audit report upload (Pending, Uploaded, Failed)
 */
@Entity(tableName = "audit_records")
data class AuditRecord(
    @PrimaryKey(autoGenerate = true)
    val auditId: Int = 0,
    
    val technicianName: String,
    
    val technicianId: String,
    
    val auditDate: Long,
    
    val totalItemsReviewed: Int,
    
    val itemsFailing: Int,
    
    val itemsExpiringSoon: Int,
    
    val storageConditionsPassed: Boolean,
    
    val missingItemsCount: Int,
    
    val damagedItemsCount: Int,
    
    val notes: String,
    
    val uploadStatus: String
) {
    /**
     * Companion object containing constants for audit upload statuses.
     */
    companion object {
        /**
         * Available upload status values.
         */
        object UploadStatus {
            const val PENDING = "Pending"
            const val UPLOADED = "Uploaded"
            const val FAILED = "Failed"
        }
    }
    
    /**
     * Calculates the compliance rate as a percentage.
     * 
     * @return Compliance rate (0.0 to 100.0) or 0.0 if no items reviewed
     */
    fun getComplianceRate(): Double {
        if (totalItemsReviewed == 0) return 0.0
        val passingItems = totalItemsReviewed - itemsFailing
        return (passingItems.toDouble() / totalItemsReviewed.toDouble()) * 100.0
    }
    
    /**
     * Determines if the audit has critical findings requiring immediate action.
     * 
     * @return true if failing items exceed 10% or storage conditions failed
     */
    fun hasCriticalFindings(): Boolean {
        val failureRate = if (totalItemsReviewed > 0) {
            (itemsFailing.toDouble() / totalItemsReviewed.toDouble()) * 100.0
        } else 0.0
        
        return failureRate > 10.0 || !storageConditionsPassed || damagedItemsCount > 0
    }
}