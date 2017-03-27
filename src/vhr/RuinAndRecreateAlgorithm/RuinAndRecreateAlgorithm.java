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
        return null;
    }
}
