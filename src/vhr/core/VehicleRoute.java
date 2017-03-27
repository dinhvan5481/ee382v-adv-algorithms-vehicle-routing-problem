package vhr.core;

import java.util.*;

/**
 * Created by quachv on 3/22/2017.
 */
public class VehicleRoute {
    protected int id;
    protected Customer depot;
    protected HashMap<Integer, Customer> customers;
    protected LinkedList<Customer> route;

    public VehicleRoute(int id, Customer depot) {
        this.id = id;
        this.depot = depot;
        route = new LinkedList<>();
    }

    public void addCustomer(Customer cusotmer) {
        if(!customers.containsKey(cusotmer.getId())) {
            customers.put(cusotmer.getId(), cusotmer);
        }
    }

    public void removeCustomer(int customerId) {
        if(customers.containsKey(customerId)) {
            customers.remove(customerId);
        }
    }

    public Set<Integer> getCustomerKeys() {
        return customers.keySet();
    }

    public int numberOfCustomers() {
        return customers.size();
    }

    public double getTotalDemand() {
        double result = 0;
        for (Integer customerId :
                customers.keySet()) {
            Customer customer = customers.get(customerId);
            result += customer.getDemand();
        }
        return result;
    }

    public double getRouteCost(ICostCalculator costCalulator) {
        double result = 0;
        Customer from = depot;
        for (int i = 0; i < route.size() - 1; i++) {
            Customer to = route.get(i);
            result += costCalulator.calculate(from, to);
            from = to;
        }
        result += costCalulator.calculate(from, depot);
        return result;
    }

    public void buildRoute() {
        // TODO: this routine has a lot potential to optimal, and many strategy to build the route

    }

    public boolean isRouteValid() {
        boolean result = false;
        if(customers.size() != route.size()) {
            return result;
        }
        for (int i = 0; i < route.size() - 1; i++) {
            if(!customers.containsKey(route.get(i).getId())) {
                return result;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Customer iterator = depot.getPathOut().getTo();
        while(iterator.getId() != depot.getId()) {
            sb.append(" " + iterator.getId());
        }
        return "VehicleRoute " + id + ":" + sb.toString();
    }

}
