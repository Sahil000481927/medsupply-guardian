/**
 * File Overview: Navigation graph for MedSupply Guardian application
 * 
 * This file defines the complete navigation structure using Navigation Compose,
 * including all screen routes, arguments, and navigation actions. Implements
 * type-safe navigation with proper argument passing between screens.
 * 
 * @author Sahil Patel
 * @version 1.5
 */

package com.sahilpatel.medsupplyguardian.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sahilpatel.medsupplyguardian.ui.screens.audit.AuditViewModel
import com.sahilpatel.medsupplyguardian.ui.screens.audit.steps.AuditStep1Screen
import com.sahilpatel.medsupplyguardian.ui.screens.audit.steps.AuditStep2Screen
import com.sahilpatel.medsupplyguardian.ui.screens.audit.steps.AuditStep3Screen
import com.sahilpatel.medsupplyguardian.ui.screens.audit.steps.AuditStep4Screen
import com.sahilpatel.medsupplyguardian.ui.screens.audit.steps.AuditStep5Screen
import com.sahilpatel.medsupplyguardian.ui.screens.home.HomeScreen
import com.sahilpatel.medsupplyguardian.ui.screens.supplies.SuppliesListScreen
import com.sahilpatel.medsupplyguardian.ui.screens.supplies.SupplyDetailsScreen
import com.sahilpatel.medsupplyguardian.ui.screens.audit.AuditStartScreen
import com.sahilpatel.medsupplyguardian.ui.screens.settings.SettingsScreen
import com.sahilpatel.medsupplyguardian.utils.Constants

/**
 * Main navigation graph for the application.
 * 
 * Defines all navigation routes and their corresponding composable screens.
 * Handles navigation arguments for screens that require parameters such as
 * item IDs.
 * 
 * @param navController Navigation controller for managing navigation actions
 * @param startDestination Initial route to display when app launches
 */
@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Constants.Routes.HOME
) {
    val auditViewModel: AuditViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        
        composable(route = Constants.Routes.HOME) {
            HomeScreen(
                onNavigateToSupplies = { filterType, filterValue ->
                    navController.navigate(Constants.Routes.supplies(filterType, filterValue))
                },
                onNavigateToAudit = {
                    navController.navigate(Constants.Routes.AUDIT_START)
                },
                onNavigateToSettings = {
                    navController.navigate(Constants.Routes.SETTINGS)
                }
            )
        }
        
        composable(
            route = Constants.Routes.SUPPLIES,
            arguments = listOf(
                navArgument("filterType") { type = NavType.StringType; nullable = true },
                navArgument("filterValue") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val filterType = backStackEntry.arguments?.getString("filterType")
            val filterValue = backStackEntry.arguments?.getString("filterValue")
            
            SuppliesListScreen(
                onNavigateToDetails = { itemId ->
                    navController.navigate(Constants.Routes.supplyDetails(itemId))
                },
                onNavigateBack = {
                    navController.popBackStack()
                },
                filterType = if (filterType == "none") null else filterType,
                filterValue = if (filterValue == "none") null else filterValue
            )
        }
        
        composable(
            route = Constants.Routes.SUPPLY_DETAILS,
            arguments = listOf(
                navArgument("itemId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getInt("itemId") ?: 0
            SupplyDetailsScreen(
                itemId = itemId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(route = Constants.Routes.AUDIT_START) {
            AuditStartScreen(
                onNavigateToStep = { 
                    navController.navigate("${Constants.Routes.AUDIT}/1")
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(route = Constants.Routes.AUDIT + "/{stepNumber}") {
            val step = it.arguments?.getString("stepNumber")?.toInt() ?: 1
            when (step) {
                1 -> AuditStep1Screen(
                    viewModel = auditViewModel,
                    onNext = { navController.navigate("${Constants.Routes.AUDIT}/2") },
                    onPrevious = { navController.popBackStack() },
                    onItemClick = { itemId -> navController.navigate(Constants.Routes.supplyDetails(itemId)) }
                )
                2 -> AuditStep2Screen(
                    viewModel = auditViewModel,
                    onNext = { navController.navigate("${Constants.Routes.AUDIT}/3") },
                    onPrevious = { navController.popBackStack() }
                )
                3 -> AuditStep3Screen(
                    viewModel = auditViewModel,
                    onNext = { navController.navigate("${Constants.Routes.AUDIT}/4") },
                    onPrevious = { navController.popBackStack() }
                )
                4 -> AuditStep4Screen(
                    viewModel = auditViewModel,
                    onNext = { navController.navigate("${Constants.Routes.AUDIT}/5") },
                    onPrevious = { navController.popBackStack() }
                )
                5 -> AuditStep5Screen(
                    viewModel = auditViewModel,
                    onSubmit = { 
                        auditViewModel.submitAudit()
                        navController.navigate(Constants.Routes.HOME) { popUpTo(Constants.Routes.HOME) { inclusive = true } }
                     },
                    onPrevious = { navController.popBackStack() }
                )
            }
        }
        
        composable(route = Constants.Routes.SETTINGS) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}