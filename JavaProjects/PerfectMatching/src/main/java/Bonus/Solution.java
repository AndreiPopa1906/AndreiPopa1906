package Bonus;

import Compulsory.Destination;
import Compulsory.Driver;
import Compulsory.Passenger;
import Compulsory.Person;
import Homework.Problem;
import org.jgrapht.Graph;
import org.jgrapht.alg.matching.HopcroftKarpMaximumCardinalityBipartiteMatching;
import org.jgrapht.alg.matching.MaximumWeightBipartiteMatching;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author Andrei Popa
 */

public class Solution {
    /**
     * The problem instance containing drivers, passengers, and their destinations
     */
    private Problem problem;
    /**
     * The bipartite graph representing the potential matches between drivers and passengers
     */
    private Graph<Person, DefaultEdge> bipartiteGraph;
    private static final Logger logger = Logger.getLogger(Problem.class.getName());
    public Solution(Problem problem) {
        this.problem = problem;
        this.bipartiteGraph = new SimpleWeightedGraph<>(DefaultEdge.class);
        transformToBipartiteGraph();
    }
    /**
     * Transforms the list of drivers and passengers into a bipartite graph
     */
    private void transformToBipartiteGraph() {
        Set<Driver> drivers = new HashSet<>();
        Set<Passenger> passengers = new HashSet<>();

        problem.assignRoutesToDrivers();

        for (Person p : problem.getPeople()) {
            if (p instanceof Driver) {
                drivers.add((Driver) p);
                bipartiteGraph.addVertex(p);
            } else if (p instanceof Passenger) {
                passengers.add((Passenger) p);
                bipartiteGraph.addVertex(p);
            }
        }

        for (Driver driver : drivers) {
            for (Passenger passenger : passengers) {
                if (driver.getRoute().isEnRoute(passenger.getDestination())) {
                    bipartiteGraph.addEdge(driver, passenger);
                }
            }
        }
    }

//    private void transformToBipartiteGraph() {
//        Set<Driver> drivers = new HashSet<>();
//        Set<Passenger> passengers = new HashSet<>();
//
//        for (Person p : problem.getPeople()) {
//            if (p instanceof Driver) {
//                drivers.add((Driver) p);
//                bipartiteGraph.addVertex(p);
//            } else if (p instanceof Passenger) {
//                passengers.add((Passenger) p);
//                bipartiteGraph.addVertex(p);
//            }
//        }
//
//        for (Driver driver : drivers) {
//            for (Passenger passenger : passengers) {
//                if (driver.getDestination().equals(passenger.getDestination())) {
//                    bipartiteGraph.addEdge(driver, passenger);
//                }
//            }
//        }
//    }

    /**
     * Solves the matching problem using the Hopcroft-Karp algorithm and the greedy algorithm
     */
    public void solveWithJGraphT() {
        Set<Person> setDrivers = new HashSet<>(this.problem.getDrivers());
        Set<Person> setPassengers = new HashSet<>(this.problem.getPassengers());

        HopcroftKarpMaximumCardinalityBipartiteMatching<Person, DefaultEdge> hopcroftKarpMatching =
                new HopcroftKarpMaximumCardinalityBipartiteMatching<>(this.bipartiteGraph, setDrivers, setPassengers);

        var hopcroftKarpMatchResult = hopcroftKarpMatching.getMatching();
        logger.info("[LOGGER] Number of matchings with Hopcroft-Karp: " + hopcroftKarpMatchResult.getEdges().size());

        for (DefaultEdge edge : hopcroftKarpMatchResult.getEdges()) {
            Person driver = this.bipartiteGraph.getEdgeSource(edge);
            Person passenger = this.bipartiteGraph.getEdgeTarget(edge);
            if (driver instanceof Driver && passenger instanceof Passenger) {
                logger.info("[LOGGER] Match: Driver " + driver.getName() + " with Passenger " + passenger.getName());
            }
        }

        logger.info("---------------------------------------");

        problem.executeGreedyAlgorithm();
        logger.info("[LOGGER] Number of matchings with the greedy algorithm: " + problem.getAssignments().size());

//        logger.info("Beginning comparison...");
//
//        if (hopcroftKarpMatchResult.getEdges().size() >= problem.getAssignments().size()) {
//            logger.info("Hopcroft-Karp algorithm found more matchings than the greedy algorithm.");
//        } else if (hopcroftKarpMatchResult.getEdges().size() < problem.getAssignments().size()) {
//            logger.info("Greedy algorithm found more matchings than the Hopcroft-Karp algorithm.");
//        }
    }

    /**
     * Finds the maximum set of persons such that no two persons have the same destination
     * @return a set of persons where no two persons share the same destination
     */
    public Set<Person> findMaximumCardinalitySet() {
        Set<Destination> usedDestinations = new HashSet<>();
        Set<Person> uniqueDestinationPeople = new HashSet<>();

        for (Person person : this.problem.getPeople()) {
            if (!usedDestinations.contains(person.getDestination())) {
                usedDestinations.add(person.getDestination());
                uniqueDestinationPeople.add(person);
            }
        }

        return uniqueDestinationPeople;
    }

    public static void main(String[] args) {
        Problem problem = new Problem(5000, 5000, 0.1, 1000);
        problem.assignRoutesToDrivers();

        Solution solution = new Solution(problem);

        solution.solveWithJGraphT();
    }
}



