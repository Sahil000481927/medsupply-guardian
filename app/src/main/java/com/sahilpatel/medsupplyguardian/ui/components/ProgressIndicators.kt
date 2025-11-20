/**
 * File Overview: Reusable progress indicator components
 * 
 * This file contains composable functions for displaying progress indicators
 * throughout the application including loading states, audit workflow progress,
 * and upload simulation feedback.
 * 
 * @author Sahil Patel
 * @version 1.0
 */

package com.sahilpatel.medsupplyguardian.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

/**
 * Full-screen loading indicator displayed during app startup.
 * 
 * Shows a centered circular progress indicator with optional message
 * text. Used when loading initial data from Room database.
 * 
 * @param message Optional loading message to display below the indicator
 */
@Composable
fun FullScreenLoadingIndicator(message: String = "Loading...") {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

/**
 * Linear progress indicator for audit workflow steps.
 * 
 * Displays a horizontal progress bar showing current step completion
 * status across the 5-step audit workflow.
 * 
 * @param currentStep Current step number (1-5)
 * @param totalSteps Total number of steps in the workflow (default: 5)
 */
@Composable
fun AuditStepProgress(
    currentStep: Int,
    totalSteps: Int = 5
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Step $currentStep of $totalSteps",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        LinearProgressIndicator(
            progress = { currentStep.toFloat() / totalSteps.toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}

/**
 * Upload progress overlay with semi-transparent background.
 * 
 * Displays a modal overlay with circular progress indicator and message,
 * simulating audit report upload to external systems. Prevents user
 * interaction during the upload process.
 * 
 * @param isVisible Whether the overlay is currently visible
 * @param message Upload status message to display
 * @param onUploadComplete Callback invoked when simulated upload completes
 */
@Composable
fun UploadProgressOverlay(
    isVisible: Boolean,
    message: String = "Uploading audit report...",
    onUploadComplete: () -> Unit = {}
) {
    if (isVisible) {
        LaunchedEffect(Unit) {
            delay(2500L)
            onUploadComplete()
        }
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f)),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .padding(32.dp)
                    .wrapContentSize(),
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

/**
 * Inline circular progress indicator for list items.
 * 
 * Small progress indicator suitable for displaying loading states
 * within cards, list items, or other compact UI elements.
 * 
 * @param modifier Optional modifier for customization
 */
@Composable
fun InlineLoadingIndicator(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(24.dp),
            strokeWidth = 2.dp,
            color = MaterialTheme.colorScheme.primary
        )
    }
}