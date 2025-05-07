package com.daffa0049.motocurity.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.Location
import android.os.Build
import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.mutableDoubleStateOf
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
import com.daffa0049.motocurity.component.dialog.BluetoothDialog
import com.daffa0049.motocurity.dataClass.MotorDataClass
import com.daffa0049.motocurity.ui.theme.MotocurityTheme
import com.daffa0049.motocurity.viewModel.BluetoothViewModel
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
        MotorDetailContent(
            modifier = Modifier.padding(innerPadding),
            motorViewModel = motorViewModel,
            navHostController = navHostController
        )
    }
}

@SuppressLint("MutableCollectionMutableState")
@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun MotorDetailContent(
    modifier: Modifier = Modifier,
    motorViewModel: MotorViewModel,
    navHostController: NavHostController
){
    val blueToothTest = BluetoothViewModel()
    val data = motorViewModel.selectedData.collectAsState()
    var dataFromBt by remember { mutableStateOf("") }
    var socket by remember { mutableStateOf<BluetoothSocket?>(null) }
    var distanceToActivate by remember(data.value.id)
    { mutableStateOf(data.value.distanceToActivate.toString()) }
    var distanceIsErr by remember { mutableStateOf(false) }
    var distanceIsActive by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val latitude: Double?
    val longitude: Double?
    val lines = dataFromBt.lines()
    val latLine = lines.find { it.contains("Latitude:") }
    val lonLine = lines.find { it.contains("Longitude:") }
    latitude = latLine?.substringAfter("Latitude:")?.trim()?.toDoubleOrNull()
    longitude = lonLine?.substringAfter("Longitude:")?.trim()?.toDoubleOrNull()
    var showNotification: Boolean by remember { mutableStateOf(false) }


    Column(
        modifier = modifier.padding(16.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = data.value.nameMotor,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
        Text("latitude: $latitude - longitude: $longitude")
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
                    enabled = distanceIsActive,
                    trailingIcon = {
                        Text("Meters")
                    }
                )
                Button(
                    onClick = {
                        if(distanceIsActive){
                            distanceIsErr = distanceToActivate.isEmpty()
                            if(!distanceIsErr){
                                motorViewModel.editDistanceToACtivate(data.value.id, distanceToActivate.toLong())
                                distanceIsActive = false
                                Toast.makeText(context, "Distance to activate the alarm is $distanceToActivate meters now", Toast.LENGTH_SHORT).show()
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
        BluetoothButton(
            blueToothTest,
            socket = {
                socket = it
            }
        )
        LaunchedEffect(socket) {
            socket?.takeIf { it.isConnected }?.let { safeSocket ->
                blueToothTest.readFromSocket(
                    socket = safeSocket,
                    output = { line ->
                        Log.d("BluetoothData", "Received: $line")
                        dataFromBt = line
                    }
                )
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

        LocationTracker(
            longitude = longitude?:0.0,
            latitude = latitude?:0.0,
            distanceToActivate = distanceToActivate
        ) {
            showNotification = true
        }
        if(showNotification){
            Notification(
                motorDataClass = data.value
            )
            showNotification = false
        }
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
fun Notification(motorDataClass: MotorDataClass){
    val context = LocalContext.current
    var importance = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        NotificationManager.IMPORTANCE_MAX
    } else {
        @Suppress("DEPRECATION")
        android.app.Notification.PRIORITY_MAX
    }
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
            Log.w("Notification", "Permission not granted for POST_NOTIFICATIONS")
            return@with
        }
        notify(1001, builder.build())
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
fun BluetoothButton(
    bluetoothViewModel: BluetoothViewModel,
    socket:(BluetoothSocket)->Unit
){
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val bluetoothPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        arrayOf(
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH_SCAN
        )
    } else {
        arrayOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { }
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.all { it.value }
        if (allGranted) {
            val isGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) == PackageManager.PERMISSION_GRANTED
                &&
                ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
                &&
                ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_SCAN
                ) == PackageManager.PERMISSION_GRANTED
            }
            else {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH
                ) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.BLUETOOTH_ADMIN
                        ) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
            }
            if(!isGranted){
                permissionLauncher.launch(bluetoothPermissions)
            }
        }
    }
    Button(
        onClick = {
            val notGranted = bluetoothPermissions.filter {
                ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
            }
            if (notGranted.isNotEmpty()) {
                launcher.launch(notGranted.toTypedArray())
            } else {
                showDialog = true
                @Suppress("DEPRECATION")
                bluetoothViewModel.updateBondedDevices(BluetoothAdapter.getDefaultAdapter())
            }
        }
    ) {
        Text("Connect To Device")
    }
    if(showDialog){
        BluetoothDialog(
            bluetoothViewModel = bluetoothViewModel,
            onDismissReq = {
                showDialog = false
                @Suppress("DEPRECATION")
                bluetoothViewModel.stopScan(BluetoothAdapter.getDefaultAdapter())
            },
            onDeviceSelected = {
                socket(it)
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun RequestBluetoothPermission(){
    val bluetoothPermissions = arrayOf(
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.BLUETOOTH_SCAN,
    )
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ){}
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.all { it.value }
        if (allGranted) {
            val isGranted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
            &&
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            &&
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_SCAN
            ) == PackageManager.PERMISSION_GRANTED
            if(!isGranted){
                permissionLauncher.launch(Manifest.permission.BLUETOOTH_CONNECT)
            }
        }
    }

// Call this when you need to request permission
    LaunchedEffect(Unit) {
        val notGranted = bluetoothPermissions.filter {
            ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
        }
        if (notGranted.isNotEmpty()) {
            launcher.launch(notGranted.toTypedArray())
        }
    }
}

@Composable
fun ShowDeleteDialog(motorDataClass: MotorDataClass, motorViewModel: MotorViewModel, navHostController: NavHostController){
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
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
                motorViewModel.deleteMotor(
                    motorDataClass.id,
                    onSuccess = {
                        Toast.makeText(context, "Delete Successful!", Toast.LENGTH_SHORT).show()
                        navHostController.navigateUp()
                    },
                    onError = {e->
                        Toast.makeText(context, e, Toast.LENGTH_SHORT).show()
                    }
                )
                       },
            onDismiss = { showDialog = false }
        )
    }
}

@SuppressLint("AutoboxingStateCreation")
@Composable
fun LocationTracker(
    latitude: Double,
    longitude: Double,
    distanceToActivate: String, // assumed to be in meters
    onMoved: () -> Unit
) {
    var lastLat by remember { mutableDoubleStateOf(latitude) }
    var lastLon by remember { mutableDoubleStateOf(longitude) }

    val threshold = distanceToActivate.toFloatOrNull() ?: 0f

    // Every time lat/lon changes, check the distance
    LaunchedEffect(latitude, longitude) {
        val results = FloatArray(1)
        Location.distanceBetween(
            lastLat, lastLon,
            latitude, longitude,
            results
        )
        val distanceMoved = results[0] // in meters

        if (distanceMoved > threshold) {
            onMoved()
            lastLat = latitude
            lastLon = longitude
        }
    }
}

@RequiresApi(Build.VERSION_CODES.N)
@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun DetailMotorScreenPreview() {
    val motorViewModel = MotorViewModel()
    motorViewModel.selectedData
    MotocurityTheme {
        MotorDetailScreen(navHostController = rememberNavController(), motorViewModel)
    }
}