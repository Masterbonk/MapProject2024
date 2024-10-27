package com.example.mapofdenmark;

import com.example.mapofdenmark.PathfindingPackage.EdgeWeightedDigraph;
import com.example.mapofdenmark.ST.*;
import com.example.mapofdenmark.help_class.ColorLoader;
import com.example.mapofdenmark.help_class.RedBlackBSTInteger;
import com.example.mapofdenmark.help_class.RedBlackBSTLong;
import javafx.scene.paint.Color;


import javax.xml.stream.*;
import java.io.*;
import java.util.*;
import java.util.zip.ZipInputStream;

import static com.example.mapofdenmark.help_class.ColorLoader.color_area_converter;
import static com.example.mapofdenmark.help_class.ColorLoader.color_line_converter;

public class Model implements Serializable {
    int M = 50;
    /**
     * Walkways are paths like forest paths and stuff.
     */
    RTree Walkways = new RTree(M);
    ArrayList<DrawnObject> WalkwaysList = new ArrayList<>();

    /**
     * City buildings
     */
    RTree Buildings = new RTree(M);
    ArrayList<DrawnObject> BuildingsList = new ArrayList<>();

    /**
     * Big roads like primary and motorways
     */
    RTree PrimaryRoads = new RTree(M);
    ArrayList<DrawnObject> PrimaryRoadsList = new ArrayList<>();

    /**
     * Secondary roads, tertiary roads, ie. anything smaller than a primary and motorway
     */
    RTree SecondaryRoads = new RTree(M);
    ArrayList<DrawnObject> SecondaryRoadsList = new ArrayList<>();

    /**
     * Any water area, whether natural or otherwise
     */
    RTree Water = new RTree(M);
    ArrayList<DrawnObject> WaterList = new ArrayList<>();

    /**
     * Forest and other base like areas, also contains the base of cities.
     */
    RTree Forest = new RTree(M);
    ArrayList<DrawnObject> ForestList = new ArrayList<>();

    /**
     * Forest and other base like areas, also contains the base of cities.
     */
    RTree Structure = new RTree(M);
    ArrayList<DrawnObject> StructureList = new ArrayList<>();

    /**
     * Mostly relations like Bornholm, ie. something always drawn and very big.
     */
    RTree Islands = new RTree(M);
    ArrayList<DrawnObject> IslandsList = new ArrayList<>();

    /**
     * The extra lines, not put into the other categories
     */
    RTree Lines = new RTree(M);
    ArrayList<DrawnObject> LinesList = new ArrayList<>();

    //used to make the vertices into kd tree

    //Turner search tree
    public TST tst = new TST();
    EdgeWeightedDigraph graph;
    public static RedBlackBSTInteger<Way> id2way = new RedBlackBSTInteger<>();

    float minlat, maxlat, minlon, maxlon;
    public static List<Integer> index = new ArrayList<>();
    public static int indexCounter = 1;
    String fileName;

    /**
     * Checks if the file is a .obj file, if not, will make a model.
     *
     * @param filename The name of the file
     * @return Returns either a finished model using a .obj file or makes a new model.
     */
    static Model load(String filename) throws IOException, ClassNotFoundException, XMLStreamException, FactoryConfigurationError {
        if (filename.endsWith(".obj")) {
            try (var in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(filename)))) {
                var model = (Model) in.readObject();
                id2way = (RedBlackBSTInteger<Way>) in.readObject();
                index = (List<Integer>) in.readObject();


                return model;

            }
        }
        return new Model(filename);
    }

    /**
     * Calls different parsers depending on the type of file the file name is referencing. <br>
     * parseZIP() if .osm.zip <br>
     * parseOSM() if .osm <br>
     * parseTXT() if none of the others <br> <br>
     * Then calls save() on the model these parsers create.
     *
     * @param filename The name of the file, we wish to read.
     */
    public Model(String filename) throws XMLStreamException, FactoryConfigurationError, IOException {
        if (filename.endsWith(".osm.zip")) {
            parseZIP(filename);
        } else if (filename.endsWith(".osm")) {
            parseOSM(filename);
        }
        graph = new EdgeWeightedDigraph(indexCounter - 1);//this is supposed to be amount of intersections

        //adding edges/vertices to graph
        for (int way : id2way.values()) {
            id2way.get(way).addingEdgesToGraph(graph, way);

            //next step: make sure you think about direction of edge and cycle/walk or not
        }


        fileName = filename;
    }

    /**
     * Makes a new file, with all the java memory structure and objects recorded, allowing us to quickly load a lot of files.
     *
     * @param filename Uses the file name as a base for the new files name.
     */
    void save(String filename) throws IOException {
        try (var out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(this);
            out.writeObject(id2way);
            out.writeObject(index);


        }
    }

    /**
     * Unpacks the zip file, then uses parseOSM() on the file.
     *
     * @param filename The name of the file
     */
    private void parseZIP(String filename) throws IOException, XMLStreamException, FactoryConfigurationError {
        var input = new ZipInputStream(new FileInputStream(filename));
        input.getNextEntry();
        parseOSM(input);
    }

    /**
     * If the parseOSM gets a string, this function is called. Creates a FileInpuStream using the file name, then uses that to call the other parseOSM() function
     *
     * @param filename The string data of the file name, that we wish to parse
     */
    private void parseOSM(String filename) throws FileNotFoundException, XMLStreamException, FactoryConfigurationError {
        parseOSM(new FileInputStream(filename));
    }

    /**
     * Takes a .osm file, then reads through it, taking note of different tagkinds line nodes, ways and bounds.
     * <br>     <br>
     * Start of tags:
     * <br> bounds - Gives the min-max latitude and longitude of the osm file
     * <br> node - records the node id for later use.
     * <br> way - resets the necessary variables, and begins looking for tags and nd's that is part of it
     * <br> tag - a key and value combo, determines something about whatever node, way or relation was read before this.
     * <br> nd - Adds the node with the corresponding id to a list of nodes, that a way gets later
     * <br>
     * <br>
     * End of tags:
     * <br> way - All nd elements is added to the way, which is constructed here.
     *
     * @param inputStream The inputStream is a direct connection to the file we wish to read, is put into a lot of other readers to be usable
     */
    private void parseOSM(InputStream inputStream) throws XMLStreamException, FactoryConfigurationError {
        // We believe the XMLStreamReader is a part of the Streaming API for XML (StAX) framework
        // docs: https://docs.oracle.com/cd/E13222_01/wls/docs92/xml/stax.html
        var input = XMLInputFactory.newInstance().createXMLStreamReader(new BufferedInputStream(inputStream));
        RedBlackBSTLong<Node> id2node = new RedBlackBSTLong<>();
        var way = new ArrayList<Node>();
        Node nodes = new Node(0, 0);
        var wayId = 0;
        long nodeId = 0L;

        //Way, line + area
        byte typeLine = 0;
        boolean area = false;
        byte typeArea = -1;
        /*if condition%2==0, then cars can drive on it
          if condition%3==0, then bikes can bike on it
          if condition%5==0, then you can walk on it
         */
        byte condition = 1;//condition starts as 1 because of multiplication
        /*
           if direction%2==0, then cars can only go in one direction
           if direction%3==0, then bikes can only go in one direction
           if direction%5==0, then bikes can only go in the opposite direction
           if direction is negative, then the edge is a one way edge, but in the opposite direction as drawn
         */
        byte direction = 1;//direction starts as 1 because of multiplication

        short speed = 50;
        boolean illegal_tag_detected = false;

        //Relation, complicated area
        var relation = new ArrayList<Integer>();

        //Addresses
        String city = "";
        String housenumber = "";
        String postcode = "";
        String street = "";
        String objectName = "";

        while (input.hasNext()) {
            var tagKind = input.next();
            if (tagKind == XMLStreamConstants.START_ELEMENT) {
                var name = input.getLocalName();
                switch (name) {
                    case "bounds" -> {
                        minlat = Float.parseFloat(input.getAttributeValue(null, "minlat"));
                        maxlat = Float.parseFloat(input.getAttributeValue(null, "maxlat"));
                        minlon = Float.parseFloat(input.getAttributeValue(null, "minlon"));
                        maxlon = Float.parseFloat(input.getAttributeValue(null, "maxlon"));
                    }
                    case "node" -> {
                        nodeId = Long.parseLong(input.getAttributeValue(null, "id"));
                        var lat = Float.parseFloat(input.getAttributeValue(null, "lat"));
                        var lon = Float.parseFloat(input.getAttributeValue(null, "lon"));
                        nodes = new Node(lat, lon);
                        id2node.put(nodeId, nodes);
                    }
                    case "way" -> {
                        way.clear();
                        typeLine = 0;
                        area = false;
                        typeArea = -1;
                        direction = 1;
                        condition = 1;
                        speed = 0;
                        illegal_tag_detected = false;
                        wayId = Integer.parseInt(input.getAttributeValue(null, "id"));
                    }
                    case "relation" -> {

                        relation.clear();
                        area = false;
                        typeArea = 0;
                        illegal_tag_detected = false;
                    }
                    case "member" -> {
                        var type = input.getAttributeValue(null, "type"); //The key
                        var ref = input.getAttributeValue(null, "ref");
                        if (type.equals("way")) {
                            relation.add(Integer.parseInt(ref)); //both the refrence and the type shares the same index
                        }
                    }
                    case "tag" -> {
                        var k = input.getAttributeValue(null, "k"); //The key
                        var v = input.getAttributeValue(null, "v"); //The value
                        //Inputs different parts of ad dresses into the ternary search tree with the values 1 representing a city 2 representing a street 3 representing a municipality and 4 representing a full result
                        switch (k) {
                            case "addr:city" -> city = new String(v.getBytes());
                            case "addr:housenumber" -> housenumber = new String(v.getBytes());
                            case "addr:postcode" -> postcode = new String(v.getBytes());
                            case "addr:street" -> street = new String(v.getBytes());
                            case "name" -> objectName = new String(v.getBytes()).toLowerCase();
                            case "tourism" -> {
                                if (nodeId > 0 && !objectName.isEmpty()) {
                                    tst.put(objectName, nodes.lat, nodes.lon);
                                }
                            }

                            case "oneway" -> { //determines if a way is a oneway street or not
                                if (((v.equals("yes")) || v.equals("roundabout")) || v.equals("motorway")) {
                                    if (direction % 2 != 0) {
                                        direction *= 2;
                                    }
                                    if (direction % 3 != 0) {
                                        direction *= 3;
                                    }
                                } else if (v.equals("-1") && direction > 0) {
                                    direction *= -1;
                                } else if (v.equals("no")) {
                                    if (direction % 2 == 0) {
                                        direction /= 2;
                                    }
                                    if (direction % 3 == 0) {
                                        direction /= 3;
                                    }
                                }
                            }
                            case "oneway:bicycle" -> {
                                if (v.equals("no") && direction % 3 == 0) {
                                    direction /= 3;
                                } else if (v.equals("yes") && direction % 3 != 0) {
                                    direction *= 3;
                                } else if (v.equals("-1") && direction > 0) {
                                    direction *= -1;
                                }
                            }
                            case "footway" -> {
                                if (v.equals("crossing")) {
                                    if (condition % 5 != 0) {
                                        condition *= 5;
                                    } else if (condition % 3 != 0) {
                                        condition *= 3;
                                    }
                                    typeLine = 6;
                                } else if (v.equals("no") && condition % 5 == 0) {
                                    condition /= 5;
                                } else if (condition % 5 != 0) {
                                    condition *= 5;
                                    typeLine = 6;//walk
                                }
                            }
                            case "steps" -> {//cannot bike on stairs
                                if (condition % 3 == 0) {
                                    condition /= 3;
                                }
                            }
                            case "crossing" -> {
                                if (!v.equals("no")) {
                                    if (condition % 5 != 0) {
                                        condition *= 5;
                                    }
                                    if (condition % 3 != 0) {
                                        condition *= 3;
                                    }
                                    typeLine = 6;
                                }

                            }
                            case "foot" -> {
                                if (!(v.equals("no") || v.equals("private") || v.equals("destination")) && condition % 5 != 0) {
                                    condition *= 5;
                                    typeLine = 6;
                                } else if (condition % 5 == 0) {
                                    condition /= 5;
                                }
                            }
                            case "junction" -> {
                                if (direction % 2 != 0) {
                                    direction *= 2;
                                }
                                if (direction % 3 != 0) {
                                    direction *= 3;
                                }
                            }
                            case "bicycle" -> {
                                if ((v.equals("no") || v.equals("dismount")) && condition % 3 == 0) {
                                    condition /= 3;
                                } else if (condition % 3 != 0) {
                                    condition *= 3;
                                }

                            }
                            case "noexit" -> {//pretend it is a oneway street
                                if (v.equals("yes") && direction % 2 != 0) {
                                    if (direction % 2 != 0) {
                                        direction *= 2;
                                    } else if (direction % 3 != 0) {
                                        direction *= 3;
                                    }
                                } else if (v.equals("no")) {
                                    if (direction % 2 == 0) {
                                        direction /= 2;
                                    } else if (direction % 3 == 0) {
                                        direction /= 3;
                                    }
                                }

                            }
                            case "cycleway" -> {
                                if (v.equals("opposite_lane") && direction % 5 != 0) {
                                    direction *= 5;
                                }
                                typeLine = 1;
                            }
                            case "cycleway:left:oneway" -> {
                                if ((v.equals("opposite_lane")) && direction < 0) {
                                    direction *= -1;
                                } else if (v.equals("yes")) {
                                    if (direction % 3 != 0) {
                                        direction *= 3;
                                    }
                                    if (direction > 0) {
                                        direction *= -1;
                                    }
                                }

                            }
                            case "cycleway:right:oneway" -> {
                                if (v.equals("opposite_lane") && direction > 0) {
                                    direction *= -1;
                                } else if (v.equals("no") && direction > 0) {
                                    direction *= -1;
                                } else if (v.equals("yes")) {
                                    if (direction % 3 != 0) {
                                        direction *= 3;
                                    }
                                    if (direction < 0) {
                                        direction *= -1;
                                    }
                                }

                            }
                            case "vehicle" -> {
                                if ((v.equals("yes") || v.equals("permissive"))) {
                                    if (condition % 2 != 0) {
                                        condition *= 2;
                                    } else if (condition % 3 != 0) {
                                        condition *= 3;
                                    }
                                } else {
                                    if (condition % 2 == 0) {
                                        condition /= 2;
                                    }
                                    if (condition % 3 == 0) {
                                        condition /= 3;
                                    }
                                }

                            }
                            case "motorcar" -> {
                                if ((v.equals("yes") || v.equals("permissive") || v.equals("designated")) && condition % 2 != 0) {
                                    condition *= 2;
                                } else if (condition % 2 == 0) {
                                    condition /= 2;
                                }
                            }
                            case "service" -> {
                                if ((v.equals("slipway") || v.equals("emergency_access")) && condition % 2 == 0) {
                                    condition /= 2;
                                }
                            }
                            case "area", "building" -> {
                                typeArea = -1;
                                area = true;
                            }
                            case "waterway" -> typeLine = 10;
                            case "landuse" -> {
                                if (!v.equals("industrial") && !v.equals("military") && !v.equals("port")) {
                                    area = true;
                                } else {
                                    illegal_tag_detected = true;
                                }
                            }
                            case "boundary", "military" -> illegal_tag_detected = true;
                            case "place" -> {
                                if (v.equals("island") || v.equals("islet") || v.equals("archipelago")) {
                                    area = true;
                                } else {
                                    illegal_tag_detected = true;
                                }
                            }
                            case "natural" -> {
                                if (!v.equals("coastline") && !v.equals("earth_bank") && !v.equals("strait")) {
                                    area = true;
                                }
                            }
                            case "maxspeed" -> {
                                try {
                                    speed = Short.parseShort(v);
                                } catch (Exception ignored) {
                                    speed = typeSpeed(v);
                                }
                            }
                            case "maxspeed:advisory" -> {
                                try {
                                    if (v.equals("303")) speed = 30;
                                    else if (speed == 0) speed = Short.parseShort(v);
                                } catch (Exception ignored) {
                                    speed = typeSpeed(v);
                                }
                            }
                            case "maxspeed:backward", "maxspeed:forward", "maxspeed:practical", "maxspeed:practical:backward", "maxspeed:practical:forward", "maxspeed:type" -> {
                                try {
                                    if (speed == 0) speed = Short.parseShort(v);
                                } catch (Exception ignored) {
                                    speed = typeSpeed(v);
                                }
                            }
                        }
                        byte temp = Converter(typeLine, k, v);
                        if (typeLine < temp) {
                            typeLine = temp;
                        }
                        temp = ConverterArea(typeArea, k, v);
                        if (typeArea < temp && area) {
                            typeArea = temp;
                        }
                        condition = ConverterCondition(condition, typeLine);//mode of transport is determined/updated
                    }
                    case "nd" -> {
                        var ref = Long.parseLong(input.getAttributeValue(null, "ref"));
                        var node = id2node.get(ref);
                        way.add(node);
                    }
                }
            } else if (tagKind == XMLStreamConstants.END_ELEMENT) {
                var name = input.getLocalName();
                switch (name) {
                    case "node" -> {
                        if (!city.isEmpty() || !street.isEmpty()) {
                            tst.put((street + " " + housenumber + " " + city + " " + postcode).toLowerCase(), nodes.lat, nodes.lon);
                            city = "";
                            housenumber = "";
                            postcode = "";
                            street = "";
                        }
                        nodeId = 0L;
                        objectName = "";
                    }
                    case "way" -> {
                        if (!area) {
                            if (typeLine == -1) {
                                typeLine = 0;
                            }
                            if (speed == 0) {
                                speed = implicitSpeed(typeLine);
                            }
                            Line created;
                            if (condition != 1) {//if you can walk, drive or bike on it
                                created = new Pathway(way, typeLine, direction, condition, speed);
                            } else {
                                created = new Line(way, typeLine);
                            }
                            id2way.put(wayId, created);
                            if (typeLine != 8) {//corridors inside should not be drawn
                                inputIntoDrawnArea(created, typeLine, false);
                            }
                            if (!objectName.isEmpty()) {
                                Node center = wayCenter(created);
                                tst.put(objectName, center.lat, center.lon);
                            }
                        } else if (typeArea != -1 && !illegal_tag_detected) {
                            Area created = new Area(way, typeArea);
                            id2way.put(wayId, created);
                            inputIntoDrawnArea(created, typeArea, true);
                            if (!objectName.isEmpty()) {
                                Node center = wayCenter(created);
                                tst.put(objectName, center.lat, center.lon);
                            }
                        }
                        //makes sure the start and the end point counts as an intersection so edges are created correctly

                        wayId = 0;
                        objectName = "";
                    }
                    case "relation" -> {
                        id2node = null;
                        if (area && typeArea != -1 && !relation.isEmpty() && !illegal_tag_detected) {
                            var rel = new ComplicatedArea(relation, typeArea);
                            inputIntoDrawnArea(rel, typeArea, true);
                        }
                        objectName = "";
                    }
                }
            }
        }
        ConstructTrees();
    }

    private void ConstructTrees() {
        if (!BuildingsList.isEmpty()) Buildings = new RTree(M, BuildingsList);
        if (!ForestList.isEmpty()) Forest = new RTree(M, ForestList);
        if (!StructureList.isEmpty()) Structure = new RTree(M, StructureList);
        if (!WaterList.isEmpty()) Water = new RTree(M, WaterList);
        if (!IslandsList.isEmpty()) Islands = new RTree(M, IslandsList);
        if (!PrimaryRoadsList.isEmpty()) PrimaryRoads = new RTree(M, PrimaryRoadsList);
        if (!SecondaryRoadsList.isEmpty()) SecondaryRoads = new RTree(M, SecondaryRoadsList);
        if (!WalkwaysList.isEmpty()) Walkways = new RTree(M, WalkwaysList);
        if (!LinesList.isEmpty()) Lines = new RTree(M, LinesList);

        //analyzeTrees(ForestList);

        BuildingsList = null;
        ForestList = null;
        StructureList = null;
        WaterList = null;
        IslandsList = null;
        PrimaryRoadsList = null;
        SecondaryRoadsList = null;
        WalkwaysList = null;
        LinesList = null;
    }

    public void analyzeTrees(ArrayList<DrawnObject> list) {
        /*
        RTree r1 = new RTree(30);
        RTree r2 = new RTree(50);

        for (DrawnObject d : list) {
            r1.insert(d);
            r2.insert(d);
        }

        float[] q3 = new float[]{8.444f, 8.46f, -55.132f, -55.119f};

        ArrayList<DrawnObject> result1 = r1.query(q3[0], q3[1], q3[2], q3[3]);
        ArrayList<DrawnObject> result2 = r2.query(q3[0], q3[1], q3[2], q3[3]);

        System.out.println(result1.size());
        System.out.println(result2.size());

        for (DrawnObject d : result2) {
            if (!result1.contains(d)) {
                ArrayList<RNode> leaves = new ArrayList<>();
                getLeaves(r1.getRoot(), leaves);

                boolean contained = false;
                for (RNode n : leaves) {
                    if (n.values.contains(d)) {
                        contained = true;
                        break;
                    }
                }
                if (contained) {
                    System.out.println("Contained");
                } else {
                    System.out.println("Not contained");
                }
            }
        }*/


        System.out.println("Number of values: " + list.size());

        //insertion
        RTree tree = new RTree(2);
        for (DrawnObject d : list) {
            tree.insert(d);
        }
        printInfo(tree, "Dynamically inserted - M=2");

        tree = new RTree(5);
        for (DrawnObject d : list) {
            tree.insert(d);
        }
        printInfo(tree, "Dynamically inserted - M=5");

        for (int i = 1; i < 21; i++) {
            tree = new RTree(i * 10);
            for (DrawnObject d : list) {
                tree.insert(d);
            }
            printInfo(tree, "Dynamically inserted - M=" + (i * 10));
        }
        for (int i = 3; i < 21; i++) {
            tree = new RTree(i * 100);
            for (DrawnObject d : list) {
                tree.insert(d);
            }
            printInfo(tree, "Dynamically inserted - M=" + (i * 100));
        }

        printInfo(new RTree(2, list), "Bulk-loaded - M=2");
        printInfo(new RTree(5, list), "Bulk-loaded - M=5");
        for (int i = 1; i < 21; i++) {
            printInfo(new RTree(i * 10, list), "Bulk-loaded - M=" + (i * 10));
        }
        for (int i = 3; i < 21; i++) {
            printInfo(new RTree(i * 100, list), "Bulk-loaded - M=" + (i * 100));
        }

    }

    private void printInfo(RTree tree, String s) {
        float[] q0 = new float[]{8.18f, 8.55f, -55.36f, -54.95f};
        float[] q1 = new float[]{8.25f, 8.345f, -55.195f, -55.1f};
        float[] q2 = new float[]{8.419f, 8.446f, -55.08f, -55.055f};
        float[] q3 = new float[]{8.444f, 8.46f, -55.132f, -55.119f};

        int numberOfNodes = 1 + countDescendantNodes(tree.getRoot());
        int numberOfLeaves = countLeaves(tree.getRoot());
        double leavesArea = getLeavesArea(tree.getRoot());
        double totalBoundArea = getTotalBoundArea(tree.getRoot(), false);
        ArrayList<RNode> leaves = new ArrayList<>();
        getLeaves(tree.getRoot(), leaves);

        System.out.println();
        System.out.println(s);
        System.out.println("Number of leaves: " + numberOfLeaves);
        System.out.println("Area of leaves' MBRs: " + leavesArea);
        System.out.println("Number of nodes: " + numberOfNodes);
        System.out.println("Total MBR area: " + totalBoundArea);
        System.out.println("Rectangles examined in q0: " + getRectanglesExamined(tree, q0));
        System.out.println("Rectangles examined in q1: " + getRectanglesExamined(tree, q1));
        System.out.println("Rectangles examined in q2: " + getRectanglesExamined(tree, q2));
        System.out.println("Rectangles examined in q3: " + getRectanglesExamined(tree, q3));
    }

    private int countDescendantNodes(RNode n) {
        int sum = n.children.size();

        for (RNode c : n.children) {
            sum += countDescendantNodes(c);
        }

        return sum;
    }

    private int countLeaves(RNode n) {
        int sum = 0;

        if (n.isLeaf()) return 1;

        for (RNode c : n.children) {
            sum += countLeaves(c);
        }

        return sum;
    }

    private void getLeaves(RNode n, ArrayList<RNode> leaves) {
        if (n.isLeaf()) leaves.add(n);

        for (RNode c : n.children) {
            getLeaves(c, leaves);
        }
    }

    private double getLeavesArea(RNode n) {
        double sum = 0d;

        if (n.isLeaf()) return (n.maxX - n.minX) * (n.maxY - n.minY);

        for (RNode c : n.children) {
            sum += getLeavesArea(c);
        }

        return sum;
    }

    private double getTotalBoundArea(RNode n, boolean count) {
        double sum = 0d;
        if (count) {
            sum = (n.maxX - n.minX) * (n.maxY - n.minY);
        }

        if (!n.isLeaf()) {
            for (RNode c : n.children) {
                sum += getTotalBoundArea(c, true);
            }
        }

        return sum;
    }

    private int getRectanglesExamined(RTree tree, float[] q) {
        System.out.println(tree.query(q[0], q[1], q[2], q[3]).size());
        return tree.rectanglesExamined;
    }

    private void inputIntoDrawnArea(DrawnObject object, byte type, boolean area) {
        if (area) {
            switch (type) {
                case 11 -> BuildingsList.add(object); //Buildings.insert(object);
                case 1, 6, 8, 9, 12, 13, 15 -> ForestList.add(object); //Forest.insert(object);
                case 2, 3, 4, 5, 7 -> StructureList.add(object); //Structure.insert(object);
                case 10, 14 -> WaterList.add(object); //Water.insert(object);
                default ->
                        IslandsList.add(object);//Islands.insert(object); //This is the background, and is basically just type 0
            }
        } else { //This is ways
            switch (type) {
                case 1, 4, 5 -> SecondaryRoadsList.add(object); //SecondaryRoads.insert(object);
                case 2, 3, 7 -> PrimaryRoadsList.add(object); //PrimaryRoads.insert(object);
                case 6, 9 -> WalkwaysList.add(object); //Walkways.insert(object);
                default ->
                        LinesList.add(object); //Lines.insert(object);//The base lines, not accounted for by the other categories.
            }
        }
    }

    /**
     * Converts the integers category and type into an array of integers, adding values depending on the key and value given
     *
     * @param type  The integer type that is given a specific value, depending on the value
     * @param key   The key that determines what category it falls into
     * @param value The value dependent of the key
     */
    private byte Converter(byte type, String key, String value) {//use this to set a maxspeed
        if (key.equals("highway")) {
            switch (value) {
                case "cycleway" -> type = 1;
                case "motorway" -> type = 2;
                case "primary", "motorway_link", "trunk_link", "motorway_junction" -> type = 3;
                case "secondary", "secondary_link", "primary_link" -> type = 4;
                case "residential", "yes", "tertiary", "service", "unclassified", "tertiary_link" -> type = 5;
                case "path", "footway", "bridleway", "steps", "sidewalk", "track" -> type = 6;
                case "trunk" -> type = 7;
                case "living_street" -> type = 0;
                case "corridor" -> type = 8;
                case "crossing" -> type = 9;//different color
            }
        }
        return type;
    }

    /**
     * converts tag into a number that represents the type of area it is
     *
     * @param typeArea
     * @param k        key
     * @param v        value
     * @return a number that represents the area
     */
    private byte ConverterArea(byte typeArea, String k, String v) {
        switch (k) {
            case "landuse" -> {
                switch (v) {
                    case "grass", "recreation_ground", "village_green" -> typeArea = 1;
                    case "construction" -> typeArea = 2;
                    case "retail", "commercial" -> typeArea = 3;
                    case "industrial" -> typeArea = 4;
                    case "allotments" -> typeArea = 5;
                    case "farmland" -> typeArea = 6;
                    case "farmyard" -> typeArea = 7;
                    case "forest" -> typeArea = 8;
                    case "flowerbed", "meadow", "orchard", "plant_nursery", "vineyard" ->
                            typeArea = 9; //General green stuff
                    case "basin", "salt_pond" -> typeArea = 10; //General water stuff
                }
            }
            case "building" -> typeArea = 11;
            case "natural" -> {
                switch (v) {
                    case "grassland", "scrub" -> typeArea = 12; //Grass like area
                    case "wood" -> typeArea = 13; //Forest area
                    case "bay", "water", "wetland" -> typeArea = 14; //Water area
                    case "sand", "scree", "beach" -> typeArea = 15; //sand
                }
            }
            default -> typeArea = 0; //The base color, things like land
        }
        return typeArea;
    }

    /**
     * Generates a suitable number that can be used later on to determine which modes of transport can traverse over a way
     *
     * @param condition the number that will be changed
     * @param type      type of line
     * @return an updated number where a suitable prime factor is multiplied with the condition number
     */
    private byte ConverterCondition(byte condition, int type) {
        //2=car, 3=bike, 5=walk
        //cycleway:right

        if (((type > 3 && type < 6) || type == 1 || type == 8) && condition % 3 != 0) {
            condition *= 3;//bike
        }
        if (((type > 1 && type < 6) || type == 7 || type == 0) && condition % 2 != 0) {
            condition *= 2;//drive
        }
        if ((((type > 3 && type < 10 && type != 7) || type == 0)) && condition % 5 != 0) {
            condition *= 5;//walk
        }
        return condition;
    }

    /**
     * If a max speed isn't given, then one is assumed based on the type of way
     *
     * @param type the type of way
     * @return
     */
    private short implicitSpeed(short type) {
        return switch (type) {
            case 1 -> 30; //Cycleway
            case 2 -> 130; //Motor way
            case 3 -> 80; //Primary
            case 4, 5 -> 50; //Secondary,  tetirary
            case 6, 8 -> 5; //walking path thingy
            case 7 -> 90; //Trunk
            case 0 -> 15; //Living street
            default -> 30;
        };
    }

    /**
     * converts a value from maxspeed into a number
     *
     * @param v
     * @return max speed as a number
     */
    private short typeSpeed(String v) {
        return switch (v) {
            case "DK:urban", "EU:urban" -> 50;
            case "DK:rural", "DE:rural" -> 80;
            case "signals", "DK:zone40", "EU:zone40" -> 40;
            case "none" -> 250;
            case "EU:zone10" -> 10;
            case "DK:zone20", "EU:zone20" -> 20;
            case "DK:zone30", "EU:zone30" -> 30;
            default -> 0;
        };
    }

    private Node wayCenter(Way way) {
        var wayLength = (way.coords.length - 1) / 2;
        if (wayLength % 2 != 0) wayLength--;
        float lon = way.coords[wayLength + 1];
        float lat = way.coords[wayLength];
        return new Node(lat, lon);
    }

}

