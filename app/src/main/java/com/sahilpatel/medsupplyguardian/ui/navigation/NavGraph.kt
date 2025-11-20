/**
 * File Overview: Navigation graph for MedSupply Guardian application
 * 
 * This file defines the complete navigation structure using Navigation Compose,
 * including all screen routes, arguments, and navigation actions. Implements
 * type-safe navigation with proper argument passing between screens.
 * 
 * @author Sahil Patel
 * @version 1.0
 */

package com.sahilpatel.medsupplyguardian.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sahilpatel.medsupplyguardian.ui.screens.home.HomeScreen
import com.sahilpatel.medsupplyguardian.ui.screens.supplies.SuppliesListScreen
import com.sahilpatel.medsupplyguardian.ui.screens.supplies.SupplyDetailsScreen
import com.sahilpatel.medsupplyguardian.ui.screens.audit.AuditStartScreen
import com.sahilpatel.medsupplyguardian.ui.screens.audit.steps.*
import com.sahilpatel.medsupplyguardian.ui.screens.settings.SettingsScreen
import com.sahilpatel.medsupplyguardian.utils.Constants

/**
 * Main navigation graph for the application.
 * 
 * Defines all navigation routes and their corresponding composable screens.
 * Handles navigation arguments for screens that require parameters such as
 * item IDs and audit step numbers.
 * 
 * @param navController Navigation controller for managing navigation actions
 * @param startDestination Initial route to display when app launches
 */
@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Constants.Routes.HOME
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        
        composable(route = Constants.Routes.HOME) {
            HomeScreen(
                onNavigateToSupplies = {
                    navController.navigate(Constants.Routes.SUPPLIES)
                },
                onNavigateToAudit = {
                    navController.navigate(Constants.Routes.AUDIT_START)
                },
                onNavigateToSettings = {
                    navController.navigate(Constants.Routes.SETTINGS)
                }
            )
        }
        
        composable(route = Constants.Routes.SUPPLIES) {
            SuppliesListScreen(
                onNavigateToDetails = { itemId ->
                    navController.navigate(Constants.Routes.supplyDetails(itemId))
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
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
                onNavigateToStep = { stepNumber ->
                    navController.navigate(Constants.Routes.auditStep(stepNumber))
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(
            route = Constants.Routes.AUDIT_STEP,
            arguments = listOf(
                navArgument("stepNumber") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val stepNumber = backStackEntry.arguments?.getInt("stepNumber") ?: 1
            
            when (stepNumber) {
                Constants.Audit.STEP_VERIFY_QUANTITY -> {
                    Step1VerifyQuantity(
                        onNavigateNext = {
                            navController.navigate(Constants.Routes.auditStep(2))
                        },
                        onNavigateBack = {
                            navController.popBackStack()
                        }
                    )
                }
                Constants.Audit.STEP_CHECK_EXPIRY -> {
                    Step2CheckExpiry(
                        onNavigateNext = {
                            navController.navigate(Constants.Routes.auditStep(3))
                        },
                        onNavigateBack = {
                            navController.popBackStack()
                        }
                    )
                }
                Constants.Audit.STEP_VALIDATE_STORAGE -> {
                    Step3ValidateStorage(
                        onNavigateNext = {
                            navController.navigate(Constants.Routes.auditStep(4))
                        },
                        onNavigateBack = {
                            navController.popBackStack()
                        }
                    )
                }
                Constants.Audit.STEP_MISSING_DAMAGED -> {
                    Step4MissingDamaged(
                        onNavigateNext = {
                            navController.navigate(Constants.Routes.auditStep(5))
                        },
                        onNavigateBack = {
                            navController.popBackStack()
                        }
                    )
                }
                Constants.Audit.STEP_SUMMARY -> {
                    Step5Summary(
                        onNavigateToHome = {
                            navController.navigate(Constants.Routes.HOME) {
                                popUpTo(Constants.Routes.HOME) { inclusive = false }
                            }
                        },
                        onNavigateBack = {
                            navController.popBackStack()
                        }
                    )
                }
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