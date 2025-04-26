package com.daffa0049.motocurity.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.daffa0049.motocurity.ui.screens.AddMotorScreen
import com.daffa0049.motocurity.ui.screens.EditMotorScreen
import com.daffa0049.motocurity.ui.screens.HomeScreen
import com.daffa0049.motocurity.ui.screens.KEY_ID_USER
import com.daffa0049.motocurity.ui.screens.LoginScreen
import com.daffa0049.motocurity.ui.screens.MotorDetailScreen
import com.daffa0049.motocurity.ui.screens.ProfileScreen
import com.daffa0049.motocurity.ui.screens.RegisterScreen
import com.daffa0049.motocurity.viewModel.MotorViewModel

@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun SetupNavGraph(navHostController: NavHostController = rememberNavController()){
    val motorViewModel = MotorViewModel()
    NavHost(
        navController = navHostController,
        startDestination = Screen.LoginScreen.route
    ) {
        composable(route = Screen.LoginScreen.route){
            LoginScreen(navHostController = navHostController)
        }
        composable(route = Screen.RegisterScreen.route){
            RegisterScreen(navHostController = navHostController)
        }
        composable(
            route = Screen.HomeScreen.route,
            arguments = listOf(
                navArgument(KEY_ID_USER){type = NavType.StringType}
            )
        ){navBackStackEntry->
            val id = navBackStackEntry.arguments?.getString(KEY_ID_USER)
            HomeScreen(navHostController = navHostController, motorViewModel = motorViewModel, id)
        }
        composable(route = Screen.AddMotorScreen.route){
            AddMotorScreen(navHostController = navHostController, motorViewModel = motorViewModel)
        }
        composable(route = Screen.ProfileScreen.route){
            ProfileScreen(navHostController = navHostController)
        }
        composable(route = Screen.MotorDetailScreen.route){
            MotorDetailScreen(navHostController = navHostController, motorViewModel = motorViewModel)
        }
        composable(route = Screen.EditMotorScreen.route){
            EditMotorScreen(navHostController = navHostController, motorViewModel = motorViewModel)
        }
    }
}