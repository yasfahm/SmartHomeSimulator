package service;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class ConsoleService {

    private static PrintWriter printWriter = null;
    private static List<String> consoleList = new ArrayList<String>();

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
    	consoleList.add(consoleLog);
    	System.out.println("APPENED " + consoleLog);
        printWriter.append(consoleLog);
        printWriter.flush();
    }
    
    /**
     * Return the comple console list as string
     * @return console log string list
     */
    public static String getConsole() {
    	return String.join("",consoleList);
    }
    

    /**
     * Initialize process, check potential IO problem
     */
	public static void initialize() {
		String path = "src/main/resources/consoleLogs.txt";
		File file = new File(path);
		if(!file.exists()) {
			System.out.println("File not exist");
			File newLog = new File(path);
			try {
				newLog.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		java.io.PrintWriter output = null;
		try {
			output = new java.io.PrintWriter(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		output.print("");
		output.close();
		
	}
}
