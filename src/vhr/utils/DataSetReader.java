package vhr.utils;

import vhr.core.IDataExtract;
import vhr.core.VRPInstance;
import vhr.core.Coordinate2D;
import vhr.core.Customer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by quachv on 3/15/2017.
 */
public class DataSetReader implements IDataExtract {
    public DataSetReader() {

    }

    public VRPInstance extractDataFrom(String fileName) {
        VRPInstance result = new VRPInstance();
        boolean isInCoordSection, isInDemandSection, isInDepotSection, isInSpecificationSection;
        try {
            File file = new File(fileName);
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            isInCoordSection = false;
            isInDemandSection = false;
            isInDepotSection = false;
            isInSpecificationSection = true;
            while ((line = bufferedReader.readLine()) != null) {
                if(line.trim().equals("NODE_COORD_SECTION")) {
                    isInCoordSection = true;
                    isInDemandSection = false;
                    isInDepotSection = false;
                    isInSpecificationSection = false;
                    continue;
                } else if(line.trim().equals("DEPOT_SECTION")) {
                    isInCoordSection = false;
                    isInDemandSection = false;
                    isInDepotSection = true;
                    isInSpecificationSection = false;
                    continue;
                } else if(line.trim().equals("DEMAND_SECTION")) {
                    isInCoordSection = false;
                    isInDemandSection = true;
                    isInDepotSection = false;
                    isInSpecificationSection = false;
                    continue;
                } else if(line.trim().equals("EOF")) {
                    break;
                }
                if(isInSpecificationSection) {
                    String[] parts = line.split(":");
                    if(parts.length > 1) {
                        String entryName = parts[0].trim().toLowerCase();
                        String value;
                        if(parts.length > 2) {
                            value = String.join(": ", Arrays.copyOfRange(parts, 1, parts.length));
                        } else {
                            value = parts[1].trim();
                        }
                        if(entryName.equals("name")) {
                            result.setInstanceName(value);
                        }
                        else if(entryName.equals("comment")) {
                            result.setComment(value);
                        } else if(entryName.equals("dimension")) {
                            result.setNumberOfCustomers(Integer.parseInt(value));
                        } else if(entryName.equals("capacity")) {
                            result.setCapacity(Integer.parseInt(value));
                        }
                    }
                } else if(isInCoordSection) {
                    String[] parts = line.trim().split(" ");
                    int id = Integer.parseInt(parts[0]);
                    Coordinate2D coord = new Coordinate2D(Double.parseDouble(parts[1]),
                            Double.parseDouble(parts[2]));
                    Customer customer = result.getCustomer(id);
                    if(customer == null) {
                        customer = new Customer(Integer.parseInt(parts[0]), coord);
                        result.addCustomer(customer);
                    } else {
                        customer.setCoordinate(coord);
                    }
                } else if(isInDemandSection) {
                    String[] parts = line.trim().split(" ");
                    int id = Integer.parseInt(parts[0]);
                    Customer customer = result.getCustomer(id);
                    if(customer == null) {
                        customer = new Customer(id);
                    }
                    customer.setDemand(Double.parseDouble(parts[1]));
                    result.addCustomer(customer);
                } else if(isInDepotSection) {
                    int depotId = Integer.parseInt(line.trim());
                    if(depotId < 0) {
                        continue;
                    }
                    Customer depot = result.getCustomer(depotId);
                    if(depot == null) {
                        depot = new Customer(depotId);
                        result.addCustomer(depot);
                    }
                    result.setDepot(depot);
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}
