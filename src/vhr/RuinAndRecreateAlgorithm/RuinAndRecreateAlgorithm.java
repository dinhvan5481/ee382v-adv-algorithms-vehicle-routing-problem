package vhr.RuinAndRecreateAlgorithm;

import vhr.core.ICostCalculator;
import vhr.core.VRPAlgorithm;
import vhr.core.VRPInstance;
import vhr.core.VRPSolution;

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
        VRPSolution vrpSolutionK = generateInitialSolution(vrpInstance);
        VRPSolution vrpSolutionK_p1 = null;
        RuinStragegy ruinStragegy = null;

        // TODO: need to change for loop to while loop which checks on running flag
        for (int numberOfRuns = 0; numberOfRuns < max_run; numberOfRuns++) {
            ruinStragegy = chooseRuinStragegy();
            ruinRate = selectRuinRate();
            numberOfNodesWillBeRuined = (int) Math.ceil(ruinRate * vrpInstance.getNumberOfCustomers());
            ruinStragegy(vrpSolutionK, numberOfNodesWillBeRuined);
            vrpSolutionK_p1 = recreate();
            vrpSolutionK = decideWhatSolutionWillBeUsedForNextLoop(vrpSolutionK, vrpSolutionK_p1);
        }

        return vrpSolutionK;
    }


}
