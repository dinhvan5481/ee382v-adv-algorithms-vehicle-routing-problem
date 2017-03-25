package vhr.utils;

/**
 * Created by quachv on 3/22/2017.
 */
public class StringUtil {
    public static void appendStringLine(StringBuilder sb, String line) {
        sb.append(line + System.getProperty("line.separator"));
    }
}
