#ifndef ANT_H
#define ANT_H

#include "Edge.h"
#include "Node.h"
#include <iostream>
#include <limits>
#include <random>
#include <vector>
#include <cmath>

class Ant {
public:
    std::vector<std::vector<Edge>> edges;
    std::vector<int> tour;
    double alpha, beta;
    int n_nodes;
    double distance;

    Ant(const std::vector<std::vector<Edge>>& edges, double alpha, double beta, int n_nodes);
    int NodeSelection();
    std::vector<int> UpdateTour();
    double CalculateDistance();
};

#endif
