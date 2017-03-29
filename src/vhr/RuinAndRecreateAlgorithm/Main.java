package vhr.RuinAndRecreateAlgorithm;

import vhr.RuinAndRecreateAlgorithm.InitializeSolution.GenerateClusteringnIntialSolutionStrategy;
import vhr.core.Euclid2DDistanceCalculator;
import vhr.core.IDistanceCalulator;
import vhr.core.VRPInstance;
import vhr.core.VRPSolution;
import vhr.utils.DataSetReader;

/**
 * Created by dinhvan5481 on 3/28/17.
 */
public class Main {
    public static void main(String[] args) {
        IDistanceCalulator distanceCalulator = new Euclid2DDistanceCalculator();
        GenerateClusteringnIntialSolutionStrategy generateInitialSolution = new GenerateClusteringnIntialSolutionStrategy(distanceCalulator);
        VRPInstance cvhrInstance = DataSetReader.extractData("./data/A-VRP/A-n32-k5.vrp");
        VRPSolution vrpSolution = generateInitialSolution.generateSolution(cvhrInstance);
        System.out.print(vrpSolution.toString());


    }
}
