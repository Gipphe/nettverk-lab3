package main;

public class InvalidCurrency extends RuntimeException {
    private final String currency;

    InvalidCurrency(String msg, String currency) {
        super(msg);
        this.currency = currency;
    }

    public String getCurrency() {
        return currency;
    }
}
