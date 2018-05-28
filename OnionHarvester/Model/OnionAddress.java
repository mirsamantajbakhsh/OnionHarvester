package OnionHarvester.Model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Mir Saman Tajbakhsh on 01-Jan-18.
 */
public class OnionAddress {
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private String onionAddress;
    private int port;
    private Date date;

    public OnionAddress(String onionAddress, int Port) {
        this.onionAddress = onionAddress;
        this.port = Port;
        this.date = new Date();
    }

    public String getOnionAddress() {
        return onionAddress;
    }

    public int getPort() {
        return port;
    }

    public Date getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "[\"" + getOnionAddress().substring(0, 16) + "\"," + getPort() + ",\"" + dateFormat.format(getDate()) + "\"]";
    }
}
