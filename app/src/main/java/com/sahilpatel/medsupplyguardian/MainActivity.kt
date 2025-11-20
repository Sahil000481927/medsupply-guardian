/**
 * File Overview: Main Activity for MedSupply Guardian application
 * 
 * This is the entry point of the application, setting up the navigation
 * and applying the Material Design 3 theme. Manages the overall app structure
 * and theme configuration.
 * 
 * @author Sahil Patel
 * @version 1.0
 */

package com.sahilpatel.medsupplyguardian

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.sahilpatel.medsupplyguardian.ui.navigation.NavGraph
import com.sahilpatel.medsupplyguardian.ui.screens.settings.SettingsViewModel
import com.sahilpatel.medsupplyguardian.ui.theme.MedSupplyGuardianTheme

/**
 * Main Activity class for the MedSupply Guardian application.
 * 
 * Initializes the app with Material Design 3 theming, sets up navigation,
 * and manages the overall application lifecycle. Applies theme settings
 * from user preferences.
 */
class MainActivity : ComponentActivity() {
    /**
     * Called when the activity is first created.
     * 
     * Sets up the compose UI with navigation and theming, enabling
     * edge-to-edge display for modern Android UI standards.
     * 
     * @param savedInstanceState Bundle containing saved state if activity is recreated
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            val settingsViewModel: SettingsViewModel = viewModel()
            val settingsState by settingsViewModel.uiState.collectAsState()
            
            MedSupplyGuardianTheme(
                darkTheme = settingsState.isDarkTheme,
                dynamicColor = true
            ) {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}