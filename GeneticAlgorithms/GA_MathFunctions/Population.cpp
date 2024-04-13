#include "Population.h"
#include "Chromosome.h"
#include "MathFunction.h"
#include <algorithm>
#include <cmath>
#include <random>
#include <vector>
#include <random>

Population::Population(int number_of_chromosomes, int dimensions, int precision,
                       MathFunction math_function): math_function(math_function), dimensions(dimensions),
                                                    precision(precision) {
    int number_of_genes = calculate_length();

    for (int i = 0; i < number_of_chromosomes; i++) {
        auto chromosome = new Chromosome(number_of_genes);
        chromosomes.push_back(*chromosome);
    }
}

int Population::calculate_length() {
    return static_cast<int>(
        std::ceil(dimensions *
                  std::log2(std::pow(10, precision) * (math_function.bounds.max -
                                                       math_function.bounds.min))));
}

void Population::introduce_diversity() {
    double diversity_threshold = 0.05;

    for (auto& chromosome : chromosomes) {
        if (fabs(chromosome.fitness - average) < diversity_threshold) {
            chromosome.randomize(); // Implement a method to randomize the chromosome
        }
    }
}


void Population::normalize_fitness() {
    for (auto&chromosome: chromosomes) {
        chromosome.calculate_fitness(math_function, dimensions);
    }

    double fitness_sum = 0;
    for (auto&chromosome: chromosomes) {
        fitness_sum += chromosome.fitness;
    }

    for (auto&chromosome: chromosomes) {
        chromosome.fitness /= fitness_sum;
    }
}



void Population::calculate_cumsum() {
    // sort them based on fitness value (descending order)
    std::sort(chromosomes.begin(), chromosomes.end(),
              [](const Chromosome&lhs, const Chromosome&rhs) {
                  return lhs.fitness > rhs.fitness;
              });

    for (int i = 0; i < chromosomes.size(); i++) {
        chromosomes[i].cumsum = 0;
        for (int j = i; j < chromosomes.size(); j++) {
            chromosomes[i].cumsum += chromosomes[j].fitness;
        }
    }
    average = 1.0 / (static_cast<int>(chromosomes.size()));
}

Chromosome* Population::tournament_selection(int tournament_size) {
    std::uniform_real_distribution<double> distribution(0, chromosomes.size() - 1);
    auto random_generator = std::mt19937(std::random_device()());

    std::vector<Chromosome*> tournament_participants;

    for (int i = 0; i < tournament_size; ++i) {
        int rand_index = static_cast<int>(distribution(random_generator));
        tournament_participants.push_back(&chromosomes[rand_index]);
    }

    Chromosome* best = tournament_participants[0];
    for (int i = 1; i < tournament_size; ++i) {
        if (tournament_participants[i]->fitness > best->fitness) {
            best = tournament_participants[i];
        }
    }

    return best;
}


// void Population::extinction(int stagnation_threshold) {
//     for (auto& chromosome : chromosomes) {
//         if (chromosome.is_stagnant(stagnation_threshold)) {
//             chromosome = Chromosome(calculate_length());
//         } else {
//             chromosome.increment_change_counter();
//         }
//     }
// }


// void Population::crossover(double crossover_rate, double elitism_rate, double mutation_rate) {
//     auto elites_chromosomes = elitism(elitism_rate);
//     std::vector<Chromosome> new_chromosomes = elites_chromosomes;
//
//     std::uniform_real_distribution<double> distribution(0, 1);
//     auto random_generator = std::mt19937(std::random_device()());
//
//     for (int i = 0; i < (chromosomes.size() - elites_chromosomes.size()) / 2; i++) {
//         int tournament_size = 10;
//         //auto parent1 = tournament_selection(tournament_size);
//         auto parent1 = pick_parent();
//         Chromosome* parent2;
//         do {
//             //parent2 = tournament_selection(tournament_size);
//             parent2 = pick_parent();
//         }while (parent2 == parent1);
//
//         auto child1 = *parent1;
//         auto child2 = *parent2;
//
//         for (int gene = 0; gene < child1.genes.size(); gene++) {
//             if (distribution(random_generator) < crossover_rate) {
//                 std::swap(child1.genes[gene], child2.genes[gene]);
//             }
//         }
//
//         child1.mutate(mutation_rate);
//         child2.mutate(mutation_rate);
//
//         new_chromosomes.push_back(child1);
//         new_chromosomes.push_back(child2);
//     }
//
//     chromosomes = new_chromosomes;
// }


void Population::crossover(double crossover_rate, double elitism_rate, double mutation_rate) {
    auto elites_chromosomes = elitism(elitism_rate);
    std::vector<Chromosome> new_chromosomes = elites_chromosomes;

    std::uniform_real_distribution<double> distribution(0, 1);
    auto random_generator = std::mt19937(std::random_device()());

    for (int i = 0; i < chromosomes.size() - elites_chromosomes.size(); i+=2) {
        int tournament_size = 10;
        //auto parent1 = tournament_selection(tournament_size);
        auto parent1 = &chromosomes[i];
        auto parent2 = &chromosomes[i+1];


        auto child1 = *parent1;
        auto child2 = *parent2;

        for (int gene = 0; gene < child1.genes.size(); gene++) {
            if (distribution(random_generator) < crossover_rate) {
                std::swap(child1.genes[gene], child2.genes[gene]);
            }
        }

        child1.mutate(mutation_rate);
        child2.mutate(mutation_rate);

        new_chromosomes.push_back(child1);
        new_chromosomes.push_back(child2);
    }

    chromosomes = new_chromosomes;
}

double Population::get_best_fitness() {
    double best_fitness = std::numeric_limits<double>::max();
    for (const auto& chromosome : chromosomes) {
        if (chromosome.fitness < best_fitness) {
            best_fitness = chromosome.fitness;
        }
    }
    return best_fitness;
}



Chromosome* Population::pick_parent() {
    std::uniform_real_distribution<double> distribution(0, 1);
    auto random_generator = std::mt19937(std::random_device()());
    double random = distribution(random_generator);

    for (auto&chromosome: chromosomes) {
        if (random >= chromosome.cumsum) {
            return &chromosome;
        }
    }
    return &chromosomes[chromosomes.size() - 1];
}

void Population::mutate(double mutation_rate) {
    for (auto&chromosome: chromosomes) {
        chromosome.mutate(mutation_rate);
    }
}

std::vector<Chromosome> Population::elitism(double elitism_rate) {
    int number_of_elites = floor(chromosomes.size() * elitism_rate);
    std::vector<Chromosome> elite_chromosomes;

    for (int i = 0; i < number_of_elites; i++) {
        elite_chromosomes.push_back(chromosomes[i]);
    }

    // for (int i = chromosomes.size() - 1; i > chromosomes.size() - 1 - number_of_elites; i--) {
    //     elite_chromosomes.push_back(chromosomes[i]);
    // }
    return elite_chromosomes;
}