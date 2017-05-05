package vhr.ClarkWrightSavingsAlgorithm;

import vhr.RuinAndRecreateAlgorithm.Recreate.AbstractRecreateStrategy;
import vhr.RuinAndRecreateAlgorithm.Recreate.GreedyInsertionStrategy;
import vhr.RuinAndRecreateAlgorithm.Recreate.IRecreateStrategy;
import vhr.core.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by quachv on 4/21/2017.
 */
public class ClarkWrightSavingsAlgorithm implements IVRPAlgorithm {
    protected HashSet<CWSaving> cwSavingHashSet;
    private static int MAX_RUN = 1000;

    public ClarkWrightSavingsAlgorithm() {
        cwSavingHashSet = new HashSet<>();
    }


    @Override
    public VRPSolution solve(VRPInstance vrpInstance) throws Exception {
        HashSet<CWSavingPath> cwSavingPaths = new HashSet<>();
        HashSet<Integer> servedCustomers = new HashSet<>();
        HashSet<Integer> remainUnservedCustomers = new HashSet<>();
        VRPSolution vrpSolution = new VRPSolution(vrpInstance);
        buildSavingCostTable(vrpInstance);

        List<CWSaving> cwSavingsInOrder = cwSavingHashSet.stream()
                .sorted(Comparator.comparingDouble(CWSaving::getCost))
                .collect(Collectors.toList());
        int countOfUnservedCustomers = vrpInstance.getNumberOfCustomers();
        int runCounter = 0;

        while (countOfUnservedCustomers > 0 && runCounter < MAX_RUN) {
            for (CWSaving cwSaving : cwSavingsInOrder) {
                int countServedCustomers = countServedCustomers(cwSaving, servedCustomers);
                if (countServedCustomers == 0) {
                    CWSavingPath cwSavingPath = new CWSavingPath(vrpInstance);
                    cwSavingPath.add(cwSaving);
                    cwSavingPaths.add(cwSavingPath);
                    markServedCustomer(cwSaving, servedCustomers);
                } else if (countServedCustomers == 1) {
                    int servedCustomerId = isServedCustomer(cwSaving.getCustomerId1(), servedCustomers)
                            ? cwSaving.getCustomerId1() : cwSaving.getCustomerId2();
                    CWSavingPath cwSavingPath = cwSavingPaths.stream().filter(p -> p.contains(servedCustomerId))
                            .findFirst().orElse(null);
                    if (cwSavingPath == null) {
                        throw new Exception("Invalid program state");
                    }
                    if (!cwSavingPath.add(cwSaving)) {
                        continue;
                    }
                    markServedCustomer(cwSaving, servedCustomers);
                } else {
                    int countInteriorCustomers = countInteriorCustomer(cwSaving, cwSavingPaths);
                    if (countInteriorCustomers > 0) {
                        continue;
                    }
                    CWSavingPath cwSavingPath1 = cwSavingPaths.stream().filter(p -> p.contains(cwSaving.getCustomerId1()))
                            .findFirst().orElse(null);
                    CWSavingPath cwSavingPath2 = cwSavingPaths.stream().filter(p -> p.contains(cwSaving.getCustomerId2()))
                            .findFirst().orElse(null);
                    if (cwSavingPath1 == null || cwSavingPath2 == null) {
                        throw new Exception("Invalid program state");
                    }
                    if (cwSavingPath1.equals(cwSavingPath2)) {
                        continue;
                    }

                    CWSavingPath newCWSavingPath = cwSavingPath1.merge(cwSavingPath2);
                    if (newCWSavingPath == null) {
                        continue;
                    }
                    cwSavingPaths.add(newCWSavingPath);
                    cwSavingPaths.remove(cwSavingPath1);
                    cwSavingPaths.remove(cwSavingPath2);
                }
            }
            HashSet<Integer> unservedCustomers = new HashSet<>(vrpInstance.getCustomerIds());
            unservedCustomers.removeAll(servedCustomers);
            int numberOfRouteOverMaxRoute = cwSavingPaths.size() - vrpInstance.getMaxTruck();
            cwSavingPaths.stream().filter(p -> p.countNumberOfCustomers() == 1)
                    .forEach(p -> unservedCustomers.addAll(p.customerIds));
            if (numberOfRouteOverMaxRoute > 0) {
                cwSavingPaths.stream().sorted(Comparator.comparingInt(CWSavingPath::countNumberOfCustomers))
                        .limit(numberOfRouteOverMaxRoute).forEach(p -> {
                    unservedCustomers.addAll(p.customerIds);
                    servedCustomers.removeAll(p.customerIds);
                    cwSavingPaths.remove(p);
                });
            }

            cwSavingsInOrder = cwSavingHashSet.stream().filter(p -> p.hasCustomerIds(unservedCustomers))
                    .collect(Collectors.toList());
            Collections.shuffle(cwSavingsInOrder);
            countOfUnservedCustomers = unservedCustomers.size();
            runCounter++;
            remainUnservedCustomers.clear();
            remainUnservedCustomers.addAll(unservedCustomers);
        }

        for (CWSavingPath cwSavingPath :
                cwSavingPaths) {
            VehicleRoute vehicleRoute = vrpSolution.createNewRoute();
            for (Integer customerId :
                    cwSavingPath.customerIds) {
                vehicleRoute.addCustomer(customerId);
            }
            vehicleRoute.setRoute(cwSavingPath.path);
        }

        if(remainUnservedCustomers.size() > 0) {
            IRecreateStrategy greedyInsertionStrategy = new GreedyInsertionStrategy(vrpInstance, vrpInstance.getCostCalculator(), vrpInstance.getDistanceCalculator());
            vrpSolution = greedyInsertionStrategy.recreate(vrpSolution, remainUnservedCustomers);
        }

        return vrpSolution;
    }

    private boolean isAInteriorCustomer(int customerId, HashSet<CWSavingPath> cwSavingPaths) {
        CWSavingPath cwSavingPath = cwSavingPaths.stream()
                .filter(p -> p.headCustomerId == customerId || p.tailCustomerId == customerId)
                .findFirst().orElse(null);
        return cwSavingPath == null;
    }

    private int countInteriorCustomer(CWSaving cwSaving, HashSet<CWSavingPath> cwSavingPaths) {
        int count = 0;
        if(isAInteriorCustomer(cwSaving.getCustomerId1(), cwSavingPaths)) {
            count++;
        }
        if(isAInteriorCustomer(cwSaving.getCustomerId2(), cwSavingPaths)) {
            count++;
        }
        return count;
    }

    private void markServedCustomer(CWSaving cwSaving, HashSet<Integer> servedCustomers) {
        if(!servedCustomers.contains(cwSaving.getCustomerId1())) {
            servedCustomers.add(cwSaving.getCustomerId1());
        }
        if(!servedCustomers.contains(cwSaving.getCustomerId2())) {
            servedCustomers.add(cwSaving.getCustomerId2());
        }

    }

    private boolean isServedCustomer(int customerId, HashSet<Integer> servedCustomers) {
        return servedCustomers.contains(customerId);
    }
    private int countServedCustomers(CWSaving cwSaving, HashSet<Integer> servedCustomers) {
        int count = 0;
        if(servedCustomers.contains(cwSaving.getCustomerId1())) {
            count++;
        }
        if(servedCustomers.contains(cwSaving.getCustomerId2())) {
            count++;
        }
        return count;
    }

    private void buildSavingCostTable(VRPInstance vrpInstance) {
        Customer depot = vrpInstance.getDepot();
/*        vrpInstance.getCustomerIds().forEach(id -> {
            CWSaving cwSaving = new CWSaving(depot.getId(), id);
            try {
                cwSaving.calculateSavingCost(vrpInstance);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            cwSavingHashSet.add(cwSaving);
        });*/
        ArrayList<Integer> customerIds = new ArrayList<>(vrpInstance.getCustomerIds());
        for (int index = 0; index < customerIds.size() - 1; index++) {
            Customer customer1 = vrpInstance.getCustomer(customerIds.get(index));
            for (int innerIndex = index + 1; innerIndex < customerIds.size(); innerIndex++) {
                Customer customer2 = vrpInstance.getCustomer(customerIds.get(innerIndex));
                CWSaving cwSaving = new CWSaving(customer1.getId(), customer2.getId());
                try {
                    cwSaving.calculateSavingCost(vrpInstance);
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                cwSavingHashSet.add(cwSaving);
            }
        }
    }

    private class CWSavingPath {
        private VRPInstance vrpInstance;
        private LinkedList<Integer> path;
        private HashSet<Integer> customerIds;
        private double totalDemand;
        private int headCustomerId;
        private int tailCustomerId;

        public CWSavingPath(VRPInstance vrpInstance) {
            this.vrpInstance = vrpInstance;
            customerIds = new HashSet<>();
            path = new LinkedList<>();
            totalDemand = 0;
            headCustomerId = -1;
            tailCustomerId = -1;

        }

        public boolean add(CWSaving cwSaving) {
            Customer customer1 = vrpInstance.getCustomer(cwSaving.getCustomerId1());
            Customer customer2 = vrpInstance.getCustomer(cwSaving.getCustomerId2());
            if(customerIds.size() == 0) {
                totalDemand += customer1.getDemand();
                totalDemand += customer2.getDemand();
                customerIds.add(customer1.getId());
                customerIds.add(customer2.getId());
                headCustomerId = customer1.getId();
                tailCustomerId = customer2.getId();
                path.push(customer1.getId());
                path.push(customer2.getId());
                return true;
            }

            if(!customerIds.contains(cwSaving.getCustomerId1()) && !customerIds.contains(cwSaving.getCustomerId2())) {
                return false;
            }

            if(customerIds.contains(cwSaving.getCustomerId1()) && customerIds.contains(cwSaving.getCustomerId2())) {
                return true;
            }

            int withinPathCustomerId = customerIds.contains(cwSaving.getCustomerId1()) ? cwSaving.getCustomerId1() : cwSaving.getCustomerId2();
            int newCustomerId = cwSaving.getAnotherEndOf(withinPathCustomerId);
            if(totalDemand + vrpInstance.getCustomer(newCustomerId).getDemand() > vrpInstance.getCapacity()) {
                return false;
            }
            totalDemand += vrpInstance.getCustomer(newCustomerId).getDemand();

            if(withinPathCustomerId == headCustomerId) {
                headCustomerId = newCustomerId;
                path.addFirst(newCustomerId);
            } else if(withinPathCustomerId == tailCustomerId) {
                tailCustomerId = newCustomerId;
                path.addLast(newCustomerId);
            } else {
                int indexOfInteriorCustomer = path.indexOf(withinPathCustomerId);
                path.add(indexOfInteriorCustomer + 1, newCustomerId);
                double costInsertAfter = vrpInstance.getRouteCost(path);
                path.remove(indexOfInteriorCustomer + 1);
                path.add(indexOfInteriorCustomer, newCustomerId);
                double costInsertBefore = vrpInstance.getRouteCost(path);
                if(costInsertAfter < costInsertBefore) {
                    path.remove(indexOfInteriorCustomer);
                    path.add(indexOfInteriorCustomer + 1, newCustomerId);
                }
            }
            addCustomerIds(cwSaving);
            return true;
        }


        public CWSavingPath merge(CWSavingPath anotherPath){
            if(totalDemand + anotherPath.totalDemand > vrpInstance.getCapacity()) {
                return null;
            }
            if(headCustomerId != anotherPath.headCustomerId && headCustomerId != anotherPath.tailCustomerId
                    && tailCustomerId != anotherPath.headCustomerId && tailCustomerId != anotherPath.tailCustomerId) {
                    return null;
                }

            CWSavingPath mergedPath = new CWSavingPath(vrpInstance);
            mergedPath.totalDemand = totalDemand + anotherPath.totalDemand;
            if(headCustomerId == anotherPath.headCustomerId || tailCustomerId == anotherPath.tailCustomerId) {
                mergedPath.headCustomerId = anotherPath.tailCustomerId;
                mergedPath.tailCustomerId = tailCustomerId;
            }

            if(headCustomerId == anotherPath.tailCustomerId || tailCustomerId == anotherPath.headCustomerId) {
                mergedPath.headCustomerId = anotherPath.headCustomerId;
                mergedPath.tailCustomerId = tailCustomerId;
            }

            return mergedPath;
        }

        public boolean contains(int customerId) {
            return customerIds.contains(customerId);
        }

        public int countNumberOfCustomers() {
            return customerIds.size();
        }

        public double getTotalDemand() {
            return totalDemand;
        }

        private void addCustomerIds(CWSaving cwSaving) {
            customerIds.add(cwSaving.getCustomerId1());
            customerIds.add(cwSaving.getCustomerId2());
        }

        @Override
        public boolean equals(Object o) {
            if(o == null || o.getClass() != getClass()) {
                return false;
            }
            CWSavingPath other = (CWSavingPath)o;
            if(other.path.size() != path.size()) {
                return false;
            }
            for (int i = 0; i < path.size(); i++) {
                if(!path.get(i).equals(other.path.get(i))) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public int hashCode() {
            List<Integer> orderedCustomerIds = customerIds.stream().sorted().collect(Collectors.toList());
            return orderedCustomerIds.hashCode();
        }
    }

    private class CWSaving {
        private int customerId1;
        private int customerId2;
        private double savingCost;
        private double cost;

        public CWSaving(int customerId1, int customerId2) {
            this.customerId1 = customerId1;
            this.customerId2 = customerId2;
        }

        public void calculateSavingCost(VRPInstance vrpInstance) throws Exception {
            Customer depot = vrpInstance.getDepot();
            Customer customer1;
            Customer customer2;
            if(depot.getId() == customerId1) {
                customer1 = depot;
            } else {
                customer1 = vrpInstance.getCustomer(customerId1);
            }

            if(depot.getId() == customerId2) {
                customer2 = depot;
            } else {
                customer2 = vrpInstance.getCustomer(customerId2);
            }
            if(customer1 == null || customer2 == null) {
                throw new Exception("Customer doesn't exist");
            }
            savingCost = vrpInstance.getDistance(depot, customer1) + vrpInstance.getDistance(depot, customer2) - vrpInstance.getDistance(customer1, customer2);
            cost = vrpInstance.getDistance(customer1, customer2);
        }

        public double getSavingCost(){
            return savingCost;
        }

        public double getCost() {
            return cost;
        }

        public int getCustomerId1() {
            return customerId1;
        }

        public int getCustomerId2() {
            return customerId2;
        }

        public int getAnotherEndOf(int customerId) {
            if(!hasCustomerId(customerId)) {
                return -1;
            }
            return customerId == customerId1 ? customerId2 : customerId1;
        }

        public boolean hasCustomerId(int customerId) {
            return customerId1 == customerId || customerId2 == customerId;
        }

        public boolean hasCustomerIds(Collection<Integer> ids) {
            return ids.contains(customerId1) || ids.contains(customerId2);
        }

        @Override
        public boolean equals(Object obj) {
            if(obj == null || (obj instanceof CWSaving)) {
                return false;
            }
            return ((customerId1 == ((CWSaving) obj).customerId1) && customerId2 == ((CWSaving) obj).customerId2)
                    || ((customerId1 == ((CWSaving) obj).customerId2) && customerId2 == ((CWSaving) obj).customerId1);
        }

        @Override
        public int hashCode() {
            int bigger = customerId1 >= customerId2 ? customerId1 : customerId2;
            int smaller = customerId1 < customerId2 ? customerId1 : customerId2;
            int hash = 17;
            hash = hash * 31 + bigger;
            hash = hash * 31 + smaller;
            return hash;
        }
    }
}
