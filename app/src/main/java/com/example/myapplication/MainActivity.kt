
package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("current.json")
    fun getWeather(
        @Query("q") location: String,
        @Query("key") apiKey: String
    ): Call<Any>
}
class MainActivity : AppCompatActivity() {

    private lateinit var weatherTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        weatherTextView = findViewById(R.id.weatherTextView)
        val fetchWeatherButton: Button = findViewById(R.id.fetchWeatherButton)


        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.weatherapi.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val weatherApiService = retrofit.create(WeatherApiService::class.java)

        // Set up a click listener for the fetchWeatherButton
        fetchWeatherButton.setOnClickListener {
            // Make the API request
            val location = "bangalore" // Location
            val apiKey = "6214394c4e1f402083e142314232409" //API key

            val call = weatherApiService.getWeather(location, apiKey)

            call.enqueue(object : Callback<Any> {
                override fun onResponse(call: Call<Any>, response: Response<Any>) {
                    if (response.isSuccessful) {
                        val weatherResponse = response.body()
                        displayWeather(weatherResponse.toString())
                    } else {
                        // Handle the error
                        weatherTextView.text = "Error: ${response.code()}"
                    }
                }

                override fun onFailure(call: Call<Any>, t: Throwable) {
                    // Handle network error
                    weatherTextView.text = "Network Error: ${t.message}"
                }
            })
        }
    }

    // Display weather information in the UI
    private fun displayWeather(weatherResponse: String?) {
        if (weatherResponse != null) {
            weatherTextView.text = weatherResponse
        } else {
            // Handle the case where the weatherResponse is null or empty
            weatherTextView.text = "No Weather Data Available"
        }
    }
}




