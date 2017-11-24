package OnionHarvester;

import java.io.FileWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mir Saman Tajbakhsh
 */
public class OH {

    private static Vector<String> onions = new Vector<>(1000);
    private static int counter = 0;
    private static String startOnion = "aaaaaaaaaaaaaaaa";
    
    private static String IP = "127.0.0.1";
    private static int Port = 9150;
    private static int ThreadCount = 10;
    private static int TimeOut = 5000;
    

    private static FileWriter fw;

    private static synchronized String getNextOnion() {
        try {
            return onions.elementAt(counter++);
        } catch (Exception ex) {
            counter = 0;
            createOnions(calculateNextOnion(onions.lastElement()));
            return onions.elementAt(counter++);
        }
    }

    private static void addAlive(String address) {
        try {
            fw.write(address + "\r\n");
            fw.flush();
        } catch (IOException ex) {
            Logger.getLogger(OH.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void createOnions(String start) {
        String onion = start;
        onions.clear();

        for (int i = 0; i < onions.capacity(); i++) {
            onions.add(i, onion);
            onion = calculateNextOnion(onion);
        }
    }

    private static String calculateNextOnion(String onion) {
        //TODO Check onion to be correct.
        return CharMapper.getNextToken(onion.toLowerCase());
    }

    public static void main(String[] args) {
        
        for (int i = 0; i < args.length; i++) {
            switch (args[i].toLowerCase()) {
                case "--ip":
                    IP = args[++i];
                    break;
                case "--port":
                    Port = Integer.parseInt(args[++i]);
                    break;
                case "--start":
                    startOnion = args[++i];
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
        //Fill list
        createOnions(startOnion);
        try {
            fw = new FileWriter("OnlineOnions.txt", true);
        } catch (IOException ex) {
            Logger.getLogger(OH.class.getName()).log(Level.SEVERE, null, ex);
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                String nextStart = getNextOnion();
                System.out.println("Start Onion for next time:\r\n\t" + nextStart);
                try {
                    FileWriter fw2 = new FileWriter("last.txt", true);
                    fw2.write(nextStart + "\r\n");
                    fw2.flush();
                    fw2.close();
                } catch (IOException ex) {
                    Logger.getLogger(OH.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        for (int i = 0; i < ThreadCount; i++) {
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    String onion = getNextOnion();
                    while (onion != null) {
                        String tmp = onion + ".onion";
                        int[] ports = new int[]{80, 443};
                        for (int p : ports) {
                            if (checkAddress(tmp, p)) {
                                addAlive(tmp + ":" + p);
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
                        //System.out.println(onion + ":" + p + " is OK.");
                        return true;
                    } catch (Exception ex) {
                        //System.out.println(onion + ":" + p + " is NOK.");
                        return false;
                    }
                }

                /*
                private SSLSocketFactory trustAllCerts() {
                    TrustManager[] trustAllCerts = new TrustManager[]{
                        new X509TrustManager() {
                            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                                return new X509Certificate[0];
                            }

                            public void checkClientTrusted(
                                    java.security.cert.X509Certificate[] certs, String authType) {
                            }

                            public void checkServerTrusted(
                                    java.security.cert.X509Certificate[] certs, String authType) {
                            }
                        }
                    };

                    SSLContext ssc = null;
                    SSLSocketFactory ssf = null;
                    try {
                        ssc = SSLContext.getInstance("SSL");
                        ssc.init(null, trustAllCerts, new java.security.SecureRandom());
                        ssf = ssc.getSocketFactory();
                        return ssf;
                    } catch (KeyManagementException ex) {

                    } catch (NoSuchAlgorithmException ex) {

                    }
                    return null;
                }
                */
            };
            new Thread(r).start();
        }
    }
}
