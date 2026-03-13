package com.example.oppurtunityscanner

import android.content.Context
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.oppurtunityscanner.ui.theme.OppurtunityscannerTheme

class MainActivity : ComponentActivity() {
    
    private var hasPermissionState = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        hasPermissionState.value = isNotificationServiceEnabled(this)

        setContent {
            OppurtunityscannerTheme {
                val hasPermission by hasPermissionState

                if (hasPermission) {
                    AppNavigation()
                } else {
                    PermissionScreen(onPermissionGranted = {
                        hasPermissionState.value = isNotificationServiceEnabled(this)
                    })
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Update state when returning from Settings
        hasPermissionState.value = isNotificationServiceEnabled(this)
    }

    private fun isNotificationServiceEnabled(context: Context): Boolean {
        val pkgName = context.packageName
        val flat = Settings.Secure.getString(context.contentResolver, "enabled_notification_listeners")
        if (flat != null && flat.isNotEmpty()) {
            val names = flat.split(":")
            for (name in names) {
                val cn = android.content.ComponentName.unflattenFromString(name)
                if (cn != null && cn.packageName == pkgName) {
                    return true
                }
            }
        }
        return false
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val viewModel: OpportunityViewModel = viewModel()

    NavHost(navController = navController, startDestination = "dashboard") {
        composable("dashboard") {
            OpportunityDashboard(
                viewModel = viewModel,
                onOpportunityClick = { id ->
                    navController.navigate("detail/$id")
                }
            )
        }
        composable(
            route = "detail/{opportunityId}",
            arguments = listOf(navArgument("opportunityId") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("opportunityId") ?: 0
            OpportunityDetailScreen(
                opportunityId = id,
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
