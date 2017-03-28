package vhr.core;

import java.util.*;

import static vhr.utils.StringUtil.appendStringLine;

/**
 * Created by quachv on 3/15/2017.
 */
public class VRPInstance {
    private String instanceName;
    private String comment;
    private int numberOfCustomers;
    private double capacity;
    private HashMap<Integer, Customer> customers;
    private Customer depot;

    public VRPInstance() {
        this.customers = new HashMap<>();
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

    public void setDepot(Customer depot) {
        this.depot = depot;
        if(customers.containsKey(depot.getId())) {
            customers.remove(depot.getId());
        }
    }

    public boolean isSolutionValid(VRPSolution solution) {
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


}
