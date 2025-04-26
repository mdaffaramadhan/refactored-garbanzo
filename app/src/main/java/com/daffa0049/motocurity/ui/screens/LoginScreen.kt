package com.daffa0049.motocurity.ui.screens

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.daffa0049.motocurity.db.AuthViewModel
import com.daffa0049.motocurity.ui.theme.MotocurityTheme

@Composable
fun LoginScreen(navHostController: NavHostController){
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailIsError by remember { mutableStateOf(false) }
    var passwordIsError by remember { mutableStateOf(false) }
    var loginIsError by remember { mutableStateOf(false) }
    var loginErrMessage by remember { mutableStateOf("") }

    val context = LocalContext.current
    Scaffold {
            innerPadding->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ){
            Card(
                modifier = Modifier
                    .wrapContentSize()
                    .align(Alignment.Center)
                    .padding(8.dp),
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Motocurity",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "Login",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        modifier = Modifier.padding(4.dp),
                        text = "Use your username and password to login",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth(),
                        value = email,
                        onValueChange = {email = it},
                        label = { Text(text = "Email") },
                        isError = emailIsError,
                        supportingText = {
                            ErrorMessage(emailIsError, "Fill the email")
                        },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        )
                    )
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth(),
                        value = password,
                        onValueChange = {password = it},
                        label = { Text(text = "Password") },
                        isError = passwordIsError,
                        supportingText = {
                            ErrorMessage(passwordIsError, "Fill the password")
                        },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        )
                    )
                    ErrorMessage(
                        loginIsError,
                        loginErrMessage
                    )
                    Button(
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .width(128.dp)
                        ,
                        onClick = {
                            emailIsError = email.isBlank()
                            passwordIsError = password.isBlank()
                            if(!emailIsError && !passwordIsError){
                                AuthViewModel().loginUser(
                                    email,
                                    password,
                                    onSuccess = {
                                        Toast.makeText(context, "Login successfull!", Toast.LENGTH_SHORT).show()
                                        navHostController.navigate(
                                            "homeScreen",

                                        )
                                    },
                                    onError = {err->
                                        loginIsError = true
                                        loginErrMessage = err
                                    }
                                )
                            }
                                  },

                        ) {
                        Text(text = "Login")
                    }
                    Button(
                        modifier = Modifier.padding(4.dp),
                        onClick = {navHostController.navigate("registerScreen")},
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary)
                    ) {
                        Text(
                            buildAnnotatedString {
                                append("Don't have an\n")
                                append("Account? Register")
                            })
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun LoginScreenPreview() {
    MotocurityTheme {
        LoginScreen(navHostController = rememberNavController())
    }
}