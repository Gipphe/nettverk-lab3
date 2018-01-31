package main;

/**
 * Created by Jørgen Eide on 29.01.2018.
 * Class represents a client TCP solution (Echo).
 **/

import com.sun.xml.internal.fastinfoset.util.StringArray;

import java.io.*;
import  java.net.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientTCP {

    //private static String ipString;


    private static String[] stringReader(String s)  {
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

   /*private static boolean ipCheck(String consoleInput) {
        Pattern regIp = Pattern.compile("(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})");
        Matcher ipMatch = regIp.matcher(consoleInput);
        ipString = ipMatch.group(1);
        return ipMatch.find();
    } */

    public static void main(String args[]) throws Exception {

      Socket clientSocket = new Socket("158.39.196.152", 5555); //Bør legge inn variabler her istedenfor hardkodet adr.

      System.out.println("Hello, Welcome To Our TCP Client! You are now connected to the server " + clientSocket.getInetAddress() + " through port number: " + clientSocket.getLocalPort());
      System.out.println("The client IP is " + clientSocket.getLocalAddress().getHostAddress() + " and is using the local port: " + clientSocket.getLocalPort());
      System.out.println("\n" + "Use our client to ask for currency-exchange rates.");
      System.out.println("By typing <amount><currency1>2<currency2> you will get the price of currency2 given in currency1");
      System.out.println("Start by sending the server a message as described above: ");

      //Inbound:
      BufferedReader keyboardInput = new BufferedReader(new InputStreamReader(System.in));
      BufferedReader serverInput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

      //Outbound:
      PrintWriter outboundToServer = new PrintWriter(clientSocket.getOutputStream(), true);

      String userInput;

      while((userInput = keyboardInput.readLine()) != null && !userInput.isEmpty()) {

          System.out.println("--------------------------------------------------");
          System.out.println("Your input: " + userInput + "\n");
          outboundToServer.println(userInput);
          String serverMsg = serverInput.readLine();

          String[] stringResult = stringReader(userInput);

          System.out.println("You got a respons from the Server with IP: " + clientSocket.getInetAddress() + "through port number: " + clientSocket.getLocalPort());
          System.out.println("You asked how much " + stringResult[0] + " " + stringResult[1] + " is in " + stringResult[2]);
          System.out.println(stringResult[0] + " " + stringResult[1] + " is " + serverMsg + " " + stringResult[2]);
          System.out.println("--------------------------------------------------" + "\n");
      }
    }
}