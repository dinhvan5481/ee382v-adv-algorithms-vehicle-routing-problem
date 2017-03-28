package vhr.core;

/**
 * Created by dinhvan5481 on 3/15/17.
 */
public class AbstractCoordinate implements ICoordinate {
    
    protected double xCord;
    protected double yCord;
    protected double zCord;
    
    protected AbstractCoordinate(double xCord, double yCord, double zCord) {
        this.xCord = xCord;
        this.yCord = yCord;
        this.zCord = zCord;
    }

    public AbstractCoordinate substract(AbstractCoordinate op) {
        return new AbstractCoordinate(xCord - op.getX(), yCord - op.getY(), zCord - op.getZ());
    }

    @Override
    public double getX() {
        return this.xCord;
    }

    @Override
    public double getY() {
        return yCord;
    }

    @Override
    public double getZ() {
        return zCord;
    }
}
