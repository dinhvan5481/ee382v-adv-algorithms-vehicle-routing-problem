package vhr.core;

/**
 * Created by dinhvan5481 on 3/15/17.
 */
public class Customer {
    private ICoordinate coordinate;
    private double serviceTime;
    private double demand;

    public Customer(ICoordinate coordinate, double serviceTime, double demand) {
        this.coordinate = coordinate;
        this.serviceTime = serviceTime;
        this.demand = demand;
    }



}
