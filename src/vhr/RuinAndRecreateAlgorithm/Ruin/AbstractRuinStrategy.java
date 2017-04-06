package vhr.RuinAndRecreateAlgorithm.Ruin;

import vhr.core.Customer;
import vhr.core.VRPInstance;
import vhr.core.VRPSolution;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by quachv on 3/29/2017.
 */
public abstract class AbstractRuinStrategy implements IRuinStrategy {
    protected List<Integer> removedCustomerIds;

    protected AbstractRuinStrategy() {
        removedCustomerIds = new ArrayList<>();
    }

    @Override
    public VRPSolution ruin(VRPInstance vrpInstance, VRPSolution vrpSolution, double ruinRate) {
        removedCustomerIds.clear();
        VRPSolution ruinedSolution = null;
        try {
            ruinedSolution = vrpSolution.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        ruinSolution(vrpInstance, ruinedSolution, ruinRate);
        ruinedSolution.cleanEmptyRoute();
        return ruinedSolution;
    }

    @Override
    public List<Integer> getRemovedCustomerIds() {
        return removedCustomerIds;
    }

    protected abstract void ruinSolution(VRPInstance vrpInstance, VRPSolution vrpSolution, double ruinRate);

}
