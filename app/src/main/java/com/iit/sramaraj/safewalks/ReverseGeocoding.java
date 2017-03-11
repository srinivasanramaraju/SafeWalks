package com.iit.sramaraj.safewalks;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Nivash on 11/1/2016.
 */
public class ReverseGeocoding extends AsyncTask<InputValues,Void,String> {
    Double lat,lng;
    String streetAddress;
    private HttpsURLConnection conn;
    @Override
    protected String doInBackground(InputValues... params) {
        InputValues inp= params[0];
        lat = inp.lat;
        lng = inp.lng;
        Log.d("Reverse Geocoding", "latitute " + lat + "Longitude" + lng);
        try {
            String newurl = "https://maps.googleapis.com/maps/api/geocode/json?latlng="+lat+","+lng+"&key=API_KEY";
            URL dataurl = new URL(newurl);
            conn = (HttpsURLConnection) dataurl.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + newurl);
            System.out.println("Response Code : " + responseCode);
            BufferedReader in =
                    new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            System.out.print(response);
            String responsestring = response.toString();
        
            JSONObject jsonObject = new JSONObject(responsestring);
            JSONArray responseArray= jsonObject.getJSONArray("results");
            for (int i = 0; i < responseArray.length(); i++) {
                JSONObject responsejsonObject = responseArray.getJSONObject(i);
                streetAddress = responsejsonObject.getString("formatted_address");
                break;

            }
        }

        catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("Reverse Geocoding- Street Address", streetAddress);
        return streetAddress;
    }
}
