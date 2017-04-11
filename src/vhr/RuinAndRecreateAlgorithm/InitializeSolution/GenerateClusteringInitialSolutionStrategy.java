package vhr.RuinAndRecreateAlgorithm.InitializeSolution;

import vhr.core.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.concurrent.ThreadLocalRandom;

import static java.util.Collections.max;
import static java.util.Collections.min;

/**
 * Created by quachv on 3/27/2017.
 */
public class GenerateClusteringInitialSolutionStrategy implements IGenerateInitialSolutionStrategy {

    protected long randomSeed;
    protected GenerateClusteringInitialSolutionStrategy(long randomSeed) {
        this.randomSeed = randomSeed;
    }


    @Override
    public VRPSolution generateSolution(VRPInstance vrpInstance) {
        Customer depot = vrpInstance.getDepot();
        Customer baseCustomer;
        List<Node> customerNodes = new ArrayList<>();

        int minCustomerId = min(vrpInstance.getCustomerIds());
        int maxCustomerId = max(vrpInstance.getCustomerIds());
        int baseCustomerId = ThreadLocalRandom.current().nextInt(minCustomerId, maxCustomerId + 1);
        baseCustomer = vrpInstance.getCustomer(baseCustomerId);
        vrpInstance.getCustomers().forEach((Customer customer) -> {
            Node customerNode = new Node(customer.getId(),
                    ((AbstractCoordinate) depot.getCoordinate())
                            .substract((AbstractCoordinate)customer.getCoordinate())
            );
            customerNode
                    .setDistance(vrpInstance.getDistance(depot, customer));
            customerNodes.add(customerNode);
        });

        AbstractCoordinate baseVector = ((AbstractCoordinate) depot.getCoordinate())
                .substract((AbstractCoordinate)baseCustomer.getCoordinate());

        customerNodes.forEach((Node node) -> node.thetaFrom(baseVector));
        customerNodes.sort(Comparator.comparingDouble(Node::getTheta));
        List<Node> customerNodesWithNegativeTheta = customerNodes.stream()
                .filter(n -> n.getTheta() < 0).collect(Collectors.toList());

        List<Node> customerNodesWIthPositiveTheta = customerNodes.stream()
                .filter(n -> n.getTheta() >= 0).collect(Collectors.toList());

        customerNodesWIthPositiveTheta.sort(Comparator.comparingDouble(Node::getTheta));

        VRPSolution vrpSolution = new VRPSolution(vrpInstance);
        VehicleRoute vehicleRoute = vrpSolution.createNewRoute();
        LinkedList<Integer> route = new LinkedList<>();
        double totalDemand = 0;

        for (Node node : customerNodesWIthPositiveTheta) {
            Customer customer = vrpInstance.getCustomer(node.getId());
            if (totalDemand + customer.getDemand() < vrpInstance.getCapacity()) {
                totalDemand += customer.getDemand();
            } else {
                vehicleRoute.setRoute(route);
                route = new LinkedList<>();
                vehicleRoute = vrpSolution.createNewRoute();
                totalDemand = customer.getDemand();
            }
            route.push(customer.getId());
            vehicleRoute.addCustomer(customer.getId());
        }

        for (Node node : customerNodesWithNegativeTheta) {
            Customer customer = vrpInstance.getCustomer(node.getId());
            if (totalDemand + customer.getDemand() < vrpInstance.getCapacity()) {
                totalDemand += customer.getDemand();
            } else {
                vehicleRoute.setRoute(route);
                route = new LinkedList<>();
                vehicleRoute = vrpSolution.createNewRoute();
                totalDemand = customer.getDemand();
            }
            route.push(customer.getId());
            vehicleRoute.addCustomer(customer.getId());
        }

        if(route.size() > 0) {
            vehicleRoute.setRoute(route);
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

    public static class Builder {

        private long randomSeed;

        public Builder(long randomSeed) {
            this.randomSeed = randomSeed;
        }

        public IGenerateInitialSolutionStrategy build() {
            return new GenerateClusteringInitialSolutionStrategy(randomSeed);
        }
    }
}
