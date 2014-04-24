
package io.botcoin.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 *
 * @author Nicolò Paternoster <paternoster.nicolo at gmail.com>
 * adapted from vogella tutorial
 */
public class LogFormatterCSV extends Formatter{
  // This method is called for every log records
  public String format(LogRecord rec) {
    StringBuffer buf = new StringBuffer(1000);
    buf.append(rec.getLevel()+","+(formatMessage(rec)).replaceAll(",", " ")+","+ calcDate(rec.getMillis())+"\n");
    return buf.toString();
  }

  private String calcDate(long millisecs) {
    SimpleDateFormat date_format = new SimpleDateFormat("MMM_dd_HH_mm_ss");
    Date resultdate = new Date(millisecs);
    return date_format.format(resultdate);
  }

  // This method is called just after the handler using this
  // formatter is created
  public String getHead(Handler h) {
    return "level,message,time\n";
  }

} 