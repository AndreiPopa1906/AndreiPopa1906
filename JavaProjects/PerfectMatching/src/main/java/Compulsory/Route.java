package Compulsory;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Route {
    /**
     * The route a driver is taking
     */
    private List<Destination> stops;

    public Route(List<Destination> someDestinations) {
        this.stops = someDestinations;
    }

    /**
     * Constructor that creates a route with a random number of destinations.
     * @param allDestinations The list with all the possible destinations.
     * @param maxDestinations The maximum number of destinations to be considered.
     */
    public Route(List<Destination> allDestinations, int maxDestinations) {
        if (maxDestinations < 3) {
            throw new IllegalArgumentException("Maximum number of destinations must be at least 3.");
        }
        maxDestinations = Math.min(maxDestinations, allDestinations.size());

        Random rand = new Random();
        int numberOfStops = 3 + rand.nextInt(maxDestinations - 2);
        Collections.shuffle(allDestinations);
        this.stops = allDestinations.stream().limit(numberOfStops).collect(Collectors.toList());
    }

    /**
     * Checks if the given destination is on the route.
     * @param destination The destination to check.
     * @return true if the destination is on the route, false otherwise.
     */
    public boolean isEnRoute(Destination destination) {
        for (Destination stop : stops) {
            if (stop.equals(destination)) {
                return true;
            }
        }
        return false;
    }


    public List<Destination> getStops() {
        return stops;
    }

    public void setStops(List<Destination> stops) {
        this.stops = stops;
    }

    public boolean passesThrough(Destination destination) {
        return stops.contains(destination);
    }
}
