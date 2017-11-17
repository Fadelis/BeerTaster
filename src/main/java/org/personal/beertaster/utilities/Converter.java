package org.personal.beertaster.utilities;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import org.personal.beertaster.elements.Beer;
import org.personal.beertaster.elements.Brewery;

public class Converter {

  private static final String VAR_SEPARATOR = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";

  private Converter() {
  }

  public static List<Brewery> readCSV() throws Exception {
    final HashMap<Integer, Brewery> breweryMap = new HashMap<>();

    //Read breweries CSV
    final int INDEX_ID = 0;
    final int INDEX_NAME = 1;
    final int INDEX_CITY = 4;
    final int INDEX_COUNTRY = 7;
    List<String> rows = getFileData("resources/breweries.csv");
    for (int n = 1; n < rows.size(); n++) {
      final String[] splitFields = rows.get(n).split(VAR_SEPARATOR);

      final int id = parseInt(splitFields[INDEX_ID]);
      if (id == -1) {
        continue;
      }

      final Brewery brewery = new Brewery(id);
      brewery.setName(unescape(splitFields[INDEX_NAME]));
      brewery.setCity(unescape(splitFields[INDEX_CITY]));
      brewery.setCountry(unescape(splitFields[INDEX_COUNTRY]));

      breweryMap.put(id, brewery);
    }

    //Read beers CSV and add them to breweries objects
    final int INDEX_BREWERY_ID = 1;
    final int INDEX_BEER_NAME = 2;
    final int INDEX_BEER_CAT = 3;
    final int INDEX_BEER_STYLE = 4;
    rows = getFileData("resources/beers.csv");
    for (int n = 1; n < rows.size(); n++) {
      final String[] splitFields = rows.get(n).split(VAR_SEPARATOR);

      final int id = parseInt(splitFields[INDEX_ID]);
      if (id == -1) {
        continue;
      }

      final int breweryID = parseInt(splitFields[INDEX_BREWERY_ID]);
      final Beer beer = new Beer(id);
      beer.setBreweryID(breweryID);
      beer.setName(unescape(splitFields[INDEX_BEER_NAME]));
      beer.setCategoryID(parseInt(splitFields[INDEX_BEER_CAT]));
      beer.setStyleID(parseInt(splitFields[INDEX_BEER_STYLE]));

      Optional.ofNullable(breweryMap.get(breweryID))
          .ifPresent(brewery -> brewery.addBeer(beer));
    }

    //Read breweries geodata CSV and set them to breweries objects
    final int INDEX_LAT = 2;
    final int INDEX_LON = 3;
    rows = getFileData("resources/geocodes.csv");
    for (int n = 1; n < rows.size(); n++) {
      final String[] splitFields = rows.get(n).split(VAR_SEPARATOR);

      final int breweryID = parseInt(splitFields[INDEX_BREWERY_ID]);
      if (breweryID == -1) {
        continue;
      }

      final double lat = parseDouble(splitFields[INDEX_LAT]);
      final double lon = parseDouble(splitFields[INDEX_LON]);

      Optional.ofNullable(breweryMap.get(breweryID))
          .ifPresent(brewery -> brewery.setCoordinates(lat, lon));
    }

    return new ArrayList<>(breweryMap.values());
  }

  private static List<String> getFileData(final String filePath) throws IOException {
    return Files.readAllLines(Paths.get(filePath));
  }

  /**
   * Remove quotes from string variables
   */
  private static String unescape(final String str) {
    if (str == null || str.isEmpty()) {
      return "";
    }

    final String STRING_VAR = "\"";
    if (str.startsWith(STRING_VAR) && str.endsWith(STRING_VAR)) {
      return str.replaceAll(STRING_VAR, "");
    } else {
      return str;
    }
  }
}
