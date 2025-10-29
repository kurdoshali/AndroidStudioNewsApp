package com.example.project1newsapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import coil.compose.AsyncImage
import com.example.project1newsapp.ui.theme.Project1NewsAppTheme
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class ComposeMap : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Project1NewsAppTheme {
                ComposeMapApp()
            }
        }
    }
}
@Composable
fun ComposeMapApp() {
    val staffordVA = LatLng(38.4221, -77.4083)
//    val cameraPositionState = rememberCameraPositionState {
//        position = CameraPosition.fromLatLngZoom(staffordVA, 10f)
//    }
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE) }



    val coroutineScope = rememberCoroutineScope()
//    var markerPosition by remember { mutableStateOf<LatLng?>(null) }
    var addressInfo by remember { mutableStateOf(prefs.getString("address","Long Click on Map") ?: "Long Click on Map") }
    val savedLat = prefs.getFloat("lat", Float.MIN_VALUE)
    val savedLng = prefs.getFloat("lng", Float.MIN_VALUE)
//    var buttonColor by remember {mutableStateOf(Color.Red)}

    val initialMarker =
        if (savedLat != Float.MIN_VALUE && savedLng != Float.MIN_VALUE)
            LatLng(savedLat.toDouble(), savedLng.toDouble())
        else null

    var markerPosition by remember { mutableStateOf<LatLng?>(initialMarker) }
    var buttonColor by remember { mutableStateOf(if (initialMarker != null) Color.Blue else Color.Red) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialMarker ?: staffordVA, 5f)
    }

    LaunchedEffect(markerPosition) {

        markerPosition?.let { latLng ->
            addressInfo = "Resolving address..."


            addressInfo = withContext(Dispatchers.IO) {
                getAddressGeocodeCurrent(context, latLng)

            }
            prefs.edit().apply {
                putString("address", addressInfo)
                putFloat("lat", latLng.latitude.toFloat())
                putFloat("lng", latLng.longitude.toFloat())
                apply()
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapLongClick = { latLng ->
                // markerPosition=latLng
                markerPosition = latLng
                addressInfo = "resolving Address....."
                println("Long clicked at : ${latLng.latitude}, ${latLng.longitude}")

            }
        ) {
            markerPosition?.let { position ->
                Marker(
                    state = MarkerState(position = position),
                    title = addressInfo,
                    snippet = "Lat: ${position.latitude}, Lng: ${position.longitude}"
                )
                buttonColor=Color.Blue
            }


        }
//        Button(
//            onClick = { /* Handle click */
//                println("$markerPosition.longitude")
//                val yelpIntent= Intent(context, HomeScreen::class.java)
//                yelpIntent.putExtra("lat", markerPosition!!.latitude)
//                yelpIntent.putExtra("long", markerPosition!!.longitude)
//                yelpIntent.putExtra("addressInfo", addressInfo)
//                context.startActivity(yelpIntent)
//            },
//            colors = ButtonDefaults.buttonColors(
//                containerColor = buttonColor
//            ),
//            modifier = Modifier
//                .align(Alignment.BottomCenter)
//                .padding(16.dp)
//
//        ) {
//            Text(addressInfo)
//        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.White.copy(alpha = 0.9f))
                .padding(8.dp)
        ) {

            Button(
                onClick = {
                    println("$markerPosition.longitude")
                    val yelpIntent= Intent(context, HomeScreen::class.java)
                    yelpIntent.putExtra("lat", markerPosition!!.latitude)
                    yelpIntent.putExtra("long", markerPosition!!.longitude)
                    yelpIntent.putExtra("addressInfo", addressInfo)
                    context.startActivity(yelpIntent)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonColor
                ),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp)

            ) {
                Text(addressInfo)
            }
            val context = LocalContext.current
            val apiKey = context.getString(R.string.newsKey)
            val localManager = remember { LocalManager() }

            //make sure both the getValue and SetValue imports are added for by/remember
            var myResultsList by remember { mutableStateOf<List<Results>>(emptyList()) }

            LaunchedEffect(addressInfo, ) {
                val result = withContext(Dispatchers.IO) {
                    localManager.retrieveLocalNews( addressInfo, apiKey)
                }
                myResultsList = result
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                items(myResultsList) {currentResult->
                    ResultsCard(
                        result=currentResult,
                        modifier=Modifier.padding(1.dp),

                    )
                }
            }
        }


    }

}

@Composable
fun ResultsCard ( modifier:Modifier=Modifier, result: Results){
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

@SuppressLint("NewApi")
suspend fun getAddressGeocodeCurrent(context: android.content.Context, latLng: LatLng): String =
    suspendCoroutine { continuation ->
        val geocoder = Geocoder(context, Locale.getDefault())
        geocoder.getFromLocation(
            latLng.latitude,
            latLng.longitude,
            1,
            @RequiresApi(Build.VERSION_CODES.TIRAMISU)
            object : Geocoder.GeocodeListener {
                override fun onGeocode(addressList: MutableList<Address>) {
                    val result = if (addressList.isNotEmpty()) {
                        val address = addressList[0]
                        val city = address.locality ?: "Unknown City"
                        val state = address.adminArea ?: "Unknown State"
                        "$city, $state"
                    } else {
                        "No address found."
                    }
                    continuation.resume(result)
                }

                override fun onError(errorMessage: String?) {
                    continuation.resume("Geocoding failed: ${errorMessage ?: "Unknown error"}")
                }
            }
        )
    }
@Composable
fun Greeting6(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview5() {
    Project1NewsAppTheme {
        Greeting6("Android")
    }
}