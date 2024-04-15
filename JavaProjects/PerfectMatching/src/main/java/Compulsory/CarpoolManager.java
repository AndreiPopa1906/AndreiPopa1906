package Compulsory;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class CarpoolManager {
    private LinkedList<Driver> drivers;
    private TreeMap<Destination, List<Passenger>> passengersByDestination;

    public CarpoolManager(List<Person> people) {
        initializeDrivers(people);
        initializePassengersByDestination(people);
    }

    private void initializeDrivers(List<Person> people) {
        drivers = people.stream()
                .filter(Driver.class::isInstance)
                .map(Driver.class::cast)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    private void initializePassengersByDestination(List<Person> people) {
        passengersByDestination = people.stream()
                .filter(Passenger.class::isInstance)
                .map(Passenger.class::cast)
                .collect(Collectors.groupingBy(
                        Passenger::getDestination,
                        TreeMap::new,
                        Collectors.toList()
                ));
    }

    public LinkedList<Driver> getDrivers() {
        return drivers;
    }

    public TreeMap<Destination, List<Passenger>> getPassengersByDestination() {
        return passengersByDestination;
    }

}


