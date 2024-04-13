#include "Simulated_Annealing.h"

#include <vector>
#include <random>
#include <algorithm>
#include <cmath>

SimulatedAnnealing::SimulatedAnnealing(const std::vector<Node>& nodes, double initialTemperature, double coolingRate)
    : nodes(nodes), temperature(initialTemperature), coolingRate(coolingRate) {}

std::vector<int> SimulatedAnnealing::Run() {
    std::vector<int> currentTour = GenerateRandomTour();
    std::vector<int> bestTour = currentTour;
    double bestDistance = CalculateTourDistance(currentTour, nodes); 
    while (temperature > 1) {
        std::vector<int> newTour = GenerateNeighborTour(currentTour);
        double newDistance = CalculateTourDistance(newTour, nodes); 
        if (AcceptanceProbability(bestDistance, newDistance, temperature) > ((double) rand() / RAND_MAX)) {
            currentTour = newTour;
            if (newDistance < bestDistance) {
                bestTour = newTour;
                bestDistance = newDistance;
            }
        }
        temperature *= 1 - coolingRate;
    }

    return bestTour;
}

std::vector<int> SimulatedAnnealing::GenerateRandomTour() {
    std::vector<int> tour(nodes.size());
    std::iota(tour.begin(), tour.end(), 0);
    std::shuffle(tour.begin(), tour.end(), std::mt19937(std::random_device()()));
    return tour;
}

std::vector<int> SimulatedAnnealing::GenerateNeighborTour(const std::vector<int>& tour) {
    std::vector<int> newTour = tour;
    int i = rand() % newTour.size();
    int j = rand() % newTour.size();
    while (j == i) {
        j = rand() % newTour.size();
    }
    std::swap(newTour[i], newTour[j]);
    return newTour;
}

double SimulatedAnnealing::CalculateTourDistance(const std::vector<int>& tour, const std::vector<Node>& nodes) {
    double totalDistance = 0.0;
    for (size_t i = 0; i < tour.size(); ++i) {
        int nextIndex = (i + 1) % tour.size();
        totalDistance += Distance(nodes[tour[i]], nodes[tour[nextIndex]]);
    }
    return totalDistance;
}


double SimulatedAnnealing::AcceptanceProbability(double currentDistance, double newDistance, double temperature) {
    if (newDistance < currentDistance) {
        return 1.0;
    }
    return exp((currentDistance - newDistance) / temperature);
}

double SimulatedAnnealing::Distance(const Node& a, const Node& b) {
    return std::sqrt((b.x - a.x) * (b.x - a.x) + (b.y - a.y) * (b.y - a.y));
}



// SimulatedAnnealing::SimulatedAnnealing(const std::vector<Node>& nodes) : nodes(nodes) {}

// SimulatedAnnealing::SimulatedAnnealing(AntColony& colony, double initialTemp, double finalTemp, double coolingRate)
// : colony(colony), temperature(initialTemp), finalTemp(finalTemp), coolingRate(coolingRate) {}

// void SimulatedAnnealing::Run() {
//     std::vector<int> currentTour = colony.GenerateRandomTour(); // Generează un tur inițial aleatoriu
//     double bestDistance = colony.CalculateTourDistance(currentTour);

//     while (temperature > finalTemp) {
//         std::vector<int> newTour = GenerateNeighborTour(currentTour); // Generează un tur vecin
//         double newDistance = colony.CalculateTourDistance(newTour);

//         if (newDistance < bestDistance || AcceptanceProbability(bestDistance, newDistance, temperature) > RandomDouble()) {
//             currentTour = newTour;
//             bestDistance = newDistance;
//         }

//         temperature *= coolingRate; // Răcește temperatura
//     }

//     colony.SetBestTour(currentTour, bestDistance); // Actualizează cel mai bun tur
// }

// double SimulatedAnnealing::AcceptanceProbability(double oldDistance, double newDistance, double temperature) {
//     if (newDistance < oldDistance) return 1.0;
//     return std::exp((oldDistance - newDistance) / temperature);
// }

// double RandomDouble() { /* returnează un număr real aleatoriu între 0 și 1 */ }

// std::vector<int> SimulatedAnnealing::GenerateNeighborTour(const std::vector<int>& currentTour) {
//         std::vector<int> newTour = currentTour;

//         // Alegerea a două indici aleatorii diferiți
//         std::random_device rd;
//         std::mt19937 g(rd());
//         std::uniform_int_distribution<> dis(0, newTour.size() - 1);

//         int i = dis(g);
//         int j = dis(g);
//         while (j == i) {
//             j = dis(g); // Asigură-te că i și j sunt diferiți
//         }

//         // Schimbă nodurile din tur
//         std::swap(newTour[i], newTour[j]);

//         return newTour;
//     }

