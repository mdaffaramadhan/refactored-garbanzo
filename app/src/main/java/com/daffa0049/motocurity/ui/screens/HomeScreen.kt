package com.daffa0049.motocurity.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.daffa0049.motocurity.R
import com.daffa0049.motocurity.dataClass.MotorDataClass
import com.daffa0049.motocurity.ui.theme.MotocurityTheme
import com.daffa0049.motocurity.viewModel.MotorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navHostController: NavHostController) {
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
        HomeScreenContent(modifier = Modifier.padding(innerPadding))
    }
}

@Composable
fun HomeScreenContent(modifier: Modifier = Modifier){
    val viewModel: MotorViewModel = viewModel()
    val data = viewModel.dataDummy
    Column {
        if(data.isEmpty()){
            Column(
                modifier = modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Belum ada motor yang ditambahkan")
            }
        }
        else{
            LazyColumn(
                modifier = modifier.fillMaxSize().padding(16.dp),
                contentPadding = PaddingValues(bottom = 84.dp)
            ) {
                items(data){
                    ListMotor(motorDataClass = it)
                }
            }
        }
    }
}


@Composable
fun ListMotor(motorDataClass: MotorDataClass){
    Card(
        modifier = Modifier.padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier.size(76.dp)
            ){
                Image(
                    painter = painterResource(R.drawable.ic_launcher_background),
                    contentDescription = "Tes"
                )
            }
            Column(
                modifier = Modifier.width(200.dp).padding(8.dp),
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
                    text = if(motorDataClass.isConnected) "Connected" else "Not Connected",
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 4.sp
                )
                Text(
                    text = motorDataClass.battery.toString()+"%",
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 4.sp
                )
            }
            RadioButton(
                selected = motorDataClass.isOn,
                onClick = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun HomeScreenPreview() {
    MotocurityTheme {
        HomeScreen(navHostController = rememberNavController())
    }
}
