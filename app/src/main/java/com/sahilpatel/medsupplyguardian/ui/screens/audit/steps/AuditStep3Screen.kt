package com.sahilpatel.medsupplyguardian.ui.screens.audit.steps

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sahilpatel.medsupplyguardian.ui.screens.audit.AuditViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuditStep3Screen(
    viewModel: AuditViewModel,
    onNext: () -> Unit,
    onPrevious: () -> Unit
) {
    val checklistState = remember {
        mutableStateOf(
            mapOf(
                "Temperature within safe range" to false,
                "Humidity acceptable" to false,
                "Storage area clean" to false,
                "Items properly sealed" to false,
                "Nothing obstructing access" to false
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Audit Step 3 / 5") },
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
                progress = { 3 / 5f },
                modifier = Modifier.fillMaxWidth()
            )
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Step 3: Validate Storage Conditions",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                checklistState.value.keys.forEach { condition ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = checklistState.value[condition] ?: false,
                            onCheckedChange = { isChecked ->
                                val updatedState = checklistState.value.toMutableMap()
                                updatedState[condition] = isChecked
                                checklistState.value = updatedState
                            }
                        )
                        Text(text = condition, modifier = Modifier.padding(start = 8.dp))
                    }
                }
                Button(
                    onClick = {
                        val allConditionsMet = checklistState.value.values.all { it }
                        viewModel.updateStorageConditions(allConditionsMet)
                        onNext()
                    },
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