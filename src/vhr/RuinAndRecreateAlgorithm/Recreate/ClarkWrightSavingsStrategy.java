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
            InsertPositionAndSaving bestInsertPosition = findPositionOfMinimumCostWhenInsert(customer, routes);
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
    private InsertPositionAndSaving findPositionOfMinimumCostWhenInsert(Customer removedCustomer, Collection<VehicleRoute> routes)    {
        InsertPositionAndSaving bestInsertPosition = null;
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

            InsertPositionAndSaving insertPositionAndSaving = new InsertPositionAndSaving(route.getId());
            int bestPosition = -1;
            maxSaving = Double.MIN_VALUE;
            //try to insert before maxSavingIndex customer
            if(maxSavingIndex>0)
            {
                int position = maxSavingIndex-1;
                originalPath.add(position, removedCustomer.getId());
                double saving = getTotalSavingFrom2Neighbors(removedCustomer,
                        position==0? null : originalPath.get(position-1),
                        position==originalPath.size()-1? null : originalPath.get(position+1));

                if(saving > maxSaving) {
                    maxSaving = saving;
                    bestPosition = position;
                }
                originalPath.remove(position);
            }
            //try to insert after maxSavingIndex customer
            int position = maxSavingIndex+1;
            originalPath.add(position, removedCustomer.getId());
            double saving = getTotalSavingFrom2Neighbors(removedCustomer,
                    position==0? null : originalPath.get(position-1),
                    position==originalPath.size()-1? null : originalPath.get(position+1));

            if(saving > maxSaving) {
                maxSaving = saving;
                bestPosition = position;
            }
            originalPath.remove(position);

            insertPositionAndSaving.setSaving(maxSaving);
            insertPositionAndSaving.setPosition(bestPosition);
            if(insertPositionAndSaving.compareTo(bestInsertPosition) > 0) {
                bestInsertPosition = insertPositionAndSaving;
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

    private class InsertPositionAndSaving implements Comparable<InsertPositionAndSaving> {
        private int routeId;
        private int position;
        private double saving;

        public InsertPositionAndSaving(int routeId) {
            this.routeId = routeId;
        }

        public int getPosition() {
            return position;
        }

        public double getSaving() {
            return saving;
        }

        public int getRouteId() {
            return routeId;
        }

        @Override
        public int compareTo(InsertPositionAndSaving o) {
            if(o == null) {
                return -1;
            }
            return Double.compare(this.getSaving(), o.getSaving());
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public void setSaving(double saving) {
            this.saving = saving;
        }
    }
}
