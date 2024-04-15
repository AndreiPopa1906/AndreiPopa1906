package Bonus;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Popa Andrei
 */
public class Application {
    /**
     * Finds maximal cliques among these persons using the MaximalGroupFinder class
     *
     * @param args command line arguments, not used in this application
     */
    public static void main(String[] args) {
        try {
            ExcelUtility excelUtility = new ExcelUtility();
            String pathToExcelFile = "src/main/java/Bonus/sample_abilities.xlsx";
            Map<String, List<String>> personAbilities = excelUtility.readAbilitiesFromExcel(pathToExcelFile);
            MaximalGroupFinder maximalGroupFinder = new MaximalGroupFinder(personAbilities);
            Collection<Set<String>> maximalCliques = maximalGroupFinder.findMaximalCliques();
            maximalCliques.forEach(clique -> System.out.println("Maximal clique: " + clique));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}


