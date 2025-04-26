package com.daffa0049.motocurity.navigation

import com.daffa0049.motocurity.ui.screens.KEY_ID_USER

sealed class Screen(val route: String) {
    data object LoginScreen: Screen("loginScreen")
    data object RegisterScreen: Screen("registerScreen")
    data object HomeScreen: Screen("homeScreen/{$KEY_ID_USER}"){
        fun withId(id: String) = "homeScreen/{$id}"
    }
    data object AddMotorScreen: Screen("addMotorScreen")
    data object ProfileScreen: Screen("profileScreen")
    data object MotorDetailScreen: Screen("motorDetailScreen")
    data object EditMotorScreen: Screen("editMotorScreen")
}