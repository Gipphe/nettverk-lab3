package main;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Conversion {
    final Currency from;
    final Currency to;
    final double amount;

    Conversion(String s) {
        s = s.toUpperCase();
        Matcher matcher = Pattern.compile("(\\d+[.|,]?\\d*)(\\w+)2(\\w+)").matcher(s);
        if (matcher.find()) {
            String correctedAmount = matcher.group(1).replace(',', '.');
            amount = Double.parseDouble(correctedAmount);
            try {
                from = Currency.valueOf(matcher.group(2).toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new InvalidCurrency(e.getMessage(), matcher.group(2).toUpperCase());
            }
            try {
                to = Currency.valueOf(matcher.group(3).toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new InvalidCurrency(e.getMessage(), matcher.group(3).toUpperCase());
            }
        } else {
            amount = 0.0;
            from = null;
            to = null;
        }
    }
}
