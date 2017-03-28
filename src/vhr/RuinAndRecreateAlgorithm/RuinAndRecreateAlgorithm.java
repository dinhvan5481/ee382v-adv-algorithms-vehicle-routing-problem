package vhr.RuinAndRecreateAlgorithm;

import vhr.core.*;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created by dinhvan5481 on 3/26/17.
 */
public class RuinAndRecreateAlgorithm implements VRPAlgorithm {

    protected ICostCalculator costCalulator;
    public RuinAndRecreateAlgorithm(ICostCalculator iCostCalculator) {
        this.costCalulator = iCostCalculator;
    }

    @Override
    public VRPSolution solve(VRPInstance vrpInstance) {
        float ruinRate = 0;
        int numberOfNodesWillBeRuined = 0;
        int runCounter = 0;
        boolean continueSearchSolutionFlag = true;
//        VRPSolution vrpSolutionK = generateInitialSolution(vrpInstance);
//        VRPSolution vrpSolutionK_p1 = null;
//        RuinStragegy ruinStragegy = null;
//        HashSet<Customer> unServerdCustomers = new HashSet<>();
//
//        // TODO: need to change for loop to while loop which checks on running flag
//        while (continueSearchSolutionFlag) {
//            ruinStragegy = chooseRuinStragegy();
//            ruinRate = selectRuinRate();
//            numberOfNodesWillBeRuined = (int) Math.ceil(ruinRate * vrpInstance.getNumberOfCustomers());
//            vrpSolutionK_p1 = ruinStragegy(vrpSolutionK, numberOfNodesWillBeRuined, unServerdCustomers);
//            recreate(vrpSolutionK_p1, unServerdCustomers);
//            vrpSolutionK = decideWhatSolutionWillBeUsedForNextLoop(vrpSolutionK, vrpSolutionK_p1);
//            runCounter++;
//            checkIfNeedToRunMoreSearch(vrpSolutionK, vrpSolutionK_p1, runCounter);
//        }

//        return vrpSolutionK;
        return null;
    }


}
