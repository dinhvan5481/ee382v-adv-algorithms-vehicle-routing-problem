package vhr.core;

import java.util.LinkedList;

/**
 * Created by quachv on 3/22/2017.
 */
public interface ICostCalculator {
    double calculate(DeliveryPath path);
    double calculate(Customer from, Customer to);
    double calculateRouteCost(LinkedList<Integer> routeWithoutDepot, VRPInstance vrpInstance);
}
