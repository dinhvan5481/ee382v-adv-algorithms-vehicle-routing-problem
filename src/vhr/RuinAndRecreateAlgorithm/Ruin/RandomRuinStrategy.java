package vhr.RuinAndRecreateAlgorithm.Ruin;

import vhr.core.VRPInstance;
import vhr.core.VRPSolution;
import vhr.core.VehicleRoute;

import java.util.Collections;
import java.util.Random;

import static java.util.Collections.*;
import static java.util.Collections.max;

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
    protected void ruinSolution(VRPInstance vrpInstance, VRPSolution ruinSolution, double ruinRate) {
        int numberOfNodeWillBeRemoved = (int) Math.floor(ruinRate * vrpInstance.getNumberOfCustomers());

        for (int i = 0; i < numberOfNodeWillBeRemoved; i++) {
            int customerId;
            {
                customerId = random.nextInt(max(vrpInstance.getCustomerIds()) - min(vrpInstance.getCustomerIds()) + 1)
                        + min(vrpInstance.getCustomerIds());
            } while (vrpInstance.getCustomer(customerId) == null);
            removedCustomerIds.add(customerId);
        }
        ruinSolution.getRoutes().forEach((VehicleRoute route) -> {
            removedCustomerIds.forEach((Integer customerId) -> {
                route.removeCustomer(customerId);
            });
        });
    }

    public static class Builder {
        private long seed;

        public Builder(long seed) {
            this.seed = seed;
        }

        public AbstractRuinStrategy build() {
            return new RandomRuinStrategy(seed);
        }
    }
}
