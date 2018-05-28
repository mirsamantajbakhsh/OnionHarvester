package OnionHarvester.Presenter.Service.OnionGrabber;

import OnionHarvester.Helper.Helper;
import OnionHarvester.Model.OnionAddress;
import OnionHarvester.Presenter.Service.Service;
import OnionHarvester.Presenter.Service.logger.LoggerService;
import OnionHarvester.Variables;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.util.*;

import static OnionHarvester.Variables.*;

/**
 * Created by Mir Saman on 22-Feb-18.
 */
public class OnionGrabber extends Service {

    int counter = 0;
    String startOnion = "aaaaaaaaaaaaaaaa";
    String endOnion = "aaaaaaaaaaaaaaaa";
    Vector<String> onions = new Vector<>();
    String ID = "FooBar";
    Vector<OnionAddress> foundAddresses = new Vector<>();
    JSONObject jobj;
    String URLGenerate = "http://onionharvester.com/dispatcher/generate";
    String URLResponse = "http://onionharvester.com/dispatcher/response";
    Set<Integer> ports = new HashSet<>();
    CharMapper cm = new CharMapper();
    int Port = -1; //Calculate random port in init.

    @Override
    public void execute() {
        super.execute();

        for (int i = 0; i < Variables.ThreadCount; i++) {
            Runnable r = () -> {
                String onion = getNextOnion();
                while (onion != null) {
                    String tmp = onion + ".onion";
                    for (int p : getPorts()) {
                        if (checkAddress(tmp, p)) {
                            System.out.println("Address found: " + tmp + ":" + p);
                            addAlive(tmp, p);
                        }
                    }
                    onion = getNextOnion();
                }
            };

            (new Thread(r)).start();
        }
    }

    @Override
    public void init() {
        Port = new Random().nextInt(5000) + 20000;
        startIndividualTor();
    }

    private boolean startIndividualTor() {
        try {
            //Unzip TOR
            File f = java.nio.file.Files.createTempDirectory("OnionHarvester").toFile();
            Helper.unZipIt(TORZip, f);

            //Create torrc
            File torrc = new File(f.getAbsolutePath() + File.separator + "Data" + File.separator + "Tor" + File.separator + "torrc");
            String content = new String(Base64.getDecoder().decode(TORRC), "UTF-8");
            content += "\r\n" + "SocksPort " + String.valueOf(Port) + "\r\n";
            content += "DataDirectory " + f.getAbsolutePath() + File.separator + "Data" + File.separator + "Tor" + "\r\n";
            content += "GeoIPFile " + f.getAbsolutePath() + File.separator + "Data" + File.separator + "Tor" + File.separator + "geoip" + "\r\n";
            content += "GeoIPv6File " + f.getAbsolutePath() + File.separator + "Data" + File.separator + "Tor" + File.separator + "geoip6" + "\r\n";
            Helper.writeContent(torrc, content);

            //Start TOR
            return startTOR(f, torrc);

        } catch (IOException ex) {
            LoggerService.getInstance().Log(OnionGrabber.class, "Cannot start tor binary. " + ex.getMessage().replaceAll("\r\n", " "), LoggerService.LogLevel.Error);
            return false;
        }
    }

    private boolean startTOR(File TorFolder, File torrc) {
        Process tmp;
        final boolean[] ErrorFound = new boolean[]{false};

        try {
            tmp = Runtime.getRuntime().exec(new String[]{TorFolder.getAbsolutePath() + File.separator + "Tor" + File.separator + "tor.exe", "--defaults-torrc", torrc.getAbsoluteFile().getAbsolutePath()});
        } catch (Exception ex) {
            tmp = null;
        }
        final Process p = tmp;

        //Runnable r = () -> {
        boolean torStarted = false;

        if (p != null) {
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            try {
                String line = br.readLine();

                while (!torStarted) {
                    if (line != null && line.contains("Bootstrapped 100%: Done")) {
                        System.out.println(Thread.currentThread().getName() + " - TOR STARTED");
                        torStarted = true;
                        continue;
                    }
                    line = br.readLine();
                }

                ErrorFound[0] = !isConnected();

            } catch (IOException e) {
                LoggerService.getInstance().Log(OnionGrabber.class, "Cannot get TOR output to check its start.", LoggerService.LogLevel.Error);
                ErrorFound[0] = true;
            }
        }
        //};
        //Thread torThread = new Thread(r, "TorThread");
        //torThread.start();
        //try {
        //torThread.join();
        return !ErrorFound[0];

        //} catch (InterruptedException e) {
        //    LoggerService.getInstance().Log(OnionGrabber.class, "Cannot join the TOR output thread and wait for the response.", LoggerService.LogLevel.Error);
        //    return false;
        //}
    }

    private boolean isConnected() {
        InetSocketAddress HiddenerProxyAddress = new InetSocketAddress(Variables.IP, Port);
        Proxy HiddenProxy = new Proxy(Proxy.Type.SOCKS, HiddenerProxyAddress);
        Socket underlying = new Socket(HiddenProxy);
        InetSocketAddress sa = InetSocketAddress.createUnresolved("facebookcorewwwi.onion", 80);
        try {
            underlying.connect(sa, 120000); //Wait 2 minute
            return true;
        } catch (IOException e) {
            LoggerService.getInstance().Log(OnionGrabber.class, "Cannot connect to FACEBOOK. Please check the TOR.", LoggerService.LogLevel.Warning);
            return false;
        }
    }

    @Override
    public void setParam(String paramName, Object paramValue) {
        super.setParam(paramName, paramValue);

        try {
            getClass().getField(paramName).setInt(paramValue, 9150);
        } catch (IllegalAccessException e) {
        } catch (NoSuchFieldException e) {
        }
    }

    private boolean checkAddress(String onion, int p) {
        InetSocketAddress HiddenerProxyAddress = new InetSocketAddress(IP, Port);
        Proxy HiddenProxy = new Proxy(Proxy.Type.SOCKS, HiddenerProxyAddress);
        Socket underlying = new Socket(HiddenProxy);
        InetSocketAddress unresolvedAdr = InetSocketAddress.createUnresolved(onion, p);

        try {
            underlying.connect(unresolvedAdr, TimeOut);
            return true;
        } catch (Exception ex) {
            //TODO Check if TOR works or not.
            return false;
        }
    }

    private synchronized String getNextOnion() {
        try {
            return onions.elementAt(counter++);
        } catch (Exception ex) { //The list is cleared. Inform the server
            sendResponse(true);
            counter = 0;
            Object[] ans = getNewOnions();
            if ((boolean) ans[0]) {
                //I should change the value of startOnion here, because of getNextOnion() is synchronized and the value of startOnion cannot be changed in getNewOnions();
                startOnion = String.valueOf(ans[1]);
                endOnion = String.valueOf(ans[2]);
                ID = String.valueOf(ans[3]);
                createOnions(startOnion);
                return onions.elementAt(counter++);
            } else {
                return null;
            }
        }
    }

    private boolean sendResponse(boolean complete) {
        try {
            HttpClient httpclient = HttpClients.createDefault();
            HttpPost httppost = new HttpPost(URLResponse);

            // Request parameters and other properties.
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("id", ID));
            final String[] data = {""};
            foundAddresses.forEach(onionAddress -> data[0] += onionAddress.toString() + ",");
            if (!data[0].equalsIgnoreCase("")) {
                data[0] = data[0].substring(0, data[0].length() - 1);
                params.add(new BasicNameValuePair("addresses", "[" + data[0] + "]"));
            }
            params.add(new BasicNameValuePair("complete", String.valueOf(complete).toLowerCase()));
            httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

            //Execute and get the response.
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();

            if (entity != null && response.getStatusLine().getStatusCode() == 200) {
                //Check Answer. It should be "Thanks for contribution."
                //BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                //StringBuffer result = new StringBuffer();
                //String line = "";
                //while ((line = rd.readLine()) != null) {
                //    result.append(line);
                //}

                //String responseStr = result.toString();
                //System.out.println(responseStr);

                foundAddresses.clear();
                return true;
            }
            return false;
        } catch (Exception ex) {
            return false;
        }
    }

    private void addAlive(String address, Integer port) {
        foundAddresses.add(new OnionAddress(address, port));
    }

    private void createOnions(String start) {
        String onion = start;
        onions.clear();

        onions.add(onion);

        while (!(onion = calculateNextOnion(onion)).equalsIgnoreCase(endOnion)) {
            onions.add(onion);
        }
    }

    private String calculateNextOnion(String onion) {
        return cm.getNextToken(onion.toLowerCase());
    }

    public Object[] getNewOnions() {
        Vector<Object> out = new Vector<>();

        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(URLGenerate);

        // add request header
        request.addHeader("User-Agent", "OnionHarvester - Java Client");
        try {
            HttpResponse response = client.execute(request);

            if (response.getStatusLine().getStatusCode() != 200) {
                out.add(false);
                return out.toArray();
            }

            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }

            String temp = result.toString();
            jobj = new JSONObject(temp);
            jobj.getJSONArray("ports").iterator().forEachRemaining(o -> {
                getPorts().add(Integer.valueOf((String) o));
            });
            out.add(true);
            out.add(jobj.getString("start"));
            out.add(jobj.getString("end"));
            out.add(jobj.getString("id"));
        } catch (Exception ex) {
            out.add(false);
        } finally {
            return out.toArray();
        }
    }

    private Set<Integer> getPorts() {
        return ports;
    }
}
