package com.sahilpatel.medsupplyguardian.ui.screens.audit.steps

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.sahilpatel.medsupplyguardian.data.database.entities.SupplyItem
import com.sahilpatel.medsupplyguardian.ui.screens.audit.AuditUiState
import com.sahilpatel.medsupplyguardian.ui.screens.audit.AuditViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuditStep1Screen(
    viewModel: AuditViewModel,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onItemClick: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Audit Step 1 / 5") },
                navigationIcon = {
                    IconButton(onClick = onPrevious) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            LinearProgressIndicator(
                progress = { 1 / 5f },
                modifier = Modifier.fillMaxWidth()
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Step 1: Verify Stock Quantity",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) { 
                    items(uiState.supplyItems) { item ->
                        AuditQuantityItem(item, uiState, { id, qty -> viewModel.updateVerifiedQuantity(id, qty.toIntOrNull() ?: 0) }, onItemClick)
                    }
                }
                Button(
                    onClick = onNext,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text("Next")
                }
            }
        }
    }
}

@Composable
private fun AuditQuantityItem(
    item: SupplyItem,
    uiState: AuditUiState,
    onQuantityChanged: (Int, String) -> Unit,
    onItemClick: (Int) -> Unit
) {
    val isBelowMinimum = item.isBelowMinimum()
    val containerColor = if (isBelowMinimum) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceVariant

    ElevatedCard(
        modifier = Modifier.fillMaxWidth().clickable { onItemClick(item.itemId) },
        colors = CardDefaults.elevatedCardColors(containerColor = containerColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "Min: ${item.minimumRequired}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            OutlinedTextField(
                value = uiState.verifiedQuantities[item.itemId]?.toString() ?: item.currentQuantity.toString(),
                onValueChange = { onQuantityChanged(item.itemId, it) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(0.5f)
            )
        }
    }
}