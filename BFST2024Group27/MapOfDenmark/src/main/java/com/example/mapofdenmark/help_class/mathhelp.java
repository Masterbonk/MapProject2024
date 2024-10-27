package com.example.mapofdenmark.help_class;

import java.io.Serializable;

public class mathhelp implements Serializable {
    /** calculates the distance between to points with lon and lat by use of the Haversine formula
     * first the degrees are converted to radians, and from here it is put into the Haversine formulae,
     * solving for d, and thereby calculating the distance
     * Afterward, the distance gets rounded to 5 s.f. and units are added depending on magnitude. The units are either km or m
     * @return the distance in kilometers or meters depending on magnitude
     * The implemtation of the haversine formula is inspired by the following implentation of Haversine in javascript
     * https://stackoverflow.com/questions/639695/how-to-convert-latitude-or-longitude-to-meters
     * **/

    public static float calculateDistance(float lat1, float lon1, float lat2, float lon2){
        final float radius_of_earth = 6371.137f;
        //in order to use the Haversine formula we convert degrees to radians:
        //the difference between longitude and latitudes of the two points is calculated:
        float distance_lon = Math.abs(lon2 *(float) Math.PI/180 -lon1*(float)Math.PI/180);
        float distance_lat = Math.abs(lat2 * (float)Math.PI/180-lat1*(float)Math.PI/180);
        //avoid potential errors

        //double insidesqr = Math.pow(Math.sin(distance_lat/2),2)+Math.cos(lat1)*Math.cos(lat2)*Math.pow(Math.sin(distance_lon/2),2);

        /*double insidesqr =1-Math.cos(distance_lat)+Math.cos(lat1)*Math.cos(lat2)*(1-Math.cos(distance_lon));
        double root = Math.sqrt(insidesqr/2);
        double distance = 2*Math.asin(root);*/
        float a = (float)Math.sin(distance_lat/2)*(float)Math.sin(distance_lat/2)+(float)Math.cos(lat1*Math.PI/180)*(float)Math.cos(lat2*Math.PI/180)*(float)Math.sin(distance_lon/2)*(float)Math.sin(distance_lon/2);
        float c = 2*(float)Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return radius_of_earth*c;
    }

}
