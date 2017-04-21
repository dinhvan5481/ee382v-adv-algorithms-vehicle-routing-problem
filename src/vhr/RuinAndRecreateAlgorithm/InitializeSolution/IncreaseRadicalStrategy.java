package vhr.RuinAndRecreateAlgorithm.InitializeSolution;

import vhr.core.*;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * Created by quachv on 4/20/2017.
 */
public class IncreaseRadicalStrategy implements IGenerateInitialSolutionStrategy {

    protected Customer baseCustomer;
    public IncreaseRadicalStrategy() {

    }

    public void setBaseCustomer(Customer customer) {
        baseCustomer = customer;
    }


    @Override
    public VRPSolution generateSolution(VRPInstance vrpInstance) {
        Customer depot = vrpInstance.getDepot();
        HashMap<Integer, Node> customerNodes = new HashMap<>();

        vrpInstance.getCustomers().forEach((Customer customer) -> {
            Node customerNode = new Node(customer.getId(),
                    ((AbstractCoordinate) depot.getCoordinate())
                            .substract((AbstractCoordinate)customer.getCoordinate())
            );
            customerNode
                    .setDistance(vrpInstance.getDistance(depot, customer));
            customerNodes.put(customer.getId(), customerNode);
        });

        // Add customer to route
        VRPSolution result = new VRPSolution(vrpInstance);
        addCustomersToRoute(vrpInstance, result, customerNodes.values());

        // Build route
        result.getRoutes().forEach(r -> buildDeliverRoute(vrpInstance, r, customerNodes));
        return result;
    }

    private void addCustomersToRoute(VRPInstance vrpInstance, VRPSolution vrpSolution, Collection<Node> customerNodes) {
        int numOfRoutes = vrpInstance.getMaxTruck();
        ArrayList<VehicleRoute> routes = new ArrayList<>(numOfRoutes);
        ArrayList<Integer> customerIds = new ArrayList<>(vrpInstance.getCustomerIds());
        for (int i = 0; i < numOfRoutes; i++) {
            routes.add(vrpSolution.createNewRoute());
        }

        Collections.shuffle(customerIds);
        Collections.shuffle(routes);
        while (customerIds.size() > 0) {
            int customerIdIndex = ThreadLocalRandom.current().nextInt(customerIds.size());
            int routeIndex = ThreadLocalRandom.current().nextInt(routes.size());
            VehicleRoute route = routes.get(routeIndex);
            route.addCustomer(customerIds.get(customerIdIndex));
            customerIds.remove(customerIdIndex);
        }


    }

    private void buildDeliverRoute(VRPInstance vrpInstance, VehicleRoute route, HashMap<Integer, Node> customerNodes) {
        if(customerNodes.size() == 0) {
            return;
        }
        ArrayList<Node> customerNodeList = new ArrayList<>();
        ArrayList<Node> leftSize = new ArrayList<>();
        ArrayList<Node> rightSize = new ArrayList<>();
        route.getCustomerKeys().forEach(id -> customerNodeList.add(customerNodes.get(id)));

        Node farthestCustomerNode = customerNodeList.stream().max(Comparator.comparing(Node::getDistance)).orElse(null);
        if(farthestCustomerNode == null) {
            return;
        }
        Customer farthestCustomer = vrpInstance.getCustomer(farthestCustomerNode.getId());
        Coordinate2D p0 = (Coordinate2D) vrpInstance.getDepot().getCoordinate();
        Coordinate2D p1 = (Coordinate2D) vrpInstance.getCustomer(farthestCustomerNode.getId()).getCoordinate();
        customerNodeList.forEach(node -> {
            Coordinate2D p = (Coordinate2D) vrpInstance.getCustomer(node.getId()).getCoordinate();
            if(sideOfLine(p0, p1, p) < 0) {
                leftSize.add(node);
            } else {
                rightSize.add(node);
            }
        });
        LinkedList<Integer> path = new LinkedList<>();
        leftSize.sort(Comparator.comparingDouble(Node::getDistance));
        leftSize.forEach(node -> path.add(node.getId()));
        rightSize.forEach(node -> node.setDistance(vrpInstance.getDistance(farthestCustomer, vrpInstance.getCustomer(node.getId()))));
        rightSize.sort(Comparator.comparingDouble(Node::getDistance));
        rightSize.forEach(node -> path.add(node.getId()));
        route.setRoute(path);
    }

    private int sideOfLine(Coordinate2D p0, Coordinate2D p1, Coordinate2D p) {
        return (int) ((p1.getX() - p0.getX())*(p.getY() - p0.getY()) - (p.getX() - p0.getY())*(p1.getY() - p0.getY()));
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
