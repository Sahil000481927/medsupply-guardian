package com.sahilpatel.medsupplyguardian.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.sahilpatel.medsupplyguardian.data.database.entities.SupplyItem
import com.sahilpatel.medsupplyguardian.ui.screens.supplies.SupplyDetailsUiState

@Composable
fun CriticalAlertCard(count: Int, onClick: () -> Unit) {
    Text(text = "CriticalAlertCard")
}

@Composable
fun DashboardCard(
    title: String,
    count: Int,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    containerColor: androidx.compose.ui.graphics.Color,
    contentColor: androidx.compose.ui.graphics.Color
) {
    Text(text = "DashboardCard")
}

@Composable
fun ExpiringItemsCard(count: Int, daysThreshold: Int, onClick: () -> Unit) {
    Text(text = "ExpiringItemsCard")
}

@Composable
fun QuantityUpdateDialog(
    uiState: SupplyDetailsUiState,
    onDismiss: () -> Unit,
    onQuantityChange: (String) -> Unit,
    onSave: () -> Unit,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    Text(text = "QuantityUpdateDialog")
}

@Composable
fun SupplyDetailItem(label: String, value: String) {
    Text(text = "SupplyDetailItem")
}