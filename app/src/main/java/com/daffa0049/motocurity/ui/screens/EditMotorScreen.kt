package com.daffa0049.motocurity.ui.screens

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
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
            navHostController.navigateUp()
        }
    }
}

@Composable
fun EditMotorContent(modifier: Modifier = Modifier, motorViewModel: MotorViewModel, onClick: () -> Unit){
    val data = motorViewModel.selectedData.collectAsState()

    var nameMotor by remember { mutableStateOf(data.value.nameMotor) }
    var plateMotor by remember { mutableStateOf(data.value.plateNum) }
    var trackerCode by remember { mutableStateOf(data.value.trackCode) }

    var nameMotorIsErr by remember { mutableStateOf(false) }
    var plateMotorIsErr by remember { mutableStateOf(false) }
    var trackCodeIsErr by remember { mutableStateOf(false) }

    val context = LocalContext.current
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = nameMotor,
            onValueChange = {nameMotor = it},
            label = { Text(text = "Name Motor") },
            isError = nameMotorIsErr,
            supportingText = {
                ErrorMessage(nameMotorIsErr)
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            )
        )
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = plateMotor,
            onValueChange = {plateMotor = it},
            label = { Text(text = "Plate Motor") },
            isError = plateMotorIsErr,
            supportingText = {
                ErrorMessage(plateMotorIsErr)
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            )
        )
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = trackerCode,
            onValueChange = {trackerCode = it},
            label = { Text(text = "Tracker Code") },
            isError = trackCodeIsErr,
            supportingText = {
                ErrorMessage(trackCodeIsErr)
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done
            )
        )

        Button(
            modifier = Modifier.padding(8.dp),
            onClick = {
                nameMotorIsErr = nameMotor.isBlank()
                plateMotorIsErr = plateMotor.isBlank()
                trackCodeIsErr = trackerCode.isBlank()
                if(!nameMotorIsErr && !plateMotorIsErr && !trackCodeIsErr){
                    motorViewModel.editMotor(
                        id = data.value.id,
                        nameMotor = nameMotor,
                        plateMotor = plateMotor,
                        trackCode = trackerCode,
                        onSuccess = {
                            motorViewModel.getMotorById(id = data.value.id)
                            Toast.makeText(context, "Edit Successful!", Toast.LENGTH_SHORT).show()
                            onClick()
                        },
                        onError = {
                            Toast.makeText(context, "Edit Failed!", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
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
    val motorViewModel = MotorViewModel()
    motorViewModel.selectedData
    MotocurityTheme {
        EditMotorScreen(navHostController = rememberNavController(), motorViewModel = motorViewModel)
    }
}
