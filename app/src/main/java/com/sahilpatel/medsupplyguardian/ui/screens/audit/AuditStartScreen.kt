/**
 * File Overview: Audit Start Screen composable
 * 
 * This file contains the audit workflow introduction screen that explains
 * the 5-step audit process and allows the technician to begin the compliance
 * audit workflow.
 * 
 * @author Sahil Patel
 * @version 1.0
 */

package com.sahilpatel.medsupplyguardian.ui.screens.audit

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sahilpatel.medsupplyguardian.utils.Constants

/**
 * Audit start screen composable displaying workflow overview.
 * 
 * Provides an introduction to the 5-step audit process with descriptions
 * of each step and a button to begin the workflow.
 * 
 * @param onNavigateToStep Callback to navigate to a specific audit step
 * @param onNavigateBack Callback to navigate back to home screen
 * @param viewModel ViewModel for audit state management
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuditStartScreen(
    onNavigateToStep: (Int) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: AuditViewModel = viewModel()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Compliance Audit") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Assignment,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "5-Step Audit Workflow",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Complete all steps to ensure compliance",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            
            Text(
                text = "Audit Steps",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            AuditStepCard(
                stepNumber = 1,
                title = "Verify Quantities",
                description = "Confirm current stock quantities for all items",
                icon = Icons.Default.Inventory
            )
            
            AuditStepCard(
                stepNumber = 2,
                title = "Check Expiration Dates",
                description = "Review and validate expiration dates",
                icon = Icons.Default.DateRange
            )
            
            AuditStepCard(
                stepNumber = 3,
                title = "Validate Storage Conditions",
                description = "Ensure proper storage temperature and environment",
                icon = Icons.Default.Thermostat
            )
            
            AuditStepCard(
                stepNumber = 4,
                title = "Report Missing or Damaged Items",
                description = "Document any discrepancies or damage",
                icon = Icons.Default.ReportProblem
            )
            
            AuditStepCard(
                stepNumber = 5,
                title = "Review and Submit",
                description = "Review findings and upload audit report",
                icon = Icons.Default.CloudUpload
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = {
                    viewModel.resetAudit()
                    onNavigateToStep(Constants.Audit.STEP_VERIFY_QUANTITY)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Start Audit",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

/**
 * Card component displaying an individual audit step.
 * 
 * Shows step number, title, description, and icon for each step
 * in the audit workflow overview.
 * 
 * @param stepNumber Step number (1-5)
 * @param title Step title
 * @param description Step description
 * @param icon Step icon
 */
@Composable
fun AuditStepCard(
    stepNumber: Int,
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Box(
                    modifier = Modifier.size(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stepNumber.toString(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
            
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}