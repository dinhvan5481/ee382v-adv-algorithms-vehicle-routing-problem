package vhr.RuinAndRecreateAlgorithm.Ruin;

import vhr.core.VRPInstance;
import vhr.core.VRPSolution;
import vhr.core.VehicleRoute;

import java.util.Collections;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static java.util.Collections.*;
import static java.util.Collections.max;

/**
 * Created by quachv on 3/29/2017.
 */
public class RandomRuinStrategy extends AbstractRuinStrategy {

    public RandomRuinStrategy() {
        super();
    }
    @Override
    protected void ruinSolution(VRPInstance vrpInstance, VRPSolution ruinSolution, double ruinRate) {
        int numberOfNodeWillBeRemoved = (int) Math.floor(ruinRate * vrpInstance.getNumberOfCustomers());
        int minCustomerId = min(vrpInstance.getCustomerIds());
        int maxCustomerId = max(vrpInstance.getCustomerIds());

        for (int i = 0; i < numberOfNodeWillBeRemoved; i++) {
            int customerId;
            do {
                customerId = ThreadLocalRandom.current().nextInt(minCustomerId, maxCustomerId + 1);
            } while (vrpInstance.getCustomer(customerId) == null || removedCustomerIds.contains(customerId));
            removedCustomerIds.add(customerId);
        }
    }

    public static class Builder {

        public Builder() {

        }

        public AbstractRuinStrategy build() {
            return new RandomRuinStrategy();
        }
    }
}
