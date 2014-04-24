
package io.botcoin.utils;

/**
 *
 * @author Vogella http://www.vogella.com/articles/Logging/article.html#overview_logger
 */
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

//This custom formatter formats parts of a log record to a single line
class LogFormatterHTML extends Formatter {
  // This method is called for every log records
  public String format(LogRecord rec) {
    StringBuffer buf = new StringBuffer(1000);
    buf.append("<tr>");
    buf.append("<td>");
    if (rec.getLevel().intValue() == Level.WARNING.intValue()) { //Format it for transactions
      buf.append("<p style='color:blue'>");
      buf.append(formatMessage(rec));
      buf.append("</p>");
    }
    else if (rec.getLevel().intValue() == Level.SEVERE.intValue()) { //Format it for errors
      buf.append("<p style='color:red'>");
      buf.append(formatMessage(rec));
      buf.append("</p>");
    }
    else {
      buf.append(formatMessage(rec));
    }

    buf.append("</td>");
    buf.append("<td>");
    buf.append(calcDate(rec.getMillis()));
    buf.append("</td>");
  
    buf.append("</tr>\n");
    return buf.toString();
  }

  private String calcDate(long millisecs) {
    SimpleDateFormat date_format = new SimpleDateFormat("MMM dd,yyyy HH:mm");
    Date resultdate = new Date(millisecs);
    return date_format.format(resultdate);
  }

  // This method is called just after the handler using this
  // formatter is created
  public String getHead(Handler h) {
    return "<HTML>\n<HEAD>\n" + (new Date()) 
        + "\n</HEAD>\n<BODY>\n<PRE>\n"
        + "<table width=\"100%\" border>\n  "
        + "<tr><th>Log Message</th>" +
        "<th>Time</th>" +
        "</tr>\n";
  }

  // This method is called just after the handler using this
  // formatter is closed
  public String getTail(Handler h) {
    return "</table>\n  </PRE></BODY>\n</HTML>\n";
  }
} 