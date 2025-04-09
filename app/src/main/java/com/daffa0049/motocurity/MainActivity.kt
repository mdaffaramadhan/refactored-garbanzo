package com.daffa0049.motocurity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.daffa0049.motocurity.navigation.SetupNavGraph
import com.daffa0049.motocurity.ui.theme.MotocurityTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MotocurityApp()
        }
    }
}

@Composable
fun MotocurityApp() {
    MotocurityTheme {
        Surface {
            val navController = rememberNavController()
            SetupNavGraph(navHostController = navController)
        }
    }
}
