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
    }

    public double cost(ICostCalculator calulator) {
        return calulator.calculate(this);
    }

    public Customer getFrom() {
        return from;
    }

    public Customer getTo() {
        return to;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null || !(obj instanceof DeliveryPath)) {
            return false;
        }
        return (getFrom() == ((DeliveryPath) obj).getFrom() && getTo() == ((DeliveryPath) obj).getTo())
                || (getFrom() == ((DeliveryPath) obj).getTo() && getTo() == ((DeliveryPath) obj).getFrom());
    }

    @Override
    public int hashCode() {
        return getFrom().hashCode() + getTo().hashCode();
    }
}
