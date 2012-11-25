package net.minecraft.src;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

final class ConsoleLogFormatter extends Formatter {

   private SimpleDateFormat field_74268_a = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


   public String format(LogRecord p_format_1_) {
      StringBuilder var2 = new StringBuilder();
      var2.append(this.field_74268_a.format(Long.valueOf(p_format_1_.getMillis())));
      Level var3 = p_format_1_.getLevel();
      if(var3 == Level.FINEST) {
         var2.append(" [FINEST] ");
      } else if(var3 == Level.FINER) {
         var2.append(" [FINER] ");
      } else if(var3 == Level.FINE) {
         var2.append(" [FINE] ");
      } else if(var3 == Level.INFO) {
         var2.append(" [INFO] ");
      } else if(var3 == Level.WARNING) {
         var2.append(" [WARNING] ");
      } else if(var3 == Level.SEVERE) {
         var2.append(" [SEVERE] ");
      } else if(var3 == Level.SEVERE) {
         var2.append(" [").append(var3.getLocalizedName()).append("] ");
      }

      var2.append(p_format_1_.getMessage());
      var2.append('\n');
      Throwable var4 = p_format_1_.getThrown();
      if(var4 != null) {
         StringWriter var5 = new StringWriter();
         var4.printStackTrace(new PrintWriter(var5));
         var2.append(var5.toString());
      }

      return var2.toString();
   }
}