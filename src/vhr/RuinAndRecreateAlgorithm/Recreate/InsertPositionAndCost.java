package vhr.RuinAndRecreateAlgorithm.Recreate;

/**
 * Created by My Luc on 4/19/2017.
 */
public class InsertPositionAndCost implements Comparable<InsertPositionAndCost>  {
    private int routeId;
    private int position;
    private double cost;

    public InsertPositionAndCost(int routeId) {
        this.routeId = routeId;
    }

    public int getPosition() {
        return position;
    }

    public double getCost() {
        return cost;
    }

    public int getRouteId() {
        return routeId;
    }

    @Override
    public int compareTo(InsertPositionAndCost o) {
        if(o == null) {
            return -1;
        }
        return Double.compare(this.getCost(), o.getCost());
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }
}
