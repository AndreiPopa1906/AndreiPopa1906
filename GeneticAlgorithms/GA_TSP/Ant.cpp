#include "Ant.h"
#include "Edge.h"
#include "Node.h"
#include <algorithm>

// class Edge {
// public:
//     int a, b;
//     double heuristic, pheromone;

//     Edge() : a(0), b(0), heuristic(0.0), pheromone(0.0) {}

//     Edge(int a, int b, double heuristic, double pheromone)
//         : a(a), b(b), heuristic(heuristic), pheromone(pheromone) {}
// };

inline double Distance(const Node& a, const Node& b) {
    return std::sqrt((b.x - a.x) * (b.x - a.x) + (b.y - a.y) * (b.y - a.y));
}

inline double SumDistance(const std::vector<Node>& points) {
    std::cout << "Nodes size in SumDistance: " << points.size() << std::endl;
    double s = 0;
    for (size_t i = 0; i < points.size(); ++i) {
        double dist = Distance(points[i], points[(i + 1) % points.size()]);
        s += dist;
    }
    return s;
}


Ant::Ant(const std::vector<std::vector<Edge>>& edges, double alpha, double beta, int n_nodes)
    : edges(edges), alpha(alpha), beta(beta), n_nodes(n_nodes), distance(0.0) {}

int Ant::NodeSelection() {
    double roulette_wheel = 0;
    std::vector<int> states;
    for (int i = 0; i < n_nodes; ++i) {
        if (std::find(tour.begin(), tour.end(), i) == tour.end())
            states.push_back(i);
    }

    double heuristic_value = 0;
    for (int new_state : states) {
        heuristic_value += edges[tour.back()][new_state].heuristic;
    }

    for (int new_state : states) {
        double A = std::pow(edges[tour.back()][new_state].pheromone, alpha);
        double B = std::pow((heuristic_value / edges[tour.back()][new_state].heuristic), beta);
        roulette_wheel += A * B;
    }

    std::random_device rd;
    std::mt19937 gen(rd());
    std::uniform_real_distribution<> dis(0, roulette_wheel);

    double random_value = dis(gen);
    double wheel_position = 0;

    for (int new_state : states) {
        double A = std::pow(edges[tour.back()][new_state].pheromone, alpha);
        double B = std::pow((heuristic_value / edges[tour.back()][new_state].heuristic), beta);
        wheel_position += A * B;
        if (wheel_position >= random_value) {
            return new_state;
        }
    }

    return -1;
}

// std::vector<int> Ant::UpdateTour() {
//     printf("Entered UpdateTour\n");
//     std::random_device rd;
//     std::mt19937 gen(rd());
//     std::uniform_int_distribution<> dis(0, n_nodes - 1);

//     tour.push_back(dis(gen));
//     while (tour.size() < n_nodes) {
//         tour.push_back(NodeSelection());
//     }
//     printf("Exited UpdateTour\n");

//     return tour;
// }

std::vector<int> Ant::UpdateTour() {
    //printf("Entered UpdateTour\n");
    std::random_device rd;
    std::mt19937 gen(rd());
    std::uniform_int_distribution<> dis(0, n_nodes - 1);
    //printf("Passed initialisation\n");

    tour.push_back(dis(gen)); // Adăugați primul nod aleator
    //printf("Passed tour access\n");

    while (tour.size() < n_nodes) {
        int next_node = NodeSelection();
        if (next_node == -1) {
            // Gestionare caz în care NodeSelection returnează -1
            std::cerr << "Eroare: NodeSelection a returnat -1" << std::endl;
            break; // Ieșire din buclă pentru a evita loop infinit sau comportament nedefinit
        }
        tour.push_back(next_node);
    }

    return tour;
}


double Ant::CalculateDistance() {
    //printf("Entered CalculateDistance\n");
    distance = 0;
    //std :: cout << "N_nodes: " <<  n_nodes << std::endl;
    for (int i = 0; i < n_nodes; ++i) {
        //printf("Iterator: %d\n", i);
        distance += edges[tour[i]][tour[(i + 1) % n_nodes]].heuristic;
        //printf("Iterator(2): %d\n", i);
        //std::cout << "Distance: " << distance << std::endl;
    }

    //printf("Exited CalculateDistance\n");
    return distance;
}

