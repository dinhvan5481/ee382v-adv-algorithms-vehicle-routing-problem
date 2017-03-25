package vhr.utils;

import vhr.core.Customer;

import java.util.HashMap;

import static vhr.utils.StringUtil.appendStringLine;

/**
 * Created by quachv on 3/15/2017.
 */
public class CVRPInstance {
    private String instanceName;
    private String comment;
    private int numberOfNodes;
    private int capacity;
    private HashMap<Integer, Customer> customers;
    private Customer depot;

    public CVRPInstance() {
        this.customers = new HashMap<>();
    }


    public void setInstanceName(String name) {
        this.instanceName = name;
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

    public Customer getCustomer(int id) {
        return this.customers.get(id);
    }

    public Customer getDepot() {
        return depot;
    }

    public void setDepot(Customer depot) {
        this.depot = depot;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        appendStringLine(sb, "Instance name: " + instanceName);
        if(!comment.isEmpty()) {
            appendStringLine(sb, "Comment: " + comment);
        }
        appendStringLine(sb, "Number of customer: " + numberOfNodes);
        appendStringLine(sb, "Vehicle capacity: " + capacity);
        appendStringLine(sb, "Depot: " + depot.toString());
        for (Integer customerId: customers.keySet()) {
            Customer customer = customers.get(customerId);
            appendStringLine(sb, customer.toString());
        }
        return sb.toString();
    }


}
