package com.shekharhandigol.aiarticlesummarizer.ui.login

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shekharhandigol.aiarticlesummarizer.R
import kotlinx.coroutines.launch

@Composable
fun MainLoginScreen(
    onLoginSuccess: () -> Unit,
    skipLogin: () -> Unit
) {

    if (GoogleSignInHelper.isUserLoggedIn()) {
        onLoginSuccess()
    }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val viewModel: LoginViewModel = hiltViewModel()
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {
            GoogleSignInHelper.doGoogleSignIn(
                context = context,
                scope = scope,
                activityResultLauncher = null,
                onSuccess = {
                    Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
                },
                onError = { exception, message ->

                }
            )
        }



    Box(modifier = Modifier.fillMaxSize()) {
        IconButton(
            onClick = skipLogin,
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Icon(
                Icons.Default.Close, contentDescription = "Skip Login",
                modifier = Modifier
                    .size(56.dp)
                    .padding(8.dp)
            )
        }
        LoginScreen(
            onLoginClick = viewModel::loginWithEmailPassword,
            onGoogleSignInClick = {
                GoogleSignInHelper.doGoogleSignIn(
                    context = context,
                    scope = scope,
                    activityResultLauncher = launcher,
                    onSuccess = {
                        Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
                        onLoginSuccess()
                    },
                    onError = { exception, message ->

                    }
                )
            }
        )
    }
}

@Composable
fun LoginScreen(
    onLoginClick: (String, String) -> Unit,
    onGoogleSignInClick: (context: Context) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val errorMessage by remember { mutableStateOf("") }
    val isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.google_cloud),
            contentDescription = "Sign in with Google",
            modifier = Modifier
                .size(200.dp)
                .padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
            },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            isError = email.isBlank(),
            singleLine = true,
            maxLines = 1,
            enabled = !isLoading
        )

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it

            },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            isError = password.isBlank()
        )

        if (errorMessage.isNotBlank()) {
            Text(
                text = errorMessage, color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .border(1.dp, MaterialTheme.colorScheme.error, RoundedCornerShape(4.dp))
                    .padding(8.dp)

            )
        }
        OutlinedButton(
            onClick = {
                onLoginClick(email, password)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            enabled = email.isNotBlank() && password.isNotBlank()
        ) {
            Text("Login")
        }

        val scope = rememberCoroutineScope()
        val context = LocalContext.current

        OutlinedButton(
            onClick = {
                scope.launch {
                    onGoogleSignInClick(context)
                }

            }, modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Image(
                painter = painterResource(id = R.drawable.google_fancy_image),
                contentDescription = "Sign in with Google",
                modifier = Modifier.size(24.dp)
            )
            Text("Sign in with Google", modifier = Modifier.padding(start = 8.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(
        onLoginClick = { _, _ -> },
        onGoogleSignInClick = {}
    )
}

@Preview(showBackground = true)
@Composable
fun MainLoginScreenPreview() {
    MainLoginScreen(
        onLoginSuccess = {},
        skipLogin = {}
    )
}
