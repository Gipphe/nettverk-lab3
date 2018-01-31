package main;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;

public class CurrencyConverter {
    private HashMap<Currency, Double> currencies;

    CurrencyConverter(HashMap<Currency, Double> currencies) {
        this.currencies = currencies;
    }

    public double convert(Currency from, Currency to, double amount) {
        if (amount == 0.0) {
            return 0.0;
        }
        if (from.equals(to)) {
            return amount;
        }

        double inDollars = amount;
        if (from != Currency.USD) {
            inDollars = currencies.get(from) * amount;
        }
        return round(inDollars / currencies.get(to), 2);
    }
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
