package com.example.hermes

import android.content.Context
import android.widget.Toast
import androidx.activity.ComponentActivity
import okhttp3.*
import java.io.IOException

class SendData(private val context: Context){

    private val client = OkHttpClient()

    // Function to send location data to server
    fun sendLocationToServer(data: LocationData) {
        // Replace this URL with your actual server IP
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
                (context as ComponentActivity).runOnUiThread {
                    Toast.makeText(
                        context,
                        "Failed to send data: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                (context as ComponentActivity).runOnUiThread {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            context,
                            "Location sent successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            context,
                            "Server error: ${response.code}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        })
    }
}
