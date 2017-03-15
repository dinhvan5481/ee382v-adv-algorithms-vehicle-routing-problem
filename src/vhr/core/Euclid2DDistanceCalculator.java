package vhr.core;

/**
 * Created by dinhvan5481 on 3/15/17.
 */
public class Euclid2DDistanceCalculator implements IDistanceCalulator{

    @Override
    public double distance(ICoordinate from, ICoordinate to) {
        double result;
        try {
            result = Math.sqrt(Math.pow(from.getX() - to.getX(), 2) + Math.pow(from.getY() - to.getY(), 2));
            return result;
        }
        catch (Exception e) {
            throw e;
        }
    }
}
