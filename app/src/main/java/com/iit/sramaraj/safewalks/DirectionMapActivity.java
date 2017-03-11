package com.iit.sramaraj.safewalks;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by Nivash on 6/21/2016.
 */
public class DirectionMapActivity extends AppCompatActivity {
    GoogleMap map;
    ArrayList<LatLng> markerPoints;
    TextView sensoravailabliity,pedcount;
    private static double latitude,longitude;
    InputValues getPlacemeteroutput;
    InputValues placemeterinput=new InputValues();
    List<List<HashMap<String, String>>> routes = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directionmap);
        sensoravailabliity=(TextView)findViewById(R.id.pedtxt);
        pedcount =(TextView)findViewById(R.id.pedcounttxt);
        markerPoints = new ArrayList<LatLng>();
            // Getting reference to SupportMapFragment of the activity_main
        final ArrayList<String> myList = (ArrayList<String>) getIntent().getSerializableExtra("array_list");

       int pedlist = getIntent().getIntExtra("ped_no",0);
        SupportMapFragment fm = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
            // Getting Map for the SupportMapFragment
        map = fm.getMap();
        if(map!=null) {
            int position = getIntent().getIntExtra("position", 0);

            // Enable MyLocation Button in the Map
            map.setMyLocationEnabled(true);
            InputValues in = new InputValues();
            in.InputValues(myList);
            // Setting onclick event listener for the map
            LatLng point = new LatLng(in.startlat[position], in.startlng[position]);
            LatLng point1 = new LatLng(in.endlat[position],in.endlng[position]);

            pedcount.setText(String.valueOf(pedlist));
            // Already two locations
            if (markerPoints.size() > 1) {
                markerPoints.clear();
                map.clear();
            }
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(point,13));
            // Adding new item to the ArrayList
            markerPoints.add(point);
            markerPoints.add(point1);

            // Creating MarkerOptions
            MarkerOptions options = new MarkerOptions();

            // Setting the position of the marker
            options.position(point);

            /**
             * For the start location, the color of marker is GREEN and
             * for the end location, the color of marker is RED.
             */
            if (markerPoints.size() == 1) {
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            } else if (markerPoints.size() == 2) {
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            }

            // Add new marker to the Google Map Android API V2
            map.addMarker(options);

            // Checks, whether start and end locations are captured
            if (markerPoints.size() >= 2) {
                LatLng origin = markerPoints.get(0);
                LatLng dest = markerPoints.get(1);



                String url = "https://maps.googleapis.com/maps/api/directions/json?origin=iittower,chicago,IL&destination=555E33rdPl,Chicago,IL&mode=walking&alternatives=true&key=API_KEY";

                ParserTask parserTask = new ParserTask();

                // Invokes the thread for parsing the JSON data
                parserTask.execute(in.polypoints[position]);

            }


        }
    }

    private String getDirectionsUrl(LatLng origin,LatLng dest){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=iittower,chicago,IL&destination=555E33rdPl,Chicago,IL&mode=walking&alternatives=true&key="API_KEY";

        return url;
    }
    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Exception", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        Log.d("Download Url",data);
        return data;
    }

    // Fetches data from url passed
    public class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";
    System.out.print("Download task class");
            try{
                // Fetching the data from web service
                Log.d("Background Task - url", url[0]);
                data = downloadUrl(url[0]);
                Log.d("Background Task", "Downloadin");
            }catch(Exception e){
                Log.d("Background Task", e.toString());
            }
            Log.d("Background return data", data);
            return data;

        }

        // Executes in UI thread, after the execution of
        // doInBackground()


    }

    public class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> > {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            System.out.print("parser task class");
            JSONObject jObject;


            try{
                 String polystring= strings[0];
               // jObject =  //new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(polystring);
            }catch(Exception e){
                e.printStackTrace();
            }
            Log.d("parser return -- routes", String.valueOf(routes));
            /*use the location Return The count for the location from placemeter */
            /* Based on the count trigger the streelights */

            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            PolylineOptions ptLineOptions =null;
            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yy");
            DateFormat timeFormat = new SimpleDateFormat("HH:mm");
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, -05);
            Date date = new Date();
            MarkerOptions markerOptions = new MarkerOptions();
            PlacemeterClass pedclass= new PlacemeterClass();
            StreetlightClass stclass= new StreetlightClass();
            ReverseGeocoding rev=new ReverseGeocoding();
            for (List i:routes)
            {
                for( int j=0; j<i.size();j++) {
                    HashMap hashMap = (HashMap) i.get(j);
                    Iterator<Object> it = hashMap.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry pair = (Map.Entry) it.next();

                        Log.d("inside while", pair.getKey() + " " + pair.getValue());
                        if(pair.getKey().equals("lat"))
                        {
                            latitude=Double.parseDouble((String) pair.getValue());
                        }
                        else if (pair.getKey().equals("lng"))
                        {
                            longitude=Double.parseDouble((String) pair.getValue());
                        }

                    }
                    Log.d("after while", String.valueOf(j));
                    placemeterinput.lat= latitude;
                    placemeterinput.lng= longitude;
                    placemeterinput.startDate=(String)dateFormat.format(date);
                    placemeterinput.endDate=(String)dateFormat.format(date);
                    placemeterinput.startTime=(String)timeFormat.format(cal.getTime());
                    placemeterinput.endTime=(String)timeFormat.format(date);
                    try
                    {
                        String locationname=rev.execute(placemeterinput).get();
                        placemeterinput.locationname=locationname;
                        getPlacemeteroutput= pedclass.execute(placemeterinput).get();
                        Log.d("DirectionMapActivity", String.valueOf(getPlacemeteroutput.pedtotalcount));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
                Log.d("inside for", String.valueOf(i));

            }



            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();
                ptLineOptions =new PolylineOptions();
                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                int size =path.size();
                for(int j=0;j<path.size();j++){
                    //Log.d("DirectionMap -size", Integer.toString(path.size()));
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                   // Log.d("DirectionMap -latsensor", Double.toString(lat));
                   // Log.d("DirectionMap - lngsensor", Double.toString(lng));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }
                //Log.d("DirectionMap - oversensor", "over");
                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.BLUE);

            }

            // Drawing polyline in the Google Map for the i-th route
            map.addPolyline(lineOptions);
            map.addPolyline(ptLineOptions);


        }
    }

}
