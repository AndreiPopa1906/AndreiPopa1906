#ifndef EDGE_H
#define EDGE_H

class Edge {
public:
    int a, b;
    double heuristic, pheromone;

    Edge();
    Edge(int a, int b, double heuristic, double pheromone);
};

#endif
