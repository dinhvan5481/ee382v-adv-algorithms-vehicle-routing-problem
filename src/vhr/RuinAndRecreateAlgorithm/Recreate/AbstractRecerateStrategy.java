package vhr.RuinAndRecreateAlgorithm.Recreate;

import vhr.core.ICostCalculator;
import vhr.core.IDistanceCalulator;
import vhr.core.VRPInstance;
import vhr.core.VRPSolution;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dinhvan5481 on 4/2/17.
 */
public abstract class AbstractRecerateStrategy implements IRecreateStrategy {

    protected VRPInstance vrpInstance;
    protected ICostCalculator costCalculator;
    protected IDistanceCalulator distanceCalulator;

    protected AbstractRecerateStrategy(VRPInstance vrpInstance, ICostCalculator costCalculator, IDistanceCalulator distanceCalulator) {
        this.vrpInstance = vrpInstance;
        this.costCalculator = costCalculator;
        this.distanceCalulator = distanceCalulator;
    }

    @Override
    public VRPSolution recreate(VRPSolution ruinedSolution, List<Integer> unservedCustomer) {
        VRPSolution copiedRuinedSolution = null;
        try {
            copiedRuinedSolution = ruinedSolution.clone();

        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        List<Integer> copiedUnservedCustomer = new ArrayList<>(unservedCustomer);
        return recreateRuinedSolution(copiedRuinedSolution, copiedUnservedCustomer);
    }

    protected abstract VRPSolution recreateRuinedSolution(VRPSolution ruinedSolution, List<Integer> unservedCustomers);
}
