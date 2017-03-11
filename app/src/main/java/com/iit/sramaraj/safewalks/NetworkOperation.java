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
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Nivash on 6/18/2016.
 */
public class NetworkOperation extends AsyncTask<InputValues,Void,ArrayList> {
    String start,destination;
    private HttpsURLConnection conn;
    protected ArrayList doInBackground(InputValues...params) {
        ArrayList<String> count = new ArrayList<>();
        InputValues input=params[0];
        InputValues inp= new InputValues();
        start= input.start;

        destination = input.destination;

        Log.d("Network Operation", "Start place" + start + "destination" + destination);
        try {
            String newurl = "https://maps.googleapis.com/maps/api/directions/json?origin="+start+"&destination="+destination+"&mode=walking&alternatives=true&key=API_KEY";
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
            System.out.println("***  Measure point  response BEGIN ***");
            System.out.println(responsestring);


            System.out.println("*** Measure point respose END ***");
            JSONObject jsonObject = new JSONObject(responsestring);
            DirectionsJSONParser parser = new DirectionsJSONParser();
            //parser.parse(jsonObject);
            JSONArray responseArray= jsonObject.getJSONArray("routes");
            for (int i = 0; i < responseArray.length(); i++) {
                JSONObject responsejsonObject = responseArray.getJSONObject(i);
                String summary= (String) responsejsonObject.get("summary");
                JSONObject points =(JSONObject)responsejsonObject.get("overview_polyline");
                String polypoints= points.getString("points");
                //System.out.println(summary);
                System.out.println("polypoints"+polypoints);
                count.add(summary);
                count.add(polypoints);

                JSONArray numbercountObject= responsejsonObject.getJSONArray("legs");
            for(int j=0;j<numbercountObject.length();j++) {
                JSONObject value = (JSONObject) numbercountObject.getJSONObject(j);
                JSONObject values =(JSONObject) value.get("distance");
                String distance = values.getString("text");
                count.add(distance);
                JSONObject endlocobj=(JSONObject)value.get("end_location");
                JSONObject startlocobj=(JSONObject)value.get("start_location");
                String endlat= endlocobj.getString("lat");
                String endlng= endlocobj.getString("lng");
                String startlat= startlocobj.getString("lat");
                String startlng= startlocobj.getString("lng");

                //System.out.println(distance);
                JSONObject dvalues = (JSONObject) value.get("duration");
                String duration = dvalues.getString("text");
                count.add(duration);
                count.add(startlat);
                count.add(startlng);
                count.add(endlat);
                count.add(endlng);

            }
            }

        }

        catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return count;
    }


}

