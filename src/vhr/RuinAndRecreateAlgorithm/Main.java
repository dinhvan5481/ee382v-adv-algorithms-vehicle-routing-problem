package vhr.RuinAndRecreateAlgorithm;

import vhr.RuinAndRecreateAlgorithm.InitializeSolution.GenerateClusteringnIntialSolutionStrategy;
import vhr.core.*;
import vhr.utils.DataSetReader;

/**
 * Created by dinhvan5481 on 3/28/17.
 */
public class Main {
    public static void main(String[] args) {
        IDistanceCalulator distanceCalulator = new Euclid2DDistanceCalculator();
        ICostCalculator costCalculator = new CVRPCostCalculator(distanceCalulator);
        GenerateClusteringnIntialSolutionStrategy generateInitialSolution = new GenerateClusteringnIntialSolutionStrategy(distanceCalulator, costCalculator);
        VRPInstance cvhrInstance = DataSetReader.extractData("./data/A-VRP/A-n37-k6.vrp");
        VRPSolution vrpSolution = generateInitialSolution.generateSolution(cvhrInstance);
        System.out.print(vrpSolution.toString());
        System.out.print(vrpSolution.isAValidSolution());


    }
}
