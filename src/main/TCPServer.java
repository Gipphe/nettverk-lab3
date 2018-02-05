package main;

import java.io.IOException;
import java.net.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TCPServer {
    public static void main(String... args) {
        if (args.length == 1) {
            new TCPServer(Integer.parseInt(args[0]));
        } else {
            new TCPServer(5555);
        }
    }

    private static String extractAddress(String s) {
        Matcher m = Pattern.compile("^/(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})").matcher(s);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }

    private TCPServer(int port) {
        HashMap<Currency, Double> currencies = CurrencyReader.read("currencies.csv");
        CurrencyConverter converter = new CurrencyConverter(currencies);

        StringBuilder ips = new StringBuilder();
        ips.append("\n");
        try {
            Enumeration<NetworkInterface> enumerator = NetworkInterface.getNetworkInterfaces();
            while (enumerator.hasMoreElements()) {
                NetworkInterface networkInterface = enumerator.nextElement();
                List<InterfaceAddress> addresses = networkInterface.getInterfaceAddresses();
                for (InterfaceAddress address : addresses) {
                    String realAddress = extractAddress(address.toString());
                    if (realAddress != null) {
                        ips.append(realAddress)
                                .append("\n");
                    }
                }
            }
        } catch (SocketException ignored) {}
        System.out.println("TCPServer initializing...");
        System.out.println("Server addresses are: " + ips.toString());

        int count = 0;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            //noinspection InfiniteLoopStatement
            while (true) {
                count += 1;
                ClientTransceiver clientTransceiver = new ClientTransceiver(String.valueOf(count), serverSocket.accept(), converter);
                clientTransceiver.start();
            }
        } catch (IOException e) {
            System.out.println("Exception occurred when trying to listen on port " + port + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }

        }
    }
}
