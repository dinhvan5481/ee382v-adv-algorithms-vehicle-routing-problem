package vhr.ClarkWrightSavingsAlgorithm;

import vhr.core.*;
import vhr.utils.DataSetReader;

/**
 * Created by quachv on 4/21/2017.
 */
public class MainCWSaving {
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
        VRPSolution result = null;
        ClarkWrightSavingsAlgorithm clarkWrightSavingsAlgorithm = new ClarkWrightSavingsAlgorithm();
        try {
            result = clarkWrightSavingsAlgorithm.solve(cvrpInstance);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

    }
}
