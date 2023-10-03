package com.example.myapplication


import android.content.Context
import android.Manifest
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat.isLocationEnabled
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
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
interface WeatherApiService {
    @GET("current.json")
    fun getWeather(
        @Query("q") location: String,
        @Query("key") apiKey: String
    ): Call<WeatherResponse>
}

data class Current(
    val temp_c: Double
)
class MainActivity : AppCompatActivity() {

    private lateinit var locationTextView: TextView
    private lateinit var temperatureTextView: TextView
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        locationTextView = findViewById(R.id.locationTextView)
        temperatureTextView = findViewById(R.id.temperatureTextView)
        val fetchWeatherButton: Button = findViewById(R.id.fetchWeatherButton)

        // Initialize the FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Check if location services are enabled
        //val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // Handle the case where GPS provider is disabled
            locationTextView.text = "Location services are disabled."
        }

        fetchWeatherButton.setOnClickListener {
            // Request location permissions if not granted
            if (hasLocationPermissions()) {
                // Request the user's location
                requestLocation()
            } else {
                // Handle the case where location permissions are not granted
                locationTextView.text = "Location permissions are not granted."
            }
        }
    }

    // Function to check if location permissions are granted
    private fun hasLocationPermissions(): Boolean {
        val fineLocationPermission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarseLocationPermission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        return fineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                coarseLocationPermission == PackageManager.PERMISSION_GRANTED
    }

    // Function to request the user's location
    private fun requestLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    val apiKey = "6214394c4e1f402083e142314232409" // Replace with your API key

                    val retrofit = Retrofit.Builder()
                        .baseUrl("https://api.weatherapi.com/v1/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()

                    val weatherApiService = retrofit.create(WeatherApiService::class.java)

                    val call = weatherApiService.getWeather("$latitude,$longitude", apiKey)

                    call.enqueue(object : Callback<WeatherResponse> {
                        override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                            if (response.isSuccessful) {
                                val weatherResponse = response.body()
                                val temperature = weatherResponse?.current?.temp_c
                                val Location = "Your Location" // Replace with location data
                                locationTextView.text = "Location: $Location"
                                temperatureTextView.text = "Temperature: $temperature°C"
                            } else {
                                locationTextView.text = "Error: ${response.code()}"
                            }
                        }

                        override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                            locationTextView.text = "Network Error: ${t.message}"
                        }
                    })
                }
            }
    }
}