
package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class MainActivity : AppCompatActivity() {

    private lateinit var weatherTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        weatherTextView = findViewById(R.id.weatherTextView)
        val fetchWeatherButton: Button = findViewById(R.id.fetchWeatherButton)


        val retrofit = Retrofit.Builder()
            .baseUrl("http://api.weatherapi.com/v1/current.json?key=1bae9284527d4d7bbef133833232409&q=bangalore&aqi=no/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()

        // Create the WeatherApiService interface
        val weatherApiService = retrofit.create(WeatherApiService::class.java)

        // Set up a click listener for the fetchWeatherButton
        fetchWeatherButton.setOnClickListener {
            // Make the API request
            val location = "Bangalore"
            val apiKey = "1bae9284527d4d7bbef133833232409" // Replace with your API key

            val call = weatherApiService.getWeather(location, apiKey)

            call.enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful) {
                        val weatherResponse = response.body()
                        // Call displayWeather() to update the UI with weather information
                        displayWeather(weatherResponse)
                    } else {
                        // Handle the error
                        weatherTextView.text = "Error: ${response.code()}"
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
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



interface WeatherApiService {
    @GET("weather")
    fun getWeather(
        @Query("q") location: String,
        @Query("appid") apiKey: String
    ): Call<String>
}
