package com.iit.sramaraj.safewalks;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Nivash on 10/25/2016.
 */

public class PlacemeterClass extends AsyncTask<InputValues,Void,InputValues> {
    int id;
    Double latitude,longitude;
    Double flat,flng;
    String name;
    int founddevicce=0;
    int peddirection1Count,peddirection2Count,pedtotalCount,direction1Count,direction2Count,alltotalCount;
    String dir1str,dir2str,peddir1str,peddir2str;
    private static double curlatitude,curlongitude;
    Double lat,lng;
    String startDate, endDate,startTime,endTime;
    private HttpsURLConnection conn;
    InputValues iv= new InputValues();

    @Override
    protected InputValues doInBackground(InputValues...params) {
        ArrayList<Integer> count = new ArrayList<>();
        InputValues input=params[0];
        String webPage = "https://api.placemeter.net/api/v1/sensors";
        String authStringEnc = "API_KEY";
        String data = "";
        try {
            URL url = new URL(webPage);
            conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestProperty("Authorization", "Token " + authStringEnc);
            InputStream is = conn.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);

            int numCharsRead;
            char[] charArray = new char[1024];
            StringBuffer sb = new StringBuffer();
            while ((numCharsRead = isr.read(charArray)) > 0) {
                sb.append(charArray, 0, numCharsRead);
            }
            String result = sb.toString();

            System.out.println("*** BEGIN ***");
            System.out.println(result);
            System.out.println("*** END ***");
            String locationname = input.locationname;
            lat = input.lat;
            lng = input.lng;
            //Get the instance of JSONArray that contains JSONObjects
            Log.d("NetworkOperation", "lat from other class " + lat + "  " + lng);
            JSONArray jsonArray = new JSONArray(result);

            //Iterate the jsonArray and print the info of JSONObjects
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                id = Integer.parseInt(jsonObject.optString("id").toString());
                name = jsonObject.optString("name").toString();
                Log.d("location -placemeter class", name);
                /*To do  fetch device based on location ypu need id to get results of that device.
                so find the every ids in that route using lat long.*/
                if (locationname.equalsIgnoreCase(name)) {
                    founddevicce = 1;
                    break;
                }
            }
           /* if (founddevicce == 0) {
                Log.d("Placemeter Class","No device found in records");
                return iv;
            }
            else {*/
                Log.d("NetworkOperation", "doInBackground- measure pts:--" + id + "--" + name + "--" + latitude + "--" + longitude);
                startDate = input.startDate;
                startTime = input.startTime;
                endDate = input.endDate;
                endTime = input.endTime;
                Log.d("Network Operation", "Start Date" + startDate + "Time" + startTime);
                Log.d("Network Operation", "End Date" + endDate + "Time" + endTime);
                String endDateString = "" + endDate + endTime;
                String startdateString = "" + startDate + startTime;
                Log.d("NetworkOperations", "doInBackground: " + startdateString);
                DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyhh:mm");
                dateFormat.setTimeZone(TimeZone.getDefault());
                java.util.Date sdate = dateFormat.parse(startdateString);
                java.util.Date edate = dateFormat.parse(endDateString);
                long fstartTime = (long) sdate.getTime() / 1000;
                long fendTime = (long) edate.getTime() / 1000;
                Log.d("NetworkOperation", "doInBackground: fstartime" + fstartTime + "end timing" + fendTime);
                String newurl = "https://api.placemeter.net/api/v1/measurementpoints/"+id+"/data?start="+fstartTime+"&end="+fendTime+"&resolution=day&[classes=ped,all]";
                URL dataurl = new URL(newurl);
                conn = (HttpsURLConnection) dataurl.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", "Token " + authStringEnc);
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
                String responsestring = response.toString();
                System.out.println("***  Measure point  response BEGIN ***");
                System.out.println(responsestring);
                System.out.println("*** Measure point respose END ***");

                JSONObject jsonObject = new JSONObject(responsestring);
                JSONArray responseArray = jsonObject.getJSONArray("data");
                //Iterate the jsonArray and print the info of JSONObjects
                for (int i = 0; i < responseArray.length(); i++) {
                    direction1Count = 0;
                    direction2Count = 0;
                    JSONObject responsejsonObject = responseArray.getJSONObject(i);

                    JSONObject numbercountObject = responsejsonObject.getJSONObject("all");
                    dir1str = numbercountObject.getString("direction_1");
                    if (!dir1str.equals("null")) {
                        direction1Count = Integer.parseInt(dir1str);
                    }
                    dir2str = numbercountObject.getString("direction_2");
                    if (!dir2str.equals("null")) {
                        direction2Count = Integer.parseInt(dir2str);
                    }
                    Log.d("NetworkOperation", "directionCount :--" + direction1Count + "--" + direction2Count);
                    alltotalCount += direction1Count + direction2Count;

                    if (jsonObject.has("ped")) {
                        JSONObject pednumbercountObject = responsejsonObject.getJSONObject("ped");
                        peddir1str = pednumbercountObject.getString("direction_1");
                        if (!peddir1str.equals("null")) {
                            peddirection1Count = Integer.parseInt(peddir1str);
                        }
                        peddir2str = pednumbercountObject.getString("direction_2");
                        if (!peddir2str.equals("null")) {
                            peddirection2Count = Integer.parseInt(peddir2str);
                        }
                        pedtotalCount += peddirection1Count + peddirection2Count;

                        iv.pedtotalcount=pedtotalCount;
                    }
                }
           // }
                Log.d("NetworkOperation", "totalCount :--" + alltotalCount + "--" + pedtotalCount);

                }catch(ParseException e)
                {
                    e.printStackTrace();
                }   catch(MalformedURLException e){
                e.printStackTrace();
                 }catch(IOException e){
                e.printStackTrace();
                }catch(JSONException e) {
            e.printStackTrace();
        }

        return iv;
    }

    @Override
    protected void onPostExecute(InputValues iv) {
        if (pedtotalCount > 1)
        {
            StreetlightClass stclass= new StreetlightClass();
            stclass.execute(iv);
        }
    }
}

