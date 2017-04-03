package vhr.RuinAndRecreateAlgorithm.Recreate;

import vhr.core.ICostCalculator;
import vhr.core.IDistanceCalulator;
import vhr.core.VRPInstance;
import vhr.core.VRPSolution;

import java.util.List;

/**
 * Created by dinhvan5481 on 4/1/17.
 */
public class BestInsertionRecreateStrategy extends AbstractRecerateStrategy {

    public BestInsertionRecreateStrategy(VRPInstance vrpInstance, ICostCalculator costCalculator, IDistanceCalulator distanceCalulator) {
        super(vrpInstance, costCalculator, distanceCalulator);
    }

    @Override
    protected VRPSolution recreateRuinedSolution(VRPSolution ruinedSolution, List<Integer> unservedCustomers) {
        return null;
    }
}
