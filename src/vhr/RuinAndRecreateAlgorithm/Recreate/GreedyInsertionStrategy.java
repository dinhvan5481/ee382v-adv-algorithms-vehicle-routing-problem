package vhr.RuinAndRecreateAlgorithm.Recreate;

import vhr.core.*;

import java.util.*;

/**
 * Created by quachv on 4/3/2017.
 */
public class GreedyInsertionStrategy extends AbstractRecreateStrategy {

    public GreedyInsertionStrategy(VRPInstance vrpInstance, ICostCalculator costCalculator, IDistanceCalculator distanceCalulator) {
        super(vrpInstance, costCalculator, distanceCalulator);
    }

    @Override
    protected void recreateRuinedSolution(VRPSolution ruinedSolution, List<Integer> removedCustomerIds) {
        Collection<VehicleRoute> routes = ruinedSolution.getRoutes();
        HashMap<Integer, InsertPositionAndCost> insertPositionDict = new HashMap<>(removedCustomerIds.size());
        List<Integer> unassignedCustomer = new ArrayList<>();

        for (Integer customerId :
                removedCustomerIds) {
            Customer customer = vrpInstance.getCustomer(customerId);
            InsertPositionAndCost bestInsertPosition = findPositionOfMinimumCostWhenInsert(customer, routes);
            if(bestInsertPosition != null) {
                VehicleRoute route = ruinedSolution.getRoute(bestInsertPosition.getRouteId());
                route.addCustomer(customerId);
                route.getRoute().add(bestInsertPosition.getPosition(), customerId);
            } else  {
                unassignedCustomer.add(customerId);
            }
        }

        if(unassignedCustomer.size() > 0) {
            VehicleRoute newRoute = ruinedSolution.createNewRoute();
            LinkedList<Integer> path = new LinkedList<>();
            for(int i = 0; i < unassignedCustomer.size(); i++) {
                int customerId = unassignedCustomer.get(i);
                if(newRoute.getTotalDemand() + vrpInstance.getCustomer(customerId).getDemand() > vrpInstance.getCapacity() ) {
                    newRoute.setRoute(path);
                    path = new LinkedList<>();
                    newRoute = ruinedSolution.createNewRoute();
                }
                path.push(customerId);
                newRoute.addCustomer(customerId);
            }
            if(path.size() > 0) {
                newRoute.setRoute(path);
            }
        }
    }

    private InsertPositionAndCost findPositionOfMinimumCostWhenInsert(Customer customer, Collection<VehicleRoute> routes) {
        InsertPositionAndCost bestInsertPosition = null;
        Iterator<VehicleRoute> routeIterator = routes.iterator();
        while (routeIterator.hasNext()) {
            VehicleRoute route = routeIterator.next();
            if(route.getTotalDemand() + customer.getDemand() > vrpInstance.getCapacity()) {
                continue;
            }
            LinkedList<Integer> originalPath = new LinkedList<>(route.getRoute());
            InsertPositionAndCost insertPositionAndCost = new InsertPositionAndCost(route.getId());
            int bestPosition = -1;
            double minCost = Double.MAX_VALUE;
            for (int position = 0; position < originalPath.size(); position++) {
                originalPath.add(position, customer.getId());
                double cost = costCalculator.calculateRouteCost(originalPath);
                if(cost < minCost) {
                    minCost = cost;
                    bestPosition = position;
                }
                originalPath.remove(position);
            }
            insertPositionAndCost.setCost(minCost);
            insertPositionAndCost.setPosition(bestPosition);
            if(insertPositionAndCost.compareTo(bestInsertPosition) < 0) {
                bestInsertPosition = insertPositionAndCost;
            }
        }
        return bestInsertPosition;
    }

    private class InsertPositionAndCost implements Comparable<InsertPositionAndCost> {
        private int routeId;
        private int position;
        private double cost;

        public InsertPositionAndCost(int routeId) {
            this.routeId = routeId;
        }

        public int getPosition() {
            return position;
        }

        public double getCost() {
            return cost;
        }

        public int getRouteId() {
            return routeId;
        }

        @Override
        public int compareTo(InsertPositionAndCost o) {
            if(o == null) {
                return -1;
            }
            return Double.compare(this.getCost(), o.getCost());
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public void setCost(double cost) {
            this.cost = cost;
        }
    }

    public static class Builder {
        private final VRPInstance vrpInstance;
        private ICostCalculator costCalculator;
        private IDistanceCalculator distanceCalculator;

        public Builder(VRPInstance vrpInstance, ICostCalculator costCalculator, IDistanceCalculator distanceCalculator) {
            this.vrpInstance = vrpInstance;
            this.costCalculator = costCalculator;
            this.distanceCalculator = distanceCalculator;
        }

        public IRecreateStrategy build() {
            return new GreedyInsertionStrategy(vrpInstance, costCalculator, distanceCalculator);
        }
    }
}
