#ifndef GENETIC_H
#define GENETIC_H

#include <iostream>
#include <limits>
#include <random>
#include <vector>
#include <cmath>
#include "Node.h"
#include "Edge.h"


class Genetic {
public:
    std::vector<std::vector<int>> population;
    int size;
    std::vector<double> fitness;
    double record;
    double currentDist;
    std::vector<int> current;
    std::vector<int> fitest;
    int fitestIndex;
    double mutation_rate;
    int num_generations = 2000;

    //Genetic(const std::vector<std::vector<int>>& population, int populationSize);
    Genetic(const std::vector<std::vector<int>>& initialPopulation, int populationSize);
    Genetic(int populationSize);

    void NormalizeFitness();
    void CalculateFitness(const std::vector<Node>& points);
    double AdjustMutationRate(int generation, int maxGenerations);
    void Mutate(std::vector<int>& genes, int generation, int maxGenerations);
    //void Mutate(std::vector<int>& genes);
    std::vector<int> CrossOver(const std::vector<int>& genes1, const std::vector<int>& genes2);
    std::vector<int> RouletteSelection(const std::vector<double>& fitness);
    void NaturalSelection();
};

#endif // GENETIC_H
