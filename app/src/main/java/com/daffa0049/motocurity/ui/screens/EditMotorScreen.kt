package com.daffa0049.motocurity.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
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
import com.daffa0049.motocurity.R
import com.daffa0049.motocurity.dataClass.MotorDataClass
import com.daffa0049.motocurity.ui.theme.MotocurityTheme
import com.daffa0049.motocurity.viewModel.MotorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMotorScreen(navHostController: NavHostController, motorViewModel: MotorViewModel){
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Edit Motor"
                    )
                },
                actions = {
                    IconButton(onClick = {navHostController.navigate("profileScreen")}) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profile"
                        )
                    }
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
        EditMotorContent(modifier = Modifier.padding(innerPadding), motorViewModel = motorViewModel){
            navHostController.navigate("motorDetailScreen")
        }
    }
}

@Composable
fun EditMotorContent(modifier: Modifier = Modifier, motorViewModel: MotorViewModel, onClick: () -> Unit){
    var nameMotor by remember { mutableStateOf(motorViewModel.selectedData.nameMotor) }
    var plateMotor by remember { mutableStateOf(motorViewModel.selectedData.plateNum) }
    var trackerCode by remember { mutableStateOf(motorViewModel.selectedData.trackCode) }
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
            label = { Text(text = "Tracker Code") },
            readOnly = true
        )
        Button(
            modifier = Modifier.padding(8.dp),
            onClick = { onClick() }
        ) {
            Text(
                text = "Edit"
            )
        }
    }
}


@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun EditMotorScreenPreview() {
    val motorDataClass = MotorDataClass(
        1,
        "Vario Kabru",
        "W 7165 XXX",
        75,
        picMotor = R.drawable.ic_launcher_background,
        "123",
        isOn = true,
        isConnected = true
    )
    val motorViewModel = MotorViewModel()
    motorViewModel.selectedData = motorDataClass
    MotocurityTheme {
        EditMotorScreen(navHostController = rememberNavController(), motorViewModel = motorViewModel)
    }
}
