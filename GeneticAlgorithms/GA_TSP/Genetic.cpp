#include "Genetic.h"
#include "Node.h"
#include "Edge.h"
#include <algorithm>
#include <unordered_set>

template<typename T>
T PickSelection(const std::vector<T>& myList, const std::vector<double>& probabilities) {
    std::random_device rd;
    std::mt19937 gen(rd());
    std::uniform_real_distribution<> dis(0, 1);

    double r = dis(gen);
    size_t i = 0;
    while (r > 0 && i < probabilities.size()) {
        r -= probabilities[i];
        i++;
    }
    return i > 0 ? myList[i - 1] : myList[0]; // Return first element if no selection
}

double Distance(const Node& a, const Node& b) {
    return std::sqrt((b.x - a.x) * (b.x - a.x) + (b.y - a.y) * (b.y - a.y));
}

double SumDistance(const std::vector<Node>& points) {
    //std::cout << "Nodes size in SumDistance: " << points.size() << std::endl;
    double s = 0;
    for (size_t i = 0; i < points.size(); ++i) {
        double dist = Distance(points[i], points[(i + 1) % points.size()]);
        s += dist;
    }
    return s;
}

std::vector<int> LexicalOrder(std::vector<int> orderList) {
    int x = -1;
    int y = -1;

    for (size_t i = 0; i < orderList.size() - 1; ++i) {
        if (orderList[i] < orderList[i + 1]) {
            x = i;
            for (size_t j = i + 1; j < orderList.size(); ++j) {
                if (orderList[i] < orderList[j]) {
                    y = j;
                }
            }
            break;
        }
    }

    if (x == -1) return orderList; 

    std::swap(orderList[x], orderList[y]);
    std::reverse(orderList.begin() + x + 1, orderList.end());
    return orderList;
}

Genetic::Genetic(int populationSize) // am schimbat mutation rate
    : size(populationSize), record(std::numeric_limits<double>::max()),
      currentDist(std::numeric_limits<double>::max()), fitestIndex(0), mutation_rate(0.00001) {
    population.resize(populationSize);
    for (auto& individual : population) {
        individual = std::vector<int>();
    }
    fitness.resize(populationSize, 0.0);
}


Genetic::Genetic(const std::vector<std::vector<int>>& initialPopulation, int populationSize)
    : size(populationSize), record(std::numeric_limits<double>::max()),
      currentDist(std::numeric_limits<double>::max()), fitestIndex(0), mutation_rate(0.00001) {
    if (!initialPopulation.empty()) {
        population = initialPopulation;
    } else {
        population.resize(populationSize);
        for (auto& individual : population) {
            individual = std::vector<int>();
        }
    }
    fitness.resize(populationSize, 0.0);
}


// void Genetic::CalculateFitness(const std::vector<Node>& points) {
//     printf("Start CalculateFitness\n");
//     for (int i = 0; i < size; ++i) {
//         if (population[i].empty()) {
//             std::cerr << "Eroare: Populația la indexul " << i << " este goală." << std::endl;
//             continue;
//         }
//         std::vector<Node> nodes;
//          for (int j : population[i]) {
//             if (j < 0 || j >= points.size()) {
//                 std::cerr << "Eroare: Index invalid in population[" << i << "] = " << j << std::endl;
//                 continue; // Sări peste acest index
//             }
//             nodes.push_back(points[j]);
//         }
//         printf("Passed push_back\n");
//         double dist = SumDistance(nodes);

//         if (dist < currentDist) {
//             current = population[i];
//             currentDist = dist;
//         }
//         printf("Passed currentDist\n");

//         if (dist < record) {
//             record = dist;
//             fitest = population[i];
//             fitestIndex = i;
//         }
//         if (i >= fitness.size()) {
//             std::cerr << "Eroare: Indexul " << i << " este in afara limitelor pentru fitness" << std::endl;
//             continue; 
//         }
//         fitness[i] = 1 / (dist + 1);
//     }
//     printf("Passed for loop\n");

//     NormalizeFitness();
//     std::cout << "End CalculateFitness" << std::endl;
// }

void Genetic::CalculateFitness(const std::vector<Node>& points) {
    //printf("Start CalculateFitness\n");
    for (int i = 0; i < size; ++i) {
        if (population[i].size() != points.size()) {
            std::cerr << "Eroare: Dimensiunea indivizilor din populație nu corespunde cu numărul de puncte." << std::endl;
            continue;
        }

        double dist = 0;
        for (size_t j = 0; j < population[i].size(); ++j) {
            int index = population[i][j];
            int nextIndex = population[i][(j + 1) % population[i].size()];

            if (index < 0 || index >= points.size() || nextIndex < 0 || nextIndex >= points.size()) {
                std::cerr << "Eroare: Index invalid in population[" << i << "] la poziția " << j << std::endl;
                dist = std::numeric_limits<double>::max();
                break;
            }

            dist += Distance(points[index], points[nextIndex]);
        }

        if (dist < currentDist) {
            current = population[i];
            currentDist = dist;
        }

        if (dist < record) {
            record = dist;
            fitest = population[i];
            fitestIndex = i;
        }

        fitness[i] = dist != std::numeric_limits<double>::max() ? 1 / (dist + 1) : 0;
    }

    NormalizeFitness();
    //std::cout << "End CalculateFitness" << std::endl;
}


void Genetic::NormalizeFitness() {
    double s = 0;
    for (double fit : fitness) {
        s += fit;
    }
    for (double& fit : fitness) {
        fit /= s;
    }
}

void Genetic::Mutate(std::vector<int>& genes, int generation, int maxGenerations) {
    // Calcularea unei rate de mutație dinamice
    double dynamicMutationRate = mutation_rate;
    if (generation < maxGenerations) {
        dynamicMutationRate *= 0.70;
    }

    std::random_device rd;
    std::mt19937 gen(rd());
    std::uniform_real_distribution<> dis(0, 1);
    std::uniform_int_distribution<> indexDist(0, genes.size() - 1);

    for (int i = 0; i < genes.size(); ++i) {
        if (dis(gen) < dynamicMutationRate) {
            int a = indexDist(gen);
            int b = indexDist(gen);
            std::swap(genes[a], genes[b]);
        }
    }
}

std::vector<int> Genetic::CrossOver(const std::vector<int>& genes1, const std::vector<int>& genes2) {
    std::random_device rd;
    std::mt19937 gen(rd());
    std::uniform_real_distribution<> dis(0.0, 1.0);

    double crossoverRate = 0.7;
    if (dis(gen) > crossoverRate) {
        return dis(gen) < 0.5 ? genes1 : genes2;
    }

    std::uniform_int_distribution<> indexDist(0, genes1.size() - 1);
    int start = indexDist(gen);
    int end = indexDist(gen);

    if (start > end) {
        std::swap(start, end);
    }

    std::vector<int> new_genes(genes1.size(), -1);
    std::unordered_set<int> genesSet;

    for (int i = start; i <= end; ++i) {
        new_genes[i] =(dis(gen) > 0.5) ? genes1[i] : genes2[i];
        genesSet.insert(genes1[i]);
    }

    // Inserarea genelor din genes2, evitând duplicatele
    for (int gene : genes2) {
        if (genesSet.find(gene) == genesSet.end()) { // Verifică dacă gena nu există deja
            for (int& slot : new_genes) {
                if (slot == -1) { // Găsește primul slot liber
                    slot = gene;
                    genesSet.insert(gene); // Adaugă gena în set pentru a preveni duplicatele
                    break;
                }
            }
        }
    }


    // Completează restul genelor din al doilea părinte
    // int idx = (end + 1) % genes1.size(); // Începe de la următorul index după end
    // for (int i = (end + 1) % genes1.size(); new_genes[idx] == -1; idx = (idx + 1) % genes1.size()) {
    //     if (genesSet.find(genes2[i]) == genesSet.end()) {
    //         new_genes[idx] = genes2[i];
    //         genesSet.insert(genes2[i]);
    //     }
    //     i = (i + 1) % genes1.size(); // Continuă cu următorul index din genes2
    // }

    return new_genes;
}

std::vector<int> Genetic::RouletteSelection(const std::vector<double>& fitness) {
    // Suma fitness-urilor pentru a crea o ruletă
    double totalFitness = std::accumulate(fitness.begin(), fitness.end(), 0.0);
    std::random_device rd;
    std::mt19937 gen(rd());
    std::uniform_real_distribution<> dis(0.0, totalFitness);

    // Rotim ruleta
    double spin = dis(gen);
    double partialSum = 0.0;

    for (size_t i = 0; i < fitness.size(); ++i) {
        partialSum += fitness[i];
        if (partialSum >= spin) {
            return population[i]; // Selectează individul curent
        }
    }

    // În cazul improbabil în care nu selectăm un individ (din cauza erorilor de rotunjire), returnăm ultimul individ
    return population.back();
}

void Genetic::NaturalSelection() {
    std::vector<std::vector<int>> nextPopulation;
    for (int i = 0; i < size; ++i) {
        //std::vector<int> parent1 = RouletteSelection(fitness);
        std::vector<int> parent1 = PickSelection(population, fitness);
        std::vector<int> parent2 = RouletteSelection(fitness);
        std::vector<int> child = CrossOver(parent1, parent2);

        Mutate(child, i, num_generations); // presupunând că num_generations este definit în clasa Genetic
        nextPopulation.push_back(child);
    }
    population = nextPopulation;
}


// std::vector<int> Genetic::CrossOver(const std::vector<int>& genes1, const std::vector<int>& genes2) {
//     if (genes1.size() != genes2.size()) {
//         throw std::invalid_argument("Genes vectors must be of the same size.");
//     }

//     std::random_device rd;
//     std::mt19937 gen(rd());
//     std::uniform_real_distribution<> chance(0.0, 1.0);

//     double crossoverRate = 0.7;

//     // Decide dacă se va face încrucișarea sau nu
//     if (chance(gen) > crossoverRate) {
//         // Nu se face încrucișare, returnează unul dintre părinți
//         return (chance(gen) < 0.5) ? genes1 : genes2;
//     }

//     std::uniform_int_distribution<> dist(0, genes1.size() - 1);

//     int point1 = dist(gen);
//     int point2 = dist(gen);

//     // Asigură că point1 este mai mic decât point2
//     if (point1 > point2) {
//         std::swap(point1, point2);
//     }

//     std::vector<int> new_genes(genes1.size(), -1);
//     std::unordered_set<int> added_genes;

//     // Copiază secțiunea dintre cele două puncte din primul părinte
//     for (int i = point1; i <= point2; ++i) {
//         new_genes[i] = genes1[i];
//         added_genes.insert(genes1[i]);
//     }

//     // Completează restul genelor din al doilea părinte
//     int geneIdx = 0;
//     for (int i = 0; i < new_genes.size(); ++i) {
//         if (new_genes[i] == -1) {
//             while (added_genes.find(genes2[geneIdx]) != added_genes.end()) {
//                 geneIdx++;
//             }
//             new_genes[i] = genes2[geneIdx];
//             added_genes.insert(genes2[geneIdx]);
//         }
//     }

//     return new_genes;
// }



// void Genetic::Mutate(std::vector<int>& genes, int generation, int maxGenerations) {
//     std::random_device rd;
//     std::mt19937 gen(rd());

//     double dynamicMutationRate = AdjustMutationRate(generation, maxGenerations);

//     std::uniform_real_distribution<> dis(0, 1);
//     std::uniform_int_distribution<> indexDist(0, genes.size() - 1);

//     for (int i = 0; i < genes.size(); ++i) {
//         if (dis(gen) < dynamicMutationRate) {
//             int a = indexDist(gen);
//             int b = indexDist(gen);
//             std::swap(genes[a], genes[b]);
//         }
//     }
// }

// double Genetic::AdjustMutationRate(int generation, int maxGenerations) {
//     double decay = static_cast<double>(generation) / maxGenerations;
//     return mutation_rate * (1 - decay);
// }


// std::vector<int> Genetic::CrossOver(const std::vector<int>& genes1, const std::vector<int>& genes2) {
//     std::random_device rd;
//     std::mt19937 gen(rd());
//     std::uniform_int_distribution<> startDist(0, genes1.size() - 1);

//     int start = startDist(gen);
//     int end = std::min(start + startDist(gen), static_cast<int>(genes2.size() - 1));

//     std::vector<int> new_genes(genes1.begin() + start, genes1.begin() + end);
//     for (int p : genes2) {
//         if (std::find(new_genes.begin(), new_genes.end(), p) == new_genes.end()) {
//             new_genes.push_back(p);
//         }
//     }
//     return new_genes;
// }





// std::vector<int> Genetic::CrossOver(const std::vector<int>& genes1, const std::vector<int>& genes2) {
//     if (genes1.size() != genes2.size()) {
//         throw std::invalid_argument("Genes vectors must be of the same size.");
//     }

//     std::random_device rd;
//     std::mt19937 gen(rd());
//     std::uniform_int_distribution<> dist(0, genes1.size() - 1);

//     int point1 = dist(gen);
//     int point2 = dist(gen);

//     // Asigură că point1 este mai mic decât point2
//     if (point1 > point2) {
//         std::swap(point1, point2);
//     }

//     std::vector<int> new_genes(genes1.size(), -1);
//     std::unordered_set<int> added_genes;

//     // Copiază secțiunea dintre cele două puncte din primul părinte
//     for (int i = point1; i <= point2; ++i) {
//         new_genes[i] = genes1[i];
//         added_genes.insert(genes1[i]);
//     }

//     // Completează restul genelor din al doilea părinte
//     int geneIdx = 0;
//     for (int i = 0; i < new_genes.size(); ++i) {
//         if (new_genes[i] == -1) {
//             while (added_genes.find(genes2[geneIdx]) != added_genes.end()) {
//                 geneIdx++;
//             }
//             new_genes[i] = genes2[geneIdx];
//             added_genes.insert(genes2[geneIdx]);
//         }
//     }

//     return new_genes;
// }



// void Genetic::NaturalSelection() {
//     std::vector<std::vector<int>> nextPopulation;
//     for (int i = 0; i < size; ++i) {
//         // Se presupune existența unei funcții `PickSelection`
//         std::vector<int> generation1 = PickSelection(population, fitness);
//         std::vector<int> generation2 = PickSelection(population, fitness);
//         std::vector<int> genes = CrossOver(generation1, generation2);
//         Mutate(genes);
//         nextPopulation.push_back(genes);
//     }
//     population = nextPopulation;
// }

// void Genetic::NaturalSelection() { // ---------------------------
//     std::vector<std::vector<int>> nextPopulation;
//     for (int i = 0; i < size; ++i) {
//         std::vector<int> generation1 = PickSelection(population, fitness);
//         std::vector<int> generation2 = PickSelection(population, fitness);
//         std::vector<int> genes = CrossOver(generation1, generation2);

//         // Modifică apelul funcției Mutate pentru a include generația curentă și numărul maxim de generații
//         Mutate(genes, i, num_generations); // presupunând că num_generations este definit în clasa Genetic

//         nextPopulation.push_back(genes);
//     }
//     population = nextPopulation;
// } // --------------------------

// void Genetic::Mutate(std::vector<int>& genes) {
//     std::random_device rd;
//     std::mt19937 gen(rd());
//     std::uniform_real_distribution<> dis(0, 1);
//     std::uniform_int_distribution<> indexDist(0, genes.size() - 1);

//     for (int i = 0; i < genes.size(); ++i) {
//         if (dis(gen) < mutation_rate) {
//             int a = indexDist(gen);
//             int b = indexDist(gen);
//             std::swap(genes[a], genes[b]);
//         }
//     }
// }