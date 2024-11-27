package src.system;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;
/**
 * カスタムログのフォーマッター
 */
public class CustomLogFormatter extends SimpleFormatter {
   private final SimpleDateFormat dateFormat =
         new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
   /**
    * フォーマットを行う.
    */
   public String format(LogRecord logRecord) {
      final StringBuilder builder = new StringBuilder();

      builder.append(this.dateFormat.format(new Date(logRecord.getMillis())));
      builder.append(" ");

      Level level = logRecord.getLevel();
      if (level == Level.FINEST) {
         builder.append("FINEST");
      } else if (level == Level.FINER) {
         builder.append("FINER ");
      } else if (level == Level.FINE) {
         builder.append("FINE ");
      } else if (level == Level.CONFIG) {
         builder.append("CONFIG");
      } else if (level == Level.INFO) {
         builder.append("INFO ");
      } else if (level == Level.WARNING) {
         builder.append("WARN ");
      } else if (level == Level.SEVERE) {
         builder.append("SEVERE");
      } else {
         builder.append(Integer.toString(logRecord.getLevel().intValue()));
         builder.append(" ");
      }
      builder.append(" ");
      builder.append(logRecord.getLoggerName());
      builder.append(" - ");
      builder.append(logRecord.getMessage());
      builder.append("\n");

      return builder.toString();
   }
}
