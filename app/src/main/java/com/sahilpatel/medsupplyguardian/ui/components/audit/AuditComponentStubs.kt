package com.sahilpatel.medsupplyguardian.ui.components.audit

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.sahilpatel.medsupplyguardian.ui.screens.audit.AuditUiState

@Composable
fun AuditStep1Screen(uiState: AuditUiState, onQuantityChanged: (Int, Int) -> Unit, onNext: () -> Unit) {
    Text(text = "AuditStep1Screen")
}

@Composable
fun AuditStep2Screen(uiState: AuditUiState, onExpiryChecked: (Int) -> Unit, onNext: () -> Unit) {
    Text(text = "AuditStep2Screen")
}

@Composable
fun AuditStep3Screen(onConditionsResult: (Boolean) -> Unit, onNext: () -> Unit) {
    Text(text = "AuditStep3Screen")
}

@Composable
fun AuditStep4Screen(
    uiState: AuditUiState,
    onToggleMissing: (Int) -> Unit,
    onToggleDamaged: (Int) -> Unit,
    onNotesChanged: (String) -> Unit,
    onNext: () -> Unit
) {
    Text(text = "AuditStep4Screen")
}

@Composable
fun AuditStep5Screen(uiState: AuditUiState, onSubmit: () -> Unit, onComplete: () -> Unit) {
    Text(text = "AuditStep5Screen")
}