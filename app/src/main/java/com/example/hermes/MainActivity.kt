package com.example.hermes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.hermes.ui.theme.HermesTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Compose state to hold location data
        val locationState = mutableStateOf(
            LocationData(0.0, 0.0, "Waiting for location...")
        )

        // Initialize GetLocation
        val getLocation = GetLocation(this)
        getLocation.onLocationUpdate = { data ->
            locationState.value = data // Update UI state
        }
        getLocation.requestLocationPermissionAndStartUpdates()

        // Compose UI
        setContent {
            HermesTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        LocationDisplay(data = locationState.value)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Stop location updates when activity is destroyed
        GetLocation(this).stopLocationUpdates()
    }
}

@Composable
fun LocationDisplay(data: LocationData) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "Latitude: ${data.latitude}", style = MaterialTheme.typography.titleLarge)
        Text(text = "Longitude: ${data.longitude}", style = MaterialTheme.typography.titleLarge)
        Text(text = "Message: ${data.message}", style = MaterialTheme.typography.bodyLarge)
    }
}

@Preview(showBackground = true)
@Composable
fun LocationDisplayPreview() {
    HermesTheme {
        LocationDisplay(
            LocationData(28.6139, 77.2090, "Hello from Hermes")
        )
    }
}
