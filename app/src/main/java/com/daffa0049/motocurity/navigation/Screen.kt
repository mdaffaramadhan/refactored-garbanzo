package com.daffa0049.motocurity.navigation

sealed class Screen(val route: String) {
    data object LoginScreen: Screen("loginScreen")
    data object RegisterScreen: Screen("registerScreen")
    data object HomeScreen: Screen("homeScreen")
    data object AddMotorScreen: Screen("addMotorScreen")
    data object ProfileScreen: Screen("profileScreen")
}