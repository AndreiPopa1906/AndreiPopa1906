#include "AntColony.h"
#include "Edge.h"
#include "Node.h"

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

double translateValue(double value, double min1, double max1, double min2, double max2) {
    return min2 + (max2 - min2) * ((value - min1) / (max1 - min1));
}



// Constructor
// AntColony::AntColony(std::string variation, int size, double elitist_weight, double minFactor, double alpha, double beta,
//         double rho, double phe_deposit_weight, double pheromone, int max_iterations, const std::vector<Node>& nodes)
// : variation(variation), size(size), elitist_weight(elitist_weight), minFactor(minFactor),
//     alpha(alpha), beta(beta), rho(rho), phe_deposit_weight(phe_deposit_weight),
//     max_iterations(max_iterations), n_nodes(nodes.size()), nodes(nodes),
//     best_distance(std::numeric_limits<double>::max()),
//     local_best_distance(std::numeric_limits<double>::max()) {

//     // Inițializarea matricei de muchii
//     edges = std::vector<std::vector<Edge>>(n_nodes, std::vector<Edge>(n_nodes));

//     for (int i = 0; i < n_nodes; ++i) {
//         for (int j = 0; j < n_nodes; ++j) {
//             double heuristic = 0; // Calculează sau obține valoarea euristică între nodul i și nodul j
//             if (i != j) {
//                 // Presupunând că Node are membri x și y pentru coordonate
//                 heuristic = std::sqrt(std::pow(nodes[i].x - nodes[j].x, 2) + std::pow(nodes[i].y - nodes[j].y, 2));
//             }
//             edges[i][j] = Edge(i, j, heuristic, pheromone);
//         }
//     }

//     // Crearea furnicilor cu acces la matricea de muchii
//     for (int i = 0; i < size; ++i) {
//         ants.emplace_back(edges, alpha, beta, n_nodes);
//     }
// }

// AntColony(std::string variation, int size, double elitist_weight, double minFactor, double alpha, double beta,
//       double rho, double phe_deposit_weight, double pheromone, int max_iterations, const std::vector<Node>& nodes)
// : variation(variation), size(size), elitist_weight(elitist_weight), minFactor(minFactor),
//   alpha(alpha), beta(beta), rho(rho), phe_deposit_weight(phe_deposit_weight),
//   max_iterations(max_iterations), n_nodes(nodes.size()), nodes(nodes),
//   best_distance(std::numeric_limits<double>::max()),
//   local_best_distance(std::numeric_limits<double>::max()) {

//     // Inițializarea matricei de muchii
//     edges.resize(n_nodes);
//     for (int i = 0; i < n_nodes; ++i) {
//         edges[i].resize(n_nodes);
//         for (int j = 0; j < n_nodes; ++j) {
//             double heuristic = (i != j) ? std::sqrt(std::pow(nodes[i].x - nodes[j].x, 2) + std::pow(nodes[i].y - nodes[j].y, 2)) : 0.0;
//             edges[i][j] = Edge(i, j, heuristic, pheromone);
//         }
//     }

//     // Crearea furnicilor cu acces la matricea de muchii
//     for (int i = 0; i < size; ++i) {
//         ants.emplace_back(edges, alpha, beta, n_nodes);
//     }
// }

// Constructor
AntColony::AntColony(std::string variation, int size, double elitist_weight, double minFactor, double alpha, double beta,
        double rho, double phe_deposit_weight, double pheromone, int max_iterations, const std::vector<Node>& nodes)
: variation(variation), size(size), elitist_weight(elitist_weight), minFactor(minFactor),
    alpha(alpha), beta(beta), rho(rho), phe_deposit_weight(phe_deposit_weight),
    max_iterations(max_iterations), n_nodes(nodes.size()), nodes(nodes),
    best_distance(std::numeric_limits<double>::max()),
    local_best_distance(std::numeric_limits<double>::max()) {

    // Inițializarea matricei de muchii
    edges = std::vector<std::vector<Edge>>(n_nodes, std::vector<Edge>(n_nodes, Edge()));

    for (int i = 0; i < n_nodes; ++i) {
        for (int j = 0; j < n_nodes; ++j) {
            double heuristic = 0; // Calculează sau obține valoarea euristică între nodul i și nodul j
            if (i != j) {
                heuristic = std::sqrt(std::pow(nodes[i].x - nodes[j].x, 2) + std::pow(nodes[i].y - nodes[j].y, 2));
            }
            edges[i][j] = Edge(i, j, heuristic, pheromone);
        }
    }

    for (int i = 0; i < size; ++i) {
        ants.emplace_back(edges, alpha, beta, n_nodes);
    }
}



void AntColony::AddPheromone(const std::vector<int>& tour, double distance, double heuristic = 1) {
    double pheromone_to_add = this->phe_deposit_weight / distance;
    for (size_t i = 0; i < this->n_nodes; ++i) {
        this->edges[tour[i]][tour[(i + 1) % this->n_nodes]].pheromone += pheromone_to_add * heuristic;
    }
}

void AntColony::ACS() {
    for (auto& ant : this->ants) {
        std::vector<int> aux1 = ant.UpdateTour();
        double aux2 = ant.CalculateDistance();
        this->AddPheromone(aux1, aux2);
        if (ant.distance < this->best_distance) {
            this->best_tour = ant.tour;
            this->best_distance = ant.distance;
        }
    }

    for (size_t x = 0; x < this->n_nodes; ++x) {
        for (size_t y = x + 1; y < this->n_nodes; ++y) {
            this->edges[x][y].pheromone *= (1 - this->rho);
        }
    }

    printf("End of ACS\n");
}

void AntColony::ELITIST() {
    for (auto& ant : this->ants) {
        std::vector<int> aux1 = ant.UpdateTour();
        double aux2 = ant.CalculateDistance();
        this->AddPheromone(aux1, aux2);
        if (ant.distance < this->best_distance) {
            this->best_tour = ant.tour;
            this->best_distance = ant.distance;
        }
    }

    this->AddPheromone(this->best_tour, this->best_distance, this->elitist_weight);

    for (size_t x = 0; x < this->n_nodes; ++x) {
        for (size_t y = 0; y < this->n_nodes; ++y) {
            this->edges[x][y].pheromone *= (1 - this->rho);
        }
    }
}

void AntColony::MAX_MIN(int counter) {
    double _best_distance = std::numeric_limits<double>::max();
    std::vector<int> _best_tour;
    for (auto& ant : this->ants) {
        ant.UpdateTour();
        if (ant.CalculateDistance() < _best_distance) {
            _best_tour = ant.tour;
            _best_distance = ant.distance;
        }
    }

    double max_pheromone;
    if ((counter + 1) / this->max_iterations <= 0.75) {
        this->AddPheromone(_best_tour, _best_distance);
        max_pheromone = this->phe_deposit_weight / _best_distance;
    } else {
        if (_best_distance < this->best_distance) {
            this->best_tour = _best_tour;
            this->best_distance = _best_distance;
        }
        this->AddPheromone(this->best_tour, this->best_distance);
        max_pheromone = this->phe_deposit_weight / this->best_distance;
    }

    double min_pheromone = max_pheromone * this->minFactor;

    for (size_t x = 0; x < this->n_nodes; ++x) {
        for (size_t y = x + 1; y < this->n_nodes; ++y) {
            this->edges[x][y].pheromone *= (1 - this->rho);
            if (this->edges[x][y].pheromone > max_pheromone) {
                this->edges[x][y].pheromone = max_pheromone;
            } else if (this->edges[x][y].pheromone < min_pheromone) {
                this->edges[x][y].pheromone = min_pheromone;
            }
        }
    }
}

void AntColony::Simulate(int counter) {
    if (this->variation == "ACS") {
        this->ACS();
    } else if (this->variation == "ELITIST") {
        this->ELITIST();
    } else if (this->variation == "MAX-MIN") {
        this->MAX_MIN(counter);
    }
}

//Pentru Simulated Annealing

// std::vector<int> AntColony::GenerateRandomTour() {
//     std::vector<int> tour(n_nodes); // n_nodes este numărul de noduri din graf
//     std::iota(tour.begin(), tour.end(), 0); // Inițializează turul cu nodurile în ordine (0, 1, 2, ...)
    
//     std::random_device rd;
//     std::mt19937 g(rd());
    
//     std::shuffle(tour.begin(), tour.end(), g); // Amestecă nodurile pentru a obține un tur aleatoriu

//     return tour;
// }

// double AntColony::CalculateTourDistance(const std::vector<int>& tour) {
//     double totalDistance = 0.0;

//     for (size_t i = 0; i < tour.size(); ++i) {
//         const Node& currentNode = nodes[tour[i]];
//         const Node& nextNode = nodes[tour[(i + 1) % tour.size()]]; // Folosește modulo pentru a reveni la primul nod după ultimul

//         totalDistance += Distance(currentNode, nextNode);
//     }

//     return totalDistance;
// }

// void AntColony::SetBestTour(const std::vector<int>& tour) {
//     double tourDistance = CalculateTourDistance(tour);

//     if (best_tour.empty() || tourDistance < best_distance) {
//         best_tour = tour;
//         best_distance = tourDistance;
//     }
// }

// std::vector<int> AntColony::GenerateNeighbourTour(const std::vector<int>& currentTour) {
//     if (currentTour.size() < 2) {
//         return currentTour;
//     }

//     std::vector<int> neighbourTour = currentTour;

//     std::random_device rd;
//     std::mt19937 gen(rd());
//     std::uniform_int_distribution<> dis(0, neighbourTour.size() - 1);

//     int index1 = dis(gen);
//     int index2 = dis(gen);

//     while (index1 == index2) {
//         index2 = dis(gen); 
//     }

//     std::swap(neighbourTour[index1], neighbourTour[index2]);

//     return neighbourTour;
// }