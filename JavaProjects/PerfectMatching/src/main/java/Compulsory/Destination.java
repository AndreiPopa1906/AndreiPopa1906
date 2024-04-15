package Compulsory;

import java.util.*;

public class Destination implements Comparable<Destination> {
    private String locationName;

    private List<Route> neighboringRoutes;

    public Destination(String locationName) {
        this.locationName = locationName;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    @Override
    public String toString() {
        return "Destination{" +
                "locationName='" + locationName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Destination)) return false;
        Destination that = (Destination) o;
        return Objects.equals(getLocationName(), that.getLocationName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLocationName());
    }

    /**
     * To compare
     * @param other the object to be compared.
     * @return
     */
    @Override
    public int compareTo(Destination other) {
        return this.locationName.compareTo(other.locationName);
    }

    public String getName() {
        return locationName;
    }
}
