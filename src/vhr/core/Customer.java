package vhr.core;

import vhr.utils.StringUtil;

import java.lang.ref.WeakReference;

/**
 * Created by dinhvan5481 on 3/15/17.
 */
public class Customer {
    private int id;
    private ICoordinate coordinate;
    private double demand;
    private WeakReference<DeliveryPath> pathIn;
    private WeakReference<DeliveryPath> pathOut;

    public Customer(int id) {
        this.id = id;
    }

    public Customer(int id, ICoordinate coordinate) {
        this(id);
        this.coordinate = coordinate;
    }

    public int getId() {
        return id;
    }

    public ICoordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(ICoordinate coordinate) {
        this.coordinate = coordinate;
    }

    public double getDemand() {
        return demand;
    }

    public void setDemand(double demand) {
        this.demand = demand;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        StringUtil.appendStringLine(sb,
                "ID: " + id + " Coordinate: " + coordinate.getX() + " " + coordinate.getY() + " Demand: " + demand);
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null || !(obj instanceof Customer)) {
            return false;
        }
        return coordinate == ((Customer) obj).coordinate;
    }

    @Override
    public int hashCode() {
        return coordinate.hashCode();
    }
}
