package vhr.RuinAndRecreateAlgorithm;

import vhr.RuinAndRecreateAlgorithm.InitializeSolution.GenerateClusteringInitialSolutionStrategy;
import vhr.RuinAndRecreateAlgorithm.InitializeSolution.IGenerateInitialSolutionStrategy;
import vhr.RuinAndRecreateAlgorithm.Recreate.GreedyInsertionStrategy;
import vhr.RuinAndRecreateAlgorithm.Recreate.IRecreateStrategy;
import vhr.RuinAndRecreateAlgorithm.Ruin.IRuinStrategy;
import vhr.RuinAndRecreateAlgorithm.Ruin.RandomRuinStrategy;
import vhr.RuinAndRecreateAlgorithm.Ruin.SequentialRuinStrategy;
import vhr.SolutionAcceptor.SimulationAnnealingSolutionAcceptor;
import vhr.core.*;
import vhr.utils.DataSetReader;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by dinhvan5481 on 3/28/17.
 */
public class Main {
    public static void main(String[] args) {
        String fileName = "./data/A-VRP/A-n37-k6.vrp";
        String routeSolutionFileName = "./solutions/A-VRP/A-n37-k6.csv";
        String logCostFileName = "./solutions/A-VRP/A-n37-k6-cost.csv";
        String logSearchFileName = "./solutions/A-VRP/A-n37-k6.log";
        long randomSeed = 0;
        IDataExtract dataExtract = new DataSetReader();
        IDistanceCalculator distanceCalculator = new Euclid2DDistanceCalculator();
        ICostCalculator costCalculator = new CVRPCostCalculator(distanceCalculator);
        VRPInstance cvrpInstance = null;
        try {
            cvrpInstance = new VRPInstance.Builder(dataExtract)
                    .setDataFileName(fileName)
                    .setCostCalculator(costCalculator)
                    .setDistanceCalculator(distanceCalculator)
                    .setMaxTruck(6)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        GenerateClusteringInitialSolutionStrategy generateInitialSolution =
                new GenerateClusteringInitialSolutionStrategy.Builder(randomSeed).build();
        cvrpInstance.toCSV(fileName.replace(".vrp", ".csv"));
        GenerateClusteringInitialSolutionStrategy generateInitialSolutionStrategy =
                new GenerateClusteringInitialSolutionStrategy
                        .Builder(randomSeed).build();
        IRuinStrategy randomRuinStrategy = new RandomRuinStrategy.Builder(randomSeed).build();
        IRuinStrategy sequentialRuinStrategy = new SequentialRuinStrategy.Builder().build();
        IRecreateStrategy recreateStrategy = new GreedyInsertionStrategy.Builder(cvrpInstance, costCalculator, distanceCalculator).build();
        int maxRun = 50000;

        // TODO: need to work on intial temp
        IVRPAlgorithm ruinAndRecreateAlg = new RuinAndRecreateAlgorithm.Builder(costCalculator, distanceCalculator)
                .setInitializeSolutionStrategy(generateInitialSolutionStrategy)
                .addRuinStrategies(randomRuinStrategy)
                .addRuinStrategies(sequentialRuinStrategy)
                .addRecreateStrategies(recreateStrategy)
                .setMaxRun(maxRun)
                .setLogSolution(logCostFileName)
                .build();
        VRPSolution result = null;
        try {
            result = ruinAndRecreateAlg.solve(cvrpInstance);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        result.toCSV(routeSolutionFileName);
        System.out.print(result.toString());
        FileWriter fileWriter;
        try {
            fileWriter = new FileWriter(logSearchFileName);
            fileWriter.write(result.toString());
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }




    }

}
