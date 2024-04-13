#ifndef POPULATION_H
#define POPULATION_H
#include <vector>
#include "Chromosome.h"

class Population {
public:
    std::vector<Chromosome> chromosomes;
    int dimensions;
    int precision;
    MathFunction math_function;
    double average;

    Population(int number_of_chromosomes, int dimensions, int precision, MathFunction math_function);

    int calculate_length();

    double get_best_fitness();

    void extinction(int index);

    void normalize_fitness();

    void calculate_cumsum();

    void introduce_diversity();

    Chromosome* pick_parent();

    Chromosome* tournament_selection(int tournament_size);

    void crossover(double crossover_rate, double elitism_rate,double mutation_rate);

    void mutate(double mutation_rate);

    std::vector<Chromosome> elitism(double elitism_rate);
};

#endif //POPULATION_H