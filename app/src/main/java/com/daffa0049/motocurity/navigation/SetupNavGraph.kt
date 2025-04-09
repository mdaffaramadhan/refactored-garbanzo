package com.daffa0049.motocurity.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.daffa0049.motocurity.screens.LoginScreen
import com.daffa0049.motocurity.screens.RegisterScreen

@Composable
fun SetupNavGraph(navHostController: NavHostController = rememberNavController()){
    NavHost(
        navController = navHostController,
        startDestination = Screen.LoginScreen.route
    ) {
        composable(route = Screen.LoginScreen.route){
            LoginScreen(navHostController)
        }
        composable(route = Screen.RegisterScreen.route){
            RegisterScreen(navHostController)
        }
    }
}