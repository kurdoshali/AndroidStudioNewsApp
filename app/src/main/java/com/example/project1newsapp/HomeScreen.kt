package com.example.project1newsapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import com.example.project1newsapp.ui.theme.Project1NewsAppTheme

class HomeScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Project1NewsAppTheme {
                HomeScreen(
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun HomeScreen (modifier: Modifier = Modifier){
    val context= LocalContext.current

    val prefs = remember { context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE) }

    var search by remember {
        mutableStateOf(prefs.getString("search", "") ?: "Search")
    }


    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {

        Spacer(Modifier.height(70.dp))

        TextField(
            value = search,
            onValueChange = {newText -> search=newText},

            placeholder = {Text("Search Here")},
            singleLine=true
        )
        Spacer(Modifier.height(30.dp))

        OutlinedButton(onClick = {
            prefs.edit{putString("search", search)}

            val intent = Intent(context, SourcesScreen::class.java)
            intent.putExtra("searchTerm", search)
            context.startActivity(intent)
        }, enabled=checkEmpty(search)) {
            Text("Search")
        }
        Spacer(Modifier.height(30.dp))

        Box(
            modifier= Modifier
                .shadow(2.dp)
                .size(width =275.dp, height = 175.dp),
//                .padding(16.dp)
//                .border(
//                    BorderStroke(2.dp, Color.Gray)
//                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("News by Location")
                Spacer(Modifier.height((30.dp)))
                ElevatedButton(onClick = {
                    val intent3 = Intent(context, ComposeMap::class.java)
                    context.startActivity(intent3)
                }) {
                    Text("View Map")
                }
            }
        }

        Spacer(Modifier.height(30.dp))

        Box(
            modifier= Modifier
                .size(width =275.dp, height = 175.dp)
//                .padding(16.dp)
//                .border(
//                    BorderStroke(2.dp, Color.Gray)
//                ),
                .shadow(2.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Top Headlines")
                Spacer(Modifier.height((30.dp)))
                ElevatedButton(onClick = {
                    val intent2 = Intent(context, TopHeadlinesScreen::class.java)
                    context.startActivity(intent2)
                }) {
                    Text("View Headlines")
                }
            }
        }




    }

}

fun checkEmpty(check: String): Boolean {
    return check.isNotBlank()
}

@Composable
fun Greeting2(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    Project1NewsAppTheme {
        HomeScreen(
            modifier = Modifier.fillMaxSize()
        )
    }
}