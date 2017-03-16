package vhr.utils;

import vhr.core.Customer;

import java.util.HashMap;

/**
 * Created by quachv on 3/15/2017.
 */
public class CVRPInstance {
    private String instanceName;
    private String comment;
    private int numberOfNodes;
    private int capacity;
    private HashMap<Integer, Customer> customers;

    public CVRPInstance(String name) {
        this.instanceName = name;
        this.customers = new HashMap<>();
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public int getNumberOfNodes() {
        return numberOfNodes;
    }

    public void setNumberOfNodes(int numberOfNodes) {
        this.numberOfNodes = numberOfNodes;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void addCustomer(Customer customer) {
        if(this.customers.containsKey(customer.getId())) {
            return;
        }
        this.customers.put(customer.getId(), customer);
    }
}
