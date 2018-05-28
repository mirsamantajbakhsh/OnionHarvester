package OnionHarvester;

import java.io.File;
import java.util.Vector;

/**
 * Created by Mir Saman on 22-Feb-18.
 */
public class Variables {
    public static String outputFolder = "D:\\tmp\\OH" + File.separator;
    public static int ThreadCount = 10;
    public static int TimeOut = 5000;
    public static String IP = "127.0.0.1";
    public static String TORZip = "tor\\tor-win32-0.3.2.10.zip";
    //public static String TORRC = "QnJpZGdlIG9iZnM0IDcyLjEwLjEyOS4yNTI6NDA2ODcgQkY0MTQ1NkE1QzA2MURCNjNFNDY1NEY0ODU4MTM5MzExODYwQ0I4MSBjZXJ0PXNMMHZKaHNWS3VITzBrN3hoT3BnL1VVSFp1ZnRiQVFQbTBxS1VyYzNzR1d5TFdmU05lS0kwUnpoTCthOVRwTE1hSW9VUmcgaWF0LW1vZGU9MA0KQnJpZGdlIG9iZnM0IDk0LjI0Mi4yNDkuMjozODQ3OSAwMzlDMDgwMzIxMzM1NURDQzk5NjE4NzZCNTY1MEIwQkU1NjkxOTE1IGNlcnQ9OCtRb2R2T2dSNHVmQ3ovODJ4akVFL3dRSVYwcUJQZ0tJWElFUUZvaFMwSitCTkErbThsK2NaeWgyVHhaaERnWk9IVGlBdyBpYXQtbW9kZT0wDQpCcmlkZ2Ugb2JmczQgODIuMjExLjMxLjEyMDo0NDMgMkVCNDgwNjUzNkEyNEY0RUMyODMzNkFDNDczQTc1QjJFMDgxRjQ5QSBjZXJ0PXhtRGR3NHdGSTA3c09vcjV2bGRER054bUQ1NkdHTG15WE5KUWIxZ1RyTDdYUEUxQy9kT25hT1NucUNEVFg0NXB2azBrTXcgaWF0LW1vZGU9MA0KVXNlQnJpZGdlcyAx";
    public static String TORRC = "IyBObyBDb25maWc=";

    public static Vector<Process> tors = new Vector<>();
}
