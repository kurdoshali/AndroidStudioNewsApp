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
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.project1newsapp.ui.theme.Project1NewsAppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SourcesScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Project1NewsAppTheme {
                val searchTerm = intent.getStringExtra("searchTerm").toString()
                Sources(Modifier.fillMaxSize(), searchTerm)
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Sources(modifier: Modifier= Modifier, searchTerm: String) {
    val context = LocalContext.current
    val list = listOf("business", "entertainment", "general", "health", "science", "sports", "technology")
    var expanded = remember{ mutableStateOf(false) }
    var currentVal = remember { mutableStateOf(list[0]) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {

        Spacer(Modifier.height(70.dp))

        Text(text = "Search For: $searchTerm")

        Spacer(Modifier.height(30.dp))

        Text(text= "Category")

        Spacer(Modifier.height(15.dp))

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
        Spacer(Modifier.height(20.dp))

        Text(text = "Sources")

        Spacer(Modifier.height(15.dp))

        DisplaySources(Modifier, currentVal.value, searchTerm)

        Spacer(Modifier.height(20.dp))


        TextButton(
            onClick = {
                val noSourceIntent = Intent(context, ResultsScreen::class.java).apply {
                    putExtra("searchTerm", searchTerm)
                }
                context.startActivity(noSourceIntent)
            }

        ) {
            Text(text="Search Without Source")
        }


    }


}

@Composable
fun DisplaySources (modifier: Modifier, category: String, searchTerm: String)
{
    val context = LocalContext.current
    val apiKey = context.getString(R.string.newsKey)
    val sourceManager = remember { SourcesManager() }

    //make sure both the getValue and SetValue imports are added for by/remember
    var mySourcesList by remember { mutableStateOf<List<Sources>>(emptyList()) }

    LaunchedEffect(category) {
        val result = withContext(Dispatchers.IO) {
            sourceManager.retrieveSources( category, apiKey)
        }
        mySourcesList = result
    }
//    val mySourcesList = getFakeData()
    LazyColumn (modifier=Modifier
        .height(500.dp)
    ){
        items(mySourcesList) {currentSource->
            SourcesCard(
                source=currentSource,
                modifier=Modifier.padding(1.dp),
                searchTerm =searchTerm
            )
        }
    }

}


@Composable
fun SourcesCard(source: Sources, modifier:Modifier=Modifier, searchTerm: String){
    val context=LocalContext.current
    Card(modifier=Modifier.fillMaxWidth()
        .padding(1.dp)
        .clickable(onClick={
            val sourcesIntent = Intent(context, ResultsScreen::class.java).apply {
                putExtra("sourceName", source.name)
                putExtra("sourceId", source.id)
                putExtra("searchTerm", searchTerm)
            }
            context.startActivity(sourcesIntent)
//            val yelpCardIntent = Intent(Intent.ACTION_VIEW).apply {
//                data= Uri.parse(yelp.url)
//            }
//            context.startActivity(yelpCardIntent)

        })
    )
    {
        Row(modifier=Modifier.padding(2.dp)) {

            Spacer(modifier=Modifier.width(5.dp))
            Column() {
                Text(source.name)
                Text(source.description)
//                Text(source.category)
//                Text(yelp.url)

            }

        }
    }
}

fun getFakeData():List<Sources> {
    return listOf(
        Sources("1", "daffdsaf", "t is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout. The point of using Lorem Ipsum is that it has a more-or-less normal distribution of letters, as opposed to using 'Content here, content here', making it look like readable English.", "general"),
        Sources("2", "name2", "t is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout. The point of using Lorem Ipsum is that it has a more-or-less normal distribution of letters, as opposed to using 'Content here, content here', making it look like readable English.", "not general"),
        Sources("3", "name3", "t is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout. The point of using Lorem Ipsum is that it has a more-or-less normal distribution of letters, as opposed to using 'Content here, content here', making it look like readable English.", "not general"),
        Sources("4", "name4", "t is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout. The point of using Lorem Ipsum is that it has a more-or-less normal distribution of letters, as opposed to using 'Content here, content here', making it look like readable English.", "not general"),
        Sources("5", "name5", "t is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout. The point of using Lorem Ipsum is that it has a more-or-less normal distribution of letters, as opposed to using 'Content here, content here', making it look like readable English.", "not general"),
        Sources("6", "name6", "t is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout. The point of using Lorem Ipsum is that it has a more-or-less normal distribution of letters, as opposed to using 'Content here, content here', making it look like readable English.", "not general"),
        Sources("7", "name7", "t is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout. The point of using Lorem Ipsum is that it has a more-or-less normal distribution of letters, as opposed to using 'Content here, content here', making it look like readable English.", "not general"),
        Sources("8", "name8", "t is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout. The point of using Lorem Ipsum is that it has a more-or-less normal distribution of letters, as opposed to using 'Content here, content here', making it look like readable English.", "not general"),

    )

}

@Composable
fun Greeting3(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview3() {
//    Project1NewsAppTheme {
//        val searchTerm = intent.getStringExtra("searchTerm").toString()
//        Sources(Modifier.fillMaxSize(), searchTerm)
//    }
//}