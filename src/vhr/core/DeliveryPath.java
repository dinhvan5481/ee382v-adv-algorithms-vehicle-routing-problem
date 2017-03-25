package vhr.core;

/**
 * Created by quachv on 3/22/2017.
 */
public class DeliveryPath {
    private Customer from;
    private Customer to;

    public DeliveryPath(Customer from, Customer to) {
        this.from = from;
        this.to = to;
        from.setPathOut(this);
        to.setPathOut(this);
    }

    public double cost(ICostCalulator calulator) {
        return calulator.calculate(this);
    }

    public Customer getFrom() {
        return from;
    }

    public Customer getTo() {
        return to;
    }
}
