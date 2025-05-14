package com.daffa0049.motocurity.ui.screens

import android.annotation.SuppressLint
import android.bluetooth.BluetoothSocket
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.daffa0049.motocurity.dataClass.MotorDataClass
import com.daffa0049.motocurity.viewModel.BluetoothViewModel
import com.daffa0049.motocurity.viewModel.MotorViewModel
import com.daffa0049.motocurity.viewModel.NotificationViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navHostController: NavHostController, motorViewModel: MotorViewModel) {
    val user = FirebaseAuth.getInstance().currentUser

    LaunchedEffect(Unit) {
        if (user == null) {
            navHostController.navigate("loginScreen") {
                popUpTo("homeScreen") { inclusive = true } // Prevent back navigation
            }
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Motocurity"
                    )
                },
//                navigationIcon = {
//                    Icon(
//                        imageVector = Icons.Default.Home,
//                        contentDescription = "Logo"
//                    )
//                },
                actions = {
                    IconButton(onClick = {navHostController.navigate("profileScreen")}) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profile"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(MaterialTheme.colorScheme.primaryContainer)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {navHostController.navigate("addMotorScreen")},
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
            }
        }
    ){innerPadding ->
        HomeScreenContent(
            modifier = Modifier.padding(innerPadding),
            motorViewModel = motorViewModel,
            navHostController = navHostController
            )
    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun HomeScreenContent(
    modifier: Modifier = Modifier,
    motorViewModel: MotorViewModel,
    navHostController: NavHostController
){
    val context = LocalContext.current
    val data = motorViewModel.dataDummy.collectAsState()

    LaunchedEffect(Unit) {
        motorViewModel.getMotor{ e->
            Toast.makeText(context, e, Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if(data.value.isEmpty()){
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("No motor is added yet.")
            }
        }
        else{
            Text(
                text = "List Motor",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 84.dp)
            ) {
                items(data.value){
                    ListMotor(
                        motorDataClass = it,
                        onCLick = {
                            if(it.id.isBlank()){
                                Toast.makeText(context, "Data not ready yet. Please try again.", Toast.LENGTH_SHORT).show()
                                return@ListMotor
                            }
                            else{
                                Log.d("test", "$it")
                                motorViewModel.getMotorById(it.id)
                                navHostController.navigate("motorDetailScreen")
                            }
                        },
                        motorViewModel = motorViewModel
                        )
                }
            }
        }
    }
}


@Composable
fun ListMotor(
    motorDataClass: MotorDataClass,
    motorViewModel: MotorViewModel,
    onCLick: () -> Unit
){
    val coordinate by motorViewModel.getCoordinate(motorDataClass.id).collectAsState()
    val showNotification by motorViewModel.showNotification.collectAsState()
    val context = LocalContext.current
    val notificationViewModel = NotificationViewModel()
    LaunchedEffect(coordinate) {
        motorViewModel.updateLocation(
            latitude = coordinate.first,
            longitude =  coordinate.second,
            thresholdMeters = motorDataClass.distanceToActivate.toFloat(),
            lastLon = motorDataClass.lastLon,
            lastLat = motorDataClass.lastLat
        )
    }
    if(showNotification){
        notificationViewModel.sendNotification(
            context = context,
            title = "YOUR "+motorDataClass.nameMotor+"'s ALARM IS ACTIVATED",
            content = "It seems that your "+motorDataClass.nameMotor+
                    " with plate "+motorDataClass.plateNum+" is moving ${motorDataClass.distanceToActivate} meters"
        )
        motorDataClass.lastLon = coordinate.second
        motorDataClass.lastLat = coordinate.first
        motorViewModel.updateLastLatLonDetailMotor(arrayOf(coordinate.first.toString(), coordinate.second.toString()))
        motorViewModel.resetNotification()
    }

    Card(
        modifier = Modifier
            .padding(8.dp)
            .clickable { onCLick() },

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = motorDataClass.nameMotor,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    text = motorDataClass.plateNum,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    lineHeight = 4.sp
                )
                Text(
                    text = motorDataClass.battery.toString()+"%",
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 4.sp
                )
            }
            Switch(
                checked = motorDataClass.isOn,
                onCheckedChange = {motorViewModel.switchActionForList(motorDataClass)}
            )
        }
    }
}
//
//@Preview(showBackground = true)
//@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
//@Composable
//fun HomeScreenPreview() {
//    MotocurityTheme {
//        HomeScreen(navHostController = rememberNavController(), MotorViewModel())
//    }
//}
