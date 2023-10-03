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
import retrofit2.http.GET
import retrofit2.http.Query

data class WeatherResponse(
    val current: Current
)

data class Current(
    val temp_c: Double
)

interface WeatherApiService {
    @GET("current.json")
    fun getWeather(
        @Query("q") location: String,
        @Query("key") apiKey: String
    ): Call<WeatherResponse>
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
            val apiKey = "6214394c4e1f402083e142314232409" // API key

            val call = weatherApiService.getWeather(location, apiKey)

            call.enqueue(object : Callback<WeatherResponse> {
                override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                    if (response.isSuccessful) {
                        val weatherResponse = response.body()

                        // Extract the temperature and display it
                        val temperature = weatherResponse?.current?.temp_c
                        temperature?.let {
                            displayWeather("Temperature: $it°C")
                        }
                    } else {
                        // Handle the error
                        weatherTextView.text = "Error: ${response.code()}"
                    }
                }

                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
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