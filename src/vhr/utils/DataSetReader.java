package vhr.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by quachv on 3/15/2017.
 */
public class DataSetReader {

    public static CVRPInstance extractData(String fileName) {
        CVRPInstance result = null;
        try {
            File file = new File(fileName);
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}
