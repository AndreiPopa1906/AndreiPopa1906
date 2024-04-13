#include "Edge.h"

Edge::Edge() : a(0), b(0), heuristic(0.0), pheromone(0.0) {}


Edge::Edge(int a, int b, double heuristic, double pheromone)
    : a(a), b(b), heuristic(heuristic), pheromone(pheromone) {}
