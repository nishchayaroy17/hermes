package com.example.hermes

import android.Manifest
import android.content.pm.PackageManager
import android.os.Looper
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*

class GetLocation(private val activity: ComponentActivity) {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var sendData: SendData

    // Callback to pass location data to MainActivity
    var onLocationUpdate: ((LocationData) -> Unit)? = null

    init {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
        sendData = SendData(activity)

        // Create location request
        locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 3000L
        )
            .setMinUpdateIntervalMillis(3000L)
            .build()

        // Setup location callback
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location = result.lastLocation
                val data = if (location != null) {
                    LocationData(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        message = "Hello from Hermes"
                    )
                } else {
                    // Fallback if location is null
                    LocationData(17.0, 17.0, "Hello from Hermes (default)")
                }

                // Update UI
                onLocationUpdate?.invoke(data)

                // Send to server
                sendData.sendLocationToServer(data)
            }
        }
    }

    fun requestLocationPermissionAndStartUpdates() {
        val permissionLauncher = activity.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                startLocationUpdates()
            } else {
                Toast.makeText(activity, "Location permission denied", Toast.LENGTH_SHORT).show()
                // Send fallback location
                val fallback = LocationData(17.0, 17.0, "Hello from Hermes (default)")
                onLocationUpdate?.invoke(fallback)
                sendData.sendLocationToServer(fallback)
            }
        }

        when {
            ActivityCompat.checkSelfPermission(
                activity, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                startLocationUpdates()
            }
            else -> {
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                activity, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
        Toast.makeText(activity, "Started location updates", Toast.LENGTH_SHORT).show()
    }

    fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}
