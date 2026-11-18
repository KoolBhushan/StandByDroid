package com.droidstandby.app.data

import com.droidstandby.app.model.WeatherInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

object WeatherRepository {
    private var cachedCity: String = ""
    private var cachedInfo: WeatherInfo? = null
    private var lastFetchTime: Long = 0L
    private const val CACHE_DURATION_MS = 15 * 60 * 1000L // 15 minutes

    suspend fun getWeather(cityName: String): WeatherInfo = withContext(Dispatchers.IO) {
        val trimmed = cityName.trim()
        val now = System.currentTimeMillis()
        if (trimmed.equals(cachedCity, ignoreCase = true) && cachedInfo != null && (now - lastFetchTime) < CACHE_DURATION_MS) {
            return@withContext cachedInfo!!
        }

        try {
            var lat = 18.5211
            var lon = 73.8502
            var resolvedCity = trimmed

            val isAutoLocation = trimmed.isEmpty() ||
                    trimmed.equals("Current Location", ignoreCase = true) ||
                    trimmed.equals("Auto", ignoreCase = true)

            if (isAutoLocation) {
                // Auto-detect location via IP geolocation service
                try {
                    val ipUrl = URL("http://ip-api.com/json")
                    val ipConn = (ipUrl.openConnection() as HttpURLConnection).apply {
                        connectTimeout = 6000
                        readTimeout = 6000
                        requestMethod = "GET"
                    }
                    val ipResponse = ipConn.inputStream.bufferedReader().use(BufferedReader::readText)
                    ipConn.disconnect()
                    val ipJson = JSONObject(ipResponse)
                    if (ipJson.optString("status") == "success") {
                        lat = ipJson.getDouble("lat")
                        lon = ipJson.getDouble("lon")
                        resolvedCity = ipJson.optString("city", "Current Location")
                    }
                } catch (_: Exception) {
                    lat = 18.5211
                    lon = 73.8502
                    resolvedCity = "Pune"
                }
            } else {
                // Manual city search via Open-Meteo geocoding
                val encodedCity = URLEncoder.encode(trimmed, "UTF-8")
                val geoUrl = URL("https://geocoding-api.open-meteo.com/v1/search?name=$encodedCity&count=1&language=en&format=json")
                val geoConn = (geoUrl.openConnection() as HttpURLConnection).apply {
                    connectTimeout = 8000
                    readTimeout = 8000
                    requestMethod = "GET"
                }
                val geoResponse = geoConn.inputStream.bufferedReader().use(BufferedReader::readText)
                geoConn.disconnect()

                val geoJson = JSONObject(geoResponse)
                val results = geoJson.optJSONArray("results")
                if (results == null || results.length() == 0) {
                    return@withContext WeatherInfo(
                        city = trimmed,
                        condition = "City Not Found",
                        isError = true
                    )
                }

                val firstResult = results.getJSONObject(0)
                lat = firstResult.getDouble("latitude")
                lon = firstResult.getDouble("longitude")
                resolvedCity = firstResult.optString("name", trimmed)
            }

            // Fetch live forecast from Open-Meteo
            val weatherUrl = URL(
                "https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$lon" +
                        "&current=temperature_2m,relative_humidity_2m,weather_code" +
                        "&daily=temperature_2m_max,temperature_2m_min&timezone=auto"
            )
            val weatherConn = (weatherUrl.openConnection() as HttpURLConnection).apply {
                connectTimeout = 8000
                readTimeout = 8000
                requestMethod = "GET"
            }

            val weatherResponse = weatherConn.inputStream.bufferedReader().use(BufferedReader::readText)
            weatherConn.disconnect()

            val weatherJson = JSONObject(weatherResponse)
            val current = weatherJson.getJSONObject("current")
            val temp = current.getDouble("temperature_2m")
            val humidity = current.optInt("relative_humidity_2m", 0)
            val code = current.getInt("weather_code")

            val daily = weatherJson.optJSONObject("daily")
            val maxTemp = daily?.optJSONArray("temperature_2m_max")?.optDouble(0)
            val minTemp = daily?.optJSONArray("temperature_2m_min")?.optDouble(0)

            val condition = getWeatherCondition(code)

            // Fetch live Air Quality (AQI) from Open-Meteo Air Quality API
            var aqiVal: Int? = null
            var aqiStatusStr = ""
            try {
                val aqiUrl = URL("https://air-quality-api.open-meteo.com/v1/air-quality?latitude=$lat&longitude=$lon&current=us_aqi")
                val aqiConn = (aqiUrl.openConnection() as HttpURLConnection).apply {
                    connectTimeout = 5000
                    readTimeout = 5000
                    requestMethod = "GET"
                }
                val aqiResponse = aqiConn.inputStream.bufferedReader().use(BufferedReader::readText)
                aqiConn.disconnect()

                val aqiJson = JSONObject(aqiResponse)
                val aqiCurrent = aqiJson.optJSONObject("current")
                if (aqiCurrent != null && aqiCurrent.has("us_aqi")) {
                    val aqiNum = aqiCurrent.getInt("us_aqi")
                    aqiVal = aqiNum
                    aqiStatusStr = when {
                        aqiNum <= 50 -> "Good"
                        aqiNum <= 100 -> "Moderate"
                        aqiNum <= 150 -> "Sensitive"
                        aqiNum <= 200 -> "Unhealthy"
                        aqiNum <= 300 -> "Very Unhealthy"
                        else -> "Hazardous"
                    }
                }
            } catch (_: Exception) {}

            val info = WeatherInfo(
                city = resolvedCity,
                temperature = "${Math.round(temp)}°",
                condition = condition,
                weatherCode = code,
                highTemp = if (maxTemp != null) "H: ${Math.round(maxTemp)}°" else "",
                lowTemp = if (minTemp != null) "L: ${Math.round(minTemp)}°" else "",
                humidity = "$humidity%",
                aqi = aqiVal,
                aqiStatus = aqiStatusStr,
                isError = false
            )

            cachedCity = trimmed
            cachedInfo = info
            lastFetchTime = now
            return@withContext info
        } catch (e: Exception) {
            return@withContext cachedInfo ?: WeatherInfo(
                city = if (trimmed.isEmpty() || trimmed.equals("Current Location", true)) "Pune" else trimmed,
                condition = "Weather Offline",
                isError = true
            )
        }
    }

    private fun getWeatherCondition(code: Int): String = when (code) {
        0 -> "Clear Sky"
        1 -> "Mainly Clear"
        2 -> "Partly Cloudy"
        3 -> "Overcast"
        45, 48 -> "Foggy"
        51, 53, 55 -> "Drizzle"
        61, 63, 65 -> "Rain"
        71, 73, 75 -> "Snow"
        77 -> "Snow Grains"
        80, 81, 82 -> "Rain Showers"
        85, 86 -> "Snow Showers"
        95 -> "Thunderstorm"
        96, 99 -> "Severe Storm"
        else -> "Clear"
    }
}
