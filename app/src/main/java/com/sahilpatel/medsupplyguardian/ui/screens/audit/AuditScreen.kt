/**
 * File Overview: Audit Screen composable
 * 
 * This file contains the multi-step audit workflow screen, guiding the user
 * through inventory verification, expiration checks, and discrepancy reporting.
 * 
 * @author Sahil Patel
 * @version 1.0
 */

package com.sahilpatel.medsupplyguardian.ui.screens.audit

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sahilpatel.medsupplyguardian.ui.components.audit.AuditStep1Screen
import com.sahilpatel.medsupplyguardian.ui.components.audit.AuditStep2Screen
import com.sahilpatel.medsupplyguardian.ui.components.audit.AuditStep3Screen
import com.sahilpatel.medsupplyguardian.ui.components.audit.AuditStep4Screen
import com.sahilpatel.medsupplyguardian.ui.components.audit.AuditStep5Screen

/**
 * Main audit screen composable managing the multi-step workflow.
 * 
 * Controls the display of each audit step and handles navigation between
 * steps. Finalizes and submits the audit upon completion.
 * 
 * @param onNavigateBack Callback to navigate back to the previous screen
 * @param viewModel ViewModel for managing audit workflow state
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuditScreen(
    onNavigateBack: () -> Unit,
    viewModel: AuditViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Compliance Audit - Step ${uiState.currentStep} of 5") },
                navigationIcon = {
                    if (uiState.currentStep == 1) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    } else {
                        IconButton(onClick = { viewModel.goToPreviousStep() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Previous Step")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Text(text = "Loading audit workflow...")
        } else {
            Box(modifier = Modifier.padding(paddingValues)) {
                when (uiState.currentStep) {
                    1 -> AuditStep1Screen(
                        uiState = uiState,
                        onQuantityChanged = { id, qty -> viewModel.updateVerifiedQuantity(id, qty) },
                        onNext = { viewModel.goToNextStep() }
                    )
                    2 -> AuditStep2Screen(
                        uiState = uiState,
                        onExpiryChecked = { id -> viewModel.toggleExpiryChecked(id) },
                        onNext = { viewModel.goToNextStep() }
                    )
                    3 -> AuditStep3Screen(
                        onConditionsResult = { passed -> viewModel.updateStorageConditions(passed) },
                        onNext = { viewModel.goToNextStep() }
                    )
                    4 -> AuditStep4Screen(
                        uiState = uiState,
                        onToggleMissing = { id -> viewModel.toggleMissingItem(id) },
                        onToggleDamaged = { id -> viewModel.toggleDamagedItem(id) },
                        onNotesChanged = { notes -> viewModel.updateAuditNotes(notes) },
                        onNext = { viewModel.goToNextStep() }
                    )
                    5 -> AuditStep5Screen(
                        uiState = uiState,
                        onSubmit = { viewModel.submitAudit() },
                        onComplete = onNavigateBack
                    )
                }
            }
        }
    }
}
