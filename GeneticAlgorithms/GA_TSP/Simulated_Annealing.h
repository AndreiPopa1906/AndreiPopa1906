#include <vector>
#include <random>
#include <algorithm>
#include <cmath>
#include "Node.h"
#include "Edge.h"
#include "AntColony.h"
#include "Ant.h"


class SimulatedAnnealing {
public:
    std::vector<Node> nodes;
    double temperature;
    double coolingRate;
    SimulatedAnnealing(const std::vector<Node>& nodes, double initialTemperature, double coolingRate);
    std::vector<int> GenerateRandomTour();
    std::vector<int> GenerateNeighborTour(const std::vector<int>& tour);
    double Distance(const Node& a, const Node& b);
    double CalculateTourDistance(const std::vector<int>& tour, const std::vector<Node>& nodes);
    double AcceptanceProbability(double currentDistance, double newDistance, double temperature);
    std::vector<int> Run();

};


// class SimulatedAnnealing {
// public:
//     std::vector<Node> nodes;
//     SimulatedAnnealing(const std::vector<Node>& nodes);
//     AntColony& colony;
//     double temperature, finalTemp, coolingRate;
//     SimulatedAnnealing(AntColony& colony, double initialTemp, double finalTemp, double coolingRate);
//     double AcceptanceProbability(double oldDistance, double newDistance, double temperature);
//     double RandomDouble();
//     std::vector<int> GenerateNeighborTour(const std::vector<int>& currentTour);
//     void Run();
    
// };