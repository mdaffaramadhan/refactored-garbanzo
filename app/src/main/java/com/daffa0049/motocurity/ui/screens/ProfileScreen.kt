package com.daffa0049.motocurity.ui.screens

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.daffa0049.motocurity.R
import com.daffa0049.motocurity.db.AuthViewModel
import com.daffa0049.motocurity.ui.theme.MotocurityTheme
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navHostController: NavHostController){
    val context = LocalContext.current
    val user = FirebaseAuth.getInstance().currentUser

    LaunchedEffect(Unit) {
        if (user == null) {
            navHostController.navigate("loginScreen") {
                popUpTo("profileScreen") { inclusive = true } // Prevent back navigation
            }
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Profile"
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
        ProfileContent(modifier = Modifier.padding(innerPadding)){
            AuthViewModel().logOut()
            Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()
            navHostController.navigate("loginScreen")
        }
    }
}
@Composable
fun ProfileContent(modifier: Modifier = Modifier, onCLick: () -> Unit){
    val auth = FirebaseAuth.getInstance()
    var username by remember { mutableStateOf(auth.currentUser?.displayName ?: "") }
    var usernameIsEmpty by remember { mutableStateOf(false) }
    var isEdit by remember { mutableStateOf(false) }
    Column(
        modifier = modifier.padding(16.dp).fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(128.dp)
        ){
            Image(
                painter = painterResource(R.drawable.ic_launcher_background),
                contentDescription = "Profile picture of user"
            )
        }
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = username,
            onValueChange = {username = it},
            label = { Text(text = "Username") },
            enabled = isEdit,
            isError = usernameIsEmpty,
            supportingText = {
                ErrorMessage(
                    isError = usernameIsEmpty
                )
            }
        )
        if(isEdit){
            Button(
                modifier = Modifier.padding(8.dp),
                onClick = {
                    usernameIsEmpty = username.isBlank()
                    if(!usernameIsEmpty){
                        AuthViewModel().updateUsername(username)
                        isEdit = false
                    }
                },
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = "Confirm"
                )
            }
            Button(
                modifier = Modifier,
                onClick = {isEdit = false},
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary)
            ) {
                Text(
                    text = "Cancel"
                )
            }
        }
        else{
            Button(
                modifier = Modifier.padding(8.dp),
                onClick = {isEdit = true},
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = "Edit"
                )
            }
            Button(
                modifier = Modifier,
                onClick = {onCLick()},
                colors = ButtonDefaults.buttonColors(Color.Red)
            ) {
                Text(
                    text = "Log Out"
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun ProfileScreenPreview() {
    MotocurityTheme {
        ProfileScreen(navHostController = rememberNavController())
    }
}