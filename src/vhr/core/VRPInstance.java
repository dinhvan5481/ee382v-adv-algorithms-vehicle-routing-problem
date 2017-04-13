package vhr.core;

import java.io.IOException;
import java.util.*;

import static vhr.utils.StringUtil.appendStringLine;
import java.io.FileWriter;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by quachv on 3/15/2017.
 */
public class VRPInstance {
    private String instanceName;
    private String comment;
    private int numberOfCustomers;
    private double capacity;
    private int maxTruck;
    private HashMap<Integer, Customer> customers;
    private Customer depot;

    private ConcurrentHashMap<DeliveryPath, Double> costMatrix;
    private ConcurrentHashMap<DeliveryPath, Double> distanceMatrix;
    private ConcurrentHashMap<VehicleRoute, Double> vehicleRoutes;
    private ICostCalculator costCalculator;
    private IDistanceCalculator distanceCalculator;

    public VRPInstance() {
        this.customers = new HashMap<>();
        costMatrix = new ConcurrentHashMap<>();
        distanceMatrix = new ConcurrentHashMap<>();
        vehicleRoutes = new ConcurrentHashMap<>();
    }


    public void setInstanceName(String name) {
        this.instanceName = name;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public int getNumberOfCustomers() {
        return numberOfCustomers;
    }

    public void setNumberOfCustomers(int numberOfCustomers) {
        this.numberOfCustomers = numberOfCustomers;
    }

    public double getCapacity() {
        return capacity;
    }

    public void setCapacity(double capacity) {
        this.capacity = capacity;
    }

    public void addCustomer(Customer customer) {
        if(this.customers.containsKey(customer.getId())) {
            return;
        }
        this.customers.put(customer.getId(), customer);
    }

    public Customer getCustomer(int id) {
        return this.customers.get(id);
    }

    public Customer getDepot() {
        return depot;
    }

    public Collection<Customer> getCustomers() {
        return customers.values();
    }

    public Set<Integer> getCustomerIds() {
        return customers.keySet();
    }

    public void setDepot(Customer depot) {
        this.depot = depot;
        if(customers.containsKey(depot.getId())) {
            customers.remove(depot.getId());
        }
    }

    public boolean isValid(VRPSolution solution) {
        boolean result = false;
        Iterator<VehicleRoute> routeIterator = solution.getRoutes().iterator();
        Set<Integer> routeCustomers = new HashSet<>();
        int sizeBefore = 0;
        while (routeIterator.hasNext()) {
            VehicleRoute route = routeIterator.next();
            sizeBefore = routeCustomers.size();
            routeCustomers.addAll(route.getCustomerKeys());
            if(sizeBefore + route.numberOfCustomers() != routeCustomers.size()) {
                return false;
            }
            if(route.getTotalDemand() > capacity) {
                return false;
            }
        }
        if(routeCustomers.size() != customers.size()) {
            return result;
        }
        routeCustomers.removeAll(customers.keySet());
        if(routeCustomers.size() > 0) {
            return result;
        }
        return true;
    }

    public double getDistance(Customer from, Customer to) {
        DeliveryPath path = new DeliveryPath(from, to);
        if(!distanceMatrix.containsKey(path)) {
            distanceMatrix.put(path, distanceCalculator.calculate(from.getCoordinate(), to.getCoordinate()));
        }
        return distanceMatrix.get(path);
    }

    public double getRouteCost(LinkedList<Integer> route) {
        return costCalculator.calculateRouteCost(route, this);
    }

    public ICostCalculator getCostCalculator() {
        return costCalculator;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        appendStringLine(sb, "Instance name: " + instanceName);
        if(!comment.isEmpty()) {
            appendStringLine(sb, "Comment: " + comment);
        }
        appendStringLine(sb, "Number of customer: " + numberOfCustomers);
        appendStringLine(sb, "Vehicle capacity: " + capacity);
        appendStringLine(sb, "Depot: " + depot.toString());
        for (Integer customerId: customers.keySet()) {
            Customer customer = customers.get(customerId);
            appendStringLine(sb, customer.toString());
        }
        return sb.toString();
    }

    public void toCSV(String fileName) {
        StringBuilder sb = new StringBuilder();
        FileWriter fileWriter = null;
        Collection<Customer> customers = this.customers.values();
        appendStringLine(sb, depot.getId() + " " + depot.getCoordinate().getX() + " " + depot.getCoordinate().getY());
        customers.forEach((Customer customer) -> {
            appendStringLine(sb, customer.getId() + " " + customer.getCoordinate().getX() + " " + customer.getCoordinate().getY());
        });

        try {
            fileWriter = new FileWriter(fileName);
            fileWriter.write(sb.toString(), 0, sb.length());
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public int getMaxTruck() {
        return maxTruck;
    }

    public void setMaxTruck(int maxTruck) {
        this.maxTruck = maxTruck;
    }

    public static class Builder {
        private IDataExtract dataExtract;
        private String fileName;

        private ICostCalculator costCalculator;
        private IDistanceCalculator distanceCalculator;
        private int maxTruck;

        public Builder(IDataExtract dataExtract) {
            this.dataExtract = dataExtract;
            maxTruck = -1;
        }

        public Builder setDataFileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public Builder setCostCalculator(ICostCalculator costCalculator) {
            this.costCalculator = costCalculator;
            return this;
        }



        public Builder setDistanceCalculator(IDistanceCalculator distanceCalculator) {
            this.distanceCalculator = distanceCalculator;
            return this;
        }

        public VRPInstance build() throws Exception {
            if(costCalculator == null || distanceCalculator == null || fileName.isEmpty()) {
                throw new Exception("Cost calculator or distance calculator not setting");
            }
            VRPInstance vrpInstance = dataExtract.extractDataFrom(fileName);
            vrpInstance.costCalculator = costCalculator;
            vrpInstance.distanceCalculator = distanceCalculator;
            vrpInstance.maxTruck = maxTruck;
            return vrpInstance;
        }

        public Builder setMaxTruck(int maxTruck) {
            this.maxTruck = maxTruck;
            return this;
        }
    }
}
