package main;

import java.io.*;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TCPClient {

    private static String hostIpAddress;
    private static int hostPortInt;

    private static String[] stringReader(String s) {        // Reads the string the application receives from server.
        s = s.toUpperCase();
        Pattern p = Pattern.compile("(\\d+[.|,]?\\d*)(\\w+)2(\\w+)");
        Matcher matcher = p.matcher(s);
        String[] result = new String[3];

        if (matcher.find()) {

            String correctedAmount = matcher.group(1).replace(',', '.');
            result[0] = correctedAmount;                           // Amount that is to be converted
            result[1] = matcher.group(2).toUpperCase();     // Convert from currency
            result[2] = matcher.group(3).toUpperCase();     // Convert to currency
        }
        return result;
    }

    private static boolean ipCheck(String consoleInput) {       //Checks if user-input is a valid IP-address.
        Pattern regIp = Pattern.compile("(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})");
        Matcher ipMatch = regIp.matcher(consoleInput);
        return ipMatch.find();
    }

    private static void hostIp() throws Exception {
        String ip;
        BufferedReader ipAddress = new BufferedReader(new InputStreamReader(System.in));

        while ((ip = ipAddress.readLine()) != null && !ip.isEmpty()) {
            System.out.println("You entered: " + ip);
            if (ipCheck(ip)) {
                System.out.println("IP-address accepted");
                hostIpAddress = ip;
                break;
            } else {
                System.out.println("IP-address is not valid");
                System.out.println("Please try again: ");
            }
        }
    }

    private static void hostPortNumber() throws Exception {
        String port;
        BufferedReader portNumber = new BufferedReader(new InputStreamReader(System.in));

        while ((port = portNumber.readLine()) != null && !port.isEmpty()) {
            System.out.println("You entered: " + port);
            hostPortInt = Integer.parseInt(port);
            if (port.matches("[0-9]+") && port.length() > 0 && hostPortInt < 65536) {
                System.out.println("Port number accepted");
                break;
            } else {
                System.out.println("Port-number is not valid");
                System.out.println("Port number must be numbers (0-9) and can not be above 65535. Please ask the server administrator for a valid port number");
                System.out.println("Try again: ");
            }

        }
    }

    public static void main(String args[]) throws Exception {

        System.out.println("***********************************");
        System.out.println("**** Welcome to the TCP-Client ****");
        System.out.println("***********************************\n");
        System.out.println("Please enter a valid IP-Address for the host you want to connect to: ");

        hostIp(); //Ask the user of the client for the IP-address to the server. Checks if its valid and gives "hostIpAddress" a value.

        System.out.println("***********************************");
        System.out.println("Please enter a valid port number to your host: ");

        hostPortNumber(); //Ask the user of the client for the port number to the server. Checks if its valid and gives "hostPortInt" a value.

        Socket clientSocket = new Socket(hostIpAddress, hostPortInt);

        System.out.println("You are now connected to the server " + clientSocket.getInetAddress() + " through port number: " + clientSocket.getLocalPort());
        System.out.println("The client IP is " + clientSocket.getLocalAddress().getHostAddress() + " and is using the local port: " + clientSocket.getLocalPort());
        System.out.println("\n***********************************");
        System.out.println("\n" + "Use our client to ask for currency exchange rates.");
        System.out.println("By typing <amount><currency1>2<currency2> you will get the price of currency2 given in currency1");
        System.out.println("Start by sending the server a message as described above: ");

        //Inbound:
        BufferedReader keyboardInput = new BufferedReader(new InputStreamReader(System.in));
        BufferedReader serverInput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String userInput;

        //Outbound:
        PrintWriter outboundToServer = new PrintWriter(clientSocket.getOutputStream(), true);


        while ((userInput = keyboardInput.readLine()) != null && !userInput.isEmpty()) {

            System.out.println("--------------------------------------------------");
            System.out.println("Your input: " + userInput + "\n");
            outboundToServer.println(userInput);
            String serverMsg = serverInput.readLine();

            String[] stringResult = stringReader(userInput);

            if (serverMsg.startsWith("Invalid") || userInput.equals("curr") || userInput.equals("help") || userInput.equals("hei")) {
                System.out.println(serverMsg);
            } else  {
                    System.out.println("You asked how much " + stringResult[0] + " " + stringResult[1] + " is in " + stringResult[2]);
                    System.out.println(stringResult[0] + " " + stringResult[1] + " is " + serverMsg + " " + stringResult[2]);
                    System.out.println("--------------------------------------------------" + "\n");
                }
            }
        }

    }
