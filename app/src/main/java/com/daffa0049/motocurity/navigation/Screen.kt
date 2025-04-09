package com.daffa0049.motocurity.navigation

sealed class Screen(val route: String) {
    object LoginScreen : Screen("loginScreen")
    object RegisterScreen : Screen("registerScreen")
    object HomeScreen : Screen("homeScreen")
}
