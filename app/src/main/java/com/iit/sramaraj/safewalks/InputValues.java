package com.iit.sramaraj.safewalks;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Nivash on 6/18/2016.
 */
public class InputValues {
    public Integer pedtotalcount=-1;
    public String start;
    public String destination;
    public String startDate;
    public String endDate;
    public String startTime;
    public String endTime;
    public Double lat,lng;
    public String locationname;
    public String[] summary ;
    public String[] distance;
    public String[] duration;
    public String[] polypoints;
            Double [] startlat,startlng,endlat,endlng;
    String temp,temp1,temp2,temp3;

    public int getpedtotalcount(){return pedtotalcount;}

    public Integer getPedtotalcount() {
        return pedtotalcount;
    }

    public String getStartDate() {
        return startDate;
    }
    public String getEndDate() {
        return endDate;
    }
    public String getStartTime() {
        return startTime;
    }
    public String getEndTime() {
        return endTime;
    }
    public Double getLat() {
        return lat;
    }
    public Double getLng() {
        return lng;
    }
    public void setStartDate(String startDate) {this.startDate = startDate;}
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
    public void setLat(Double lat) {
        this.lat = lat;
    }
    public void setLng(Double lng) {
        this.lng = lng;
    }
    public String getStart() {
        return start;
    }
    public String getDestination() {
        return  destination;
    }
    public void setStart(String start) {
        this.start = start;
    }
    public void setEndDate(String destination) {
        this.destination = destination;
    }

    public void InputValues(ArrayList list)
    {
       int n=list.size();
       summary=new String[n/8];
       duration=new String[n/8];
        distance=new String[n/8];
        polypoints= new String[n/8];
        startlat=new Double[n/8];
        startlng=new Double[n/8];
        endlat=new Double[n/8];
        endlng=  new Double[n/8];
        Iterator it=list.iterator();
        int k=0,i=0;
        while(i<n/8) {
            //System.out.print(list.size() + "--" + k);
            //System.out.println(list.get(k) + "--" + list.get(k + 1) + "--" + list.get(k + 2));
            summary[i] = (String) list.get(k);
            polypoints[i] = (String)list.get(k +1);
            distance[i] = (String) list.get(k + 2);
            duration[i] = (String) list.get(k + 3);
            temp = (String) list.get(k + 4);
            startlat[i]=Double.parseDouble(temp);
            temp1 = (String) list.get(k + 5);
            startlng[i]=Double.parseDouble(temp1);
            temp2 = (String) list.get(k + 6);
            endlat[i]=Double.parseDouble(temp2);
            temp3 = (String) list.get( k + 7);
            endlng[i]=Double.parseDouble(temp3);
            k = k + 8;
            //System.out.println("--"+k);
            i = i + 1;
        }
        //System.out.println("Inputvalues"+summary[0]+""+summary[1]+""+summary[2]);
    }

}
