package com.personal.beertaster.main;

import com.personal.beertaster.algorithms.BreweryManager;
import com.personal.beertaster.algorithms.SimpleNNA;
import com.personal.beertaster.algorithms.Tour;
import com.personal.beertaster.ui.MainPane;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(final String[] args) {
        BreweryManager.initialize();

        launch(args);

//        planRoute();

//		setOriginLocation(DEFAULT_LAT, DEFAULT_LON);
//		setOriginLocation(51.742503, 19.432956);

//        final Scanner scanner = new Scanner(System.in);//--lat=51.742503 --lon=19.432956
//        while (true) {
//            final double oldLat = BreweryManager.ORIGIN.getCoordinates().getLatitude();
//            final double oldLon = BreweryManager.ORIGIN.getCoordinates().getLongitude();
//            double newLat = oldLat;
//            double newLon = oldLon;
//
//            final String userInput = scanner.nextLine();
//            final String[] values = userInput.split("--");
//
//            for (int i = 0; i < values.length; i++) {
//                if (values[i].contains("lat")) {
//                    newLat = Converter.parseDouble(values[i]);
//                } else if (values[i].contains("lon")) {
//                    newLon = Converter.parseDouble(values[i]);
//                }
//            }
//
//            if (oldLat != newLat || oldLon != newLon) {
//                setOriginLocation(newLat, newLon);
//            }
//        }
    }

    private static void planRoute() {
        final long start = System.currentTimeMillis();

        final Tour tour = SimpleNNA.planSimpleNNA();
        //final Tour tour = AdvancedNNA.planAdvancedNNA();
        //final Tour tour = BruteForce.planBruteForce();
        //final Tour tour = SimulatedAnnealing.optimiseTour(SimpleNNA.planSimpleNNA());

        System.out.println(tour.toString());

        final long total = System.currentTimeMillis() - start;
        System.out.println("Calculated in " + total + " ms");
        System.out.println("Total beer " + tour.getBeerCount());

        System.out.println(getHelpText());
    }


    /**
     * Set new starting location
     */
    private static void setOriginLocation(final double lat, final double lon) {
        BreweryManager.setOriginLocation(lat, lon);

        planRoute();
    }


    private static String getHelpText() {
        final StringBuilder sb = new StringBuilder();
        sb.append(System.lineSeparator());
        sb.append("To enter new starting location use this format:");
        sb.append(System.lineSeparator());
        sb.append("--lat=51.355468 --long=11.100790");

        return sb.toString();
    }

    @Override
    public void start(final Stage primaryStage) throws Exception {
        primaryStage.setTitle("Beer Taster");
        primaryStage.setScene(new Scene(new MainPane(), 500, 500));
        primaryStage.show();
    }
}
