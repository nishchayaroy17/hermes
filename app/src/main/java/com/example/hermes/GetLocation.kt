package com.example.hermes

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import okhttp3.*
import java.io.IOException
import java.util.concurrent.TimeUnit

class GetLocation : ComponentActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private val client = OkHttpClient() // HTTP client

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize fused location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Create location request (every 3 seconds)
        locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 3000L
        )
            .setMinUpdateIntervalMillis(3000L)
            .build()

        // Setup location callback
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location = result.lastLocation
                if (location != null) {
                    val data = LocationData(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        message = "hello from hermes"
                    )
                    sendLocationToServer(data)
                }
            }
        }

        // Request location permission
        requestLocationPermission()
    }

    private fun requestLocationPermission() {
        val permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                startLocationUpdates()
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
                // Use default coordinates (17, 17)
                val fallbackData = LocationData(
                    latitude = 17.0,
                    longitude = 17.0,
                    message = "hello from hermes"
                )
                sendLocationToServer(fallbackData)
            }
        }

        when {
            ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Permission already granted
                startLocationUpdates()
            }
            else -> {
                // Request permission
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return // Permission not granted
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
        Toast.makeText(this, "Started location updates", Toast.LENGTH_SHORT).show()
    }

    private fun sendLocationToServer(data: LocationData) {
        // Replace with actual IP later
        val serverUrl = "http://192.168.1.100:5000/location"

        val requestBody = FormBody.Builder()
            .add("latitude", data.latitude.toString())
            .add("longitude", data.longitude.toString())
            .add("message", data.message)
            .build()

        val request = Request.Builder()
            .url(serverUrl)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(
                        this@GetLocation,
                        "Failed to send data: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@GetLocation,
                            "Location sent successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this@GetLocation,
                            "Server error: ${response.code}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        // Stop location updates to prevent memory leaks
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}
