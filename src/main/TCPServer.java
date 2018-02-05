package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.Arrays;
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

    private static class ClientTransceiver extends Thread {
        private Socket socket;
        private InetAddress clientAddress;
        private CurrencyConverter converter;
        private int serverPort;
        private int clientPort;
        private String id;

        private ClientTransceiver(String id, Socket socket, CurrencyConverter converter) {
            this.id = id;
            this.converter = converter;
            this.socket = socket;
            clientAddress = socket.getInetAddress();
            clientPort = socket.getPort();
            serverPort = socket.getLocalPort();
        }

        public void run() {
            try (
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
                    ) {
                String receivedText;

                while (((receivedText = in.readLine()) != null)) {
                    System.out.println("Client #" + id + " - [" + clientAddress.getHostAddress() + ":" + clientPort + "] > " + receivedText);
                    String response;
                    switch (receivedText.toLowerCase()) {
                        case "help":
                            response = "Fungerer som følger:\n";
                            response += "###FROM2TO\n";
                            response += "hvor ### er mengden som skal konverteres,\n";
                            response += "FROM er valutaen du konverterer fra,\n";
                            response += "og TO er valutaen du konverterer til.";
                            break;
                        case "currencies":
                        case "currency":
                        case "curr":
                            Currency[] currencies = Currency.values();
                            response = Arrays.toString(currencies);
                            break;
                        case "hei":
                            response = "Well hello there.";
                            break;
                        case "hello there":
                            response = "General Kenobi.";
                            break;
                        default:
                            if (!isValidConversionString(receivedText)) {
                                response = "Invalid request.";
                                break;
                            }
                            try {
                                try {
                                    Conversion conversion = new Conversion(receivedText);
                                    double result = converter.convert(conversion.from, conversion.to, conversion.amount);
                                    System.out.println(result);
                                    response = String.valueOf(result);
                                } catch (IllegalArgumentException e) {
                                    response = "Invalid currency: " + e.getMessage();
                                }
                            } catch (Exception e) {
                                System.out.println(e.getMessage());
                                response = e.getClass() + ": " + e.getMessage();
                            }
                            break;
                    }
                    out.println(response);
                    System.out.println("I (main.TCPServer) [" + socket.getLocalAddress().getHostAddress() + ":" + serverPort + "] > " + response);
                }
                socket.close();
            } catch (IOException e) {
                System.out.println("Exception occurred when trying to communicate with the client " + clientAddress.getHostAddress());
                System.out.println(e.getMessage());
            }
        }

        private static boolean isValidConversionString(String s) {
            return Pattern.compile("^(\\d+[.|,]?\\d*)(\\w+)2(\\w+)$").matcher(s).find();
        }

        private static class Conversion {
            private Currency from;
            private Currency to;
            private double amount;
            private Conversion(String s) {
                s = s.toUpperCase();
                Matcher matcher = Pattern.compile("(\\d+[.|,]?\\d*)(\\w+)2(\\w+)").matcher(s);
                if (matcher.find()) {
                    String correctedAmount = matcher.group(1).replace(',', '.');
                    amount = Double.parseDouble(correctedAmount);
                    from = Currency.valueOf(matcher.group(2).toUpperCase());
                    to = Currency.valueOf(matcher.group(3).toUpperCase());
                }
            }
        }
    }
}
