package vhr.RuinAndRecreateAlgorithm.Ruin;

import vhr.core.VRPInstance;
import vhr.core.VRPSolution;
import vhr.core.VehicleRoute;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by quachv on 3/29/2017.
 */
public abstract class AbstractRuinStrategy implements IRuinStrategy {
    protected Set<Integer> removedCustomerIds;

    protected AbstractRuinStrategy() {
        removedCustomerIds = new HashSet<>();
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
        ruinedSolution.getRoutes().forEach((VehicleRoute route) -> {
            removedCustomerIds.forEach((Integer customerId) -> {
                route.removeCustomer(customerId);
            });
        });
        ruinedSolution.cleanEmptyRoute();
        return ruinedSolution;
    }

    @Override
    public Set<Integer> getRemovedCustomerIds() {
        return removedCustomerIds;
    }

    protected abstract void ruinSolution(VRPInstance vrpInstance, VRPSolution vrpSolution, double ruinRate);

}
