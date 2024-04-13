#ifndef ANTCOLONY_H
#define ANTCOLONY_H

#include <string>
#include <iostream>
#include <limits>
#include <random>
#include <vector>
#include <cmath>
#include "Node.h"
#include "Edge.h"
#include "Ant.h"

class AntColony {
public:
    std::string variation;
    int size;
    double elitist_weight, minFactor, alpha, beta, rho, phe_deposit_weight;
    int max_iterations, n_nodes;
    std::vector<Node> nodes; // Presupunem ca exista o clasa Node
    std::vector<std::vector<Edge>> edges;
    std::vector<Ant> ants;
    std::vector<int> best_tour;
    double best_distance;
    std::vector<int> local_best_route;
    double local_best_distance;

    AntColony(std::string variation, int size, double elitist_weight, double minFactor, double alpha, double beta,
              double rho, double phe_deposit_weight, double pheromone, int max_iterations, const std::vector<Node>& nodes);

    void AddPheromone(const std::vector<int>& tour, double distance, double heuristic);
    void ACS();
    void ELITIST();
    void MAX_MIN(int counter);
    void Simulate(int counter);

    //Pentru Simulated Annealing
    // std::vector<int> GenerateRandomTour();
    // double CalculateTourDistance(const std::vector<int>& tour);
    // void SetBestTour(const std::vector<int>& tour);
    // std::vector<int> GenerateNeighbourTour(const std::vector<int>& currentTour);
};

#endif // ANTCOLONY_H

