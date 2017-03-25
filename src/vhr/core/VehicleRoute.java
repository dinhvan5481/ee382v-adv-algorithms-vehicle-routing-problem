package vhr.core;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by quachv on 3/22/2017.
 */
public class VehicleRoute {
    private int id;
    private Customer depot;
    private HashMap<Integer, Customer> customers;
    private LinkedList<Customer> path;
    private HashMap<String, Double> costMatrix;

    public VehicleRoute(int id, Customer depot) {
        this.id = id;
        this.depot = depot;
        this.addCustomer(depot);
        path = new LinkedList<>();
        path.push(this.depot);
        costMatrix = new HashMap<>();
    }

    public void addCustomer(Customer cusotmer) {
        if(!customers.containsKey(cusotmer.getId())) {
            customers.put(cusotmer.getId(), cusotmer);

        }
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

    public void buildRoute() {
        // TODO: this routine has a lot potential to optimal, and many strategy to build the route

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
