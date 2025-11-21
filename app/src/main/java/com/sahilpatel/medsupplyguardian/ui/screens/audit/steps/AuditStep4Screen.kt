package com.sahilpatel.medsupplyguardian.ui.screens.audit.steps

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sahilpatel.medsupplyguardian.data.database.entities.SupplyItem
import com.sahilpatel.medsupplyguardian.ui.screens.audit.AuditViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuditStep4Screen(
    viewModel: AuditViewModel,
    onNext: () -> Unit,
    onPrevious: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Audit Step 4 / 5") },
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
                progress = { 4 / 5f },
                modifier = Modifier.fillMaxWidth()
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Step 4: Identify Missing or Damaged Items",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.supplyItems) { item ->
                        MissingDamagedItem(item, uiState.missingItems.contains(item.itemId), uiState.damagedItems.contains(item.itemId), viewModel)
                    }
                }
                OutlinedTextField(
                    value = uiState.auditNotes,
                    onValueChange = { viewModel.updateAuditNotes(it) },
                    label = { Text("Audit Notes (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MissingDamagedItem(
    item: SupplyItem,
    isMissing: Boolean,
    isDamaged: Boolean,
    viewModel: AuditViewModel
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = item.name, modifier = Modifier.weight(1f))
            Row {
                FilterChip(
                    selected = isMissing,
                    onClick = { viewModel.toggleMissingItem(item.itemId) },
                    label = { Text("Missing") }
                )
                FilterChip(
                    selected = isDamaged,
                    onClick = { viewModel.toggleDamagedItem(item.itemId) },
                    label = { Text("Damaged") }
                )
            }
        }
    }
}