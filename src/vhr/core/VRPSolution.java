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

    public VRPSolution(VRPInstance vrpInstance) {
        routes = new HashMap<>();
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
            try {
                result += route.getRouteCost(vrpInstance.getCostCalculator());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public boolean isSolutionValid() {
        return this.vrpInstance.isValid(this);
    }

    public boolean isNumberOfRoutesValid() {
        return vrpInstance.getMaxTruck() > 0 && routes.size() <= vrpInstance.getMaxTruck();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        try {
            appendStringLine(sb, "Total cost : " + getSolutionCost());
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (Integer routeId: routes.keySet()) {
            try {
                appendStringLine(sb, "Route " + routeId + " total cost: " + routes.get(routeId)
                        .getRouteCost(vrpInstance.getCostCalculator()));
            } catch (Exception e) {
                e.printStackTrace();
            }
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
        if(o == null) {
            return better;
        }

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
        VRPSolution newVRPSolution = new VRPSolutionBuilder(vrpInstance).build();
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

        public VRPSolutionBuilder(VRPInstance vrpInstance) {
            this.vrpInstance = vrpInstance;
        }

        @Override
        public VRPSolution build() {
            return new VRPSolution(vrpInstance);
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
