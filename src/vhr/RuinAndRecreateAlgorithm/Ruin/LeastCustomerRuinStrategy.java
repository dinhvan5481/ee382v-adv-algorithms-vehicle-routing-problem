package vhr.RuinAndRecreateAlgorithm.Ruin;

import vhr.core.VRPInstance;
import vhr.core.VRPSolution;
import vhr.core.VehicleRoute;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by quachv on 4/11/2017.
 */
public class LeastCustomerRuinStrategy extends AbstractRuinStrategy {
    public LeastCustomerRuinStrategy() {

    }

    @Override
    protected void ruinSolution(VRPInstance vrpInstance, VRPSolution vrpSolution, double ruinRate) {
        Collection<VehicleRoute> routes = vrpSolution.getRoutes();
        VehicleRoute minCustomerRoute = routes.stream().min(Comparator.comparingInt(VehicleRoute::numberOfCustomers)).get();
        removedCustomerIds.addAll(minCustomerRoute.getCustomerKeys());
        removedCustomerIds.forEach(minCustomerRoute::removeCustomer);
    }
}
