package main;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Conversion {
    Currency from;
    Currency to;
    double amount;

    Conversion(String s) {
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
