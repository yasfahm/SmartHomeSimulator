package service;

import org.apache.commons.io.FileUtils;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class ConsoleService {

    private static PrintWriter printWriter = null;

    static {
        try {
            printWriter = new PrintWriter(FileUtils.getFile("src", "main", "resources", "consoleLogs.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Function responsible for updating the log file
     *
     * @param consoleLog The string to be appended onto the log file
     */
    public static void exportConsole(final String consoleLog) {
        printWriter.append(consoleLog);
        printWriter.flush();
    }
}
