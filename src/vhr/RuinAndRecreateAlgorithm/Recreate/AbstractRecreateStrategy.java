package vhr.RuinAndRecreateAlgorithm.Recreate;

import vhr.core.ICostCalculator;
import vhr.core.IDistanceCalculator;
import vhr.core.VRPInstance;
import vhr.core.VRPSolution;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dinhvan5481 on 4/2/17.
 */
public abstract class AbstractRecreateStrategy implements IRecreateStrategy {

    protected VRPInstance vrpInstance;
    protected ICostCalculator costCalculator;
    protected IDistanceCalculator distanceCalulator;

    protected AbstractRecreateStrategy(VRPInstance vrpInstance, ICostCalculator costCalculator, IDistanceCalculator distanceCalulator) {
        this.vrpInstance = vrpInstance;
        this.costCalculator = costCalculator;
        this.distanceCalulator = distanceCalulator;
    }

    @Override
    public VRPSolution recreate(VRPSolution ruinedSolution, List<Integer> removedCustomerIds) {
        VRPSolution copiedRuinedSolution = null;
        try {
            copiedRuinedSolution = ruinedSolution.clone();

        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        List<Integer> copiedRemovedCustomers = new ArrayList<>(removedCustomerIds);
        recreateRuinedSolution(copiedRuinedSolution, copiedRemovedCustomers);
        return copiedRuinedSolution;
    }

    protected abstract void recreateRuinedSolution(VRPSolution ruinedSolution, List<Integer> removedCustomerIds);
}
