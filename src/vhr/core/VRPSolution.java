package vhr.core;

/**
 * Created by quachv on 3/22/2017.
 */


import java.util.*;

import static vhr.utils.StringUtil.appendStringLine;

public class VRPSolution implements Comparable<VRPSolution> {



    protected VRPInstance vrpInstance;
    protected HashSet<VehicleRoute> routes;
    protected ICostCalculator costCalulator;

    public VRPSolution(VRPInstance vrpInstance, ICostCalculator costCalulator) {
        routes = new HashSet<>();
        this.costCalulator = costCalulator;
        this.vrpInstance = vrpInstance;
    }

    public VRPInstance getVrpInstance() {
        return vrpInstance;
    }

    public HashSet<VehicleRoute> getRoutes() {
        return routes;
    }

    public void addRoute(VehicleRoute route) {
        routes.add(route);
    }

    public double getCost() {
        double result = 0;
        Iterator<VehicleRoute> routeIterator = routes.iterator();
        while (routeIterator.hasNext()) {
            VehicleRoute route = routeIterator.next();
            result += route.getRouteCost(costCalulator);
        }
        return result;
    }

    public boolean isAValidSolution() {
        return this.vrpInstance.isSolutionValid(this);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (VehicleRoute route: routes
             ) {
            appendStringLine(sb, route.toString());

        }
        return sb.toString();
    }

    @Override
    public int compareTo(VRPSolution o) {
        // Smaller is better
        int better = -1;
        int equal = 0;
        int worse = 1;

        if(!o.isAValidSolution() && !this.isAValidSolution()) {
            return equal;
        }

        if(!o.isAValidSolution() && this.isAValidSolution()) {
            return better;
        }

        if(o.isAValidSolution() && !this.isAValidSolution()) {
            return worse;
        }

        if(o.isAValidSolution() & this.isAValidSolution()) {
            double esp = 10^-5;
            if(routes.size() == o.routes.size() && (o.getCost() - this.getCost()) < esp) {
                return equal;
            }
            if((o.getCost() - this.getCost()) > esp) {
                return better;
            } else {
                return worse;
            }
        }
        return equal;
    }
}
