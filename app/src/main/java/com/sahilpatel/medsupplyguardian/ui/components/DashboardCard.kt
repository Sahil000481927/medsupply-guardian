/**
 * File Overview: Dashboard card component for home screen
 * 
 * This file provides a reusable card component for displaying key metrics
 * and alerts on the home dashboard including critical stock and expiring items.
 * 
 * @author Sahil Patel
 * @version 1.0
 */

package com.sahilpatel.medsupplyguardian.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

/**
 * Material 3 card component for dashboard metrics.
 * 
 * Displays a metric card with title, count, subtitle message, and icon.
 * Supports different color schemes based on alert severity.
 * 
 * @param title Main title text (e.g., "Critical Stock Alerts")
 * @param count Numeric count to display prominently
 * @param subtitle Descriptive subtitle text
 * @param icon Leading icon for visual identification
 * @param containerColor Background color for the card
 * @param contentColor Text and icon color
 * @param onClick Optional click handler for navigation
 */
@Composable
fun DashboardCard(
    title: String,
    count: Int,
    subtitle: String,
    icon: ImageVector,
    containerColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onPrimaryContainer,
    onClick: (() -> Unit)? = null
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        onClick = onClick ?: {}
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = contentColor
            )
            
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = contentColor
                )
                
                Text(
                    text = count.toString(),
                    style = MaterialTheme.typography.displaySmall,
                    color = contentColor
                )
                
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = contentColor.copy(alpha = 0.8f)
                )
            }
        }
    }
}

/**
 * Critical alert dashboard card with error container styling.
 * 
 * Pre-configured dashboard card for critical stock alerts using
 * error color scheme for high visibility.
 * 
 * @param count Number of critical items
 * @param onClick Optional click handler
 */
@Composable
fun CriticalAlertCard(
    count: Int,
    onClick: (() -> Unit)? = null
) {
    DashboardCard(
        title = "Critical Stock Alerts",
        count = count,
        subtitle = if (count == 1) "$count item requires immediate attention" 
                   else "$count items require immediate attention",
        icon = Icons.Default.Warning,
        containerColor = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
        onClick = onClick
    )
}

/**
 * Expiring items dashboard card with secondary container styling.
 * 
 * Pre-configured dashboard card for items expiring soon using
 * secondary color scheme.
 * 
 * @param count Number of expiring items
 * @param daysThreshold Number of days threshold for expiration
 * @param onClick Optional click handler
 */
@Composable
fun ExpiringItemsCard(
    count: Int,
    daysThreshold: Int = 30,
    onClick: (() -> Unit)? = null
) {
    DashboardCard(
        title = "Expiring Soon",
        count = count,
        subtitle = if (count == 1) "$count item expiring within $daysThreshold days"
                   else "$count items expiring within $daysThreshold days",
        icon = Icons.Default.Schedule,
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        onClick = onClick
    )
}