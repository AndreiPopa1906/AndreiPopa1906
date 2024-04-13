// #include <algorithm>
// #include <cmath>
// #include <iostream>
// #include <fstream>
// #include <limits>
// #include <vector>
// #include <random>
// #include <ctime>
// #include <chrono>
// #include <ctime>
// #include <iomanip>
//
// #include "Chromosome.h"
// #include "Population.h"
//
// //std::ofstream fout("values.out");
//
// double initial_rate = 0.05;
// double min_mutation_rate = 0.01;
// double max_mutation_rate = 0.15;
// int some_threshold = 50;
// double some_factor = 1.1;
//
// using namespace std;
//
// int main() {
//
//     //----------CONSTANTS----------
//     int precision = 5;
//     int population_size = 200;
//     int generations = 2000;
//     double mutation_rate = 1.0 / 500.0;
//     double crossover_rate = 0.3;
//     double elitism_rate = 0.05;
//
//     auto functions = {rastrigin, michalewicz, schwefel, dejong1};
//     auto dimensions = {5,10,30};
//     int runs = 50;
//
//     for(auto dimension : dimensions) {
//         for(auto function : functions) {
//             // ----------CREATE POPULATION----------
//             Population population(population_size, static_cast<int>(dimension), precision, function);
//             std::ofstream fout("output.out", std::ios_base::app);
//             fout << setprecision(5) << fixed;
//             for(int it = 0; it < runs; it++) {
//                 auto start = std::chrono::steady_clock::now();
//                 for (int i = 1; i <= generations; i++) {
//
//                     population.normalize_fitness();
//                     population.calculate_cumsum();
//                     population.crossover(crossover_rate, elitism_rate, mutation_rate);
//
//                 }
//                 auto best_chromosome = population.chromosomes[0];
//                 if(function.function == rastrigin.function)
//                     fout<<"Rastrigin " << dimension << std::endl;
//                 else if(function.function == michalewicz.function)
//                     fout<<"Michalewicz " << dimension << std::endl;
//                 else if(function.function == schwefel.function)
//                     fout<<"Schwefel " << dimension << std::endl;
//                 else if(function.function == dejong1.function)
//                     fout<<"DeJong1 " << dimension << std::endl;
//                 auto stop = std::chrono::steady_clock::now();
//                 auto duration = std::chrono::duration_cast<std::chrono::microseconds>(stop - start);
//                 fout << "Time " << float(duration.count() / 1000000.00) << std::endl;
//                 fout << "Function Value: " << best_chromosome.calculate_function_value(function, static_cast<int>(dimension)) << std::endl;
//                 fout << "\n\n";
//             }
//         }
//     }
//
//
//     return 0;
// }

#include <algorithm>
#include <cmath>
#include <iostream>
#include <fstream>
#include <limits>
#include <vector>
#include <random>
#include <ctime>
#include <chrono>
#include <ctime>
#include <iomanip>

#include "Chromosome.h"
#include "Population.h"

using namespace std;

int main() {
    // Constants
    int precision = 5;
    int population_size = 200;
    int generations = 2000;
    double mutation_rate = 1.0 / 500.0;
    double crossover_rate = 0.3;
    double elitism_rate = 0.05;

    auto functions = {rastrigin, michalewicz, schwefel, dejong1};
    auto dimensions = {5, 10, 30};
    int runs = 50;

    // Open the output file
    ofstream fout("text.out");
    fout << setprecision(5) << fixed;

    // Run the algorithm
    for (auto dimension : dimensions) {
        for (auto function : functions) {
            Population population(population_size, static_cast<int>(dimension), precision, function);

            for (int it = 0; it < runs; it++) {
                auto start = chrono::steady_clock::now();

                for (int i = 1; i <= generations; i++) {
                    population.normalize_fitness();
                    population.calculate_cumsum();
                    population.crossover(crossover_rate, elitism_rate, mutation_rate);
                }

                auto best_chromosome = population.chromosomes[0];
                if (function.function == rastrigin.function)
                    std::cout << "Rastrigin " << dimension << endl;
                else if (function.function == michalewicz.function)
                    std::cout << "Michalewicz " << dimension << endl;
                else if (function.function == schwefel.function)
                    std::cout << "Schwefel " << dimension << endl;
                else if (function.function == dejong1.function)
                    std::cout << "DeJong1 " << dimension << endl;

                auto stop = chrono::steady_clock::now();
                auto duration = chrono::duration_cast<chrono::microseconds>(stop - start);
                std::cout << "Time " << float(duration.count() / 1000000.00) << endl;
                std::cout << "Function Value: " << best_chromosome.calculate_function_value(function, static_cast<int>(dimension)) << endl;
                std::cout << "\n\n";
            }
        }
    }

    fout.close();
    return 0;
}
