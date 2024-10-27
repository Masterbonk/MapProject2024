package com.example.mapofdenmark.help_class;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class mathhelpTest {

    //we have used online tools to compare the distance is the correct distance,
    //The online tool is less precise than our program, but we have compared every test with the online tool and rounded up or down
    //https://notunreasonable.com/2011/09/19/latlon-distance-formula-in-excel-haversine-and-spherical-law-of-cosines/
    //article written by David Wright
    @Test
    void calculateDistance() {
        //comparing with a tool found online
        float result = mathhelp.calculateDistance(40.7812f,73.9665f,42.0532f,73.9665f);
        assertEquals(141.44296264648438,result);

    }
    @Test
    void calculateDistance2() {

        double result = mathhelp.calculateDistance(55.16123774671408f, 15.024366863011753f,55.16503435279123f, 15.030826404033128f);
        assertEquals(0.588799238204956,result);

    }
    @Test
    void calculatedistance3(){
        double result = mathhelp.calculateDistance(55.13657690322505f, 15.00105315027914f,55.13295797348165f, 14.99351718108481f);
        assertEquals(0.6259190440177917,result);
    }
    @Test
    void calculatedistance4(){
        //reversed
        double result = mathhelp.calculateDistance(55.13295797348165f, 14.99351718108481f,55.13657690322505f, 15.00105315027914f);
        assertEquals(0.6259190440177917,result);
    }

    @Test
    void calculatedistance5(){
        //even smaller
        double result= mathhelp.calculateDistance(55.13778059297231f, 14.996294827005205f,55.13780107819662f, 14.996532254342483f);

        assertEquals(0.015425430610775948,result);
    }

}