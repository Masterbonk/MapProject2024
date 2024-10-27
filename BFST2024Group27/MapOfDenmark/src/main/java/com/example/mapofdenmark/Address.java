package com.example.mapofdenmark;

import javafx.scene.control.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class Address implements Serializable {
    /**
     * Returns a list of autocompleted searches
     * @param s The string to search for
     * @param tst The tree to search in
     * @return a list of max length 6 with autocompleted searches
     */
    public static List<String> autocomplete(String s, TST tst) {
        return tst.autocomplete(s.toLowerCase(),6);
    }
    private final static String REGEX = "(?<names>[0-9A-Za-zÆØÅæøåüöäëÜÖÄË,'() ]{1,3}[0-9A-Za-zÆØÅæøåüöäëÜÖÄË]* ?[0-9A-Za-zÆØÅæøåüöäëÜÖÄË]{0,2})( |\\Z)";
    private final static Pattern PATTERN = Pattern.compile(REGEX, Pattern.MULTILINE);

    /**
     * Returns a vertex from the given TST.
     * If there is not a direct match in the search tree, attempt to find and give the user options to choose from
     * @param input The search input
     * @param tst The given search tree
     * @return returns a TST.Vertex node containing lat lon coordinates for the given spot
     */
    public static TST.Vertex parse(String input, TST tst) {
        var x = tst.get(input);
        if(x == null || x.lat == 0.0) {
            var matcher = PATTERN.matcher(input);
            List<String> matches = new ArrayList<>();
            Set<String> found = new HashSet<>();
            while (matcher.find()) {
                matches.add(matcher.group("names"));
            }
            for (int i = 0 ; i < matches.size() ; i++) {
                StringBuilder s = new StringBuilder(matches.get(i));
                var y = tst.get(s.toString());
                if (y != null && y.lat != 0.0) found.add(s.toString());
                for (int j = i+1 ; j < matches.size() ; j++) {
                    s.append(" ").append(matches.get(j));
                    y = tst.get(s.toString());
                    if (y!= null && y.lat != 0.0) found.add(s.toString());
                    }
                }
            for (int i = matches.size()-1 ; 0 <= i ; i--) {
                StringBuilder s = new StringBuilder(matches.get(i));
                var y = tst.get(s.toString());
                if (y != null && y.lat != 0.0) found.add(s.toString());
                for (int j = i-1 ; 0 <= j ; j--) {
                    s.append(" ").append(matches.get(j));
                    y = tst.get(s.toString());
                    if (y!= null && y.lat != 0.0) found.add(s.toString());
                }
            }
            for (String item : matches) {
                found.addAll(tst.autocomplete(item,5));
            }

            if (!found.isEmpty()) {
                ChoiceDialog<String> cd = new ChoiceDialog<>("Select an option",found);
                cd.setHeaderText("Your input didn't match any addresses please select one of the suggestions below");
                cd.setContentText("Select your address here");
                cd.showAndWait();
                return tst.get(cd.getSelectedItem());
            }
            return null;
        }
        return x;
    }


}
