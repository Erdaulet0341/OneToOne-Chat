package com.onetoonechat;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class BackendNotification extends AsyncTask<Void, Void, Void> {
    private static final String SERVER_KEY = "AAAAps1ynsk:APA91bFswy1E0D4aGSNiiJSQWtPlBjeKyRSB6MUYjN_iueKQbsTDSbwcoxNpt_32EM2akrOZ9hlUk6XwtHarNH7ocfNpr0DNYEUuvJ4AS41Mj0H8dM1sAA3z13jQbhO1ADVxPaU8uIl_";
    private static final String TARGET_URL = "https://fcm.googleapis.com/fcm/send";

    String title;
    String message;
    String token;

    public BackendNotification(String title, String message, String token) {
        this.title = title;
        this.message = message;
        this.token = token;
        Log.d("taggg", "era");
    }

    public BackendNotification() {
    }

    protected Void doInBackground(Void... voids) {
        try {
            // Create URL object
            URL url = new URL(TARGET_URL);

            // Create connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set the required HTTP Method
            connection.setRequestMethod("POST");

            // Set the request headers
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "key=" + SERVER_KEY);

            // Enable input and output streams
            connection.setDoInput(true);
            connection.setDoOutput(true);

            // Create JSON request body
            String requestBody = String.format("{\"to\":\"%s\",\"data\":{\"title\":\"%s\",\"message\":\"%s\"}}", token, title, message);

            // Write the request body
            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(requestBody);
            outputStream.flush();
            outputStream.close();

            // Get the response status code
            int responseCode = connection.getResponseCode();

            // Read the response from the input stream
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Print the response
            Log.d("taggg","Response Code: " + responseCode);
            Log.d("taggg","Response Body: " + response);

            // Disconnect the connection
            connection.disconnect();

        } catch (Exception e) {
            Log.d("taggg", e.toString());
            e.printStackTrace();
        }
        return null;
    }
}
