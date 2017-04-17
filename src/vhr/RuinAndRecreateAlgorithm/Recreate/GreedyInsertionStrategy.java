package vhr.RuinAndRecreateAlgorithm.Recreate;

import vhr.core.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by quachv on 4/3/2017.
 */
public class GreedyInsertionStrategy extends AbstractRecreateStrategy {

    protected int MAX_MEMORY_BEST_PATH = 1000;
    protected HashMap<Integer, BestInsertPositionAndCostHitCounter> bestInsertPositionAndCostHitCounterHashMap;

    public GreedyInsertionStrategy(VRPInstance vrpInstance, ICostCalculator costCalculator, IDistanceCalculator distanceCalulator) {
        super(vrpInstance, costCalculator, distanceCalulator);
        bestInsertPositionAndCostHitCounterHashMap = new HashMap<>(MAX_MEMORY_BEST_PATH);
    }

    @Override
    protected void recreateRuinedSolution(VRPSolution ruinedSolution, List<Integer> removedCustomerIds) {
        Collection<VehicleRoute> routes = ruinedSolution.getRoutes();
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
            InsertPositionAndCost insertPositionAndCost;
            Collection<Integer> customerIds = new ArrayList<>(route.getCustomerKeys());
            customerIds.add(customer.getId());
            int hashCode = hashing(customerIds);

            if(bestInsertPositionAndCostHitCounterHashMap.containsKey(hashCode)) {
                insertPositionAndCost = bestInsertPositionAndCostHitCounterHashMap.get(hashCode).getBestInsertPositionAndCost();
                insertPositionAndCost.setRouteId(route.getId());
                bestInsertPositionAndCostHitCounterHashMap.get(hashCode).hit();
            } else {
                LinkedList<Integer> originalPath = new LinkedList<>(route.getRoute());
                insertPositionAndCost = new InsertPositionAndCost(route.getId());
                int bestPosition = -1;
                double minCost = Double.MAX_VALUE;
                for (int position = 0; position < originalPath.size(); position++) {
                    originalPath.add(position, customer.getId());
                    double cost = vrpInstance.getRouteCost(originalPath);
                    if(cost < minCost) {
                        minCost = cost;
                        bestPosition = position;
                    }
                    originalPath.remove(position);
                }
                insertPositionAndCost.setCost(minCost);
                insertPositionAndCost.setPosition(bestPosition);
                putBestInsertPositionToHashMap(hashCode, insertPositionAndCost);
            }

            if(insertPositionAndCost.compareTo(bestInsertPosition) < 0) {
                bestInsertPosition = insertPositionAndCost;
            }
        }
        return bestInsertPosition;
    }

    private static int hashing(Collection<Integer> customerIds) {
        List<Integer> orderedCustomerIds = customerIds.stream().sorted().collect(Collectors.toList());
        return orderedCustomerIds.hashCode();
   }

   private void putBestInsertPositionToHashMap(int hashCode, InsertPositionAndCost insertPositionAndCost) {
        if(bestInsertPositionAndCostHitCounterHashMap.size() < MAX_MEMORY_BEST_PATH) {
            bestInsertPositionAndCostHitCounterHashMap.put(hashCode, new BestInsertPositionAndCostHitCounter(insertPositionAndCost));
            return;
        }
        int minHitEntryKey = Collections.min(bestInsertPositionAndCostHitCounterHashMap.entrySet(),
                Comparator.comparingInt(e -> e.getValue().getHitCounter())
                ).getKey();
        bestInsertPositionAndCostHitCounterHashMap.remove(minHitEntryKey);
        bestInsertPositionAndCostHitCounterHashMap.put(hashCode, new BestInsertPositionAndCostHitCounter(insertPositionAndCost));
   }

    private class InsertPositionAndCost implements Comparable<InsertPositionAndCost> {
        private int routeId;
        private int position;
        private double cost;

        public InsertPositionAndCost() {

        }

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

        public void setRouteId(int routeId) {
            this.routeId = routeId;
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

    private class BestInsertPositionAndCostHitCounter {
        private InsertPositionAndCost insertPositionAndCost;
        private int hitCounter;

        public BestInsertPositionAndCostHitCounter(InsertPositionAndCost insertPositionAndCost) {
            this.insertPositionAndCost = insertPositionAndCost;
            hitCounter = 1;
        }

        public InsertPositionAndCost getBestInsertPositionAndCost() {
            return insertPositionAndCost;
        }

        public void hit() {
            hitCounter++;
        }

        public int getHitCounter() {
            return hitCounter;
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
