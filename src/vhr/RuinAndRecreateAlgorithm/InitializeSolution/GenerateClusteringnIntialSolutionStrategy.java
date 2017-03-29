package vhr.RuinAndRecreateAlgorithm.InitializeSolution;

import vhr.core.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by quachv on 3/27/2017.
 */
public class GenerateClusteringnIntialSolutionStrategy implements IGenerateInitialSolutionStrategy {

    private IDistanceCalulator distanceCalulator;
    private ICostCalculator costCalculator;
    public GenerateClusteringnIntialSolutionStrategy(IDistanceCalulator distanceCalulator, ICostCalculator costCalculator) {
        this.distanceCalulator = distanceCalulator;
        this.costCalculator = costCalculator;
    }

    @Override
    public VRPSolution generateSolution(VRPInstance vrpInstance) {
        Customer depot = vrpInstance.getDepot();
        Customer closestDepotCustomer = null;
        Collection<Customer> customerCollection = vrpInstance.getCustomers();
        Iterator<Customer> customerIterator = customerCollection.iterator();
        List<Node> customerNodes = new ArrayList<>();

        double closetDistance = Double.MAX_VALUE;
        while (customerIterator.hasNext()) {
            Customer customer = customerIterator.next();
            Node customerNode = new Node(customer.getId(),
                    ((AbstractCoordinate) depot.getCoordinate()).substract((AbstractCoordinate)customer.getCoordinate()));
            customerNode.setDistance(distanceCalulator.calculate(depot.getCoordinate(), customer.getCoordinate()));
            if(customerNode.getDistance() < closetDistance) {
                closetDistance = customerNode.getDistance();
                closestDepotCustomer = customer;
            }
            customerNodes.add(customerNode);
        }

        AbstractCoordinate baseVector = ((AbstractCoordinate) depot.getCoordinate())
                .substract((AbstractCoordinate)closestDepotCustomer.getCoordinate());

        customerNodes.forEach((Node node) -> node.thetaFrom(baseVector));
        customerNodes.sort(Comparator.comparingDouble(Node::getTheta));
        List<Node> customerNodesWithNegativeTheta = customerNodes.stream()
                .filter(n -> n.getTheta() < 0).collect(Collectors.toList());

        List<Node> customerNodesWIthPositiveTheta = customerNodes.stream()
                .filter(n -> n.getTheta() >= 0).collect(Collectors.toList());

        customerNodesWIthPositiveTheta.sort(Comparator.comparingDouble(Node::getTheta));

        VRPSolution vrpSolution = new VRPSolution(vrpInstance, costCalculator);
        int routeId = 0;
        VehicleRoute vehicleRoute = new VehicleRoute(routeId, vrpInstance.getDepot());
        LinkedList<Integer> route = new LinkedList<>();
        double totalDemand = 0;

        for (Node node : customerNodesWIthPositiveTheta) {
            Customer customer = vrpInstance.getCustomer(node.getId());
            if (totalDemand + customer.getDemand() < vrpInstance.getCapacity()) {
                totalDemand += customer.getDemand();
            } else {
                vehicleRoute.setRoute(route);
                vrpSolution.addRoute(vehicleRoute);
                route = new LinkedList<>();
                vehicleRoute = new VehicleRoute(++routeId, vrpInstance.getDepot());
                totalDemand = customer.getDemand();
            }
            route.push(customer.getId());
            vehicleRoute.addCustomer(customer);
        }

        for (Node node : customerNodesWithNegativeTheta) {
            Customer customer = vrpInstance.getCustomer(node.getId());
            if (totalDemand + customer.getDemand() < vrpInstance.getCapacity()) {
                totalDemand += customer.getDemand();
            } else {
                vehicleRoute.setRoute(route);
                vrpSolution.addRoute(vehicleRoute);
                route = new LinkedList<>();
                vehicleRoute = new VehicleRoute(++routeId, vrpInstance.getDepot());
                totalDemand = customer.getDemand();
            }
            route.push(customer.getId());
            vehicleRoute.addCustomer(customer);
        }


        return vrpSolution;
    }

    private class Node {
        private int id;
        private double distance;
        private double theta;
        private AbstractCoordinate vectorCoord;

        public Node(int id, AbstractCoordinate vectorCoord) {
            this.id = id;
            this.vectorCoord = vectorCoord;
        }

        public double getDistance() {
            return distance;
        }

        public void setDistance(double distance) {
            this.distance = distance;
        }

        public double getTheta() {
            return theta;
        }

        public void thetaFrom(AbstractCoordinate fromVector) {
            theta = Math.atan2(fromVector.getY(), fromVector.getX()) - Math.atan2(vectorCoord.getY(), vectorCoord.getX());
        }

        public int getId() {
            return id;
        }
    }
}
