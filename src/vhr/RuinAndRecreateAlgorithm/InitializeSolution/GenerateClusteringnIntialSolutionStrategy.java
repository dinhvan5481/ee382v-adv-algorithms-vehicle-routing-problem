package vhr.RuinAndRecreateAlgorithm.InitializeSolution;

import vhr.core.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by quachv on 3/27/2017.
 */
public class GenerateClusteringnIntialSolutionStrategy implements IGenerateInitialSolutionStrategy {

    private IDistanceCalulator distanceCalulator;
    public GenerateClusteringnIntialSolutionStrategy(IDistanceCalulator distanceCalulator) {
        this.distanceCalulator = distanceCalulator;

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

        VH
        return null;
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
