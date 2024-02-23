package de.mcc.web;

import org.apache.hc.core5.net.URIBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class SimpleAPICall {

    public static void main(String[] args) throws Exception {

        // Example Call to Worldbank API
        // If you want to use the Worldbank in your Homework, use a different API

        // we want this URI: https://api.worldbank.org/V2/country?incomeLevel=LIC&format=json
        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("api.worldbank.org")
                .setPath("V2/country")
                .addParameter("incomeLevel", "LIC")
                // It is way easier to work with JSON than with XML ==> get the JSON representation
                .addParameter("format", "json")
                .build();
        HttpRequest r = HttpRequest.newBuilder(uri)
                .GET()
                .build();
        HttpClient c = HttpClient.newHttpClient();
        HttpResponse<String> response = c.send(r, HttpResponse.BodyHandlers.ofString());

        System.out.println("Response: " + response);

        System.out.println("Raw JSON: ");
        System.out.println(response.body());

        JSONArray o = new JSONArray(response.body()); // Most APIs return JSONObject, not JSONArray

        System.out.println("First country: ");
        System.out.println(o.getJSONArray(1).getJSONObject(0));


    }
}
