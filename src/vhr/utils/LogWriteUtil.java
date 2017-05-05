package vhr.utils;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by quachv on 5/2/2017.
 */
public class LogWriteUtil {
    public static void writeToFile(String fileName, String data, boolean append) {
        FileWriter fileWriter;
        try {
            fileWriter = new FileWriter(fileName, append);
            fileWriter.write(data);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
