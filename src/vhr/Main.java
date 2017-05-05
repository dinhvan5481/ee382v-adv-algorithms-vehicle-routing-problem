package vhr;

import vhr.ClarkWrightSavingsAlgorithm.ClarkWrightSavingsAlgorithm;
import vhr.RuinAndRecreateAlgorithm.InitializeSolution.IGenerateInitialSolutionStrategy;
import vhr.RuinAndRecreateAlgorithm.InitializeSolution.IncreaseRadicalStrategy;
import vhr.RuinAndRecreateAlgorithm.Recreate.GreedyInsertionStrategy;
import vhr.RuinAndRecreateAlgorithm.Recreate.IRecreateStrategy;
import vhr.RuinAndRecreateAlgorithm.Ruin.IRuinStrategy;
import vhr.RuinAndRecreateAlgorithm.Ruin.RadicalRuinStrategy;
import vhr.RuinAndRecreateAlgorithm.Ruin.RandomRuinStrategy;
import vhr.RuinAndRecreateAlgorithm.Ruin.SequentialRuinStrategy;
import vhr.RuinAndRecreateAlgorithm.RuinAndRecreateAlgorithm;
import vhr.core.*;
import vhr.utils.DataSetReader;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by quachv on 5/2/2017.
 */
public class Main {
    public static void main(String[] args) {
        String baseSolutionPathRR = "./solutions/RR/";
        String baseSolutionPathCWS = "./solutions/CWS/";
        String baseDataPath = "./data/";
        String instanceNameAVRP = "A-VRP";
        String instanceNameBVRP = "B-VRP";
        String instanceNameCEVRP = "CE-VRP";
        String instanceNameFVRP = "F-VRP";
        String instanceNamePVRP = "P-VRP";


//        runSolution(baseDataPath, baseSolutionPathRR, instanceNameAVRP);
//        runSolution(baseDataPath, baseSolutionPathRR, instanceNameBVRP);
//        runSolution(baseDataPath, baseSolutionPathRR, instanceNameCEVRP);
//        runSolution(baseDataPath, baseSolutionPathRR, instanceNameFVRP);
//        runSolution(baseDataPath, baseSolutionPathRR, instanceNamePVRP);

        runCWSAlgorithm(baseDataPath, baseSolutionPathCWS, instanceNameAVRP);
        runCWSAlgorithm(baseDataPath, baseSolutionPathCWS, instanceNameBVRP);
        runCWSAlgorithm(baseDataPath, baseSolutionPathCWS, instanceNameCEVRP);
        runCWSAlgorithm(baseDataPath, baseSolutionPathCWS, instanceNameFVRP);
        runCWSAlgorithm(baseDataPath, baseSolutionPathCWS, instanceNamePVRP);
    }

    private static void runSolution(String baseDataPath, String baseSolutionPath, String instanceName) {
        System.out.println("Start to run ruin and recreate algorithm");
        baseSolutionPath = baseSolutionPath + instanceName + "/";
        List<String> vrpDataFileNames;
        vrpDataFileNames = Arrays.stream(new File(String.format("%s%s", baseDataPath, instanceName)).listFiles())
                .filter(f -> f.getName().endsWith(".vrp"))
                .map(f -> f.getName())
                .collect(Collectors.toList());

        IDataExtract dataExtract = new DataSetReader();
        IDistanceCalculator distanceCalculator = new Euclid2DDistanceCalculator();
        ICostCalculator costCalculator = new CVRPCostCalculator(distanceCalculator);

        List<VRPInstance> vrpInstances = new ArrayList<>();
        for (String fn :
                vrpDataFileNames) {
            String[] fileNameParts = fn.split("-");
            String numberOfTruckStr = fileNameParts[2].replaceAll("\\D+","");
            if(numberOfTruckStr.length() == 0) {
                System.out.println("Cannot retrive number of trucks from instance name: " + fn );
                continue;
            }
            int numberOfTruck = Integer.parseInt(numberOfTruckStr);
            System.out.println("Creating instance: " + fn + " with " + numberOfTruckStr + " trucks");
            try {
                vrpInstances.add(new VRPInstance.Builder(dataExtract)
                        .setDataFileName(String.format("%s%s/%s", baseDataPath, instanceName, fn))
                        .setCostCalculator(costCalculator)
                        .setDistanceCalculator(distanceCalculator)
                        .setMaxTruck(numberOfTruck)
                        .build());
            } catch (Exception e) {
                System.out.println("Error while creating instance: " + fn);
                e.printStackTrace();
            }

        }

        for (VRPInstance cvrpInstance :
                vrpInstances) {
            System.out.println("Solve for instance: " + cvrpInstance.getInstanceName());
            IGenerateInitialSolutionStrategy generateInitialSolutionStrategy = new IncreaseRadicalStrategy();
            IRuinStrategy randomRuinStrategy = new RandomRuinStrategy.Builder().build();
            IRuinStrategy sequentialRuinStrategy = new SequentialRuinStrategy.Builder().build();
            IRuinStrategy radicalRuinStrategy = new RadicalRuinStrategy();
            IRecreateStrategy recreateStrategy = new GreedyInsertionStrategy.Builder(cvrpInstance, costCalculator, distanceCalculator).build();
            int maxRun = 50000;

            IVRPAlgorithm ruinAndRecreateAlg = new RuinAndRecreateAlgorithm.Builder(costCalculator, distanceCalculator)
                    .setInitializeSolutionStrategy(generateInitialSolutionStrategy)
                    .addRuinStrategies(randomRuinStrategy)
                    .addRuinStrategies(sequentialRuinStrategy)
                    .addRuinStrategies(radicalRuinStrategy)
                    .addRecreateStrategies(recreateStrategy)
                    .setMaxRun(maxRun)
                    .setLogSolution(String.format("%s%s-cost.csv", baseSolutionPath, cvrpInstance.getInstanceName()))
                    .build();
            VRPSolution result = null;
            try {
                result = ruinAndRecreateAlg.solve(cvrpInstance);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
            int solutionCost = (int) result.getSolutionCost();
            result.toCSV(String.format("%s%s_%d.csv", baseSolutionPath, cvrpInstance.getInstanceName(), solutionCost));
            System.out.println("Result of instance: " + cvrpInstance.getInstanceName());
            System.out.print(result.toString());
        }

    }

    private static void runCWSAlgorithm(String baseDataPath, String baseSolutionPath, String instanceName) {
        System.out.println("Start to run Clarke Wright algorithm");
        baseSolutionPath = baseSolutionPath + instanceName + "/";
        List<String> vrpDataFileNames;
        vrpDataFileNames = Arrays.stream(new File(String.format("%s%s", baseDataPath, instanceName)).listFiles())
                .filter(f -> f.getName().endsWith(".vrp"))
                .map(f -> f.getName())
                .collect(Collectors.toList());

        IDataExtract dataExtract = new DataSetReader();
        IDistanceCalculator distanceCalculator = new Euclid2DDistanceCalculator();
        ICostCalculator costCalculator = new CVRPCostCalculator(distanceCalculator);

        List<VRPInstance> vrpInstances = new ArrayList<>();
        for (String fn :
                vrpDataFileNames) {
            String[] fileNameParts = fn.split("-");
            String numberOfTruckStr = fileNameParts[2].replaceAll("\\D+","");
            if(numberOfTruckStr.length() == 0) {
                System.out.println("Cannot retrive number of trucks from instance name: " + fn );
                continue;
            }
            int numberOfTruck = Integer.parseInt(numberOfTruckStr);
            System.out.println("Creating instance: " + fn + " with " + numberOfTruckStr + " trucks");
            try {
                vrpInstances.add(new VRPInstance.Builder(dataExtract)
                        .setDataFileName(String.format("%s%s/%s", baseDataPath, instanceName, fn))
                        .setCostCalculator(costCalculator)
                        .setDistanceCalculator(distanceCalculator)
                        .setMaxTruck(numberOfTruck)
                        .build());
            } catch (Exception e) {
                System.out.println("Error while creating instance: " + fn);
                e.printStackTrace();
            }
        }


        for (VRPInstance cvrpInstance :
                vrpInstances) {
            System.out.println("Solve for instance: " + cvrpInstance.getInstanceName());
            ClarkWrightSavingsAlgorithm clarkWrightSavingsAlgorithm = new ClarkWrightSavingsAlgorithm();
            VRPSolution result = null;
            try {
                result = clarkWrightSavingsAlgorithm.solve(cvrpInstance);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
            int solutionCost = (int) result.getSolutionCost();
            result.toCSV(String.format("%s%s_%d.csv", baseSolutionPath, cvrpInstance.getInstanceName(), solutionCost));
            System.out.println("Result of instance: " + cvrpInstance.getInstanceName());
            System.out.print(result.toString());
        }
    }
}
