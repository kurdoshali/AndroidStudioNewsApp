package com.example.project1newsapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import com.example.project1newsapp.ui.theme.Project1NewsAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Project1NewsAppTheme {
                LoginApp(
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun LoginApp(modifier: Modifier = Modifier) {
//    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loginBtnState by remember {mutableStateOf(false)}
    val context= LocalContext.current

    val prefs = remember { context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE) }

    var username by remember {
        mutableStateOf(prefs.getString("username", "") ?: "Enter Username")
    }


    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        Text(text = "WELCOME")
        Spacer(Modifier.height(70.dp))

        //username
//        OutlinedTextField(
//            value = username,
//            onValueChange = { username = it },
//            label = { Text("Enter your name") },
//
//            singleLine = true,
////                modifier = Modifier.fillMaxWidth()
//        )
        TextField(
            value = username,
            onValueChange = {newText ->username=newText},

            placeholder = {Text( "enter username")},
            singleLine = true

        )
        Spacer(Modifier.height(35.dp))
        //password
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Enter your password") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
//                modifier = Modifier.fillMaxWidth()
        )
        //button
        Spacer((Modifier.height(20.dp)))
        Button(onClick = {
//            prefs.edit{putString("username", username)}
            val intent = Intent(context, HomeScreen::class.java)
            context.startActivity(intent)
        }, enabled = checkUsernamePassword(username, password) )
        {Text("Login") }

    }

}

fun checkUsernamePassword(username: String, password: String): Boolean {

    return username.length >=5 && password.length >=8 && !username.contains(' ') && !password.contains(' ')
}
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Project1NewsAppTheme {
        LoginApp(
            modifier = Modifier.fillMaxSize()
        )
    }
}