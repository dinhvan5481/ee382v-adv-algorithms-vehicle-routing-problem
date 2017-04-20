package vhr.RuinAndRecreateAlgorithm.Recreate;

import vhr.RuinAndRecreateAlgorithm.ClarkWrightSavingsHelper;
import vhr.core.*;

import java.util.*;

/**
 * Created by My Luc on 4/19/2017.
 */
public class ClarkWrightSavingsStrategy extends AbstractRecreateStrategy{
    protected ClarkWrightSavingsStrategy(VRPInstance vrpInstance, ICostCalculator costCalculator, IDistanceCalculator distanceCalulator) {
        super(vrpInstance, costCalculator, distanceCalulator);
    }
    private double[][] _savingMatrix = null;
    @Override
    protected void recreateRuinedSolution(VRPSolution ruinedSolution, List<Integer> removedCustomerIds) {
        if(_savingMatrix == null)
        {
            _savingMatrix = ClarkWrightSavingsHelper.GetSavingsMatrix(vrpInstance);
        }
        Collection<VehicleRoute> routes = ruinedSolution.getRoutes();
        List<Integer> unassignedCustomer = new ArrayList<>();
        for (Integer customerId : removedCustomerIds) {
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
                double capacity = vrpInstance.getCapacity();
                double demand = vrpInstance.getCustomer(customerId).getDemand();
                double totalDemand = newRoute.getTotalDemand();
                if(totalDemand + demand > capacity) {
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
    private InsertPositionAndCost findPositionOfMinimumCostWhenInsert(Customer removedCustomer, Collection<VehicleRoute> routes)    {
        InsertPositionAndCost bestInsertPosition = null;
        Iterator<VehicleRoute> routeIterator = routes.iterator();
        while (routeIterator.hasNext()) {
            VehicleRoute route = routeIterator.next();
            if(route.getTotalDemand() + removedCustomer.getDemand() > vrpInstance.getCapacity()) {
                continue;
            }
            LinkedList<Integer> originalPath = new LinkedList<>(route.getRoute());
            int removedCustomerIndex = ClarkWrightSavingsHelper.CustomerIdsOrdered.indexOf(removedCustomer.getId());
            double[] savingsToOtherCustomers = _savingMatrix[removedCustomerIndex];

            //for current route, pick the customer that has biggest saving with removedCustomer
            double maxSaving=Double.MIN_VALUE;
            int maxSavingIndex=-1;
            int i=0;
            for(int path: originalPath){
                int customerIndex = ClarkWrightSavingsHelper.CustomerIdsOrdered.indexOf(path);
                double currentSaving = savingsToOtherCustomers[customerIndex];
                if(currentSaving > maxSaving)
                {
                    maxSaving = currentSaving;
                    maxSavingIndex =i;
                }
                i++;
            }

            InsertPositionAndCost insertPositionAndCost = new InsertPositionAndCost(route.getId());
            int bestPosition = -1;
            double minCost = Double.MAX_VALUE;
            //try to insert before maxSavingIndex customer
            if(maxSavingIndex>0)
            {
                int position = maxSavingIndex-1;
                originalPath.add(position, removedCustomer.getId());
                double cost = vrpInstance.getRouteCost(originalPath);
                if(cost < minCost) {
                    minCost = cost;
                    bestPosition = position;
                }
                originalPath.remove(position);
            }
            //try to insert after maxSavingIndex customer
            int position = maxSavingIndex+1;
            originalPath.add(position, removedCustomer.getId());
            double cost = vrpInstance.getRouteCost(originalPath);
            if(cost < minCost) {
                minCost = cost;
                bestPosition = position;
            }
            originalPath.remove(position);

            insertPositionAndCost.setCost(minCost);
            insertPositionAndCost.setPosition(bestPosition);
            if(insertPositionAndCost.compareTo(bestInsertPosition) < 0) {
                bestInsertPosition = insertPositionAndCost;
            }
        }
        return bestInsertPosition;
    }

    private double getTotalSavingFrom2Neighbors(Customer removedCustomer, Integer leftCustomerId, Integer rightCustomerId) {
        double leftSaving=0;
        if(leftCustomerId != null){
            Customer leftCustomer = vrpInstance.getCustomer(leftCustomerId);
            leftSaving = ClarkWrightSavingsHelper.GetSavings(removedCustomer, leftCustomer, vrpInstance.getDepot());
        }

        double rightSaving=0;
        if(rightCustomerId != null){
            Customer rightCustomer = vrpInstance.getCustomer(rightCustomerId);
            rightSaving = ClarkWrightSavingsHelper.GetSavings(removedCustomer, rightCustomer, vrpInstance.getDepot());
        }

        return leftSaving + rightSaving;
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
            return new ClarkWrightSavingsStrategy(vrpInstance, costCalculator, distanceCalculator);
        }
    }
}
