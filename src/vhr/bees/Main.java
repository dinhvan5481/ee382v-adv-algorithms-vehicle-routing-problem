package vhr.bees;

import vhr.utils.CVRPInstance;
import vhr.utils.DataSetReader;

public class Main {

    public static void main(String[] args) {
	// write your code here
        CVRPInstance cvhrInstance = DataSetReader.extractData(".\\data\\A-VRP\\A-n32-k5.vrp");
        System.out.print(cvhrInstance.toString());
    }
}
