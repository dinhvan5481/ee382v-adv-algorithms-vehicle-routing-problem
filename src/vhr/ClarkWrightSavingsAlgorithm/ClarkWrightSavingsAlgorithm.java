package vhr.ClarkWrightSavingsAlgorithm;

import vhr.core.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by quachv on 4/21/2017.
 */
public class ClarkWrightSavingsAlgorithm implements IVRPAlgorithm {
    protected HashSet<CWSaving> cwSavingHashSet;

    public ClarkWrightSavingsAlgorithm() {
        cwSavingHashSet = new HashSet<>();
    }


    @Override
    public VRPSolution solve(VRPInstance vrpInstance) throws Exception {
        List<CWSavingPath> cwSavingPaths = new ArrayList<>();
        HashSet<Integer> servedCustomers = new HashSet<>();
        HashSet<Integer> headerCustomer;
        VRPSolution vrpSolution = new VRPSolution(vrpInstance);
        buildSavingCostTable(vrpInstance);
        List<CWSaving> cwSavingsInOrder = cwSavingHashSet.stream()
                .sorted(Collections.reverseOrder(Comparator.comparingDouble(CWSaving::getSavingCost)))
                .collect(Collectors.toList());

        CWSavingPath currentCWSavingPath;
        boolean isMergedToAPath = false;
        for (CWSaving cwSaving : cwSavingsInOrder) {
            isMergedToAPath = false;
            currentCWSavingPath = null;
            for (CWSavingPath cwSavingPath : cwSavingPaths) {
                if(cwSavingPath.add(cwSaving)) {
                    markServedCustomer(servedCustomers, cwSaving);
                    isMergedToAPath = true;
                    currentCWSavingPath = cwSavingPath;
                    break;
                }
            }
            if(!isMergedToAPath && !iscontainserveredcustomer(servedCustomers, cwSaving)) {
                CWSavingPath cwSavingPath = new CWSavingPath(vrpInstance);
                cwSavingPath.add(cwSaving);
                cwSavingPaths.add(cwSavingPath);
                markServedCustomer(servedCustomers, cwSaving);
                currentCWSavingPath = cwSavingPath;
            }
            if(currentCWSavingPath == null) {
                continue;
            }
            for (int i = 0; i < cwSavingPaths.size() - 1; i++) {
                if(currentCWSavingPath.equals(cwSavingPaths.get(i))) {
                    continue;
                }
                CWSavingPath megedPath = currentCWSavingPath.merge(cwSavingPaths.get(i));
                if(megedPath != null) {
                    cwSavingPaths.remove(i);
                    cwSavingPaths.remove(currentCWSavingPath);
                    cwSavingPaths.add(megedPath);
                    break;
                }
            }
        }


        return null;
    }

    private boolean isAInteriorCustomer(int customerId, HashSet<CWSavingPath> cwSavingPaths) {
        CWSavingPath cwSavingPath = cwSavingPaths.stream()
                .filter(p -> p.headCustomerId == customerId || p.tailCustomerId == customerId)
                .findFirst().orElse(null);
        return cwSavingPath == null;
    }

    private void markServedCustomer(HashSet<Integer> servedCustomers, CWSaving cwSaving) {
        if(!servedCustomers.contains(cwSaving.getCustomerId1())) {
            servedCustomers.add(cwSaving.getCustomerId1());
        }
        if(!servedCustomers.contains(cwSaving.getCustomerId2())) {
            servedCustomers.add(cwSaving.getCustomerId2());
        }

    }

    private boolean iscontainserveredcustomer(HashSet<Integer> servedCustomers, CWSaving cwSaving) {
        return servedCustomers.contains(cwSaving.getCustomerId1()) || servedCustomers.contains(cwSaving.getCustomerId2());
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
        private ArrayList<CWSaving> cwSavingPath;
        private HashSet<Integer> customerIds;
        private double totalDemand;
        private int headCustomerId;
        private int tailCustomerId;

        public CWSavingPath(VRPInstance vrpInstance) {
            this.vrpInstance = vrpInstance;
            cwSavingPath = new ArrayList<>();
            customerIds = new HashSet<>();
            totalDemand = 0;
            headCustomerId = -1;
            tailCustomerId = -1;
        }

        public boolean add(CWSaving cwSaving) {
            Customer customer1 = vrpInstance.getCustomer(cwSaving.getCustomerId1());
            Customer customer2 = vrpInstance.getCustomer(cwSaving.getCustomerId2());
            if(cwSavingPath.size() == 0) {
                cwSavingPath.add(cwSaving);
                totalDemand += customer1.getDemand();
                totalDemand += customer2.getDemand();
                customerIds.add(customer1.getId());
                customerIds.add(customer2.getId());
                headCustomerId = customer1.getId();
                tailCustomerId = customer2.getId();
                return true;
            }

            if(totalDemand + customer1.getDemand() > vrpInstance.getCapacity()
                    && totalDemand + customer2.getDemand() > vrpInstance.getCapacity() ) {
                return false;
            }
            if(!cwSaving.hasCustomerId(headCustomerId) && !cwSaving.hasCustomerId(tailCustomerId)) {
                return false;
            }
            if(cwSaving.hasCustomerId(headCustomerId) && cwSaving.hasCustomerId(tailCustomerId)) {
                return false;
            }
            if(customerIds.contains(cwSaving.getCustomerId1()) && customerIds.contains(cwSaving.getCustomerId2())) {
                return false;
            }

            if(cwSaving.hasCustomerId(headCustomerId)) {
                int anotherEnd = cwSaving.getAnotherEndOf(headCustomerId);
                if(totalDemand + vrpInstance.getCustomer(anotherEnd).getDemand() > vrpInstance.getCapacity()) {
                    return false;
                }
                cwSavingPath.add(0, cwSaving);
                headCustomerId = anotherEnd;
                totalDemand += vrpInstance.getCustomer(anotherEnd).getDemand();
            }

            if(cwSaving.hasCustomerId(tailCustomerId)) {
                int anotherEnd = cwSaving.getAnotherEndOf(tailCustomerId);
                if(totalDemand + vrpInstance.getCustomer(anotherEnd).getDemand() > vrpInstance.getCapacity()) {
                    return false;
                }
                cwSavingPath.add(cwSavingPath.size(), cwSaving);
                tailCustomerId = anotherEnd;
                totalDemand += vrpInstance.getCustomer(anotherEnd).getDemand();
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
                Collections.reverse(anotherPath.cwSavingPath);
                mergedPath.cwSavingPath.addAll(anotherPath.cwSavingPath);
                mergedPath.cwSavingPath.addAll(cwSavingPath);
            }

            if(headCustomerId == anotherPath.tailCustomerId || tailCustomerId == anotherPath.headCustomerId) {
                mergedPath.headCustomerId = anotherPath.headCustomerId;
                mergedPath.tailCustomerId = tailCustomerId;
                mergedPath.cwSavingPath.addAll(anotherPath.cwSavingPath);
                mergedPath.cwSavingPath.addAll(cwSavingPath);
            }

            return mergedPath;
        }

        private LinkedList<Integer> buildRoute() {
            if(headCustomerId <= 0 || tailCustomerId <= 0) {
                return null;
            }
            LinkedList<Integer> path = new LinkedList<>();
            path.push(headCustomerId);
            int customerId1 = headCustomerId;
            int customerId2;
            for (int i = 0; i < cwSavingPath.size(); i++) {
                customerId2 = cwSavingPath.get(i).getAnotherEndOf(customerId1);
                path.push(customerId2);
                customerId1 = customerId2;
            }
            return  path;
        }

        public HashSet<CWSaving> getCWSaving(int customerId) {
            return cwSavingPath.stream().filter(cwsaving -> cwsaving.hasCustomerId(customerId))
                    .collect(Collectors.toCollection(HashSet::new));
        }

        public void removeCWSaving(CWSaving cwSaving) {
            cwSavingPath.remove(cwSaving);
        }

        public boolean contains(CWSaving cwSaving) {
            return customerIds.contains(cwSaving.getCustomerId1()) || customerIds.contains(cwSaving.getCustomerId2());
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
            if(other.cwSavingPath.size() != cwSavingPath.size()) {
                return false;
            }
            for (int i = 0; i < cwSavingPath.size(); i++) {
                if(!cwSavingPath.get(i).equals(other.cwSavingPath.get(i))) {
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
        }

        public double getSavingCost(){
            return savingCost;
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

        public int getJoinedCustomerId(CWSaving anotherCWSaving) {
            int joinedCustomerId = -1;
            if(anotherCWSaving == this) {
                return joinedCustomerId;
            }
            if(hasCustomerId(anotherCWSaving.getCustomerId1())) {
                joinedCustomerId = anotherCWSaving.getCustomerId1();
            }
            if(hasCustomerId(anotherCWSaving.getCustomerId2())) {
                joinedCustomerId = anotherCWSaving.getCustomerId2();
            }
            return joinedCustomerId;
        }

        public boolean hasCustomerId(int customerId) {
            return customerId1 == customerId || customerId2 == customerId;
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
