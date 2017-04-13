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
    protected boolean routeValid;

    public VehicleRoute(int id, final VRPInstance vrpInstance) {
        this.id = id;
        this.vrpInstance = vrpInstance;
        route = new LinkedList<>();
        customerIds = new HashSet<>();
        routeValid = true;
    }

    public int getId() {
        return id;
    }

    public void addCustomer(int customerId) {
        customerIds.add(customerId);
        routeValid = true;
    }

    public void removeCustomer(int customerId) {
        customerIds.remove(customerId);
        route.remove(new Integer(customerId));
        routeValid = false;
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

    public double getRouteCost(ICostCalculator costCalculator) throws Exception {
        if(!isRouteValid()) {
            throw new Exception("Route is not valid");
        }
        return costCalculator.calculateRouteCost(route, vrpInstance);
    }

    public void setRoute(LinkedList<Integer> path) {
        if(path.stream().filter(customerId -> !customerIds.contains(customerId)).count() > 0
                || customerIds.stream().filter(customerId -> !path.contains(customerId)).count() > 0) {
            return;
        }
        this.route = path;
        routeValid = true;
    }

    public boolean isRouteValid() {
        return routeValid;
    }

    public LinkedList<Integer> getRoute() {
        return this.route;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null || !(obj instanceof VehicleRoute)) {
            return false;
        }

        if(customerIds.size() != ((VehicleRoute) obj).customerIds.size()) {
            return false;
        }

        if(customerIds.stream().filter(customerId -> !((VehicleRoute) obj).customerIds.contains(customerId)).count() > 0
                || ((VehicleRoute) obj).customerIds.stream().filter(customerId -> customerIds.contains(customerId)).count() > 0) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return customerIds.hashCode();
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
