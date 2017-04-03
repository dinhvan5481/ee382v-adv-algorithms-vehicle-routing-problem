package vhr.core;

import javafx.util.Builder;

import java.util.*;

import static vhr.utils.StringUtil.appendStringLine;

/**
 * Created by quachv on 3/22/2017.
 */
public class VehicleRoute implements Cloneable{
    protected int id;
    protected final VRPInstance vrpInstance;
    protected Set<Integer> customerIds;
    protected LinkedList<Integer> route;

    public VehicleRoute(int id, final VRPInstance vrpInstance) {
        this.id = id;
        this.vrpInstance = vrpInstance;
        route = new LinkedList<>();
        customerIds = new HashSet<>();
    }

    public int getId() {
        return id;
    }

    public void addCustomer(int customerId) {
        customerIds.add(customerId);
    }

    public void removeCustomer(int customerId) {
        customerIds.remove(customerId);
        route.remove(new Integer(customerId));
    }

    public Set<Integer> getCustomerKeys() {
        return customerIds;
    }

    public int numberOfCustomers() {
        return customerIds.size();
    }

    public double getTotalDemand() {
        double result = 0;
        for (Integer customerId : customerIds) {
            Customer customer = vrpInstance.getCustomer(customerId);
            result += customer.getDemand();
        }
        return result;
    }

    public double getRouteCost(ICostCalculator costCalulator) {
        double result = 0;
        Customer from = vrpInstance.getDepot();
        for (int i = 0; i < route.size() - 1; i++) {
            Customer to = vrpInstance.getCustomer(route.get(i));
            result += costCalulator.calculate(from, to);
            from = to;
        }
        result += costCalulator.calculate(from, vrpInstance.getDepot());
        return result;
    }

    public void setRoute(LinkedList<Integer> route) {
        this.route = route;
    }

    public LinkedList<Integer> getRoute() {
        return this.route;
    }

    public boolean isRouteValid() {
        boolean result = false;
        if(customerIds.size() != route.size()) {
            return result;
        }
        for (int i = 0; i < route.size() - 1; i++) {
            if(!customerIds.contains(route.get(i))) {
                return result;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        appendStringLine(sb, "Route " + id + " total demand: " + getTotalDemand());
        sb.append(vrpInstance.getDepot().getId());
        route.forEach((Integer id) -> sb.append(" -> " + id.toString()));
        sb.append(" -> " + vrpInstance.getDepot().getId());
        return sb.toString();
    }

    public VehicleRoute clone() throws CloneNotSupportedException {
        VehicleRoute newVehicleRoute = new VehicleRoute(id, vrpInstance);
        LinkedList<Integer> newRoute = new LinkedList<>(route);
        customerIds.forEach(id -> newVehicleRoute.addCustomer(id));
        newVehicleRoute.setRoute(newRoute);
        return newVehicleRoute;
    }
}
