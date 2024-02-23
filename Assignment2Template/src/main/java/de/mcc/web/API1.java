package de.mcc.web;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class API1
{
    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Please enter the City Name:-");
        String cityname = sc.nextLine().replaceAll(" ", "_");
        HttpClient client = HttpClient.newHttpClient();

        //https://api-ninjas.com/api/weather
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.api-ninjas.com/v1/city?name=" + cityname))
                .setHeader("X-Api-Key", "xFwlrbxH9EicKtmg2xF5tw==TPM5DWoDoIeIXZ7D")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        //https://api-ninjas.com/api/city
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create("https://api.api-ninjas.com/v1/weather?city=" + cityname))
                .setHeader("X-Api-Key", "xFwlrbxH9EicKtmg2xF5tw==TPM5DWoDoIeIXZ7D")
                .build();

        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) System.out.println("Data Packets from city info API received...");
        if (response2.statusCode() == 200) System.out.println("Data Packets from city weather API received...\n");
        JSONArray jsonArray = new JSONArray(response.body());
        System.out.println("----------------------------");
        try
        {
        for (int i = 0; i < jsonArray.length(); i++)
        {
            JSONObject a = jsonArray.getJSONObject(i);

            String name = a.getString("name");
            double latitude = a.getDouble("latitude");
            double longitude = a.getDouble("longitude");
            String country = a.getString("country");
            int population = a.getInt("population");
            boolean isCapital = a.getBoolean("is_capital");

            System.out.println(
                    "CITY INFO->" + "\n" +
                            "Name: " + name + "\n" +
                            "Latitude:- " + latitude + "°N\n" +
                            "Longitude:- " + longitude + "°E\n" +
                            "Country:- " + country + "\n" +
                            "Population:- " + population + "-People \n" +
                            "Is Capital?:- " + isCapital);
        }
        }
        catch (JSONException e)
        {
            System.out.println("CITY INFO is unfortunately not available for "+cityname+" :( !");
        }
        try {
            JSONObject o = new JSONObject(response2.body());
            System.out.println("----------------------------");
            System.out.println(
                    "CURRENT WEATHER INFO->\n" +
                            "Cloudiness:- " + o.get("cloud_pct") + "%\n" +
                            "Tempreture:- " + o.get("temp") + "°C\n" +
                            "Feels like:- " + o.get("feels_like") + "°C\n" +
                            "Humidity:- " + o.get("humidity") + "%\n" +
                            "MIN temp:- " + o.get("min_temp") + "°C\n" +
                            "MAX temp:- " + o.get("max_temp") + "°C\n" +
                            "Wind speed:- " + o.get("wind_speed") + " km/h\n" +
                            "Wind degrees:- " + o.get("wind_degrees") + "°\n" +
                            "Sunrise:- " + time(o.get("sunrise").toString()) + "\n" +
                            "Sunset:- " + time(o.get("sunset").toString()));
        } catch (JSONException e)
        {
            System.out.println("WEATHER DATA is unfortunately not available for "+cityname+" :( !");
        }
    }
    public static String time(String timestamp)
    {
        long millis = Long.parseLong(timestamp);
        Instant instant = Instant.ofEpochSecond(millis);
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String formattedDateTime = dateTime.format(formatter);
        return formattedDateTime;
    }
}
