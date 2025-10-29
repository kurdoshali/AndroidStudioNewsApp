package com.example.project1newsapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.project1newsapp.ui.theme.Project1NewsAppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import coil.compose.AsyncImage

class ResultsScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Project1NewsAppTheme {
                val sourceName = intent.getStringExtra("sourceName") ?: ""
                val sourceId = intent.getStringExtra("sourceId") ?: ""
                val searchTerm = intent.getStringExtra("searchTerm") ?: ""
                Results(Modifier.fillMaxSize(), sourceName, sourceId, searchTerm)
            }
        }
    }
}

@Composable
fun Results(modifier: Modifier,sourceName: String, sourceId: String, searchTerm: String)
{
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {

        Spacer(Modifier.height(70.dp))

        Text(text = "$sourceName For $searchTerm")

        Spacer(Modifier.height(60.dp))

        DisplayResults(Modifier,sourceId, searchTerm)

    }
}

@Composable
fun  DisplayResults (modifier: Modifier, sourceId: String, searchTerm: String) {
    val context = LocalContext.current
    val apiKey = context.getString(R.string.newsKey)
    val resultsManager = remember { ResultsManager() }

    //make sure both the getValue and SetValue imports are added for by/remember
    var myResultsList by remember { mutableStateOf<List<Results>>(emptyList()) }

    LaunchedEffect(sourceId, searchTerm) {
        val result = withContext(Dispatchers.IO) {
            resultsManager.retrieveResults( sourceId, searchTerm, apiKey)
        }
        myResultsList = result
    }

    LazyColumn (modifier=Modifier)
    {
        items(myResultsList) {currentResult->
            ResultsCard(
                result=currentResult,
                modifier=Modifier.padding(1.dp),
                searchTerm =searchTerm
            )
        }
    }

}

@Composable
fun ResultsCard (result: Results, modifier:Modifier=Modifier, searchTerm: String){
    val context=LocalContext.current
    Card(modifier=Modifier.fillMaxWidth()
        .padding(1.dp)
        .clickable(onClick={

            val resultsIntent = Intent(Intent.ACTION_VIEW).apply {
                data= Uri.parse(result.url)
            }
            context.startActivity(resultsIntent)

        })
    )
    {
        Row(modifier=Modifier.padding(2.dp)) {
            AsyncImage(
                model = result.imageUrl,
//                painter = painterResource(R.drawable.ic_launcher_background),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .padding(1.dp)
            )
            Spacer(modifier=Modifier.width(5.dp))
            Column() {
                Text(result.title)
                Text(result.name)
                Spacer(Modifier.height(3.dp))
                Text(result.description)

            }

        }
    }
}
//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview3() {
//    Project1NewsAppTheme {
//        Results(Modifier.fillMaxSize())
//    }
//}