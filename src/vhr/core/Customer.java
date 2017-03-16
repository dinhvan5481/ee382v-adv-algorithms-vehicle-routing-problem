package vhr.core;

/**
 * Created by dinhvan5481 on 3/15/17.
 */
public class Customer {
    private int id;
    private ICoordinate coordinate;
    private double serviceTime;
    private double demand;

    public Customer(int id, ICoordinate coordinate) {
        this.id = id;
        this.coordinate = coordinate;
    }


    public ICoordinate getCoordinate() {
        return coordinate;
    }

    public double getServiceTime() {
        return serviceTime;
    }

    public double getDemand() {
        return demand;
    }

    public int getId() {
        return id;
    }

    public void setServiceTime(double serviceTime) {
        this.serviceTime = serviceTime;
    }

    public void setDemand(double demand) {
        this.demand = demand;
    }
}
