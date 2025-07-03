package com.pttdigital.CosmosDB;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalLogger {
   private static Object _locker = new Object();
   private static String _log_folder = "FTP-log";

   public static void write(String level, String text) {
      /*synchronized(_locker) {
         try {
            if (!(new File(_log_folder)).exists()) {
               (new File(_log_folder)).mkdir();
            }

            LocalDateTime now = LocalDateTime.now();
            String timeFormatted = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH"));
            String timestamp = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String fileName = _log_folder + "//" + timeFormatted + "000000.txt";
            FileWriter fileWriter = null;
            BufferedWriter writer = null;

            try {
               fileWriter = new FileWriter(fileName, true);
               writer = new BufferedWriter(fileWriter);
               writer.write(timestamp + "\t" + level.toUpperCase() + "\t" + text);
               writer.newLine();
            } catch (Exception var13) {
               var13.printStackTrace();
            }

            if (writer != null) {
               try {
                  writer.close();
               } catch (Exception var12) {
               }
            }

            if (fileWriter != null) {
               try {
                  fileWriter.close();
               } catch (Exception var11) {
               }
            }
         } catch (Exception var14) {
            var14.printStackTrace();
         }

      }*/
   }
}
