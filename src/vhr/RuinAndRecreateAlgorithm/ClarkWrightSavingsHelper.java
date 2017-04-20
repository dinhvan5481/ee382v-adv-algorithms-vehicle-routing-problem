package vhr.RuinAndRecreateAlgorithm;

import vhr.core.Customer;
import vhr.core.VRPInstance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by My Luc on 4/19/2017.
 */
public class ClarkWrightSavingsHelper {

    public static List<Integer> CustomerIdsOrdered=new ArrayList<>();

    private static double[][] _savingMatrix=null;
    public static double[][] GetSavingsMatrix(VRPInstance vrpInstance) {
        if(_savingMatrix == null)
            _savingMatrix = SavingMatrix(vrpInstance);
        return _savingMatrix;
    }

    private static double[][] SavingMatrix(VRPInstance vrpInstance) {
        Collection<Customer> customers = vrpInstance.getCustomers();
        int totalCustomers = customers.size();
        double[][] savingsMatrix = new double[totalCustomers][totalCustomers];

        List<Customer> customerList = new ArrayList<>(customers);
        customerList.sort(new CustomerIdComparator<Customer>());
        for(int c=0; c<totalCustomers; c++){
            Customer customerC = customerList.get(c);
            CustomerIdsOrdered.add(customerC.getId());
            for(int r=c+1; r<totalCustomers; r++){
                Customer customerR = customerList.get(r);
                double saving = GetSavings(customerC, customerR, vrpInstance.getDepot());
                savingsMatrix[r][c]=saving;
                savingsMatrix[c][r]=saving;
            }
        }

        return savingsMatrix;
    }

    public static double GetSavings(Customer customerC, Customer customerR, Customer depot) {
        double cDepot = distance(customerC.getCoordinate().getX(), customerC.getCoordinate().getY(), depot.getCoordinate().getX(), depot.getCoordinate().getY());
        double rDepot = distance(customerR.getCoordinate().getX(), customerR.getCoordinate().getY(), depot.getCoordinate().getX(), depot.getCoordinate().getY());
        double cr = distance(customerC.getCoordinate().getX(), customerC.getCoordinate().getY(), customerR.getCoordinate().getX(), customerR.getCoordinate().getY());

        return cDepot + rDepot - cr;
    }

    private static double distance(double x1, double y1, double x2,double y2) {
        double side1=Math.pow((x1-x2), 2);
        double side2=Math.pow((y1-y2), 2);
        return Math.sqrt(side1+side2);
    }
    private static class CustomerIdComparator<T> implements java.util.Comparator<Customer> {
        @Override
        public int compare(Customer c1, Customer c2) {
            Integer id1 = c1.getId();
            Integer id2 = c2.getId();
            return id1.compareTo(id2);
        }
    }
}
