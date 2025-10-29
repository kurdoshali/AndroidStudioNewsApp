package com.example.project1newsapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import coil.compose.AsyncImage
import com.example.project1newsapp.ui.theme.Project1NewsAppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.compareTo
import kotlin.dec
import kotlin.inc
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KMutableProperty0
import kotlin.text.compareTo

class TopHeadlinesScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Project1NewsAppTheme {
                TopHeadlines(Modifier.fillMaxSize())
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopHeadlines(modifier: Modifier)
{

    val list = listOf("business", "entertainment", "general", "health", "science", "sports", "technology")
    var expanded = remember{ mutableStateOf(false) }
    var currentVal = remember { mutableStateOf(list[0]) }


    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {

        Spacer(Modifier.height(50.dp))

        Text(text = "View Top Headlines")

        Spacer(Modifier.height(40.dp))

        Box(
            modifier = Modifier.padding(16.dp)
        ) {


            ExposedDropdownMenuBox(
                expanded = expanded.value,
                onExpandedChange = { expanded.value = !expanded.value }

            ) {
                TextField(
                    value = currentVal.value,
//                label = { Text("Categories")},
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value) },
                    readOnly = true,
                    onValueChange = { },
                    singleLine = true,
                    modifier = Modifier
                        .menuAnchor(
                            type = MenuAnchorType.PrimaryNotEditable,
                            enabled = true
                        )

                )
                ExposedDropdownMenu(
                    expanded = expanded.value,
                    onDismissRequest = { expanded.value = false }
                ) {
                    list.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(text = option) },
                            onClick = {
                                currentVal.value = option
                                expanded.value = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(40.dp))
        DisplayHeadlines(Modifier, currentVal.value)


    }
}

@Composable
fun DisplayHeadlines(modifier: Modifier, category: String)
{
    val context = LocalContext.current
    val apiKey = context.getString(R.string.newsKey)
    val headlinesManager = remember { HeadlinesManager() }
    var currPage by remember { mutableStateOf(1) }
    var maxPage by remember { mutableStateOf(1) }
    //make sure both the getValue and SetValue imports are added for by/remember
    var myHeadlinesList by remember { mutableStateOf<List<Results>>(emptyList())}

    LaunchedEffect(category, currPage) {
        val result = withContext(Dispatchers.IO) {
            headlinesManager.retrieveHeadlines( category, currPage, apiKey)
        }
        myHeadlinesList = result.results
        maxPage = result.maxPage
    }

    LazyColumn (modifier=Modifier
        .height(500.dp)
    ){
        items(myHeadlinesList) {currentHeadline->
            HeadlinesCard(
                result=currentHeadline,
                modifier=Modifier.padding(1.dp),
            )
        }
    }

    Spacer(Modifier.height(20.dp))

    Row(modifier = Modifier.padding(5.dp)) {
        //button
        Button(onClick = {currPage--}, enabled = currPage> 1) { Text("Previous") }
        //text
        Text("Page $currPage of $maxPage")
        //button
        Button(onClick = {currPage++}, enabled = currPage< maxPage) { Text("Next") }
    }

}

@Composable
fun HeadlinesCard (result: Results, modifier:Modifier=Modifier)
{
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
@Composable
fun Greeting4(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview3() {
    Project1NewsAppTheme {
        Greeting4("Android")
    }
}