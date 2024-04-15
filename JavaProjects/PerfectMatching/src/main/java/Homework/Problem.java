package Homework;

import Compulsory.*;
import com.github.javafaker.Faker;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

public class Problem {
    /**
     * The graph that represents the problem
     */
    private Graph<Person, DefaultEdge> graph;
    /**
     * List of drivers
     */
    private List<Driver> drivers;
    /**
     * List of passengers
     */
    private List<Passenger> passengers;
    /**
     * The assignments
     */
    Map<Driver, Passenger> assignments;
    private List<Destination> possibleDestinations = new ArrayList<>();
    private List<Person> people;
    /**
     * The probability of an edge to be created
     */
    private double edgeProbability;
    private Random random;

    private static final Logger logger = Logger.getLogger(Problem.class.getName());

    /**
     * Constructor that instantiates the problem with the according information
     * @param numDrivers number of drivers to be instantiated
     * @param numPassengers number of passengers to be instantiated
     * @param edgeProbability the maximum upper bound of a probability of an edge to be created
     */
    public Problem(int numDrivers, int numPassengers, double edgeProbability, int numDestinations) {
        this.graph = new SimpleGraph<>(DefaultEdge.class);
        this.people = new ArrayList<>();
        this.edgeProbability = edgeProbability;
        this.random = new Random();

        generatePeople(numDrivers, numPassengers, numDestinations);
    }

    /**
     * Generates fake data for all the drivers and all the passengers
     * @param numDrivers the number of drivers instantiated
     * @param numPassengers the number of passenger instantiated
     */

    public void generatePeople(int numDrivers, int numPassengers, int numDestinations) {
        Faker faker = new Faker();
        this.possibleDestinations = new ArrayList<>(); // This is now a list of Destination objects

        this.drivers = new ArrayList<>();
        this.passengers = new ArrayList<>();

        while (possibleDestinations.size() < numDestinations) {
            String destinationName = faker.address().cityName();
            possibleDestinations.add(new Destination(destinationName)); // Add a new Destination object
        }

        for (int i = 0; i < numDrivers; i++) {
            String name = faker.name().fullName();
            int age = faker.number().numberBetween(18, 70);
            Destination startingPoint = possibleDestinations.get(random.nextInt(possibleDestinations.size()));
            Destination destination = possibleDestinations.get(random.nextInt(possibleDestinations.size()));
            Driver driver = new Driver(name, age, startingPoint, destination);
            this.drivers.add(driver);
            this.people.add(driver);
            this.graph.addVertex(driver);
        }

        for (int i = 0; i < numPassengers; i++) {
            String name = faker.name().fullName();
            int age = faker.number().numberBetween(18, 70);
            Destination startingPoint = possibleDestinations.get(random.nextInt(possibleDestinations.size()));
            Destination destination = possibleDestinations.get(random.nextInt(possibleDestinations.size()));
            Passenger passenger = new Passenger(name, age, startingPoint, destination);
            this.passengers.add(passenger);
            this.people.add(passenger);
            this.graph.addVertex(passenger);
        }
    }

    public Graph<Person, DefaultEdge> getGraph() {
        return graph;
    }

    /**
     * A greedy implementation that finds the solution to the problem
     */
    public void executeGreedyAlgorithm() {
        assignments = new HashMap<>();

        assignRoutesToDrivers();

        Set<Driver> availableDrivers = people.stream()
                .filter(person -> person instanceof Driver)
                .map(person -> (Driver) person)
                .collect(Collectors.toSet());
        Set<Passenger> availablePassengers = people.stream()
                .filter(person -> person instanceof Passenger)
                .map(person -> (Passenger) person)
                .collect(Collectors.toSet());

        Set<Passenger> matchedPassengers = new HashSet<>();

        for (Driver driver : availableDrivers) {
            if (driver.getRoute() == null) {
                logger.warning("Driver " + driver.getName() + " does not have a route assigned.");
                continue;
            }
            for (Passenger passenger : availablePassengers) {
                if (!matchedPassengers.contains(passenger) && driver.getRoute().isEnRoute(passenger.getDestination())) {
                    logger.info("[LOGGER] Match found: Driver " + driver.getName() + " and Passenger " + passenger.getName());
                    assignments.put(driver, passenger);
                    matchedPassengers.add(passenger);
                    break;
                }
            }
        }

        availablePassengers.removeAll(matchedPassengers);

        if (assignments.isEmpty()) {
            logger.info("[LOGGER] OOPSIE... No matches were found during the greedy algorithm execution.");
        }
    }

    /**
     * Assigns a random route to every driver in the list of drivers.
     */
    public void assignRoutesToDrivers() {
        if (possibleDestinations.size() < 3) {
            logger.warning("Not enough destinations to create routes for drivers.");
            return;
        }

        for (Driver driver : drivers) {
            Collections.shuffle(possibleDestinations);
            int numberOfStops = Math.min(possibleDestinations.size(), 3 + random.nextInt(3));
            List<Destination> driverRouteDestinations = possibleDestinations.subList(0, numberOfStops);
            Route route = new Route(driverRouteDestinations);
            driver.setRoute(route);
        }
    }

    /**
     * Using Java Stream API to compute a list of all the destinations that the drivers pass through
     * @return a set of destinations
     */
    public Set<Destination> getDriverDestinations() {
        return people.stream()
                .filter(person -> person instanceof Driver)
                .map(person -> ((Driver) person).getDestination())
                .collect(Collectors.toSet());
    }

    /**
     * Using Java Stream API to compute a map of destinations and people who want to go there
     * @return a map of destinations and people who want to go there
     */
    public Map<Destination, List<Person>> getDestinationPeopleMap() {
        return people.stream()
                .collect(Collectors.groupingBy(Person::getDestination));
    }

    public static void main(String[] args) {
        Problem problem = new Problem(5, 10, 0.8,  10); // Example numbers with a higher edgeProbability

        problem.assignRoutesToDrivers();

        problem.executeGreedyAlgorithm();

        var assignments = problem.getAssignments();
        if (assignments.isEmpty()) {
            logger.info("No assignments were made.");
        } else {
            assignments.forEach((driver, passenger) -> {
                logger.info("Driver: -" + driver.getName() + "- will take Passenger: -" + passenger.getName() + "-.");
            });
        }

        Set<Destination> driverDestinations = problem.getDriverDestinations();
        logger.info("\nList of all destinations that the drivers pass through:");
        driverDestinations.forEach(destination -> System.out.println(destination.getLocationName()));

        Map<Destination, List<Person>> destinationPeopleMap = problem.getDestinationPeopleMap();
        logger.info("\nMap of destinations and people who want to go there:");
        destinationPeopleMap.forEach((destination, people) -> {
            logger.info("Destination: " + destination.getLocationName());
            people.forEach(person -> logger.info(" - " + person.getName()));
        });
    }

    //-------------------------------------------------------------------

    /**
     * Finds a maximum cardinality set of persons such that no two persons have the same destination
     */
    public Set<Person> findMaximumCardinalitySet() {
        Set<Person> maxCardinalitySet = new HashSet<>();
        Set<Destination> usedDestinations = new HashSet<>();

        for (var person : people) {
            if (!usedDestinations.contains(person.getDestination())) {
                maxCardinalitySet.add(person);
                usedDestinations.add(person.getDestination());
            }
        }

        return maxCardinalitySet;
    }
    public Map<Driver, Passenger> getAssignments() {
        return assignments;
    }
    public List<Person> getPeople() {
        return people;
    }
    public List<Driver> getDrivers() {
        return drivers;
    }

    public List<Passenger> getPassengers() {
        return passengers;
    }


}

