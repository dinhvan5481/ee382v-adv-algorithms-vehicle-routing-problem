package vhr.RuinAndRecreateAlgorithm.Ruin;

import vhr.RuinAndRecreateAlgorithm.ClarkWrightSavingsHelper;
import vhr.core.Customer;
import vhr.core.VRPInstance;
import vhr.core.VRPSolution;
import vhr.core.VehicleRoute;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by My Luc on 4/19/2017.
 */
public class ClarkWrightSavingsRuinStrategy extends AbstractRuinStrategy {
    public ClarkWrightSavingsRuinStrategy() {

    }
    private double[][] _savingMatrix = null;
    @Override
    protected void ruinSolution(VRPInstance vrpInstance, VRPSolution vrpSolution, double ruinRate) {
        if(_savingMatrix == null)
        {
            _savingMatrix = ClarkWrightSavingsHelper.GetSavingsMatrix(vrpInstance);
        }
        //key is route id, values are savings each pair in order
        List<SavingsAnd2CustomerIds> savingsOfPairs = new ArrayList<>();
        Hashtable<Integer, LinkedList<Integer>> routeIdToCustomerIds = new Hashtable<>();

        Collection<VehicleRoute> routes = vrpSolution.getRoutes();
        Iterator<VehicleRoute> routeIterator = routes.iterator();
        while (routeIterator.hasNext()) {
            VehicleRoute route = routeIterator.next();

            LinkedList<Integer> customerIdsOfCurrentRoute = route.getRoute();
            routeIdToCustomerIds.put(route.getId(), customerIdsOfCurrentRoute);
            for(int i=0;i<customerIdsOfCurrentRoute.size()-1; i++){
                //get saving of customer i and i+1
                int c1Id = customerIdsOfCurrentRoute.get(i);
                int c2Id = customerIdsOfCurrentRoute.get(i+1);
                Customer customerI = vrpInstance.getCustomer(c1Id);
                Customer customerIPlus1 = vrpInstance.getCustomer(c2Id);
                double savingOfCurrentPair = ClarkWrightSavingsHelper.GetSavings(customerI, customerIPlus1, vrpInstance.getDepot());
                SavingsAnd2CustomerIds savingsAnd2CustomerIds = new SavingsAnd2CustomerIds(c1Id, c2Id, savingOfCurrentPair, route.getId());
                savingsOfPairs.add(savingsAnd2CustomerIds);
            }
        }
        savingsOfPairs.sort(new SavingsAnd2CustomerIdsComparator<SavingsAnd2CustomerIds>());

        int numberOfNodeWillBeRemoved = (int) Math.floor(ruinRate * vrpInstance.getNumberOfCustomers());
        //savingsOfPairs is sorted increasingly based on saving. First numberOfNodeWillBeRemoved items are the targets
        for(int i=0;i<numberOfNodeWillBeRemoved; i++){
            SavingsAnd2CustomerIds pair = savingsOfPairs.get(i);
            int customer1Id = pair.customer1;
            int customer2Id = pair.customer2;
            //customer1Id always < customer2Id
            //decide to remove the first or the second customer
            LinkedList<Integer> customerIdsOfCurrentRoute = routeIdToCustomerIds.get(pair.routeId);
            int customer1Index = customerIdsOfCurrentRoute.indexOf(customer1Id);
            int customer2Index = customerIdsOfCurrentRoute.indexOf(customer2Id);

            //if remove customer1, check saving of its 2 neighbors
            int customer1LeftIdx = customer1Index -1;
            int customer1RightIdx = customer1Index +1;
            if(customer1LeftIdx <0 && customer1RightIdx == customerIdsOfCurrentRoute.size())
                continue;
            if(customer1LeftIdx <0)
                customer1LeftIdx = customer1RightIdx;
            if(customer1RightIdx == customerIdsOfCurrentRoute.size())
                customer1RightIdx = customer1LeftIdx;

            int ind1LeftId = customerIdsOfCurrentRoute.get(customer1LeftIdx);
            int ind1RightId = customerIdsOfCurrentRoute.get(customer1RightIdx);
            Customer customer1Left = vrpInstance.getCustomer(ind1LeftId);
            Customer customer1Right = vrpInstance.getCustomer(ind1RightId);
            double customer1SavingOf2Neighbors = ClarkWrightSavingsHelper.GetSavings(customer1Left, customer1Right, vrpInstance.getDepot());


            //if remove customer2, check saving of its 2 neighbors
            int customer2LeftIdx = customer2Index -1;
            int customer2RightIdx = customer2Index +1;
            if(customer2LeftIdx <0 && customer2RightIdx == customerIdsOfCurrentRoute.size())
                continue;
            if(customer2LeftIdx <0)
                customer2LeftIdx = customer2RightIdx;
            if(customer2RightIdx == customerIdsOfCurrentRoute.size())
                customer2RightIdx = customer2LeftIdx;

            int ind2LeftId = customerIdsOfCurrentRoute.get(customer2LeftIdx);
            int ind2RightId = customerIdsOfCurrentRoute.get(customer2RightIdx);
            Customer customer2Left = vrpInstance.getCustomer(ind2LeftId);
            Customer customer2Right = vrpInstance.getCustomer(ind2RightId);
            double customer2SavingOf2Neighbors = ClarkWrightSavingsHelper.GetSavings(customer2Left, customer2Right, vrpInstance.getDepot());


            if(customer1SavingOf2Neighbors < customer2SavingOf2Neighbors){
                    removedCustomerIds.add(customer1Id);
            }
            else{
                    removedCustomerIds.add(customer2Id);
            }
        }

        vrpSolution.getRoutes().forEach((VehicleRoute route) -> {
            removedCustomerIds.forEach((Integer customerId) -> {
                route.removeCustomer(customerId);
            });
        });

    }

    private class SavingsAnd2CustomerIds {
        public int customer1;
        public int customer2;
        public double saving;
        public int routeId;

        public SavingsAnd2CustomerIds(int c1, int c2, double saving, int routeId) {
            if(c1<c2){
                this.customer1 = c1;
                this.customer2 =c2;
            }
            else
            {
                this.customer1 = c2;
                this.customer2 =c1;
            }

            this.saving = saving;
            this.routeId = routeId;
        }
    }

    private class SavingsAnd2CustomerIdsComparator<T> implements Comparator<SavingsAnd2CustomerIds> {
        @Override
        public int compare(SavingsAnd2CustomerIds o1, SavingsAnd2CustomerIds o2) {
            return Double.compare(o1.saving, o2.saving);
        }
    }
}
