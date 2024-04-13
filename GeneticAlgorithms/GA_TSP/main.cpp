#include "AntColony.h"
#include "Genetic.h"  
#include "Simulated_Annealing.h"
#include <iostream>
#include <algorithm>
#include <limits>
#include <random>
#include <vector>
#include <cmath>


// std::vector<Node> InitializeRandomNodes(int num_nodes) {
//     std::vector<Node> nodes;
//     std::random_device rd;
//     std::mt19937 gen(rd());
//     std::uniform_real_distribution<> dis(0.0, 100.0); // Presupunem un interval [0, 100] pentru coordonate

//     for (int i = 0; i < num_nodes; ++i) {
//         double x = dis(gen);
//         double y = dis(gen);
//         nodes.emplace_back(x, y);
//     }

//     return nodes;
// }

std::vector<std::vector<int>> InitializeRandomPopulation(int population_size, int num_nodes) {
    std::vector<std::vector<int>> population;
    std::random_device rd;
    std::mt19937 gen(rd());

    for (int i = 0; i < population_size; ++i) {
        std::vector<int> individual(num_nodes);
        std::iota(individual.begin(), individual.end(), 0);
        std::shuffle(individual.begin(), individual.end(), gen);
        population.push_back(individual);
    }

    return population;
}

#include <fstream>
#include <string>
#include <sstream>

int num_nodes;

std::vector<Node> InitializeNodesFromFile(const std::string& filename) {
    std::vector<Node> nodes;
    std::ifstream file(filename);
    std::string line;

    if (!file.is_open()) {
        std::cerr << "Nu s-a putut deschide fisierul: " << filename << std::endl;
        return nodes;
    }

    //int num_nodes;
    if (std::getline(file, line)) {
        std::istringstream iss(line);
        if (!(iss >> num_nodes)) {
            std::cerr << "Eroare la citirea numarului de noduri." << std::endl;
            return nodes;
        }
    }

    while (std::getline(file, line)) {
        std::istringstream iss(line);
        std::string nodeName;
        double x, y;

        if (!(iss >> nodeName >> x >> y)) {
            std::cerr << "Eroare la citirea unui nod din fisier." << std::endl;
            continue;
        }

        nodes.emplace_back(x, y); // Presupunând că constructorul Node acceptă x și y
    }

    return nodes;
}


 
int main() {
    const int num_generations = 2000;
    const int population_size = 200;
    //const int num_nodes = 2000;  
    const int colony_size = 300;  
    const int max_iterations = 1000;  
    const std::string variation = "ACS"; // "ACS", "ELITIST", "MAX-MIN"
    //------------------------------------------
    //std::vector<Node> nodes = InitializeRandomNodes(num_nodes);
    //std::vector<std::vector<int>> initialPopulation = InitializeRandomPopulation(population_size, num_nodes);
    // std::string filename = "Berlin52.txt";
    // std::vector<Node> nodes = InitializeNodesFromFile(filename);
    // if (nodes.empty()) {
    //     return -1; 
    // }
    // AntColony antColony(variation, colony_size, 1.0, 0.001, 1.0, 3.0, 0.1, 1.0, 1.0, max_iterations, nodes);
    // Genetic geneticAlgorithm(initialPopulation, population_size);
    // //Genetic geneticAlgorithm(population_size);
    //---------------------------------------------------------

    std::string filename = "Berlin52.txt";
    std::vector<Node> nodes = InitializeNodesFromFile(filename);
    if (nodes.empty()) {
        return -1; 
    }
    std::vector<std::vector<int>> initialPopulation = InitializeRandomPopulation(population_size, num_nodes);

    AntColony antColony(variation, colony_size, 1.0          , 0.001, 0.001, 10.0, 0.1, 2.0, 0.05, max_iterations, nodes);
    ///////////////////variation,  colony size, elitistweight,minfac, alpha, beta, rho, dep,pher,max iterations, nodes
    Genetic geneticAlgorithm(initialPopulation, population_size);

    for (int i = 0; i < num_generations; ++i) {
        // Exemplu de rulare a AntColony
        antColony.Simulate(i);

        printf("Passed Simulate (generation: %d)\n", i);

        // Exemplu de rulare a Genetic Algorithm
        geneticAlgorithm.CalculateFitness(nodes);
        printf("Passed Calculate fitness (generation: %d)\n", i);
        geneticAlgorithm.NaturalSelection();
        printf("Passed Natural selection (generation: %d)\n", i);

        // Afișează progresul
        std::cout << "Generația " << i + 1 << " / " << num_generations << std::endl;
    }

    // Afișează rezultatele (dacă este necesar)
    std::cout << "Distanța optimă AntColony: " << antColony.best_distance << std::endl;
    std::cout << "Distanța optimă Genetic Algorithm: " << geneticAlgorithm.record << std::endl;

    double initialTemperature = 10000;
    double coolingRate = 0.05;
    SimulatedAnnealing sa(nodes, initialTemperature, coolingRate);
    std::vector<int> saTour = sa.Run();

    double saDistance = sa.CalculateTourDistance(saTour, nodes);
    std::cout << "Distanța optimă Simulated Annealing: " << saDistance << std::endl;

    return 0;
}

// int main() {
//     const int num_generations = 2000;
//     const int population_size = 200;
//     const int num_nodes = 20;
//     const int colony_size = 10;
//     const int max_iterations = 100;
//     const std::string variation = "ELITIST"; // "ACS", "ELITIST", "MAX-MIN"

//     std::vector<Node> nodes = InitializeRandomNodes(num_nodes);
//     std::vector<std::vector<int>> initialPopulation = InitializeRandomPopulation(population_size, num_nodes);

//     AntColony antColony(variation, colony_size, 1.0, 0.001, 1.0, 3.0, 0.1, 1.0, 1.0, max_iterations, nodes);
//     Genetic geneticAlgorithm(initialPopulation, population_size);

//     // Inițializarea și rularea Simulated Annealing
//     double initialTemp = 10000;
//     double finalTemp = 1;
//     double coolingRate = 0.003;
//     SimulatedAnnealing sa(antColony, initialTemp, finalTemp, coolingRate);
    
//     for (int i = 0; i < num_generations; ++i) {
//         antColony.Simulate(i);
//         geneticAlgorithm.CalculateFitness(nodes);
//         geneticAlgorithm.NaturalSelection();
        
//         if (i % 100 == 0) { // De exemplu, rulăm SA la fiecare 100 de generații
//             sa.Run();
//         }

//         std::cout << "Generația " << i + 1 << " / " << num_generations << std::endl;
//     }

//     // Afișează rezultatele
//     std::cout << "Distanța optimă AntColony: " << antColony.GetBestDistance() << std::endl;
//     std::cout << "Distanța optimă Genetic Algorithm: " << geneticAlgorithm.GetRecord() << std::endl;
//     std::cout << "Distanța optimă Simulated Annealing: " << antColony.GetBestDistance() << std::endl;

//     return 0;
// }