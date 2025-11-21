/**
 * File Overview: ViewModel for Audit Workflow
 * 
 * This ViewModel manages the complete 5-step audit workflow state, including
 * data collection across all steps, validation, and final audit record creation.
 * 
 * @author Sahil Patel
 * @version 1.3
 */

package com.sahilpatel.medsupplyguardian.ui.screens.audit

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sahilpatel.medsupplyguardian.data.database.AppDatabase
import com.sahilpatel.medsupplyguardian.data.database.entities.AuditRecord
import com.sahilpatel.medsupplyguardian.data.database.entities.SupplyItem
import com.sahilpatel.medsupplyguardian.data.preferences.UserPreferencesManager
import com.sahilpatel.medsupplyguardian.data.repository.AuditRepository
import com.sahilpatel.medsupplyguardian.data.repository.SupplyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UI state for the Audit workflow.
 * 
 * Maintains all data collected during the 5-step audit process.
 * 
 * @property isLoading Whether data is being loaded
 * @property supplyItems List of supply items for audit
 * @property verifiedQuantities Map of item IDs to verified quantities (Step 1)
 * @property expiryCheckedItems Set of item IDs confirmed for expiry (Step 2)
 * @property storageConditionsPassed Whether storage validation passed (Step 3)
 * @property missingItems Set of item IDs marked as missing (Step 4)
 * @property damagedItems Set of item IDs marked as damaged (Step 4)
 * @property auditNotes Additional notes from technician (Step 4)
 * @property isSubmitting Whether audit is being uploaded
 * @property uploadComplete Whether upload has finished
 */
data class AuditUiState(
    val isLoading: Boolean = true,
    val supplyItems: List<SupplyItem> = emptyList(),
    val verifiedQuantities: Map<Int, Int> = emptyMap(),
    val expiryCheckedItems: Set<Int> = emptySet(),
    val storageConditionsPassed: Boolean = false,
    val missingItems: Set<Int> = emptySet(),
    val damagedItems: Set<Int> = emptySet(),
    val auditNotes: String = "",
    val isSubmitting: Boolean = false,
    val uploadComplete: Boolean = false
)

/**
 * ViewModel for managing Audit workflow state and operations.
 * 
 * Coordinates data collection across all audit steps, validates inputs,
 * and creates the final audit record for storage.
 * 
 * @property application Application context for database and preferences access
 */
class AuditViewModel(application: Application) : AndroidViewModel(application) {
    
    private val supplyRepository: SupplyRepository
    private val auditRepository: AuditRepository
    private val preferencesManager: UserPreferencesManager
    
    private val _uiState = MutableStateFlow(AuditUiState())
    
    /**
     * Publicly exposed UI state.
     */
    val uiState: StateFlow<AuditUiState> = _uiState.asStateFlow()
    
    init {
        val database = AppDatabase.getDatabase(application)
        supplyRepository = SupplyRepository(database.supplyItemDao())
        auditRepository = AuditRepository(database.auditRecordDao())
        preferencesManager = UserPreferencesManager(application)
        
        loadSupplyItems()
    }
    
    /**
     * Loads all supply items for the audit.
     */
    private fun loadSupplyItems() {
        viewModelScope.launch {
            supplyRepository.getAllItems().collect { items ->
                _uiState.update { it.copy(
                    supplyItems = items,
                    isLoading = false
                )}
            }
        }
    }

    /**
     * Resets the audit workflow to initial state.
     */
    fun resetAudit() {
        _uiState.value = AuditUiState(isLoading = true)
        loadSupplyItems()
    }
    
    /**
     * Updates verified quantity for a specific item (Step 1).
     * 
     * @param itemId Item ID to update
     * @param quantity Verified quantity value
     */
    fun updateVerifiedQuantity(itemId: Int, quantity: Int) {
        _uiState.update {
            val updated = it.verifiedQuantities.toMutableMap()
            updated[itemId] = quantity
            it.copy(verifiedQuantities = updated)
        }
    }
    
    /**
     * Toggles expiry checked status for an item (Step 2).
     * 
     * @param itemId Item ID to toggle
     */
    fun toggleExpiryChecked(itemId: Int) {
        _uiState.update {
            val updated = it.expiryCheckedItems.toMutableSet()
            if (updated.contains(itemId)) {
                updated.remove(itemId)
            } else {
                updated.add(itemId)
            }
            it.copy(expiryCheckedItems = updated)
        }
    }
    
    /**
     * Updates storage conditions validation result (Step 3).
     * 
     * @param passed Whether storage conditions passed validation
     */
    fun updateStorageConditions(passed: Boolean) {
        _uiState.update { it.copy(storageConditionsPassed = passed) }
    }
    
    /**
     * Toggles missing status for an item (Step 4).
     * 
     * @param itemId Item ID to toggle
     */
    fun toggleMissingItem(itemId: Int) {
        _uiState.update {
            val updated = it.missingItems.toMutableSet()
            if (updated.contains(itemId)) {
                updated.remove(itemId)
            } else {
                updated.add(itemId)
            }
            it.copy(missingItems = updated)
        }
    }
    
    /**
     * Toggles damaged status for an item (Step 4).
     * 
     * @param itemId Item ID to toggle
     */
    fun toggleDamagedItem(itemId: Int) {
        _uiState.update {
            val updated = it.damagedItems.toMutableSet()
            if (updated.contains(itemId)) {
                updated.remove(itemId)
            } else {
                updated.add(itemId)
            }
            it.copy(damagedItems = updated)
        }
    }
    
    /**
     * Updates audit notes (Step 4).
     * 
     * @param notes Notes text from technician
     */
    fun updateAuditNotes(notes: String) {
        _uiState.update { it.copy(auditNotes = notes) }
    }
    
    /**
     * Submits the completed audit and creates audit record.
     * 
     * Calculates metrics from collected data, creates an AuditRecord,
     * saves it to the database, and simulates upload process.
     */
    fun submitAudit() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }
            
            val currentState = _uiState.value
            val totalItems = currentState.supplyItems.size
            val itemsFailing = currentState.missingItems.size + currentState.damagedItems.size
            val itemsExpiringSoon = currentState.supplyItems.count { it.isExpiringWithin(30) }
            
            val auditRecord = AuditRecord(
                technicianName = preferencesManager.staffName.first(),
                technicianId = preferencesManager.staffId.first(),
                auditDate = System.currentTimeMillis(),
                totalItemsReviewed = totalItems,
                itemsFailing = itemsFailing,
                itemsExpiringSoon = itemsExpiringSoon,
                storageConditionsPassed = currentState.storageConditionsPassed,
                missingItemsCount = currentState.missingItems.size,
                damagedItemsCount = currentState.damagedItems.size,
                notes = currentState.auditNotes,
                uploadStatus = "Uploaded"
            )
            
            auditRepository.insertAudit(auditRecord)
            
            kotlinx.coroutines.delay(2500L)
            
            _uiState.update { it.copy(
                isSubmitting = false,
                uploadComplete = true
            )}
        }
    }
}