package vhr.core;

import java.util.LinkedList;

/**
 * Created by dinhvan5481 on 3/29/17.
 */
public class CVRPCostCalculator implements ICostCalculator {

    protected IDistanceCalculator distanceCalculator;
    public CVRPCostCalculator(IDistanceCalculator distanceCalculator) {
        this.distanceCalculator = distanceCalculator;
    }

    @Override
    public double calculate(DeliveryPath path) {
        return calculate(path.getFrom(), path.getTo());
    }

    @Override
    public double calculate(Customer from, Customer to) {
        return distanceCalculator.calculate(from.getCoordinate(), to.getCoordinate());
    }

    @Override
    public double calculateRouteCost(LinkedList<Integer> routeWithoutDepot, VRPInstance vrpInstance) {
        double result = 0;
        Customer from = vrpInstance.getDepot();
        for (int i = 0; i < routeWithoutDepot.size() - 1; i++) {
            Customer to = vrpInstance.getCustomer(routeWithoutDepot.get(i));
            result += calculate(from, to);
            from = to;
        }
        result += calculate(from, vrpInstance.getDepot());
        return result;
    }
}
