package vhr.RuinAndRecreateAlgorithm;

import vhr.RuinAndRecreateAlgorithm.InitializeSolution.GenerateClusteringnIntialSolutionStrategy;
import vhr.core.*;
import vhr.utils.DataSetReader;

/**
 * Created by dinhvan5481 on 3/28/17.
 */
public class Main {
    public static void main(String[] args) {
        String fileName = "./data/A-VRP/A-n37-k6.vrp";
        String routeSolutionFileName = "./solutions/A-VRP/A-n37-k6.csv";
        VRPInstance cvrpInstance = DataSetReader.extractData(fileName);
        IDistanceCalculator distanceCalulator = new Euclid2DDistanceCalculator();
        ICostCalculator costCalculator = new CVRPCostCalculator(cvrpInstance, distanceCalulator);
        GenerateClusteringnIntialSolutionStrategy generateInitialSolution = new GenerateClusteringnIntialSolutionStrategy(distanceCalulator, costCalculator);
        cvrpInstance.toCSV(fileName.replace(".vrp", ".csv"));
        VRPSolution vrpSolution = generateInitialSolution.generateSolution(cvrpInstance);
        vrpSolution.toCSV(routeSolutionFileName);


    }
}
