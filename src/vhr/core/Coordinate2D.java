package vhr.core;

/**
 * Created by dinhvan5481 on 3/15/17.
 */
public class Coordinate2D extends AbstractCoordinate {
    public Coordinate2D(double xCord, double yCord) {
        super(xCord, yCord, 0);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null || !(obj instanceof Coordinate2D)) {
            return false;
        }
        return getX() == ((Coordinate2D) obj).getX() && getY() == ((Coordinate2D) obj).getY();
    }

    @Override
    public int hashCode() {
        long bits = java.lang.Double.doubleToLongBits(getX());
        bits ^= java.lang.Double.doubleToLongBits(getY()) * 31;
        return (((int) bits) ^ ((int) (bits >> 32)));
    }
}
