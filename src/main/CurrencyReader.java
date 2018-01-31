package main;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class CurrencyReader {
    public static HashMap<Currency, Double> read(String path) {
        FileReader reader;
        try {
            reader = new FileReader(path);

            BufferedReader buf = new BufferedReader(reader);
            HashMap<Currency, Double> result = new HashMap<>();

            String line;
            while ((line = buf.readLine()) != null) {
                String[] parts = line.split(";");
                try {
                    result.put(
                            Currency.valueOf(parts[0].toUpperCase()),
                            Double.parseDouble(parts[1])
                    );
                } catch (Exception ignored) {}
            }
            return result;
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + path);
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println("Unexpected error occurred when attempting to read the file " + path);
            System.out.println(e.getMessage());
        }
        return null;
    }
}
