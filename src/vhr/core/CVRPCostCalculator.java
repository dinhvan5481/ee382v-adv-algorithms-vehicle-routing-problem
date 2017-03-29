package vhr.core;

/**
 * Created by dinhvan5481 on 3/29/17.
 */
public class CVRPCostCalculator implements ICostCalculator {

    protected IDistanceCalulator distanceCalulator;
    public CVRPCostCalculator(IDistanceCalulator distanceCalulator) {
        this.distanceCalulator = distanceCalulator;

    }
    @Override
    public double calculate(DeliveryPath path) {
        return calculate(path.getFrom(), path.getTo());
    }

    @Override
    public double calculate(Customer from, Customer to) {
        return distanceCalulator.calculate(from.getCoordinate(), to.getCoordinate());
    }
}
