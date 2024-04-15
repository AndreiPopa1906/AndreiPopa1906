package Bonus;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.alg.clique.BronKerboschCliqueFinder;

import java.util.*;

public class MaximalGroupFinder {
    private Map<String, List<String>> personAbilities;
    private Graph<String, DefaultEdge> graph;

    /**
     * Initializes a new MaximalGroupFinder with the specified map of person abilities
     * It builds an undirected graph where each vertex represents a person, and an edge represents a common ability between two people
     *
     * @param personAbilities a map where the key is the person's name and the value is a list of abilities
     */
    public MaximalGroupFinder(Map<String, List<String>> personAbilities) {
        this.personAbilities = personAbilities;
        this.graph = new SimpleGraph<>(DefaultEdge.class);
        buildGraph();
    }

    /**
     * Builds the graph based on the personAbilities map
     */
    private void buildGraph() {
        for (String person : personAbilities.keySet()) {
            graph.addVertex(person);
        }

        for (String person1 : personAbilities.keySet()) {
            for (String person2 : personAbilities.keySet()) {
                if (!person1.equals(person2)) {
                    Set<String> abilities1 = new HashSet<>(personAbilities.get(person1));
                    abilities1.retainAll(new HashSet<>(personAbilities.get(person2)));
                    if (!abilities1.isEmpty()) {
                        graph.addEdge(person1, person2);
                    }
                }
            }
        }
    }

    /**
     * Finds and returns all maximal cliques in the graph
     *
     * @return a collection of sets, each set representing a maximal clique in the graph
     */
    public Collection<Set<String>> findMaximalCliques() {
        BronKerboschCliqueFinder<String, DefaultEdge> cliqueFinder = new BronKerboschCliqueFinder<>(graph);
        Iterator<Set<String>> cliquesIterator = cliqueFinder.iterator();

        List<Set<String>> maximalCliques = new ArrayList<>();
        while (cliquesIterator.hasNext()) {
            Set<String> clique = cliquesIterator.next();
            maximalCliques.add(clique);
        }

        return maximalCliques;
    }



    public static void main(String[] args) {
        try {
            Map<String, List<String>> personAbilities = new HashMap<>();

            MaximalGroupFinder finder = new MaximalGroupFinder(personAbilities);

            Collection<Set<String>> maximalCliques = finder.findMaximalCliques();

            for (Set<String> clique : maximalCliques) {
                System.out.println("Maximal clique: " + clique);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
