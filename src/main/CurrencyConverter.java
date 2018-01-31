package main;

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
        return inDollars / currencies.get(to);
    }
}
