package com.daffa0049.motocurity.ui.screens

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.daffa0049.motocurity.R
import com.daffa0049.motocurity.component.DeleteDialog
import com.daffa0049.motocurity.dataClass.MotorDataClass
import com.daffa0049.motocurity.ui.theme.MotocurityTheme
import com.daffa0049.motocurity.viewModel.MotorViewModel

@RequiresApi(Build.VERSION_CODES.N)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MotorDetailScreen(navHostController: NavHostController, motorViewModel: MotorViewModel){
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Motor Detail"
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
        MotorDetailContent(modifier = Modifier.padding(innerPadding), motorViewModel = motorViewModel, navHostController = navHostController)
    }
}

@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun MotorDetailContent(modifier: Modifier = Modifier, motorViewModel: MotorViewModel, navHostController: NavHostController){
    val data = motorViewModel.selectedData.collectAsState()
    var distanceToActivate by remember { mutableStateOf(data.value.distanceToActivate.toString()) }
    var distanceIsErr by remember { mutableStateOf(false) }
    var distanceIsActive by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = data.value.nameMotor,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "${data.value.plateNum} ${data.value.battery} %"
        )
        Text(
            text = "Track Code: ${data.value.trackCode}"
        )
        Box(
            modifier = Modifier.size(125.dp)
        ){
            Image(
                painter = painterResource(data.value.picMotor),
                contentDescription = "Picture of user's motor",
                contentScale = ContentScale.FillHeight
            )
        }
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.Absolute.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Alarm",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Switch(
                    checked = data.value.isOn,
                    onCheckedChange = {motorViewModel.switchActionForDetail(data.value)}
                )
            }
        }
        Card(
            modifier = Modifier.fillMaxWidth()
        )
        {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = distanceToActivate,
                    onValueChange = {distanceToActivate = it},
                    label = { Text(text = "Distance to activate alarm") },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Number
                    ),
                    isError = distanceIsErr,
                    supportingText = {
                        ErrorMessage(distanceIsErr)
                    },
                    enabled = distanceIsActive
                )
                Button(
                    onClick = {
                        if(distanceIsActive){
                            distanceIsErr = distanceToActivate.isEmpty()
                            if(!distanceIsErr){
                                motorViewModel.editDistanceToACtivate(data.value.id, distanceToActivate.toLong())
                                distanceIsActive = false
                            }
                        }
                        else{
                            distanceIsActive = true
                        }
                    }
                ) {
                    Text(
                        text = (
                                if (distanceIsActive) {
                                    "Save"
                                }
                                else {
                                    "Edit"
                                }
                                )
                    )
                }
            }
        }
        Button(
            onClick = {navHostController.navigate("editMotorScreen")}
        ) {
            Text(
                text = "Edit"
            )
        }

        ShowDeleteDialog(motorDataClass = data.value, motorViewModel = motorViewModel, navHostController = navHostController)

        DebugNotificationButton(motorDataClass = data.value)
    }
}

@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun DebugNotificationButton(motorDataClass: MotorDataClass){
    val context = LocalContext.current
    var importance = NotificationManager.IMPORTANCE_MAX

    Button(
        onClick = {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                val channelName = "Motor Alarm"
                val descriptionText = "Notification if your motorcycle is moving for a few meters"
                importance = NotificationManager.IMPORTANCE_HIGH
                val mChannel = NotificationChannel("MOTOR_ALARM", channelName, importance)
                mChannel.description = descriptionText
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(mChannel)
            }
            val builder = NotificationCompat.Builder(context, "MOTOR_ALARM")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("YOUR "+motorDataClass.nameMotor+"'s ALARM IS ACTIVATED")
                .setContentText("It seems that your "+motorDataClass.nameMotor+" with plate "+motorDataClass.plateNum+" is moving a few meters")
                .setStyle(NotificationCompat.BigTextStyle().bigText("It seems that your "+motorDataClass.nameMotor+" with plate "+motorDataClass.plateNum+" is moving a few meters"))
                .setPriority(importance)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
            with(NotificationManagerCompat.from(context)){
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return@Button
                }
                notify(1001, builder.build())
            }
        },
        colors = ButtonDefaults.buttonColors(Color.Red)
    ) {
        Text(
            text = "NOTIF_DEBUG"

        )
    }
}
@Composable
fun RequestNotificationPermission(){
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ){}
   LaunchedEffect(Unit) {
       if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
           val isGranted = ContextCompat.checkSelfPermission(
               context,
               Manifest.permission.POST_NOTIFICATIONS
           ) == PackageManager.PERMISSION_GRANTED
           if (!isGranted) {
               permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
           }
       }
   }
}

@Composable
fun ShowDeleteDialog(motorDataClass: MotorDataClass, motorViewModel: MotorViewModel, navHostController: NavHostController){
    var showDialog by remember { mutableStateOf(false) }
    Button(
        onClick = {showDialog = true},
        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary)
    ) {
        Text(
            text = "Delete"
        )
    }
    if(showDialog){
        DeleteDialog(
            title = "Delete ${motorDataClass.nameMotor}",
            message = "Are you sure you want to delete this data?",
            onDelete = {
                motorViewModel.deleteMotor(motorDataClass)
                navHostController.navigateUp()
                       },
            onDismiss = { showDialog = false }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.N)
@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun DetailMotorScreenPreview() {
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
    motorViewModel.selectData(motorDataClass)
    MotocurityTheme {
        MotorDetailScreen(navHostController = rememberNavController(), motorViewModel)
    }
}