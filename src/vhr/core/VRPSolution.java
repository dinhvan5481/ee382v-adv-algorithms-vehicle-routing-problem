package vhr.core;

/**
 * Created by quachv on 3/22/2017.
 */


import javafx.util.Builder;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static vhr.utils.StringUtil.appendStringLine;

public class VRPSolution implements Comparable<VRPSolution>, Cloneable {

    protected final VRPInstance vrpInstance;
    protected HashMap<Integer, VehicleRoute> routes;
    protected ICostCalculator costCalculator;

    public VRPSolution(VRPInstance vrpInstance, ICostCalculator costCalculator) {
        routes = new HashMap<>();
        this.costCalculator = costCalculator;
        this.vrpInstance = vrpInstance;
    }
    public Collection<VehicleRoute> getRoutes() {
        return routes.values();
    }

    public void addRoute(VehicleRoute route) {
        routes.put(route.id, route);
    }

    public VehicleRoute getRoute(int routeId) {
        return routes.get(routeId);
    }

    public void cleanEmptyRoute() {
        List<Integer> emptyRoute = routes.keySet().stream()
                .filter(id -> routes.get(id).customerIds.size() == 0)
                .collect(Collectors.toList());
        emptyRoute.forEach(id -> routes.remove(id));
    }

    public VehicleRoute createNewRoute() {
        int routeId = 0;
        while (routes.keySet().contains(routeId)) {
            routeId++;
        }
        VehicleRoute newVehicleRoute = new VehicleRouteBuilder(routeId, vrpInstance).build();
        routes.put(routeId, newVehicleRoute);
        return newVehicleRoute;
    }

    public double getSolutionCost() {
        double result = 0;
        Iterator<Integer> routeIterator = routes.keySet().iterator();
        while (routeIterator.hasNext()) {
            VehicleRoute route = routes.get(routeIterator.next());
            result += route.getRouteCost(costCalculator);
        }
        return result;
    }

    public boolean isSolutionValid() {
        return this.vrpInstance.isValid(this);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        appendStringLine(sb, "Total cost : " + getSolutionCost());
        for (Integer routeId: routes.keySet()) {
            appendStringLine(sb, "Route " + routeId + " total cost: " + routes.get(routeId).getRouteCost(costCalculator));
            appendStringLine(sb, routeId.toString());
        }
        return sb.toString();
    }

    public void toCSV(String fileName) {
        StringBuilder sb = new StringBuilder();
        FileWriter fileWriter = null;
        routes.values().forEach((VehicleRoute route) -> {
            sb.append(vrpInstance.getDepot().getId() + " ");
            route.route.forEach((Integer customerId) -> {
                sb.append(customerId + " ");
            });
            sb.append(vrpInstance.getDepot().getId());
            appendStringLine(sb, "");
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

    @Override
    public int compareTo(VRPSolution o) {
        // Smaller is better
        int better = -1;
        int equal = 0;
        int worse = 1;

        if(!o.isSolutionValid() && !this.isSolutionValid()) {
            return equal;
        }

        if(!o.isSolutionValid() && this.isSolutionValid()) {
            return better;
        }

        if(o.isSolutionValid() && !this.isSolutionValid()) {
            return worse;
        }

        if(o.isSolutionValid() & this.isSolutionValid()) {
            double esp = 10^-5;
            if(routes.size() == o.routes.size() && (o.getSolutionCost() - this.getSolutionCost()) < esp) {
                return equal;
            }
            if((o.getSolutionCost() - this.getSolutionCost()) > esp) {
                return better;
            } else {
                return worse;
            }
        }
        return equal;
    }

    public VRPSolution clone() throws CloneNotSupportedException {
        VRPSolution newVRPSolution = new VRPSolutionBuilder(vrpInstance, costCalculator).build();
        routes.forEach((k, v) -> {
            try {
                newVRPSolution.addRoute(v.clone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        });

        return newVRPSolution;
    }

    public static class VRPSolutionBuilder implements Builder<VRPSolution>{
        private final VRPInstance vrpInstance;
        private final ICostCalculator costCalculator;

        public VRPSolutionBuilder(VRPInstance vrpInstance, ICostCalculator costCalculator) {
            this.vrpInstance = vrpInstance;
            this.costCalculator = costCalculator;
        }

        @Override
        public VRPSolution build() {
            return new VRPSolution(vrpInstance, costCalculator);
        }
    }


    public static class VehicleRouteBuilder  implements Builder<VehicleRoute> {
        private VehicleRoute route;
        public VehicleRouteBuilder(int routeId, VRPInstance vrpInstance) {
            this.route = new VehicleRoute(routeId, vrpInstance);
        }

        @Override
        public VehicleRoute build() {
            return route;
        }
    }
}
