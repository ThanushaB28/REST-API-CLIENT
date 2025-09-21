package CodeTech;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class Weather {

    // Free weather API (London as default)
    private static final String FREE_API_URL = 
        "https://api.open-meteo.com/v1/forecast?latitude=51.5074&longitude=-0.1278&current=temperature_2m,relative_humidity_2m,apparent_temperature,precipitation,weather_code,wind_speed_10m&timezone=auto";

    public static void main(String[] args) {
        try {
            String weatherData = getWeatherData();
            displayFreeWeatherInfo(weatherData);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static String getWeatherData() throws Exception {
        URL url = new URL(FREE_API_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException("HTTP Response Code: " + responseCode);
        }

        StringBuilder response = new StringBuilder();
        try (Scanner scanner = new Scanner(conn.getInputStream())) {
            while (scanner.hasNext()) {
                response.append(scanner.nextLine());
            }
        }

        conn.disconnect();
        return response.toString();
    }

    public static void displayFreeWeatherInfo(String jsonResponse) {
        try {
            // Extract from "current" section
            String current = jsonResponse.substring(jsonResponse.indexOf("\"current\":"));

            String temperature = extractValue(current, "\"temperature_2m\":");
            String feelsLike = extractValue(current, "\"apparent_temperature\":");
            String humidity = extractValue(current, "\"relative_humidity_2m\":");
            String windSpeed = extractValue(current, "\"wind_speed_10m\":");

            System.out.println("🌤️  FREE WEATHER API DATA");
            System.out.println("========================");
            System.out.println("📍 Location: London (Default)");
            System.out.println("🌡️  Temperature: " + temperature + " °C");
            System.out.println("🤔 Feels like: " + feelsLike + " °C");
            System.out.println("💧 Humidity: " + humidity + " %");
            System.out.println("💨 Wind Speed: " + windSpeed + " m/s");
            System.out.println("========================");
            System.out.println("ℹ️  Using Open-Meteo API (No API Key Required)");

        } catch (Exception e) {
            System.out.println("Error parsing weather data: " + e.getMessage());
        }
    }

    private static String extractValue(String json, String key) {
        try {
            int startIndex = json.indexOf(key) + key.length();
            int endIndex = json.indexOf(",", startIndex);
            if (endIndex == -1) endIndex = json.indexOf("}", startIndex);
            return json.substring(startIndex, endIndex).trim();
        } catch (Exception e) {
            return "N/A";
        }
    }
}
