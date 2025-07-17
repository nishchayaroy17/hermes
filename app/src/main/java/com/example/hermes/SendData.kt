package com.example.hermes

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException

class SendData(private val context: Context) {

    private val client = OkHttpClient()

    // Function to send location data as JSON
    fun sendLocationToServer(data: LocationData) {

        // Update with actual URL
        val serverUrl = "http://10.73.169.85:9590/location"

        // Convert LocationData to JSON
        val jsonObject = JSONObject().apply {
            put("latitude", data.latitude)
            put("longitude", data.longitude)
            put("message", data.message)
        }

        val jsonMediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val requestBody = RequestBody.create(jsonMediaType, jsonObject.toString())

        val request = Request.Builder()
            .url(serverUrl)
            .post(requestBody)
            .header("Content-Type", "application/json")
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
                            Toast.LENGTH_LONG
                        ).show()
                        Log.e("SendData", "Server error: ${response.code}")
                    }
                }
            }
        })
    }
}