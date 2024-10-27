package com.example.mapofdenmark;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
public class TSTTest {
    TST tst;
    @BeforeEach
    void setUp() {
        tst = new TST();
        tst.put("abcdefghijklmnopqrstuvxyzæøåABCDEFGHIJKLMNOPQRSTUVXYZÆØÅ".toLowerCase(), 10,20);
    }

    @Test
    void fetchAlphabet() {
        assertEquals(tst.get("abcdefghijklmnopqrstuvxyzæøåABCDEFGHIJKLMNOPQRSTUVXYZÆØÅ".toLowerCase()).lat,10);
        assertEquals(tst.get("abcdefghijklmnopqrstuvxyzæøåABCDEFGHIJKLMNOPQRSTUVXYZÆØÅ".toLowerCase()).lon,20);
    }

    @Test
    void autoComplete() {
        String[] hits = new String[]{"bbcd","bbbd","badh","beghh"};
        tst.put("bbcd",10,10); //Hit Search 1
        tst.put("bbbd",20,20); //Hit Search 2
        tst.put("badh",30,30); //Hit Search 3
        tst.put("eeee",40,40); //Miss 1
        tst.put("giif",50,50); //Miss 2
        tst.put("beghh",60,60); //Hit Search 4
        List<String> addressList = Address.autocomplete("b",tst);
        assertEquals(addressList.size(),4);

        for(String item : hits) {
            assertTrue(addressList.contains(item));
        }
    }

    @Test
    void autoCompleteOverload() {
        tst.put("bbcd",10,10); //Hit Search 1
        tst.put("bbbd",20,20); //Hit Search 2
        tst.put("bbdh",30,30); //Hit Search 3
        tst.put("eeee",40,40); //Miss 1
        tst.put("giif",50,50); //Miss 2
        tst.put("bbghh",60,60); //Hit Search 4
        tst.put("bbbbbbddddd",70,70); //Hit Search 5
        tst.put("bcdscdg",80,80); //Hit Search 6
        tst.put("bcddcxxxsw",90,90); //Hit Search 7
        tst.put("bgg",100,100); //Hit Search 8
        assertEquals(Address.autocomplete("b",tst).size(),6); //Function should return a maximum of 6

    }
}
