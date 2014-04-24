
package io.botcoin.utils;

/**
 *
 * @author Vogella
 */
import io.botcoin.global.Settings;
import java.io.IOException;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class MyLogger {
  static private FileHandler fileCsv;
  static private Formatter formatterCsv;

  static private FileHandler fileHTML;
  static private Formatter formatterHTML;

  static public void setup() throws IOException {

    // Get the global logger to configure it
    Logger logger = Logger.getLogger(Utils.class.getName());

    logger.setLevel(Level.INFO);
    String filename = new Date().getTime()+"_log";
    fileCsv = new FileHandler(Settings.LOG_PATH+filename+".csv");
    fileHTML = new FileHandler(Settings.LOG_PATH+filename+".html");

    // Create csv Formatter
    
    formatterCsv = new LogFormatterCSV();
    fileCsv.setFormatter(formatterCsv);
    logger.addHandler(fileCsv);
    
    // Create HTML Formatter
    formatterHTML = new LogFormatterHTML();
    fileHTML.setFormatter(formatterHTML);
    logger.addHandler(fileHTML);
  }
} 