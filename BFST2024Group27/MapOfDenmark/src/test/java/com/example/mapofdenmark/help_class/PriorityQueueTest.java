package com.example.mapofdenmark.help_class;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.*;


class PriorityQueueTest {
    PriorityQueue pq;
    void insertSmall(){
        pq.insert(2, 20);
        pq.insert(1, 10);
        pq.insert(3, 30);
    }
    void insertLarge(){
        pq.insert(4, 40);
        pq.insert(5, 50);
        pq.insert(1, 10);
        pq.insert(6, 60);
        pq.insert(2, 20);
        pq.insert(7, 70);
        pq.insert(3, 30);
    }
    void insertFull(){
        pq.insert(1, 30);
        pq.insert(2, 55);
        pq.insert(3, 80);
        pq.insert(4, 20);
        pq.insert(5, 10);
        pq.insert(6, 50);
        pq.insert(7, 90);
        pq.insert(8, 40);
        pq.insert(9, 60);
        pq.insert(10, 70);
        pq.insert(0,40.5f );

    }
    void checkPosition(Map<Integer, Float> map){
        for (int i = pq.getN(); i > 1; i--){
            assertTrue(pq.keys(i/2)<pq.keys(i));
            assertEquals(map.get(pq.edgeLocation(i)), pq.keys(i));
        }
        for (int i = 0; i < pq.getN(); i++){
            if (pq.contains(i)){
                assertEquals(pq.edgeLocation(pq.index(i)-1), i);
            }
        }
    }
    void checkMin(int min, double minValue){
        assertEquals(min, pq.edgeLocation(1));
        assertEquals(minValue, pq.keys(1));
        assertEquals(1, pq.index(min)-1);
    }
    Map<Integer, Float> setMapSize(int size){
        Map<Integer, Float> map = new HashMap<>();
        for (int i = 1; i < size+1; i++){
            map.put(i, (float) i*10);
        }
        return map;
    }

    Map<Integer, Float> setMapFull(){
        float[] keys = new float[]{0,10,20,30,40,40.5f,50,55,60,70,80,90,65};
        int[] edgeLocation = new int[]{-1,5,4,1,8,0,6,2,9,10,3,7,11};
        Map<Integer, Float> map = new HashMap<>();
        for (int i = 0; i < 13; i++){
            map.put(edgeLocation[i], keys[i]);
        }
        return map;
    }

    @BeforeEach
    void setup(){
        pq = new PriorityQueue(12);
    }
    @Test
    void containsTrue() {
        pq.insert(1, 10);
        assertTrue(pq.contains(1));
    }
    @Test
    void containsFalse(){
        assertFalse(pq.contains(1));
    }
    @Test
    void containsTrueLarge(){
        insertLarge();
        assertTrue(pq.contains(3));
        assertTrue(pq.contains(1));

    }
    @Test
    void containsFalseLarge(){
        insertLarge();
        assertFalse(pq.contains(8));
        assertFalse(pq.contains(0));
        assertThrows(IndexOutOfBoundsException.class, () -> pq.contains(18));
    }
    @Test
    void minimumFirst(){
        insertSmall();
        checkMin(1,10);
        assertEquals(1, pq.delMin());
    }
    @Test
    void minimumFirstBig(){
        insertLarge();
        checkMin(1,10);
        assertEquals(1, pq.delMin());
    }

    @Test
    void fullInsert() {
        insertFull();
        assertFalse(pq.contains(11));
        pq.insert(11, 65);
        assertEquals(12, pq.getN());
        assertTrue(pq.contains(11));
        checkPosition(setMapFull());
        checkMin(5, 10);
    }
    @Test
    void smallInsert(){
        insertSmall();
        assertFalse(pq.contains(4));
        pq.insert(4,40);
        assertEquals(4, pq.getN());
        assertTrue(pq.contains(4));
        Map<Integer, Float> map = setMapSize(3);
        map.put(4, 40f);
        checkPosition(map);
        checkMin(1, 10);
    }
    @Test
    void smallInsertOutOfOrder(){
        insertSmall();
        assertFalse(pq.contains(5));
        pq.insert(5,50);
        assertEquals(4, pq.getN());
        assertTrue(pq.contains(5));
        Map<Integer, Float> map = setMapSize(3);
        map.put(5, 50f);
        checkPosition(map);
        checkMin(1, 10);
        assertFalse(pq.contains(4));
        pq.insert(4,40);
        assertEquals(5, pq.getN());
        assertTrue(pq.contains(4));
        map.put(4, 40f);
        checkPosition(map);
        checkMin(1, 10);
    }
    @Test
    void smallInsertMin(){
        insertSmall();
        assertFalse(pq.contains(4));
        pq.insert(4,5);
        assertEquals(4, pq.getN());
        assertTrue(pq.contains(4));
        Map<Integer, Float> map = setMapSize(3);
        map.put(4, 5f);
        checkPosition(map);
        checkMin(4, 5);
    }
    @Test
    void largeInsert(){
        insertLarge();
        assertFalse(pq.contains(8));
        pq.insert(8,80);
        assertEquals(8, pq.getN());
        assertTrue(pq.contains(8));
        Map<Integer, Float> map = setMapSize(7);
        map.put(8, 80f);
        checkPosition(map);
        checkMin(1, 10);
        assertFalse(pq.contains(9));
        pq.insert(9,75);
        assertEquals(9, pq.getN());
        assertTrue(pq.contains(9));
        map.put(9, 75f);
        checkPosition(map);
        checkMin(1, 10);
    }
    @Test
    void largeInsertOutOfOrder(){
        insertLarge();
        assertFalse(pq.contains(8));
        pq.insert(8,80);
        assertEquals(8, pq.getN());
        assertTrue(pq.contains(8));
        Map<Integer, Float> map = setMapSize(7);
        map.put(8, 80f);
        checkPosition(map);
        checkMin(1, 10);
    }
    @Test
    void largeInsertMin(){
        insertLarge();
        assertFalse(pq.contains(8));
        pq.insert(8,5);
        assertEquals(8, pq.getN());
        assertTrue(pq.contains(8));
        Map<Integer, Float> map = setMapSize(7);
        map.put(8, 5f);
        checkPosition(map);
        checkMin(8, 5);
    }


    @Test
    void exchSmall() {
        insertSmall();
        assertEquals(10, pq.keys(1));
        assertEquals(30, pq.keys(3));
        assertEquals(1, pq.edgeLocation(1));
        assertEquals(3, pq.edgeLocation(3));
        assertEquals(1, pq.index(1)-1);
        assertEquals(3, pq.index(3)-1);
        pq.exch(3,1);
        assertEquals(30, pq.keys(1));
        assertEquals(10, pq.keys(3));
        assertEquals(3, pq.edgeLocation(1));
        assertEquals(1, pq.edgeLocation(3));
        assertEquals(3, pq.index(1)-1);
        assertEquals(1, pq.index(3)-1);
    }
    @Test
    void exchLarge(){
        insertLarge();
        assertEquals(20, pq.keys(2));
        assertEquals(60, pq.keys(4));
        assertEquals(2, pq.edgeLocation(2));
        assertEquals(6, pq.edgeLocation(4));
        assertEquals(2, pq.index(2)-1);
        assertEquals(4, pq.index(6)-1);
        pq.exch(4,2);
        assertEquals(60, pq.keys(2));
        assertEquals(20, pq.keys(4));
        assertEquals(6, pq.edgeLocation(2));
        assertEquals(2, pq.edgeLocation(4));
        assertEquals(4, pq.index(2)-1);
        assertEquals(2, pq.index(6)-1);
    }
    @Test
    void exchFull(){
        insertFull();
        assertEquals(20,pq.keys(2));
        assertEquals(40,pq.keys(4));
        assertEquals(4,pq.edgeLocation(2));
        assertEquals(8, pq.edgeLocation(4));
        assertEquals(2, pq.index(4)-1);
        assertEquals(4, pq.index(8)-1);
        pq.exch(4,2);
        assertEquals(40,pq.keys(2));
        assertEquals(20,pq.keys(4));
        assertEquals(8,pq.edgeLocation(2));
        assertEquals(4, pq.edgeLocation(4));
        assertEquals(4, pq.index(4)-1);
        assertEquals(2, pq.index(8)-1);

    }
    @Test
    void locationForFull(){
        insertFull();
        checkPosition(setMapFull());
        checkMin(5, 10);
        assertEquals(10,pq.min());
        assertEquals(5, pq.edgeLocation(1));
        assertEquals(10, pq.keys(1));
    }

    @Test
    void lessSmall() {
        insertSmall();
        assertFalse(pq.less(1,3));
    }
    @Test
    void lessLarge(){
        insertLarge();
        assertFalse(pq.less(1,7));
        assertFalse(pq.less(2,4));
        assertFalse(pq.less(3, 5));

    }

    @Test
    void minSmall() {
        insertSmall();
        assertEquals(10, pq.min());
    }
    @Test
    void minLarge(){
        insertLarge();
        assertEquals(10, pq.min());
        pq.delMin();
        assertEquals(20, pq.min());
    }

    @Test
    void delMinSmall() {
        insertSmall();
        assertTrue(pq.contains(1));
        int size = pq.getN();
        pq.delMin();
        assertFalse(pq.contains(1));
        assertEquals(20, pq.min());
        assertEquals(size - 1, pq.getN());
        checkPosition(setMapSize(3));
        checkMin(2, 20);
    }
    @Test
    void delMinLarge(){
        insertLarge();
        assertTrue(pq.contains(1));
        int size = pq.getN();
        pq.delMin();
        assertFalse(pq.contains(1));
        assertEquals(20, pq.min());
        assertEquals(size - 1, pq.getN());
        checkPosition(setMapSize(7));
        checkMin(2, 20);
    }
    @Test
    void delMinFull(){
        insertFull();
        assertTrue(pq.contains(5));
        int size = pq.getN();
        pq.delMin();
        assertFalse(pq.contains(5));
        assertEquals(20, pq.min());
        assertEquals(size - 1, pq.getN());
        checkPosition(setMapFull());
        checkMin(4, 20);
    }

    @Test
    void changeSmallNoChange() {
        insertSmall();
        pq.change(1, 5);
        assertEquals(5, pq.keys(1));
        assertEquals(1, pq.edgeLocation(1));
        assertEquals(1, pq.index(1)-1);
        Map<Integer, Float> map = setMapSize(3);
        map.put(1, 5f);
        checkPosition(map);
    }
    @Test
    void changeLargeNoChange(){
        insertLarge();
        pq.change(1, 5);
        assertEquals(5, pq.keys(1));
        assertEquals(1, pq.edgeLocation(1));
        assertEquals(1, pq.index(1)-1);
        Map<Integer, Float> map = setMapSize(7);
        map.put(1, 5f);
        checkPosition(map);
    }
    @Test
    void changeFullNoChange(){
        insertFull();
        pq.change(5, 5);
        assertEquals(5, pq.keys(1));
        assertEquals(5, pq.edgeLocation(1));
        assertEquals(1, pq.index(5)-1);
        Map<Integer, Float> map = setMapFull();
        map.put(5, 5f);
        checkPosition(map);
    }
    @Test
    void changeSmallUpwards(){
        insertSmall();
        pq.change(3, 15);
        Map<Integer, Float> map = setMapSize(3);
        map.put(3, 15f);
        checkPosition(map);
        checkMin(1, 10);
    }
    @Test
    void changeLargeUpwards(){
        insertLarge();
        pq.change(5, 35);
        Map<Integer, Float> map = setMapSize(7);
        map.put(5, 35f);
        checkPosition(map);
        checkMin(1, 10);
        pq.change(7, 55);
        map.put(7, 55f);
        checkPosition(map);
        checkMin(1, 10);
    }
    @Test
    void changeFullUpwards(){
        insertFull();
        pq.change(6, 25);
        Map<Integer, Float> map = setMapFull();
        map.put(6, 25f);
        checkPosition(map);
        checkMin(5, 10);
        pq.change(7, 58);
        map.put(7, 58f);
        checkPosition(map);
        checkMin(5, 10);
    }
    @Test
    void changeSmallMin(){
        insertSmall();
        pq.change(3, 7);
        Map<Integer, Float> map = setMapSize(3);
        map.put(3, 7f);
        checkPosition(map);
        checkMin(3, 7);
        pq.change(2, 5);
        map.put(2,5f);
        checkPosition(map);
        checkMin(2,5);

    }
    @Test
    void changeLargeMin(){
        insertLarge();
        pq.change(5,7);
        Map<Integer, Float> map = setMapSize(7);
        map.put(5, 7f);
        checkPosition(map);
        checkMin(5, 7);
        pq.change(7, 5);
        map.put(7,5f);
        checkPosition(map);
        checkMin(7,5);
    }
    @Test
    void changeFullMin(){
        insertFull();
        pq.change(0, 7);
        Map<Integer, Float> map = setMapFull();
        map.put(0, 7f);
        checkPosition(map);
        checkMin(0,7);
        pq.change(7,5);
        map.put(7,5f);
        checkPosition(map);
        checkMin(7, 5);
    }
    @Test
    void changeSmallDownwardsMin(){
        insertSmall();
        pq.change(1, 25);
        Map<Integer, Float> map = setMapSize(3);
        map.put(1,25f);
        checkPosition(map);
        checkMin(2,20);
        pq.change(1,35);
        map.put(1,35f);
        checkPosition(map);
        checkMin(2,20);

    }
    @Test
    void changeLargeDownwardsMin(){
        insertLarge();
        pq.change(1, 45);
        Map<Integer, Float> map = setMapSize(7);
        map.put(1,45f);
        checkPosition(map);
        checkMin(2,20);
        pq.change(1,75);
        map.put(1,75f);
        checkPosition(map);
        checkMin(2,20);
    }
    @Test
    void changeFullDownwardsMin(){
        insertFull();
        pq.change(5, 45);
        Map<Integer, Float> map = setMapFull();
        map.put(5,45f);
        checkPosition(map);
        checkMin(4,20);
        pq.change(5,100);
        map.put(5,100f);
        checkPosition(map);
        checkMin(4,20);
    }
    @Test
    void changeSmallDownwards(){
        insertSmall();
        pq.change(2, 35);
        Map<Integer, Float> map = setMapSize(3);
        map.put(2, 35f);
        checkPosition(map);
        checkMin(1,10);

    }
    @Test
    void changeLargeDownwards(){
        insertLarge();
        pq.change(3, 46);
        Map<Integer, Float> map = setMapSize(7);
        map.put(3, 46f);
        checkPosition(map);
        checkMin(1,10);
        pq.change(5,65);
        map.put(5,65f);
        checkPosition(map);
        checkMin(1,10);
        pq.change(5,100);
        map.put(5, 100f);
        checkPosition(map);
        checkMin(1,10);

    }
    @Test
    void changeFullDownwards(){
        insertFull();
        pq.change(8,65);
        Map<Integer, Float> map = setMapFull();
        map.put(8, 65f);
        checkPosition(map);
        checkMin(5,10);
        pq.change(6, 72.4f);
        map.put(6, 72.4f);
        checkPosition(map);
        checkMin(5,10);
        pq.change(6, 100.4f);
        map.put(6, 100.4f);
        checkPosition(map);
        checkMin(5,10);
    }

    @Test
    void isEmptyTrueSmall() {
        insertSmall();
        for (int i = 0; i < 3; i++){
            pq.delMin();
        }
        assertTrue(pq.isEmpty());
    }
    @Test
    void isEmptyFalseSmall(){
        insertSmall();
        for (int i = 0; i < 2; i++){
            pq.delMin();
        }
        assertFalse(pq.isEmpty());
    }
    @Test
    void isEmptyTrueLarge(){
        insertLarge();
        for (int i = 0; i < 7; i++){
            pq.delMin();
        }
        assertTrue(pq.isEmpty());
    }
    @Test
    void isEmptyFalseLargeEdgecase(){
        insertLarge();
        for (int i = 0; i < 6; i++){
            pq.delMin();
        }
        assertFalse(pq.isEmpty());
    }
    @Test
    void isEmptyFalseLarge(){
        insertLarge();
        for (int i = 0; i < 3; i++){
            pq.delMin();
        }
        assertFalse(pq.isEmpty());
    }
    @AfterEach
    void destroy(){
        pq = new PriorityQueue(12);
    }
}