package vhr.RuinAndRecreateAlgorithm;

import vhr.RuinAndRecreateAlgorithm.InitializeSolution.IGenerateInitialSolutionStrategy;
import vhr.RuinAndRecreateAlgorithm.Recreate.IRecreateStrategy;
import vhr.RuinAndRecreateAlgorithm.Ruin.IRuinStrategy;
import vhr.core.*;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created by dinhvan5481 on 3/26/17.
 */
public class RuinAndRecreateAlgorithm implements VRPAlgorithm {

    protected ICostCalculator costCalulator;
    protected IDistanceCalculator distanceCalculator;
    protected IGenerateInitialSolutionStrategy generateInitialSolutionStrategy;
    protected IRuinStrategy ruinStrategy;
    protected IRecreateStrategy recreateStrategy;
    protected int maxRun;

    public RuinAndRecreateAlgorithm() {

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
//            vrpSolutionK_p1 = ruinStragegy(vrpInstance, vrpSolutionK, ruinRate);
//            recreate(vrpSolutionK_p1, unServerdCustomers);
//            vrpSolutionK = decideWhatSolutionWillBeUsedForNextLoop(vrpSolutionK, vrpSolutionK_p1);
//            runCounter++;
//            checkIfNeedToRunMoreSearch(vrpSolutionK, vrpSolutionK_p1, runCounter);
//        }


//        return vrpSolutionK;
        return null;
    }

    public static class Builder {
        private RuinAndRecreateAlgorithm result;
        protected ICostCalculator costCalulator;
        protected IDistanceCalculator distanceCalculator;
        protected IGenerateInitialSolutionStrategy generateInitialSolutionStrategy;
        protected IRuinStrategy ruinStrategy;
        protected IRecreateStrategy recreateStrategy;
        private int maxRun;

        public Builder() {

        }

        public RuinAndRecreateAlgorithm build() {

        }
    }

}
