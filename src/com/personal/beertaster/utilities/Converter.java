package com.personal.beertaster.utilities;

import com.personal.beertaster.elements.Beer;
import com.personal.beertaster.elements.Brewery;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class Converter {
    public static final String ENCODING = "UTF-8";

    public static ArrayList<Brewery> readCSV() {

        String NEW_LINE = System.lineSeparator();
        String VAR_SERPARATOR = ",";

        HashMap<Integer, Brewery> breweryMap = new HashMap<>();


        //Read breweries CSV
        StringBuilder sb = getFileData("resources/breweries.csv");

        int INDEX_ID = 0, INDEX_NAME = 1, INDEX_CITY = 4, INDEX_COUNTRY = 7;
        String[] rows = sb.toString().split(NEW_LINE);
        String[] splitedFields;
        for (int n = 1; n < rows.length; n++) {
            splitedFields = rows[n].split(VAR_SERPARATOR);

            int id = parseInt(splitedFields[INDEX_ID]);
            if (id == -1) continue;

            Brewery brewery = new Brewery(id);
            brewery.setName(unescape(splitedFields[INDEX_NAME]));
            brewery.setCity(unescape(splitedFields[INDEX_CITY]));
            brewery.setCountry(unescape(splitedFields[INDEX_COUNTRY]));

            breweryMap.put(id, brewery);
        }


        //Read beers CSV and add them to breweries objects
        sb = getFileData("resources/beers.csv");

        int INDEX_BREWERY_ID = 1, INDEX_BEER_NAME = 2, INDEX_BEER_CAT = 3, INDEX_BEER_STYLE = 4;
        rows = sb.toString().split(NEW_LINE);
        for (int n = 1; n < rows.length; n++) {
            splitedFields = rows[n].split(VAR_SERPARATOR);

            int id = parseInt(splitedFields[INDEX_ID]);
            if (id == -1) continue;

            int breweryID = parseInt(splitedFields[INDEX_BREWERY_ID]);
            Beer beer = new Beer(id);
            beer.setBreweryID(breweryID);
            beer.setName(unescape(splitedFields[INDEX_BEER_NAME]));
            beer.setCategoryID(parseInt(splitedFields[INDEX_BEER_CAT]));
            beer.setStyleID(parseInt(splitedFields[INDEX_BEER_STYLE]));

            Brewery brewery = breweryMap.get(breweryID);
            if (brewery != null) brewery.addBeer(beer);
        }


        //Read breweries geodata CSV and set them to breweries objects
        sb = getFileData("resources/geocodes.csv");

        int INDEX_LAT = 2, INDEX_LON = 3;
        rows = sb.toString().split(NEW_LINE);
        for (int n = 1; n < rows.length; n++) {
            splitedFields = rows[n].split(VAR_SERPARATOR);

            int breweryID = parseInt(splitedFields[INDEX_BREWERY_ID]);
            if (breweryID == -1) continue;

            double lat = parseDouble(splitedFields[INDEX_LAT]);
            double lon = parseDouble(splitedFields[INDEX_LON]);

            Brewery brewery = breweryMap.get(breweryID);
            if (brewery != null) brewery.setCoordinates(lat, lon);
        }

        return new ArrayList<>(breweryMap.values());
    }

    private static StringBuilder getFileData(String filePath) {
        try {
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            InputStream inStream = classloader.getResourceAsStream(filePath);
            InputStreamReader reader = new InputStreamReader(inStream, ENCODING);

            BufferedReader br = new BufferedReader(reader);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
            }

            return sb;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Remove quotes from string variables
     */
    private static String unescape(String str) {
        if (str == null || str.isEmpty()) return "";

        String STRING_VAR = "\"";
        if (str.startsWith(STRING_VAR) && str.endsWith(STRING_VAR)) {
            return str.replaceAll(STRING_VAR, "");
        } else {
            return str;
        }
    }

    private static int parseInt(String str) {
        if (str == null || str.isEmpty()) return -1;
        int value = -1;
        str = str.replaceAll("[^0-9.,-]", "");
        str = str.replaceAll(",", ".");
        if (!str.isEmpty() && !str.equals(".") && !str.equals("-")) {
            value = Integer.parseInt(str);
        }
        return value;
    }

    public static double parseDouble(String str) {
        if (str == null || str.isEmpty()) return -1;
        double value = -1;
        str = str.replaceAll("[^0-9.,-]", "");
        str = str.replaceAll(",", ".");
        if (!str.isEmpty() && !str.equals(".") && !str.equals("-")) {
            value = Double.parseDouble(str);
        }
        return value;
    }
}
