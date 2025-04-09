package com.daffa0049.motocurity.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.daffa0049.motocurity.ui.theme.MotocurityTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMotorScreen(navHostController: NavHostController){
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Add Motor"
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(MaterialTheme.colorScheme.primaryContainer),
                navigationIcon = {
                    IconButton(
                        onClick = {navHostController.navigateUp()}
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Back arrow"
                        )
                    }
                }
            )
        }
    ) {
        innerPadding ->
        AddMotorContent(modifier = Modifier.padding(innerPadding)){
            navHostController.navigate("homeScreen")
        }
    }
}
@Composable
fun AddMotorContent(modifier: Modifier = Modifier, onCLick: () -> Unit){
    var nameMotor by remember { mutableStateOf("") }
    var plateMotor by remember { mutableStateOf("") }
    var trackerCode by remember { mutableStateOf("") }
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = nameMotor,
            onValueChange = {nameMotor = it},
            label = { Text(text = "Name Motor") }
        )
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = plateMotor,
            onValueChange = {plateMotor = it},
            label = { Text(text = "Plate Motor") }
        )
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = trackerCode,
            onValueChange = {trackerCode = it},
            label = { Text(text = "Plate Motor") }
        )
        Button(
            modifier = Modifier.padding(8.dp),
            onClick = {onCLick()}
        ) {
            Text(
                text = "Submit"
            )
        }
    }
}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun AddMotorScreenPreview() {
    MotocurityTheme {
        AddMotorScreen(navHostController = rememberNavController())
    }
}