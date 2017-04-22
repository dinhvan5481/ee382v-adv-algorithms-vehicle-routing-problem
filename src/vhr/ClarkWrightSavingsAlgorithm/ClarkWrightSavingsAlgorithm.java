package vhr.ClarkWrightSavingsAlgorithm;

import vhr.core.Customer;
import vhr.core.IVRPAlgorithm;
import vhr.core.VRPInstance;
import vhr.core.VRPSolution;

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
        buildSavingCostTable(vrpInstance);
        List<CWSaving> cwSavingsInOrder = cwSavingHashSet.stream()
                .sorted(Collections.reverseOrder(Comparator.comparingDouble(CWSaving::getSavingCost)))
                .collect(Collectors.toList());

        return null;
    }

    private void buildSavingCostTable(VRPInstance vrpInstance) {
        Customer depot = vrpInstance.getDepot();
        vrpInstance.getCustomerIds().forEach(id -> {
            CWSaving cwSaving = new CWSaving(depot.getId(), id);
            try {
                cwSaving.calculateSavingCost(vrpInstance);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            cwSavingHashSet.add(cwSaving);
        });
        ArrayList<Integer> customerIds = new ArrayList<>(vrpInstance.getCustomerIds());
        for (int index = 0; index < customerIds.size() - 2; index++) {
            Customer customer1 = vrpInstance.getCustomer(customerIds.get(index));
            for (int innerIndex = 0; innerIndex < customerIds.size() - 1; innerIndex++) {
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
        private HashSet<CWSaving> cwSavingHashMap;
        private HashSet<Integer> customerIds;

        public CWSavingPath() {
            cwSavingHashMap = new HashSet<>();
            customerIds = new HashSet<>();
        }

        public void add(CWSaving cwSaving) {
            if(cwSavingHashMap.contains(cwSaving)) {
                return;
            }
            cwSavingHashMap.add(cwSaving);
            if(!customerIds.contains(cwSaving.getCustomerId1())){
                customerIds.add(cwSaving.getCustomerId1());
            }
            if(!customerIds.contains(cwSaving.getCustomerId2())){
                customerIds.add(cwSaving.getCustomerId2());
            }
        }

        public HashSet<CWSaving> getCWSaving(int customerId) {
            return cwSavingHashMap.stream().filter(cwsaving -> cwsaving.hasCustomerId(customerId))
                    .collect(Collectors.toCollection(HashSet::new));
        }

        public void removeCWSaving(CWSaving cwSaving) {
            cwSavingHashMap.remove(cwSaving);
        }

        public boolean contains(CWSaving cwSaving) {
            return customerIds.contains(cwSaving.getCustomerId1()) || customerIds.contains(cwSaving.getCustomerId2());
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
            return customerId == customerId1 ? customerId1 : customerId2;
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
