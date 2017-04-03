package vhr.RuinAndRecreateAlgorithm.Ruin;

import vhr.core.VRPInstance;
import vhr.core.VRPSolution;
import vhr.core.VehicleRoute;

import java.util.Random;

/**
 * Created by quachv on 3/29/2017.
 */
public class RandomRuinStrategy extends AbstractRuinStrategy {
    private Random random;

    public RandomRuinStrategy(long seed) {
        super();
        random = new Random(seed);
    }
    @Override
    protected VRPSolution ruinSolution(VRPInstance vrpInstance, VRPSolution vrpSolution, double ruinRate) {
        int numberOfNodeWillBeRemoved = (int) Math.floor(ruinRate * vrpInstance.getNumberOfCustomers());
        VRPSolution ruinedSolution = null;
        try {
            ruinedSolution = vrpSolution.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < numberOfNodeWillBeRemoved; i++) {
            removedCustomerIds.add(random.nextInt());
        }

        ruinedSolution.getRoutes().forEach((VehicleRoute route) -> {
            removedCustomerIds.forEach((Integer customerId) -> {
                route.removeCustomer(customerId);
            });
        });
        return ruinedSolution;
    }
}
