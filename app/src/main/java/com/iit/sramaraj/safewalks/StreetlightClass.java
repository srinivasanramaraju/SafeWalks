package com.iit.sramaraj.safewalks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Nivash on 10/26/2016.
 */
public class StreetlightClass extends AsyncTask<InputValues,Void,String> {
    int size=0;

    public static String[] idOnController;
    public static String[] controllerStrId;
    public static String[] categoryType;
    private XmlPullParserFactory xmlFactoryObject;
    private XmlPullParser myparser;
    private List<String> cookies;
    private HttpsURLConnection conn;
    private static double curlatitude,curlongitude;
    double latMin,latMax,lngMin,lngMax;
    int finalDevices=0;
    private Context mContext;
    static String returnVal;

    @Override
    protected String doInBackground(InputValues...params){
        String url = "https://slv.poc02.ssn.ssnsgs.net:8443/reports/auth.json";
        String url1 = "https://slv.poc02.ssn.ssnsgs.net:8443/reports/j_security_check";
        //String url2="https://slv.poc02.ssn.ssnsgs.net:8443/reports/api/servlet/SLVDimmingAPI?methodName=switchOn&controllerStrId=CSMART&idOnController=Stlt-498511";
        InputValues input=params[0];
        curlatitude=input.lat;
        curlongitude=input.lng;
        latMin=curlatitude-0.03350;
        latMax=curlatitude+0.03350;
        lngMax=curlongitude+0.03350;
        lngMin=curlongitude-0.03350;
        System.out.println("The Min & max are"+latMin+" "+latMax+" "+lngMin+" "+lngMax);

        String concatUrl="https://slv.poc02.ssn.ssnsgs.net:8443/reports/api/servlet/SLVAssetAPI?methodName=getDevicesInBounds&latMin="+latMin+"&latMax="+latMax+"&lngMin="+lngMin+"&lngmax="+lngMax;

        StreetlightClass http = new StreetlightClass();

        // make sure cookies is turn on
        CookieHandler.setDefault(new CookieManager());
        // 1. Send a "GET" request, so that you can extract the form's data.
        String page = null;
        try {
            page = http.GetPageContent(url);

            String postParams = http.getFormParams(page, "USER_NAME", "PASSWORD");


            // 2. Construct above post's content and then send a POST request for
            // authentication
            http.sendPost(url1, postParams);

            // 3. success then go to gmail.

            String result = http.GetPageContent(url);
            System.out.println(result);
            String result1 = http.GetPageContent(concatUrl);
            System.out.println("Not moving after this");
            xmlFactoryObject = XmlPullParserFactory.newInstance();
            XmlPullParser myparser = xmlFactoryObject.newPullParser();
            XmlPullParser newparser=xmlFactoryObject.newPullParser();
            System.out.println("The parser is instantied");
            myparser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            myparser.setInput(new StringReader(result1));
            newparser.setInput(new StringReader(result1));
            System.out.println("the input is set to the parser");
            size=noOfDevices(myparser);
            idOnController=new String[size];
            categoryType=new String [size];
            controllerStrId=new String[size];
            System.out.println(size);

            parseXMLAndStoreIt(newparser);
            System.out.println("The contents are parsed");


            for(int k=0;k<size;k++)
            {
                System.out.println(k+ " "+ idOnController[k]+ "  " +controllerStrId[k]+" "+categoryType[k]);
                if(categoryType[k].equalsIgnoreCase("streetlight"))
                {
                    idOnController[finalDevices]=idOnController[k];
                    controllerStrId[finalDevices]=controllerStrId[k];
                    finalDevices++;
                }else
                {
                    idOnController[k]=null;
                    controllerStrId[k]=null;
                }
            }

            long startTime=System.currentTimeMillis();
            long maxtime,time;
            time =startTime+30000;
            System.out.println("Current Start Time :"+ startTime);

            String onApiUrl = "https://slv.poc02.ssn.ssnsgs.net:8443/reports/api/servlet/SLVDimmingAPI?methodName=setDimmingLevels&controllerStrId="+controllerStrId+"&idOnController="+idOnController+"dimmingLevel=80.0";
            String light1 = http.GetPageContent(onApiUrl);
            System.out.println(light1);




            long endTime=System.currentTimeMillis();
            long totalTime=endTime-startTime;
            System.out.println("The total time:"+totalTime);

            // System.out.println(result1);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        if(size==0)
        {
            returnVal="notrigger";
        }else if(size!=0)
        {
            returnVal="triggered";
        }
        return returnVal ;
    }
    private void sendPost(String url, String postParams) throws Exception {

        URL obj = new URL(url);
        conn = (HttpsURLConnection) obj.openConnection();
        // cookies = conn.getHeaderFields().get("Set-Cookie");
        // Acts like a browser
        conn.setUseCaches(false);
        conn.setRequestMethod("POST");
        /// conn.setRequestProperty("Host", "accounts.google.com");
        // conn.setRequestProperty("User-Agent", USER_AGENT);
        conn.setRequestProperty("Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,q=0.8");
        conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        for (String cookie : this.cookies) {
            conn.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
        }
        conn.setRequestProperty("Connection", "keep-alive");
        //conn.setRequestProperty("Referer", "https://accounts.google.com/ServiceLoginAuth");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", Integer.toString(postParams.length()));

        conn.setDoOutput(true);
        conn.setDoInput(true);

        // Send post request
        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        wr.writeBytes(postParams);
        wr.flush();
        wr.close();

        int responseCode = conn.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Post parameters : " + postParams);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in =
                new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        // System.out.println(response.toString());

    }


    private String GetPageContent(String url) throws Exception {

        URL obj = new URL(url);
        conn = (HttpsURLConnection) obj.openConnection();

        // default is GET
        conn.setRequestMethod("GET");

        conn.setUseCaches(false);

        // act like a browser
        // conn.setRequestProperty("User-Agent", USER_AGENT);
        conn.setRequestProperty("Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,q=0.8");
        conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        if (cookies != null) {
            for (String cookie : this.cookies) {
                conn.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
            }
        }
        int responseCode = conn.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in =
                new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        // Get the response cookies
        setCookies(conn.getHeaderFields().get("Set-Cookie"));

        return response.toString();

    }

    public String getFormParams(String html, String username, String password)
            throws UnsupportedEncodingException {

        System.out.println("Extracting form's data...");

        Document doc = Jsoup.parse(html);

        //Element loginform = doc.getElementById("gaia_loginform");
        //Element loginform = doc.getElementById("slv_login_window");
        System.out.println("Stuck here !");

        Elements inputElements = doc.getElementsByTag("input");
        List<String> paramList = new ArrayList<String>();
        for (Element inputElement : inputElements) {
            String key = inputElement.attr("name");
            String value = inputElement.attr("value");

            if (key.equals("j_username"))
                value = username;
            else if (key.equals("j_password"))
                value = password;
            paramList.add(key + "=" + URLEncoder.encode(value, "UTF-8"));
        }
        // build parameters list
        StringBuilder result = new StringBuilder();
        for (String param : paramList) {
            if (result.length() == 0) {
                result.append(param);
            } else {
                result.append("&" + param);
            }
        }

        return result.toString();
    }

    public List<String> getCookies() {
        return cookies;
    }

    public void setCookies(List<String> cookies) {
        this.cookies = cookies;
    }



    public void parseXMLAndStoreIt(XmlPullParser myParser)
    {
        int event;

        String text=null;
        int i=0,j=0,l=0;


        try {
            event = myParser.getEventType();
            //System.out.println( "The Event is:"+event);
            while (event != XmlPullParser.END_DOCUMENT) {
                String name=myParser.getName();
                //System.out.println("The Name is :"+name);
                switch (event){
                    case XmlPullParser.START_TAG:
                        break;

                    case XmlPullParser.TEXT:
                        text = myParser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if(name.equals("idOnController")){
                            idOnController[i] = text;
                            System.out.println(idOnController[i]);
                            i++;
                        }
                        else if(name.equals("categoryStrId"))
                        {
                            categoryType[j]=text;
                            System.out.println(categoryType[j]);
                            j++;
                        }
                        else if(name.equals("controllerStrId")){
                            controllerStrId[l] = text;
                            System.out.println(controllerStrId[l]);
                            l++;

                        }

                        else{
                        }
                        break;
                }
                event = myParser.next();
            }
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }


    public int noOfDevices(XmlPullParser myParser)
    { int event;

        String text=null;
        try {
            event = myParser.getEventType();
            //System.out.println( "The Event is:"+event);
            while (event != XmlPullParser.END_DOCUMENT) {
                String name=myParser.getName();
                //System.out.println("The Name is :"+name);
                switch (event){
                    case XmlPullParser.START_TAG:
                        break;

                    case XmlPullParser.TEXT:
                        text = myParser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if(name.equals("SLVDevice")){
                            size++;
                        }
                }
                event = myParser.next();
            }
        }

        catch (Exception e) {
            e.printStackTrace();
        }
        //System.out.println("inside no of devices"+size);
        return size;
    }
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (size == 0) {
                Toast.makeText(mContext, "No Streetlights Near by Sorry ", Toast.LENGTH_SHORT).show();

        } else if (size != 0) {
            Toast.makeText(mContext, " Your Emergency request is triggered! ", Toast.LENGTH_SHORT).show();
        }
    }


}
