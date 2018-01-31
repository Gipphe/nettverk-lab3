package main;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.util.HashMap;

class CurrencyConverterTest {
    private static HashMap<Currency, Double> getHashMap() {
        HashMap<Currency, Double> curr = new HashMap<>();
        curr.put(Currency.USD, 1.0);
        curr.put(Currency.NOK, 0.13);
        curr.put(Currency.GBP, 1.41);
        return curr;
    }

    @Test
    void should_convert_int_value_from_to_currencies() {
        HashMap<Currency, Double> curr = getHashMap();
        CurrencyConverter converter = new CurrencyConverter(curr);

        double result = converter.convert(Currency.NOK, Currency.USD, 1);
        Assertions.assertEquals(0.13, result);
    }

    @Test
    void should_convert_double_value_from_to_currencies() {
        HashMap<Currency, Double> curr = getHashMap();
        CurrencyConverter converter = new CurrencyConverter(curr);

        double result = converter.convert(Currency.NOK, Currency.USD, 10.5);
        Assertions.assertEquals(1.36, result);
    }

    @Test
    void should_convert_through_intermediary_currency() {
        HashMap<Currency, Double> curr = getHashMap();
        CurrencyConverter converter = new CurrencyConverter(curr);

        double result = converter.convert(Currency.NOK, Currency.GBP, 20);
        Assertions.assertEquals(1.84, result);
    }
}