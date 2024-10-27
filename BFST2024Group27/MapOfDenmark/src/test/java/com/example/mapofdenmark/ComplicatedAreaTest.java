package com.example.mapofdenmark;

import com.example.mapofdenmark.help_class.RedBlackBSTInteger;
import com.example.mapofdenmark.help_class.WayRef;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.example.mapofdenmark.Model.id2way;
import static org.junit.jupiter.api.Assertions.*;

class ComplicatedAreaTest {
    HashMap<Integer, Node> id2node;
    ArrayList<Integer> ref;

    ComplicatedArea a_1;
    @BeforeEach
    void setUp() {
        id2node = new HashMap<>();
        //Points to area outside main area, this is made first
        Node n1_1 = new Node(55.07116f, 14.77023f);
        id2node.put(1,n1_1);
        Node n1_2 = new Node(55.07119f, 14.7702f);
        id2node.put(2,n1_2);
        Node n1_3 = new Node(55.0712f, 14.77023f);
        id2node.put(3,n1_3);

        Node n2_1 = new Node(55.07102f, 14.77019f);
        id2node.put(4,n2_1);
        Node n2_2 = new Node(55.07088f, 14.77012f);
        id2node.put(5,n2_2);
        Node n2_3 = new Node(55.0709f, 14.7699f);
        id2node.put(6,n2_3);
        Node n2_4 = new Node(55.07118f, 14.7699f);
        id2node.put(7,n2_4);
        Node n2_5 = new Node(55.07125f, 14.77012f);
        id2node.put(8,n2_5);

        //Ways are made
        Model.id2way = new RedBlackBSTInteger<>();
        Way w;
        ArrayList<Node> arrayList = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            arrayList.add(id2node.get(i));
        }
        w = new Line(arrayList,(byte)0);
        id2way.put(0,w);
        arrayList.clear();

        for (int i = 4; i <= 5; i++) {
            arrayList.add(id2node.get(i));
        }
        w = new Line(arrayList,(byte)0);
        id2way.put(1,w);
        arrayList.clear();

        for (int i = 5; i <= 6; i++) {
            arrayList.add(id2node.get(i));
        }
        w = new Line(arrayList,(byte)0);
        id2way.put(2,w);
        arrayList.clear();

        for (int i = 6; i <= 7; i++) {
            arrayList.add(id2node.get(i));
        }
        w = new Line(arrayList,(byte)0);
        id2way.put(3,w);
        arrayList.clear();

        for (int i = 7; i <= 8; i++) {
            arrayList.add(id2node.get(i));
        }
        w = new Line(arrayList,(byte)0);
        id2way.put(4,w);
        arrayList.clear();


        arrayList.add(id2node.get(8));
        arrayList.add(id2node.get(1));
        w = new Line(arrayList,(byte)0);
        id2way.put(5,w);
        arrayList.clear();

        //Relations
        ref = new ArrayList<>();

        ref.add(0);

        ref.add(1);
        ref.add(2);
        ref.add(3);
        ref.add(4);
        ref.add(5);


        byte b = 2;
        a_1 = new ComplicatedArea(ref, b);
    }

    @Test
    void createGroups1(){ //Basic version
        ArrayList<WayRef> array = new ArrayList<>();

        for (int i = 0; i < ref.size(); i++) {
            if(id2way.containsKey(ref.get(i))) {
                array.add(new WayRef(ref.get(i)));
            }
        }

        WayRef[][] compared = new WayRef[2][];

        compared[0] = new WayRef[1];
        compared[0][0] = new WayRef(0);

        compared[1] = new WayRef[5];
        compared[1][0] = new WayRef(1);
        compared[1][1] = new WayRef(2);
        compared[1][2] = new WayRef(3);
        compared[1][3] = new WayRef(4);
        compared[1][4] = new WayRef(5);

        WayRef[][] result = a_1.createGroups(array);

        for (int i = 0; i < compared.length; i++) {
            for (int j = 0; j < compared[i].length; j++) {
                assertEquals(result[i][j].getReference(), compared[i][j].getReference());
            }
        }
    }

    @Test
    void createGroups2(){ //In case where the order is wrong, but start and end are the same.
        ArrayList<WayRef> array = new ArrayList<>();

        for (int i = 0; i < ref.size(); i++) {
            if(id2way.containsKey(ref.get(i))) {
                array.add(new WayRef(ref.get(i)));
            }
        }

        WayRef[][] compared = new WayRef[2][];

        compared[0] = new WayRef[1];
        compared[0][0] = new WayRef(0);

        compared[1] = new WayRef[5];
        compared[1][0] = new WayRef(1);
        compared[1][1] = new WayRef(4);
        compared[1][2] = new WayRef(2);
        compared[1][3] = new WayRef(3);
        compared[1][4] = new WayRef(5);

        WayRef[][] result = a_1.createGroups(array);

        for (int i = 0; i < compared.length; i++) {
            for (int j = 0; j < compared[i].length; j++) {
                assertEquals(result[i][j].getReference(), compared[i][j].getReference());
            }
        }
    }

    @Test
    void createGroups3(){ //In case where the order is wrong, and the last element is wrong
        ArrayList<WayRef> array = new ArrayList<>();

        for (int i = 0; i < ref.size(); i++) {
            if(id2way.containsKey(ref.get(i))) {
                array.add(new WayRef(ref.get(i)));
            }
        }

        WayRef[][] compared = new WayRef[2][];

        compared[0] = new WayRef[1];
        compared[0][0] = new WayRef(0);

        compared[1] = new WayRef[5];
        compared[1][0] = new WayRef(1);
        compared[1][1] = new WayRef(5);
        compared[1][2] = new WayRef(2);
        compared[1][3] = new WayRef(4);
        compared[1][4] = new WayRef(3);

        WayRef[][] result = a_1.createGroups(array);

        for (int i = 0; i < compared.length; i++) {
            for (int j = 0; j < compared[i].length; j++) {
                assertEquals(result[i][j].getReference(), compared[i][j].getReference());
            }
        }
    }
}