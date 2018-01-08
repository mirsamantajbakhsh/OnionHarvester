package OnionHarvester;

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

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Mir Saman Tajbakhsh
 */
public class OH {

    private static Vector<String> onions = new Vector<>(128);
    private static int counter = 0;
    private static String startOnion = "aaaaaaaaaaaaaaaa";
    private static String endOnion = "aaaaaaaaaaaaaaaa";
    private static String ID = "FooBar";
    private static Vector<OnionAddress> foundAddresses = new Vector<>();
    private static JSONObject jobj;
    private static String URLGenerate = "http://onionharvester.com/dispatcher/generate";
    private static String URLResponse = "http://onionharvester.com/dispatcher/response";

    private static int Port = 9150;
    private static int ThreadCount = 10;
    private static int TimeOut = 5000;
    private static String IP = "127.0.0.1";
    static Set<Integer> ports = new HashSet<>();

    public static void main(String[] args) {
        for (int i = 0; i < args.length; i++) {
            switch (args[i].toLowerCase()) {
                case "--ip":
                    IP = args[++i];
                    break;
                case "--port":
                    Port = Integer.parseInt(args[++i]);
                    break;
                case "--thread":
                    ThreadCount = Integer.parseInt(args[++i]);
                    break;
                case "--time-out":
                    TimeOut = Integer.parseInt(args[++i]);
                    break;
            }
        }
        new OH();
    }

    public OH() {
        //Initialization
        getPorts().add(80);
        getPorts().add(443);
        Object[] ans = getNewOnions();
        if (!(boolean) ans[0]) {
            System.out.println("Cannot get the onion addresses. Please check your Internet connection.");
            System.exit(-1);
        }

        startOnion = String.valueOf(ans[1]);
        endOnion = String.valueOf(ans[2]);
        ID = String.valueOf(ans[3]);

        createOnions(startOnion);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("The result is going to write in a file named found.txt");
            try {
                sendResponse(false);
                FileWriter fw = new FileWriter("found.txt", false);
                for (OnionAddress oa : foundAddresses) {
                    fw.write(oa.toString() + "\r\n");
                }
                fw.flush();
                fw.close();

            } catch (IOException ex) {
                Logger.getLogger(OH.class.getName()).log(Level.SEVERE, null, ex);
            }
        }));

        for (int i = 0; i < ThreadCount; i++) {
            Runnable r = new Runnable() {
                @Override
                public void run() {
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
                        return false;
                    }
                }
            };
            new Thread(r).start();
        }
    }

    private static synchronized String getNextOnion() {
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

    private static boolean sendResponse(boolean complete) {
        try {
            HttpClient httpclient = HttpClients.createDefault();
            HttpPost httppost = new HttpPost(URLResponse);

            // Request parameters and other properties.
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("id", ID));
            final String[] data = {""};
            foundAddresses.forEach(onionAddress -> data[0] += onionAddress.toString());
            if (!data[0].equalsIgnoreCase("")) {
                params.add(new BasicNameValuePair("addresses", data[0]));
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

    private static synchronized void addAlive(String address, Integer port) {
        foundAddresses.add(new OnionAddress(address, port));
    }

    private static void createOnions(String start) {
        String onion = start;
        onions.clear();

        onions.add(onion);

        while (!(onion = calculateNextOnion(onion)).equalsIgnoreCase(endOnion)) {
            onions.add(onion);
        }
    }

    private static String calculateNextOnion(String onion) {
        return CharMapper.getNextToken(onion.toLowerCase());
    }

    public static synchronized Object[] getNewOnions() {
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

    private synchronized static Set<Integer> getPorts() {
        return ports;
    }
}