package vhr.RuinAndRecreateAlgorithm.Ruin;

import vhr.core.VRPInstance;
import vhr.core.VRPSolution;
import vhr.core.VehicleRoute;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by quachv on 4/11/2017.
 */
public class SequentialRuinStrategy extends AbstractRuinStrategy {

    protected SequentialRuinStrategy() {

    }

    @Override
    protected void ruinSolution(VRPInstance vrpInstance, VRPSolution vrpSolution, double ruinRate) {
        Collection<VehicleRoute> routes = vrpSolution.getRoutes();
        VehicleRoute ruinedRoute = null;
        do {
            ruinedRoute = routes.stream()
                    .skip((int) (routes.size() * ThreadLocalRandom.current().nextDouble()))
                    .findFirst().get();
        }while (ruinedRoute == null);
        int noRemoveCustomer = (int) (ruinRate * ruinedRoute.getCustomerKeys().size());
        int size = ruinedRoute.numberOfCustomers();
        if(ruinedRoute.isRouteValid()) {
            LinkedList<Integer> path = ruinedRoute.getRoute();
            for (int i = 0; i < noRemoveCustomer; i++) {
                removedCustomerIds.add(path.get(size - noRemoveCustomer + i));
            }
        }
        removedCustomerIds.forEach(ruinedRoute::removeCustomer);
    }

    public static class Builder {
        public Builder(){

        }

        public AbstractRuinStrategy build() {
            return new SequentialRuinStrategy();
        }
    }
}
