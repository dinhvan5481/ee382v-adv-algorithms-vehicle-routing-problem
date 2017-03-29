package vhr.core;

import java.util.*;

import static vhr.utils.StringUtil.appendStringLine;

/**
 * Created by quachv on 3/22/2017.
 */
public class VehicleRoute {
    protected int id;
    protected Customer depot;
    protected HashMap<Integer, Customer> customers;
    protected LinkedList<Integer> route;

    public VehicleRoute(int id, Customer depot) {
        this.id = id;
        this.depot = depot;
        route = new LinkedList<>();
        customers = new HashMap<>();
    }

    public int getId() {
        return id;
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
            Customer to = customers.get(route.get(i));
            result += costCalulator.calculate(from, to);
            from = to;
        }
        result += costCalulator.calculate(from, depot);
        return result;
    }

    public void buildRoute() {
        // TODO: this routine has a lot potential to optimal, and many strategy to build the route

    }

    public void setRoute(LinkedList<Integer> route) {
        this.route = route;
    }

    public boolean isRouteValid() {
        boolean result = false;
        if(customers.size() != route.size()) {
            return result;
        }
        for (int i = 0; i < route.size() - 1; i++) {
            if(!customers.containsKey(route.get(i))) {
                return result;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        appendStringLine(sb, "Route " + id + " total demand: " + getTotalDemand());
        sb.append(depot.getId());
        route.forEach((Integer id) -> sb.append(" -> " + id.toString()));
        sb.append(" -> " + depot.getId());
        return sb.toString();
    }

}
