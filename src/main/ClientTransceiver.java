package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.regex.Pattern;

public class ClientTransceiver extends Thread {
    private Socket socket;
    private InetAddress clientAddress;
    private CurrencyConverter converter;
    private int serverPort;
    private int clientPort;
    private String id;

    ClientTransceiver(String id, Socket socket, CurrencyConverter converter) {
        this.id = id;
        this.converter = converter;
        this.socket = socket;
        clientAddress = socket.getInetAddress();
        clientPort = socket.getPort();
        serverPort = socket.getLocalPort();
    }

    private static boolean isValidConversionString(String s) {
        return Pattern.compile("^(\\d+[.|,]?\\d*)(\\w+)2(\\w+)$").matcher(s).find();
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
                            } catch (InvalidCurrency e) {
                                response = "Invalid currency: " + e.getCurrency();
                            }
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                            response = e.getClass() + ": " + e.getMessage();
                        }
                        break;
                }
                out.println(response);
                System.out.println("Response #" + id + " - [" + socket.getLocalAddress().getHostAddress() + ":" + serverPort + "] > " + response);
            }
            socket.close();
        } catch (IOException e) {
            System.out.println("Exception occurred when trying to communicate with the client " + clientAddress.getHostAddress());
            System.out.println(e.getMessage());
        }
    }
}