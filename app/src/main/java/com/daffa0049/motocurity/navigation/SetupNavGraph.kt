package com.daffa0049.motocurity.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.daffa0049.motocurity.ui.screens.AddMotorScreen
import com.daffa0049.motocurity.ui.screens.HomeScreen
import com.daffa0049.motocurity.ui.screens.LoginScreen
import com.daffa0049.motocurity.ui.screens.ProfileScreen
import com.daffa0049.motocurity.ui.screens.RegisterScreen

@Composable
fun SetupNavGraph(navHostController: NavHostController = rememberNavController()){
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
        composable(route = Screen.HomeScreen.route){
            HomeScreen(navHostController = navHostController)
        }
        composable(route = Screen.AddMotorScreen.route){
            AddMotorScreen(navHostController = navHostController)
        }
        composable(route = Screen.ProfileScreen.route){
            ProfileScreen(navHostController = navHostController)
        }
    }
}