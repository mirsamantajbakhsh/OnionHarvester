package OnionHarvester.Helper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by Mir Saman on 14-Apr-18.
 */
public class Helper {

    public static synchronized void unZipIt(String zipFile, File outputFolder) {
        ZipUtils.extract(new File(zipFile), outputFolder);
    }

    public static synchronized boolean writeContent(File f, String content) {
        try {
            FileWriter fw = new FileWriter(f);
            fw.write(content);
            fw.flush();
            fw.close();
            return true;
        } catch (IOException e) {
            //TODO Implement Logger
            return false;
        }
    }

    public static String getCurrentTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }
}
