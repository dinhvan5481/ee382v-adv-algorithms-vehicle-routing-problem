package vhr.RuinAndRecreateAlgorithm.Ruin;

import vhr.core.Customer;
import vhr.core.VRPInstance;
import vhr.core.VRPSolution;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

import static java.util.Collections.max;
import static java.util.Collections.min;

/**
 * Created by quachv on 4/17/2017.
 */
public class RadicalRuinStrategy extends AbstractRuinStrategy {

    protected HashMap<Integer, ArrayList<DistanceToNextCustomer>> neighbourCustomerDistance;
    public RadicalRuinStrategy() {
        super();
        neighbourCustomerDistance = new HashMap<>();
    }

    @Override
    protected void ruinSolution(VRPInstance vrpInstance, VRPSolution vrpSolution, double ruinRate) {
        int numberOfNodeWillBeRemoved = (int) Math.floor(ruinRate * vrpInstance.getNumberOfCustomers());
        int minCustomerId = min(vrpInstance.getCustomerIds());
        int maxCustomerId = max(vrpInstance.getCustomerIds());
        int centerCustomerId = ThreadLocalRandom.current().nextInt(minCustomerId, maxCustomerId + 1);
        Customer centerCustomer = vrpInstance.getCustomer(centerCustomerId);
        ArrayList<DistanceToNextCustomer> neighboorCustomers;
        if(neighbourCustomerDistance.containsKey(centerCustomerId)) {
            neighboorCustomers = neighbourCustomerDistance.get(centerCustomerId);
        } else {
            neighboorCustomers = new ArrayList<>();
            vrpInstance.getCustomers().forEach(c ->
                neighboorCustomers.add(new DistanceToNextCustomer(c.getId(), vrpInstance.getDistance(centerCustomer, c)))
            );
            neighboorCustomers.sort(Comparator.comparingDouble(c -> c.distance));
            neighbourCustomerDistance.put(centerCustomerId, neighboorCustomers);
        }
        double maxDistance = neighboorCustomers.stream().max(Comparator.comparing(c -> c.distance)).get().distance;
        double randomDistance = ThreadLocalRandom.current().nextDouble(maxDistance);
        for (int i = 0; i < numberOfNodeWillBeRemoved; i++) {
            if(neighboorCustomers.get(i).distance < randomDistance) {
                removedCustomerIds.add(neighboorCustomers.get(i).neighbourCustomerId);
            }
        }
    }

    private class DistanceToNextCustomer {
        private int neighbourCustomerId;
        private double distance;

        public DistanceToNextCustomer(int neighbourCustomerId, double distance) {
            this.neighbourCustomerId = neighbourCustomerId;
            this.distance = distance;
        }
    }
}
